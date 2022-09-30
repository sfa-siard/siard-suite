package ch.admin.bar.siardsuite.util;

import javafx.event.Event;
import javafx.event.EventType;

public class SiardEvent extends Event {
  public static final EventType<SiardEvent> UPDATE_STEPPER_CONTENT_EVENT;

  public SiardEvent(EventType<? extends Event> eventType) {
    super(eventType);
  }

  static {
    UPDATE_STEPPER_CONTENT_EVENT = new EventType(ANY, "UPDATE_STEPPER_CONTENT_EVENT");
  }
}
