package ch.admin.bar.siardsuite.localwebapp.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Reads {@code GET /api/events} line by line like a browser's EventSource; {@link #close()}
 * drops the connection like closing the window.
 */
public final class EventStreamClient implements AutoCloseable {

    private static final String END_OF_STREAM = "\u0000end";

    // HTTP/1.1 like a browser on plain http; no proxy so loopback requests stay local
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .proxy(HttpClient.Builder.NO_PROXY)
            .build();

    private final HttpResponse<InputStream> response;
    private final BlockingQueue<String> lines = new LinkedBlockingQueue<>();
    private volatile boolean ended;

    private EventStreamClient(HttpResponse<InputStream> response) {
        this.response = response;
        Thread reader = new Thread(this::readLines, "event-stream-client");
        reader.setDaemon(true);
        reader.start();
    }

    public static EventStreamClient open(int port, String cookieName, String cookieValue)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/events"))
                .header("Accept", "text/event-stream")
                .header("Cookie", cookieName + "=" + cookieValue)
                .GET()
                .build();
        return new EventStreamClient(HTTP.send(request, HttpResponse.BodyHandlers.ofInputStream()));
    }

    public int status() {
        return response.statusCode();
    }

    public Optional<String> contentType() {
        return response.headers().firstValue("Content-Type");
    }

    /** Waits for the next line matching the predicate; empty on timeout or end of stream. */
    public Optional<String> awaitLine(Predicate<String> matching, Duration timeout) throws InterruptedException {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (true) {
            String line = lines.poll(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);
            if (line == null || END_OF_STREAM.equals(line)) {
                return Optional.empty();
            }
            if (matching.test(line)) {
                return Optional.of(line);
            }
        }
    }

    public boolean ended() {
        return ended;
    }

    @Override
    public void close() throws IOException {
        response.body().close();
    }

    private void readLines() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException closed) {
            // closed by the client or the server
        } finally {
            ended = true;
            lines.add(END_OF_STREAM);
        }
    }
}
