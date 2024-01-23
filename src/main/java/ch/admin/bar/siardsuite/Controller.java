package ch.admin.bar.siardsuite;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.database.DatabaseConnectionFactory;
import ch.admin.bar.siardsuite.database.DatabaseLoadService;
import ch.admin.bar.siardsuite.database.DatabaseProperties;
import ch.admin.bar.siardsuite.database.DatabaseUploadService;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.model.Failure;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.presenter.tree.SiardArchiveMetaDataDetailsVisitor;
import ch.admin.bar.siardsuite.util.preferences.DbConnection;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.visitor.ArchiveVisitor;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.util.Pair;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Controller {

    private final Model model;
    private Archive tmpArchive;
    private DatabaseLoadService databaseLoadService;
    private DatabaseUploadService databaseUploadService;
    private Workflow workflow;

    @Setter
    @NonNull
    private Optional<DbConnection> recentDatabaseConnection = Optional.empty();

    public Optional<DbConnection> popRecentDatabaseConnection() {
        val tempReturnValue = recentDatabaseConnection;
        recentDatabaseConnection = Optional.empty();
        return tempReturnValue;
    }

    public Controller(Model model) {
        this.model = model;
    }

    public void loadDatabase(
            DbmsConnectionData connectionData,
            boolean onlyMetaData,
            EventHandler<WorkerStateEvent> onSuccess,
            EventHandler<WorkerStateEvent> onFailure
    ) throws SQLException {
        tmpArchive = model.initArchive();
        this.databaseLoadService = DatabaseConnectionFactory.getInstance(model, connectionData)
                .createDatabaseLoader(tmpArchive, onlyMetaData, false);
        this.onDatabaseLoadSuccess(onSuccess);
        this.onDatabaseLoadFailed(onFailure);
        this.databaseLoadService.start();
    }

    public void loadDatabase(
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

    public void closeDbConnection() {
        DatabaseConnectionFactory.disconnect();
    }

    public void updateConnectionData(String connectionUrl, String username, String databaseName, String password) {
        this.model.setConnectionUrl(connectionUrl);
        this.model.setDatabaseName(databaseName);
        this.model.setUsername(username);
        this.model.setPassword(password);
    }

    public void onDatabaseLoadSuccess(EventHandler<WorkerStateEvent> workerStateEventEventHandler) {
        this.databaseLoadService.setOnSucceeded(workerStateEventEventHandler);
    }

    public void onDatabaseLoadFailed(EventHandler<WorkerStateEvent> workerStateEventEventHandler) {
        this.databaseLoadService.setOnFailed(workerStateEventEventHandler);
    }

    public void addDatabaseLoadingValuePropertyListener(ChangeListener<ObservableList<Pair<String, Long>>> listener) {
        this.databaseLoadService.valueProperty().addListener(listener);
    }

    public void addDatabaseLoadingProgressPropertyListener(ChangeListener<Number> listener) {
        this.databaseLoadService.progressProperty().addListener(listener);
    }

    public void updateSchemaMap(Map schemaMap) {
        this.model.setSchemaMap(schemaMap);
    }

    public void onDatabaseUploadSuccess(EventHandler<WorkerStateEvent> workerStateEventEventHandler) {
        this.databaseUploadService.setOnSucceeded(workerStateEventEventHandler);
    }

    public void onDatabaseUploadFailed(EventHandler<WorkerStateEvent> workerStateEventEventHandler) {
        this.databaseUploadService.setOnFailed(workerStateEventEventHandler);
    }

    public void addDatabaseUploadingValuePropertyListener(ChangeListener<String> listener) {
        this.databaseUploadService.valueProperty().addListener(listener);
    }

    public void addDatabaseUploadingProgressPropertyListener(ChangeListener<Number> listener) {
        this.databaseUploadService.progressProperty().addListener(listener);
    }

    public Workflow getWorkflow() {
        return workflow;
    }


    public void uploadArchive(
            DbmsConnectionData connectionData,
            EventHandler<WorkerStateEvent> onSuccess,
                              EventHandler<WorkerStateEvent> onFailure
    ) throws SQLException {
        this.databaseUploadService = DatabaseConnectionFactory.getInstance(model, connectionData).createDatabaseUploader();
        this.onDatabaseUploadSuccess(onSuccess);
        this.onDatabaseUploadFailed(onFailure);
        this.databaseUploadService.start();
    }

    public void cancelDownload() {
        if (databaseLoadService != null && databaseLoadService.isRunning()) {
            this.databaseLoadService.cancel();
        }
        releaseResources();
    }

    public void cancelUpload() {
        if (databaseUploadService != null && databaseUploadService.isRunning()) {
            this.databaseUploadService.cancel();
        }
        releaseResources();
    }

    public void releaseResources() {
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

    public void failure(Failure failure) {
        this.model.setFailure(failure);
    }

    public String errorMessage() {
        return this.model.getFailure().message();
    }

    public String errorStackTrace() {
        return this.model.getFailure().stacktrace();
    }

    public void updateArchiveMetaData(String dbName, String description, String owner, String dataOriginTimespan,
                                      String archiverName, String archiverContact, URI lobFolder, File targetArchive,
                                      boolean viewsAsTables) {
        this.model.updateArchiveMetaData(
                dbName,
                description,
                owner,
                dataOriginTimespan,
                archiverName,
                archiverContact,
                lobFolder,
                targetArchive,
                viewsAsTables);
    }

    public void initializeWorkflow(Workflow workflow, RootStage stage) {
        this.model.clearSiardArchive();
        this.workflow = workflow;
        stage.openDialog(View.forWorkflow(workflow));
    }

    public void initializeExport(RootStage stage) {
        this.workflow = Workflow.EXPORT;
        stage.openDialog(View.EXPORT_SELECT_TABLES);
    }

    public void initializeUpload(RootStage stage) {
        this.workflow = Workflow.UPLOAD;
        stage.openDialog(View.UPLOAD_DB_CONNECTION_DIALOG);
    }

    public void start(RootStage stage) {
        this.model.clearSiardArchive();
        stage.navigate(View.START);
    }

    public SiardArchive getSiardArchive() {
        return model.getSiardArchive();
    }

    public void setCurrentView(View view) {
        this.model.setCurrentView(view);
    }

    public View getCurrentView() {
        return this.model.getCurrentView();
    }

    public void setDatabaseType(String databaseType) {
        model.setDatabaseType(databaseType);
    }

    public void provideArchiveObject(ArchiveVisitor visitor) {
        this.model.provideArchiveObject(visitor);
    }

    public void provideDatabaseArchiveMetaDataProperties(SiardArchiveMetaDataDetailsVisitor visitor) {
        this.model.provideDatabaseArchiveMetaDataProperties(visitor);
    }

    public DatabaseProperties getDatabaseProps() {
        return this.model.getDatabaseProps();
    }

    public StringProperty getDatabaseProduct() {
        return this.model.getDatabaseProduct();
    }

    public List<String> getDatabaseTypes() {
        return this.model.getDatabaseTypes();
    }

    public void setSiardArchive(String name, Archive archive) {
        this.model.setSiardArchive(name, archive);
    }

    public void saveArchiveOnlyMetaData() throws IOException {
        this.getSiardArchive().save();

    }
}
