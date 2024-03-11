package ch.admin.bar.siardsuite;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.database.DatabaseConnectionFactory;
import ch.admin.bar.siardsuite.database.DatabaseLoadService;
import ch.admin.bar.siardsuite.database.DatabaseUploadService;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.database.model.LoadDatabaseInstruction;
import ch.admin.bar.siardsuite.database.model.UploadDatabaseInstruction;
import ch.admin.bar.siardsuite.framework.general.DbInteractionService;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.util.preferences.RecentDbConnection;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

public class Controller implements DbInteractionService {

    private final Model model;
    private Archive tmpArchive;
    private DatabaseLoadService databaseLoadService;
    private DatabaseUploadService databaseUploadService;

    @Setter
    @Getter
    @NonNull
    private Optional<RecentDbConnection> recentDatabaseConnection = Optional.empty();

    @Setter
    @Getter
    @NonNull
    private Optional<DbmsConnectionData> databaseConnectionData = Optional.empty();

    public Controller(Model model) {
        this.model = model;
    }

    private void loadDatabase(
            DbmsConnectionData connectionData,
            File target,
            boolean onlyMetaData,
            boolean viewsAsTables,
            EventHandler<WorkerStateEvent> onSuccess,
            EventHandler<WorkerStateEvent> onFailure
    ) throws SQLException {
        final Archive archive = model.initArchive(target, onlyMetaData);
        this.databaseLoadService = DatabaseConnectionFactory.getInstance(model, connectionData)
                .createDatabaseLoader(archive, onlyMetaData, viewsAsTables);
        this.onDatabaseLoadSuccess(onSuccess);
        this.onDatabaseLoadFailed(onFailure);
        this.databaseLoadService.start();
    }

    @Override
    public void execute(LoadDatabaseInstruction instruction) throws SQLException {
        loadDatabase(
                instruction.getConnectionData(),
                instruction.getSaveAt()
                        .orElseGet(() -> {
                            try {
                                return File.createTempFile("tmp", ".siard");
                            } catch (IOException e) {
                                throw new RuntimeException("Failed to create temp file for temp archive", e);
                            }
                        }),
                instruction.isLoadOnlyMetadata(),
                instruction.isViewsAsTables(),
                event -> {
                    instruction.getOnSuccess().accept(getSiardArchive());
                    DatabaseConnectionFactory.disconnect();
                },
                instruction.getOnFailure()
        );

        this.databaseLoadService.valueProperty().addListener(instruction.getOnStepCompleted());
        this.databaseLoadService.progressProperty().addListener(instruction.getOnProgress());
    }

    @Override
    public void execute(UploadDatabaseInstruction instruction) throws SQLException {
        uploadArchive(
                instruction.getConnectionData(),
                instruction.getArchive(),
                instruction.getSchemaNameMappings(),
                event -> {
                    instruction.getOnSuccess().handle(event);
                    DatabaseConnectionFactory.disconnect();
                },
                instruction.getOnFailure()
        );

        addDatabaseUploadingValuePropertyListener(instruction.getOnStepCompleted());
        addDatabaseUploadingProgressPropertyListener(instruction.getOnProgress());
    }

    @Override
    public void cancelRunning() {
        if (databaseLoadService != null && databaseLoadService.isRunning()) {
            this.databaseLoadService.cancel();
        }

        if (databaseUploadService != null && databaseUploadService.isRunning()) {
            this.databaseUploadService.cancel();
        }

        releaseResources();
    }

    private void closeDbConnection() {
        DatabaseConnectionFactory.disconnect();
    }

    private void onDatabaseLoadSuccess(EventHandler<WorkerStateEvent> workerStateEventEventHandler) {
        this.databaseLoadService.setOnSucceeded(workerStateEventEventHandler);
    }

    private void onDatabaseLoadFailed(EventHandler<WorkerStateEvent> workerStateEventEventHandler) {
        this.databaseLoadService.setOnFailed(workerStateEventEventHandler);
    }

    private void onDatabaseUploadSuccess(EventHandler<WorkerStateEvent> workerStateEventEventHandler) {
        this.databaseUploadService.setOnSucceeded(workerStateEventEventHandler);
    }

