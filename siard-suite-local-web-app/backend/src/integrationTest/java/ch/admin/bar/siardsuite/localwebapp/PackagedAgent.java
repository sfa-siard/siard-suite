package ch.admin.bar.siardsuite.localwebapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs the packaged agent ({@code build/quarkus-app/quarkus-run.jar}) as a separate process, so
 * tests see {@code LaunchMode.NORMAL} exactly like an installed app. Always use it in
 * try-with-resources: {@link #close()} stops the process even when a test fails, and a JVM
 * shutdown hook stops it if the test JVM itself dies.
 */
final class PackagedAgent implements AutoCloseable {

    static final Duration STARTUP_TIMEOUT = Duration.ofSeconds(30);

    private static final Path RUN_JAR = Path.of("build/quarkus-app/quarkus-run.jar");
    private static final Pattern LAUNCH_URL =
            Pattern.compile("launch URL: (http://127\\.0\\.0\\.1:(\\d+)/launch\\?token=(\\S+))");

    private final Process process;
    private final List<String> output = new CopyOnWriteArrayList<>();
    private final Thread shutdownHook;
    private String launchUrl;
    private int port;
    private String token;

    private PackagedAgent(Process process) {
        this.process = process;
        this.shutdownHook = new Thread(process::destroyForcibly);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        Thread reader = new Thread(this::readOutput, "packaged-agent-output");
        reader.setDaemon(true);
        reader.start();
    }

    /**
     * Starts the agent with the browser disabled and waits for its launch URL.
     */
    static PackagedAgent start(String... extraJvmArgs) throws IOException, InterruptedException {
        if (!Files.isRegularFile(RUN_JAR)) {
            throw new IllegalStateException(RUN_JAR.toAbsolutePath() + " missing; run quarkusBuild first");
        }
        List<String> command = new ArrayList<>();
        command.add(ProcessHandle.current().info().command().orElse("java"));
        // the launcher's JVM flags for siard-api (design D2)
        command.add("-Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
        command.add("--add-opens=java.xml/com.sun.org.apache.xerces.internal.jaxp=ALL-UNNAMED");
        command.add("--add-opens=java.xml/com.sun.org.apache.xalan.internal.xsltc.trax=ALL-UNNAMED");
        command.add("--add-opens=java.base/java.lang=ALL-UNNAMED");
        command.add("-Djava.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
        command.add("-Dsiard.agent.open-browser=false");
        command.addAll(List.of(extraJvmArgs));
        command.add("-jar");
        command.add(RUN_JAR.toString());

        PackagedAgent agent = new PackagedAgent(new ProcessBuilder(command).redirectErrorStream(true).start());
        try {
            agent.awaitLaunchUrl();
            return agent;
        } catch (RuntimeException | InterruptedException e) {
            agent.close();
            throw e;
        }
    }

    String launchUrl() {
        return launchUrl;
    }

    int port() {
        return port;
    }

    String token() {
        return token;
    }

    /** Waits for the process to end on its own; true if it did within the timeout. */
    boolean awaitExit(Duration timeout) throws InterruptedException {
        return process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() {
        process.destroy();
        try {
            if (!process.waitFor(10, TimeUnit.SECONDS)) {
                process.destroyForcibly().waitFor(10, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            process.destroyForcibly();
            Thread.currentThread().interrupt();
        } finally {
            try {
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
            } catch (IllegalStateException alreadyShuttingDown) {
                // the hook runs anyway
            }
        }
    }

    private void awaitLaunchUrl() throws InterruptedException {
        Instant deadline = Instant.now().plus(STARTUP_TIMEOUT);
        while (Instant.now().isBefore(deadline)) {
            for (String line : output) {
                Matcher matcher = LAUNCH_URL.matcher(line);
                if (matcher.find()) {
                    launchUrl = matcher.group(1);
                    port = Integer.parseInt(matcher.group(2));
                    token = matcher.group(3);
                    return;
                }
            }
            if (!process.isAlive()) {
                throw new IllegalStateException("agent exited before logging its launch URL:\n" + String.join("\n", output));
            }
            Thread.sleep(100);
        }
        throw new IllegalStateException("no launch URL within " + STARTUP_TIMEOUT + ":\n" + String.join("\n", output));
    }

    private void readOutput() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
        } catch (IOException ignored) {
            // the process ended
        }
    }
}
