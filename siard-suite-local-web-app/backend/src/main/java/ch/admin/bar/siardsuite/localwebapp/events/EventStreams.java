package ch.admin.bar.siardsuite.localwebapp.events;

import ch.admin.bar.siardsuite.localwebapp.agent.Presence;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The open {@code /api/events} streams (design D5): sends {@code connected} first, then a
 * keep-alive comment every 15 s, and reports each stream's close to {@link Presence} once,
 * whether the client disconnected or a write failed.
 */
@ApplicationScoped
public class EventStreams {

    static final Duration KEEP_ALIVE_INTERVAL = Duration.ofSeconds(15);

    @Inject
    Vertx vertx;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    Presence presence;

    private final Set<SseEventSink> open = ConcurrentHashMap.newKeySet();
    private Sse sse;
    private SseBroadcaster broadcaster;
    private Long keepAliveTimer;

    public synchronized void open(SseEventSink sink, Sse sse) {
        if (broadcaster == null) {
            this.sse = sse;
            broadcaster = sse.newBroadcaster();
            broadcaster.onClose(this::closed);
            broadcaster.onError((closedSink, error) -> closed(closedSink));
            keepAliveTimer = vertx.setPeriodic(KEEP_ALIVE_INTERVAL.toMillis(), id -> keepAlive());
        }
        open.add(sink);
        presence.streamOpened();
        sink.send(event(new ConnectedEvent()));
        broadcaster.register(sink);
    }

    @PreDestroy
    synchronized void stop() {
        if (keepAliveTimer != null) {
            vertx.cancelTimer(keepAliveTimer);
        }
    }

    private synchronized void keepAlive() {
        broadcaster.broadcast(sse.newEventBuilder().comment("keep-alive").build());
    }

    private void closed(SseEventSink sink) {
        if (open.remove(sink)) {
            presence.streamClosed();
        }
    }

    // the type is written by Jackson from AgentEvent's annotations; data is sent as plain
    // text so no non-standard content-type field ends up in the stream
    private OutboundSseEvent event(AgentEvent event) {
        try {
            return sse.newEventBuilder().data(objectMapper.writerFor(AgentEvent.class).writeValueAsString(event)).build();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("cannot serialise " + event, e);
        }
    }
}
