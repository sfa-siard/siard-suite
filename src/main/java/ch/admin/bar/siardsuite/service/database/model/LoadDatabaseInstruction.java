package ch.admin.bar.siardsuite.service.database.model;

import ch.admin.bar.siard2.api.Archive;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.util.Pair;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

@Value
public class LoadDatabaseInstruction {
    DbmsConnectionData connectionData;
    Optional<File> saveAt;
    boolean loadOnlyMetadata;
    boolean viewsAsTables;
    Consumer<Archive> onSuccess;
    EventHandler<WorkerStateEvent> onFailure;
    ChangeListener<Number> onProgress;
    ChangeListener<ObservableList<Pair<String, Long>>> onStepCompleted;

    @Builder
    public LoadDatabaseInstruction(
            @NonNull DbmsConnectionData connectionData,
            @Nullable File saveAt,
            @Nullable Boolean loadOnlyMetadata,
            @Nullable Boolean viewsAsTables,
            @Nullable Consumer<Archive> onSuccess,
            @Nullable EventHandler<WorkerStateEvent> onFailure,
            @Nullable ChangeListener<Number> onProgress,
            @Nullable ChangeListener<ObservableList<Pair<String, Long>>> onSingleValueCompleted
    ) {
        this.connectionData = connectionData;
        this.saveAt = Optional.ofNullable(saveAt);
        this.loadOnlyMetadata = Optional.ofNullable(loadOnlyMetadata).orElse(false);
        this.viewsAsTables = Optional.ofNullable(viewsAsTables).orElse(false);
        this.onSuccess = Optional.ofNullable(onSuccess).orElse(archive -> {});
        this.onFailure = Optional.ofNullable(onFailure).orElse(event -> {});
        this.onProgress = Optional.ofNullable(onProgress)
                .orElse((observable, oldValue, newValue) -> {});
        this.onStepCompleted = Optional.ofNullable(onSingleValueCompleted)
                .orElse((observable, oldValue, newValue) -> {});
    }
}