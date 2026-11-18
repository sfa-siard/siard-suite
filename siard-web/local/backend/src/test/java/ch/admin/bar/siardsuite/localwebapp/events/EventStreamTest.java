package ch.admin.bar.siardsuite.localwebapp.events;

import ch.admin.bar.siardsuite.localwebapp.support.EventStreamClient;
import ch.admin.bar.siardsuite.localwebapp.support.Sessions;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spec requirement "Event stream".
 */
@QuarkusTest
class EventStreamTest {

    @Test
    void streamOpensWithConnectedEvent() throws Exception {
        try (EventStreamClient stream = openAuthorized()) {
            assertEquals(200, stream.status());
            assertTrue(stream.contentType().orElse("").startsWith("text/event-stream"), stream.contentType().toString());
            Optional<String> data = stream.awaitLine(line -> line.startsWith("data:"), Duration.ofSeconds(2));
            assertTrue(data.isPresent(), "no event within 2 seconds");
            String json = data.get().substring("data:".length()).trim();
            assertEquals(ConnectedEvent.TYPE, new ObjectMapper().readTree(json).get("type").asText());
        }
    }

    // the keep-alive interval is 15 s, so one comment arrives well within the scenario's 60 s
    @Test
    void idleStreamIsKeptAlive() throws Exception {
        try (EventStreamClient stream = openAuthorized()) {
            Optional<String> comment = stream.awaitLine(line -> line.startsWith(":"),
                    EventStreams.KEEP_ALIVE_INTERVAL.plusSeconds(5));
            assertTrue(comment.isPresent(), "no keep-alive comment");
            assertFalse(stream.ended(), "stream closed");
        }
    }

    @Test
    void streamWithoutSession() {
        given().get("/api/events").then().statusCode(401);
    }

    private static EventStreamClient openAuthorized() throws Exception {
        return EventStreamClient.open(Sessions.port(), Sessions.cookieName(), Sessions.cookieValue());
    }
}
