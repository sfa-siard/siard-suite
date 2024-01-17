package ch.admin.bar.siardsuite.util;

import ch.admin.bar.siardsuite.database.DbmsRegistry;
import javafx.event.Event;
import javafx.event.EventType;
import lombok.Getter;

public class SiardEvent extends Event {
    public static final EventType<DbmsSelectedEvent> UPDATE_STEPPER_DBTYPE_EVENT;
    public static final EventType<SiardEvent> UPDATE_STEPPER_DBLOAD_EVENT;
    public static final EventType<SiardEvent> UPDATE_LANGUAGE_EVENT;
    public static final EventType<SiardEvent> ARCHIVE_LOADED;
    public static final EventType<SiardEvent> ARCHIVE_METADATA_UPDATED;
    public static final EventType<SiardEvent> DATABASE_DOWNLOADED;
    public static final EventType<SiardEvent> UPLOAD_DBMS_SELECTED;
    public static final EventType<SiardEvent> EXPAND_DATABASE_TABLE;
    public static final EventType<SiardEvent> UPLOAD_CONNECTION_UPDATED;
    public static final EventType<SiardEvent> UPLOAD_SUCCEDED;
    public static final EventType<SiardEvent> UPLOAD_FAILED;
    public static final EventType<SiardEvent> ERROR_OCCURED;

    public SiardEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    static {
        UPDATE_STEPPER_DBTYPE_EVENT = new EventType<>(ANY, "UPDATE_STEPPER_DBTYPE_EVENT");
        UPDATE_STEPPER_DBLOAD_EVENT = new EventType<>(ANY, "UPDATE_STEPPER_DBLOAD_EVENT");
        UPDATE_LANGUAGE_EVENT = new EventType<>(ANY, "UPDATE_LANGUAGE_EVENT");
        ARCHIVE_LOADED = new EventType<>(ANY, "UPDATE_ARCHIVE_TREE_EVENT");
        ARCHIVE_METADATA_UPDATED = new EventType<>("ARCHIVE_METADATA_UPDATED");
        DATABASE_DOWNLOADED = new EventType<>("DATABASE_DOWNLOADED");
        UPLOAD_DBMS_SELECTED = new EventType<>("UPLOAD_DBMS_SELECTED");
        UPLOAD_CONNECTION_UPDATED = new EventType<>( "UPLOAD_CONNECTION_UPDATED");
        UPLOAD_SUCCEDED = new EventType<>( "UPLOAD_SUCCEDED");
        UPLOAD_FAILED = new EventType<>( "UPLOAD_FAILED");
        EXPAND_DATABASE_TABLE = new EventType<>("EXPAND_DATABASE_TABLE");
        ERROR_OCCURED = new EventType<>("DATABASE_DOWNLOAD_FAILED");
    }

    public static class DbmsSelectedEvent extends Event {

        @Getter
        private final DbmsRegistry.Dbms<?> selectedDbms;

        public DbmsSelectedEvent(final DbmsRegistry.Dbms<?> selectedDbms) {
            super(UPDATE_STEPPER_DBTYPE_EVENT);
            this.selectedDbms = selectedDbms;
        }
    }
}
