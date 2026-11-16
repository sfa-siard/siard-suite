package ch.admin.bar.siardsuite.localwebapp.events;

import ch.admin.bar.siardsuite.localwebapp.agent.Presence;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import jakarta.enterprise.inject.Vetoed;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Spec scenario "Window closed while its stream opens": a stream whose client is already gone
 * when {@link EventStreams#open} runs must not stay counted, or the agent would never exit. The
 * test checks the count; that a count of 0 leads to the exit is covered by IdleExitTest. A real
 * connection cannot be closed reliably at that moment, so the SSE objects are fakes here.
 */
class EventStreamsTest {

    private final Vertx vertx = Vertx.vertx();

    @AfterEach
    void closeVertx() {
        vertx.close();
    }

    @Test
    void streamAlreadyClosedWhenOpenedIsNotCounted() {
        CountingPresence presence = new CountingPresence();
        EventStreams streams = new EventStreams();
        streams.vertx = vertx;
        streams.objectMapper = new ObjectMapper();
        streams.presence = presence;

        streams.open(new ClosedSink(), new FakeSse());

        assertEquals(0, presence.openStreams);
    }

    // vetoed: as a subclass it would inherit @ApplicationScoped and become a second Presence bean
    // in the @QuarkusTest application, which scans test classes too
    @Vetoed
    private static final class CountingPresence extends Presence {
        int openStreams;

        @Override
        public synchronized void streamOpened() {
            openStreams++;
        }

        @Override
        public synchronized void streamClosed() {
            openStreams--;
        }
    }

    // like Quarkus REST's sink once the client has disconnected
    private static final class ClosedSink implements SseEventSink {
        @Override
        public boolean isClosed() {
            return true;
        }

        @Override
        public CompletionStage<?> send(OutboundSseEvent event) {
            throw new IllegalStateException("Already closed");
        }

        @Override
        public void close() {
        }
    }

    private static final class FakeSse implements Sse {
        @Override
        public OutboundSseEvent.Builder newEventBuilder() {
            throw new UnsupportedOperationException("nothing is sent to a closed stream");
        }

        @Override
        public SseBroadcaster newBroadcaster() {
            return new FakeBroadcaster();
        }
    }

    private static final class FakeBroadcaster implements SseBroadcaster {
        @Override
        public void onError(BiConsumer<SseEventSink, Throwable> onError) {
        }

        @Override
        public void onClose(Consumer<SseEventSink> onClose) {
        }

        @Override
        public void register(SseEventSink sink) {
        }

        @Override
        public CompletionStage<?> broadcast(OutboundSseEvent event) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() {
        }

        @Override
        public void close(boolean cascading) {
        }
    }
}