    private void onDatabaseUploadFailed(EventHandler<WorkerStateEvent> workerStateEventEventHandler) {
        this.databaseUploadService.setOnFailed(workerStateEventEventHandler);
    }

    private void addDatabaseUploadingValuePropertyListener(ChangeListener<String> listener) {
        this.databaseUploadService.valueProperty().addListener(listener);
    }

    private void addDatabaseUploadingProgressPropertyListener(ChangeListener<Number> listener) {
        this.databaseUploadService.progressProperty().addListener(listener);
    }

    private void uploadArchive(
            DbmsConnectionData connectionData,
            Archive archive,
            Map<String, String> schemaNameMappings,
            EventHandler<WorkerStateEvent> onSuccess,
            EventHandler<WorkerStateEvent> onFailure
    ) throws SQLException {
        this.databaseUploadService = DatabaseConnectionFactory
                .getInstance(model, connectionData)
                .createDatabaseUploader(archive, schemaNameMappings);
        this.onDatabaseUploadSuccess(onSuccess);
        this.onDatabaseUploadFailed(onFailure);
        this.databaseUploadService.start();
    }

    private void cancelDownload() {
        if (databaseLoadService != null && databaseLoadService.isRunning()) {
            this.databaseLoadService.cancel();
        }
        releaseResources();
    }

    private void cancelUpload() {
        if (databaseUploadService != null && databaseUploadService.isRunning()) {
            this.databaseUploadService.cancel();
        }
        releaseResources();
    }

    private void releaseResources() {
        closeDbConnection();
        removeTmpArchive();
    }

    private void removeTmpArchive() {
        try {
            File tmpFile;
            if (tmpArchive != null && tmpArchive.getFile() != null) {
                tmpFile = tmpArchive.getFile();
            } else {
                tmpFile = Paths.get(Model.TMP_SIARD).toFile();
            }
            if (!tmpFile.delete()) {
                tmpFile.deleteOnExit();
            }
        } catch (Exception ignored) {
        }
    }

    public void initializeWorkflow(Workflow workflow, RootStage stage) {
        this.model.clearSiardArchive();
        this.setRecentDatabaseConnection(Optional.empty());

        switch (workflow) {
            case ARCHIVE:
                stage.openRecentConnectionsDialogForArchiving(
                        () -> stage.navigate(View.ARCHIVE_STEPPER),
                        dbConnection -> {
                            setRecentDatabaseConnection(Optional.of(dbConnection));
                            stage.navigate(View.ARCHIVE_STEPPER);
                        }
                );
                break;
            case OPEN:
                stage.openSelectSiardFileDialog((file, archive) -> {
                            setSiardArchive(file.getName(), archive);
                            stage.navigate(View.OPEN_SIARD_ARCHIVE_PREVIEW);
                        }
                );
                break;
            case EXPORT:
                stage.openSelectSiardFileDialog((file, archive) -> {
                            setSiardArchive(file.getName(), archive);
                            stage.openDialog(View.EXPORT_SELECT_TABLES);
                        }
                );
                break;
            case UPLOAD:
                stage.openSelectSiardFileDialog((file, archive) -> {
                            setSiardArchive(file.getName(), archive);
                            stage.openRecentConnectionsDialogForUploading(
                                    () -> stage.navigate(View.UPLOAD_STEPPER),
                                    dbConnection -> {
                                        setRecentDatabaseConnection(Optional.of(dbConnection));
                                        stage.navigate(View.UPLOAD_STEPPER);
                                    }
                            );
                        }
                );
                break;
        }
    }

    public void start(RootStage stage) {
        this.model.clearSiardArchive();
        stage.navigate(View.START);
    }

    public SiardArchive getSiardArchive() {
        return model.getSiardArchive();
    }

    public void setSiardArchive(String name, Archive archive) {
        this.model.setSiardArchive(name, archive);
    }

    public void saveArchiveOnlyMetaData() throws IOException {
        this.getSiardArchive().save();
    }
}
