package ch.admin.bar.siardsuite.localwebapp.agent;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Opens the launch URL (design D9): a Chromium-family browser in application-window mode
 * ({@code --app=<url>}), otherwise the default browser, otherwise nothing (the URL is logged
 * anyway). Browsers are looked up at their install locations; none is assumed to be present.
 */
@ApplicationScoped
public class BrowserLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(BrowserLauncher.class);

    // in order of preference (design D9): Edge, then Chrome, then Chromium
    private static final List<String> WINDOWS_EXECUTABLES = List.of("msedge.exe", "chrome.exe");
    private static final List<String> WINDOWS_INSTALL_DIRS = List.of(
            "Microsoft\\Edge\\Application\\msedge.exe",
            "Google\\Chrome\\Application\\chrome.exe");
    private static final List<String> MAC_APPS = List.of(
            "Microsoft Edge.app/Contents/MacOS/Microsoft Edge",
            "Google Chrome.app/Contents/MacOS/Google Chrome",
            "Chromium.app/Contents/MacOS/Chromium");
    private static final List<String> LINUX_EXECUTABLES = List.of(
            "microsoft-edge", "microsoft-edge-stable",
            "google-chrome", "google-chrome-stable",
            "chromium", "chromium-browser");

    public void open(String url) {
        for (Path browser : appModeBrowsers()) {
            try {
                new ProcessBuilder(browser.toString(), "--app=" + url)
                        .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                        .redirectError(ProcessBuilder.Redirect.DISCARD)
                        .start();
                LOG.info("Opened {} in application-window mode", browser);
                return;
            } catch (IOException e) {
                LOG.debug("Could not start {}", browser, e);
            }
        }
        if (openDefaultBrowser(url)) {
            return;
        }
        LOG.info("No browser could be opened; open the launch URL above in a browser");
    }

    private static boolean openDefaultBrowser(String url) {
        if (GraphicsEnvironment.isHeadless()
                || !Desktop.isDesktopSupported()
                || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            return false;
        }
        try {
            Desktop.getDesktop().browse(URI.create(url));
            LOG.info("Opened the default browser");
            return true;
        } catch (IOException | RuntimeException e) {
            LOG.debug("Could not open the default browser", e);
            return false;
        }
    }

    private static List<Path> appModeBrowsers() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        Stream<Path> candidates;
        if (os.startsWith("windows")) {
            candidates = windowsBrowsers();
        } else if (os.startsWith("mac")) {
            candidates = macBrowsers();
        } else {
            candidates = LINUX_EXECUTABLES.stream().flatMap(name -> onPath(name).stream());
        }
        return candidates.filter(Files::isRegularFile).distinct().toList();
    }

    // registered App Paths (per user, then per machine), then the default install locations
    private static Stream<Path> windowsBrowsers() {
        List<Path> paths = new ArrayList<>();
        for (String executable : WINDOWS_EXECUTABLES) {
            for (String hive : List.of("HKCU", "HKLM")) {
                registeredAppPath(hive, executable).ifPresent(paths::add);
            }
            for (String env : List.of("ProgramFiles(x86)", "ProgramFiles", "LOCALAPPDATA")) {
                String base = System.getenv(env);
                if (base != null) {
                    WINDOWS_INSTALL_DIRS.stream()
                            .filter(dir -> dir.endsWith(executable))
                            .forEach(dir -> paths.add(Path.of(base, dir)));
                }
            }
        }
        return paths.stream();
    }

    private static Optional<Path> registeredAppPath(String hive, String executable) {
        String key = hive + "\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\\" + executable;
        try {
            Process process = new ProcessBuilder("reg", "query", key, "/ve")
                    .redirectErrorStream(true)
                    .start();
            List<String> lines;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                lines = reader.lines().toList();
            }
            if (!process.waitFor(5, TimeUnit.SECONDS) || process.exitValue() != 0) {
                return Optional.empty();
            }
            // e.g. "    (Default)    REG_SZ    C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
            return lines.stream()
                    .filter(line -> line.contains("REG_SZ"))
                    .map(line -> line.substring(line.indexOf("REG_SZ") + "REG_SZ".length()).trim())
                    .map(value -> value.replace("\"", ""))
                    .filter(value -> !value.isEmpty())
                    .map(Path::of)
                    .findFirst();
        } catch (IOException | RuntimeException e) {
            return Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

    private static Stream<Path> macBrowsers() {
        Path userApps = Path.of(System.getProperty("user.home"), "Applications");
        return MAC_APPS.stream()
                .flatMap(app -> Stream.of(Path.of("/Applications", app), userApps.resolve(app)));
    }

    private static Optional<Path> onPath(String name) {
        String path = System.getenv("PATH");
        if (path == null) {
            return Optional.empty();
        }
        return Stream.of(path.split(File.pathSeparator))
                .filter(dir -> !dir.isEmpty())
                .map(dir -> Path.of(dir, name))
                .filter(Files::isExecutable)
                .findFirst();
    }
}
