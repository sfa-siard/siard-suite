package ch.admin.bar.siardsuite.database.model;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.util.Pair;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Value
public class LoadDatabaseInstruction {
    DbmsConnectionData connectionData;
    boolean loadOnlyMetadata;
    boolean viewsAsTables;
    EventHandler<WorkerStateEvent> onSuccess;
    EventHandler<WorkerStateEvent> onFailure;
    ChangeListener<Number> onProgress;
    ChangeListener<ObservableList<Pair<String, Long>>> onStepCompleted;

    @Builder
    public LoadDatabaseInstruction(
            @NonNull DbmsConnectionData connectionData,
            @Nullable Boolean loadOnlyMetadata,
            @Nullable Boolean viewsAsTables,
            @Nullable EventHandler<WorkerStateEvent> onSuccess,
            @Nullable EventHandler<WorkerStateEvent> onFailure,
            @Nullable ChangeListener<Number> onProgress,
            @Nullable ChangeListener<ObservableList<Pair<String, Long>>> onSingleValueCompleted
    ) {
        this.connectionData = connectionData;
        this.loadOnlyMetadata = Optional.ofNullable(loadOnlyMetadata).orElse(false);
        this.viewsAsTables = Optional.ofNullable(viewsAsTables).orElse(false);
        this.onSuccess = Optional.ofNullable(onSuccess).orElse(event -> {});
        this.onFailure = Optional.ofNullable(onFailure).orElse(event -> {});
        this.onProgress = Optional.ofNullable(onProgress)
                .orElse((observable, oldValue, newValue) -> {});
        this.onStepCompleted = Optional.ofNullable(onSingleValueCompleted)
                .orElse((observable, oldValue, newValue) -> {});
    }
}
