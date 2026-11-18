package ch.admin.bar.siardsuite.localwebapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Spec requirement "Browser is opened at launch", headless: the agent's {@code PATH} holds
 * only a fake {@code google-chrome} that records its arguments, so no real browser is started
 * and the test sees exactly what the agent launched. Linux only, because Windows and macOS
 * browsers are looked up at fixed install locations rather than on the {@code PATH}. That a
 * real window shows the frontend is covered by the Playwright test and the manual platform checks.
 */
class BrowserLaunchIT {

    private static final Duration WAIT = Duration.ofSeconds(10);
    // a started browser shows up about a second after the launch URL is logged
    private static final Duration NO_BROWSER_WAIT = Duration.ofSeconds(3);

    @TempDir
    Path fakeBrowserDir;

    @TempDir
    Path emptyDir;

    private Path recordedArgs;

    @BeforeEach
    void installFakeBrowser() throws IOException {
        assumeTrue(System.getProperty("os.name", "").toLowerCase(Locale.ROOT).startsWith("linux"),
                "browser lookup on the PATH is Linux only");
        recordedArgs = fakeBrowserDir.resolve("args.txt");
        Path browser = fakeBrowserDir.resolve("google-chrome");
        Files.writeString(browser, "#!/bin/sh\nprintf '%s\\n' \"$@\" > '" + recordedArgs + "'\n");
        Files.setPosixFilePermissions(browser, PosixFilePermissions.fromString("rwxr-xr-x"));
    }

    @Test
    void browserOpensOnLaunch() throws Exception {
        try (PackagedAgent agent = PackagedAgent.start(Map.of("PATH", fakeBrowserDir.toString()),
                "-Dsiard.agent.open-browser=true")) {
            assertTrue(awaitFile(recordedArgs, WAIT), "no browser started");
            assertEquals(List.of("--app=" + agent.launchUrl()), Files.readAllLines(recordedArgs));
        }
    }

    @Test
    void noBrowserCanBeOpened() throws Exception {
        try (PackagedAgent agent = PackagedAgent.start(Map.of("PATH", emptyDir.toString()),
                "-Djava.awt.headless=true", "-Dsiard.agent.open-browser=true")) {
            // start() has already seen the logged launch URL
            assertTrue(awaitOutput(agent, "No browser could be opened"), "fallback not logged");
            assertEquals(200, agent.get("/q/health").statusCode());
        }
    }

    @Test
    void browserOpeningDisabled() throws Exception {
        // PackagedAgent starts with siard.agent.open-browser=false
        try (PackagedAgent agent = PackagedAgent.start(Map.of("PATH", fakeBrowserDir.toString()))) {
            assertFalse(awaitFile(recordedArgs, NO_BROWSER_WAIT), "a browser was started");
            assertEquals(0, agent.processHandle().descendants().count(), "the agent started a process");
            assertFalse(agent.launchUrl().isEmpty());
        }
    }

    private static boolean awaitFile(Path file, Duration timeout) throws InterruptedException {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            if (Files.exists(file)) {
                return true;
            }
            Thread.sleep(100);
        }
        return false;
    }

    private static boolean awaitOutput(PackagedAgent agent, String text) throws InterruptedException {
        long deadline = System.nanoTime() + WAIT.toNanos();
        while (System.nanoTime() < deadline) {
            if (agent.output().stream().anyMatch(line -> line.contains(text))) {
                return true;
            }
            Thread.sleep(100);
        }
        return false;
    }
}
