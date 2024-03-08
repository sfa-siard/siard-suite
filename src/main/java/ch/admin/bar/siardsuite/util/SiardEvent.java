package ch.admin.bar.siardsuite.util;

import javafx.event.Event;
import javafx.event.EventType;

public class SiardEvent extends Event {
    public static final EventType<SiardEvent> UPDATE_LANGUAGE_EVENT;

    public SiardEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    static {
        UPDATE_LANGUAGE_EVENT = new EventType<>(ANY, "UPDATE_LANGUAGE_EVENT");
    }
}
