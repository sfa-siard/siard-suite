package ch.admin.bar.siardsuite.localwebapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    // talks to the local agent directly; an inherited proxy setting must not reroute loopback requests
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .proxy(HttpClient.Builder.NO_PROXY)
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

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
        return start(Map.of(), extraJvmArgs);
    }

    /**
     * Like {@link #start(String...)}, with environment variables replaced for the agent process.
     * A later {@code -Dsiard.agent.open-browser=true} overrides the default {@code false}.
     */
    static PackagedAgent start(Map<String, String> environment, String... extraJvmArgs)
            throws IOException, InterruptedException {
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

        ProcessBuilder builder = new ProcessBuilder(command).redirectErrorStream(true);
        builder.environment().putAll(environment);
        PackagedAgent agent = new PackagedAgent(builder.start());
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

    /** The agent's output so far (stdout and stderr). */
    List<String> output() {
        return List.copyOf(output);
    }

    /** The agent process and the processes it started. */
    ProcessHandle processHandle() {
        return process.toHandle();
    }

    HttpResponse<String> get(String path, String... headers) throws IOException, InterruptedException {
        // the timeout turns an unexpectedly opened event stream into a failure instead of a hang
        HttpRequest.Builder request = HttpRequest.newBuilder(uri(path)).timeout(Duration.ofSeconds(10)).GET();
        if (headers.length > 0) {
            request.headers(headers);
        }
        return HTTP.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    /** Exchanges the launch token for a session; returns the cookie as {@code name=value}. */
    String sessionCookie() throws IOException, InterruptedException {
        return get("/launch?token=" + token).headers().firstValue("Set-Cookie")
                .orElseThrow(() -> new IllegalStateException("no session cookie"))
                .split(";", 2)[0];
    }

    /**
     * Opens {@code /api/events} like a browser window and returns once the {@code connected}
     * event has arrived; closing the returned stream closes the window.
     */
    AutoCloseable openWindow(String cookie) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(uri("/api/events"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Cookie", cookie)
                .GET().build();
        HttpResponse<InputStream> response = HTTP.send(request, HttpResponse.BodyHandlers.ofInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8));
        String line;
        do {
            line = reader.readLine();
        } while (line != null && !line.startsWith("data:"));
        if (line == null) {
            reader.close();
            throw new IllegalStateException("event stream ended before the connected event (status "
                    + response.statusCode() + ")");
        }
        return reader;
    }

    private URI uri(String path) {
        return URI.create("http://127.0.0.1:" + port + path);
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
