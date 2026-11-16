package ch.admin.bar.siardsuite.localwebapp.events;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * First event on every new event stream.
 */
@JsonTypeName(ConnectedEvent.TYPE)
public record ConnectedEvent() implements AgentEvent {
    public static final String TYPE = "connected";
}
