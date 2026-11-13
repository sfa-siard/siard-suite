package ch.admin.bar.siardsuite.localwebapp.events;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

/**
 * One multiplexed event stream per browser window; it is also the window's presence signal.
 */
@Path("/api/events")
public class EventsResource {

    @Inject
    EventStreams streams;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @Operation(summary = "Event stream of the agent; each event's data is an AgentEvent")
    @APIResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.SERVER_SENT_EVENTS,
            schema = @Schema(implementation = AgentEvent.class)))
    public void events(@Context SseEventSink sink, @Context Sse sse) {
        streams.open(sink, sse);
    }
}
