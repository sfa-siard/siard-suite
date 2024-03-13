package ch.admin.bar.siardsuite.database.model;

import ch.admin.bar.siard2.api.Archive;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

@Value
public class UploadDatabaseInstruction {
    Archive archive;
    Map<String, String> schemaNameMappings;
    DbmsConnectionData connectionData;
    EventHandler<WorkerStateEvent> onSuccess;
    EventHandler<WorkerStateEvent> onFailure;
    ChangeListener<Number> onProgress;
    ChangeListener<String> onStepCompleted;

    @Builder
    public UploadDatabaseInstruction(
            @NonNull Archive archive,
            @NonNull Map<String, String> schemaNameMappings,
            @NonNull DbmsConnectionData connectionData,
            @Nullable EventHandler<WorkerStateEvent> onSuccess,
            @Nullable EventHandler<WorkerStateEvent> onFailure,
            @Nullable ChangeListener<Number> onProgress,
            @Nullable ChangeListener<String> onStepCompleted
    ) {
        this.archive = archive;
        this.schemaNameMappings = schemaNameMappings;
        this.connectionData = connectionData;

        this.onSuccess = Optional.ofNullable(onSuccess).orElse(event -> {});
        this.onFailure = Optional.ofNullable(onFailure).orElse(event -> {});
        this.onProgress = Optional.ofNullable(onProgress).orElse((observable, oldValue, newValue) -> {});
        this.onStepCompleted = Optional.ofNullable(onStepCompleted).orElse((observable, oldValue, newValue) -> {});
    }
}
