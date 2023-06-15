package ch.admin.bar.siardsuite;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.database.DatabaseConnectionFactory;
import ch.admin.bar.siardsuite.database.DatabaseLoadService;
import ch.admin.bar.siardsuite.database.DatabaseProperties;
import ch.admin.bar.siardsuite.database.DatabaseUploadService;
import ch.admin.bar.siardsuite.model.*;
import ch.admin.bar.siardsuite.model.database.DatabaseObject;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.presenter.ArchiveBrowserPresenter;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.visitor.ArchiveVisitor;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Controller {

    private final Model model;
    private Archive tmpArchive;
    private DatabaseLoadService databaseLoadService;
    private DatabaseUploadService databaseUploadService;
    private Workflow workflow;
    public String recentDatabaseConnection;

    public Controller(Model model) {
        this.model = model;
    }

    public void loadDatabase(boolean onlyMetaData, EventHandler<WorkerStateEvent> onSuccess,
                             EventHandler<WorkerStateEvent> onFailure
    ) throws SQLException {
        tmpArchive = model.initArchive();
        this.databaseLoadService = DatabaseConnectionFactory.getInstance(model)
                                                            .createDatabaseLoader(tmpArchive, onlyMetaData);
        this.onDatabaseLoadSuccess(onSuccess);
        this.onDatabaseLoadFailed(onFailure);
        this.databaseLoadService.start();
    }

    public void loadDatabase(File target, boolean onlyMetaData, EventHandler<WorkerStateEvent> onSuccess,
                             EventHandler<WorkerStateEvent> onFailure) throws SQLException {
        final Archive archive = model.initArchive(target, onlyMetaData);
        this.databaseLoadService = DatabaseConnectionFactory.getInstance(model)
                                                            .createDatabaseLoader(archive, onlyMetaData);
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

    public void updateSchemaMap(Map schemaMap) {
        this.model.setSchemaMap(schemaMap);
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


    public void uploadArchive(EventHandler<WorkerStateEvent> onSuccess,
                              EventHandler<WorkerStateEvent> onFailure) throws SQLException {
        this.databaseUploadService = DatabaseConnectionFactory.getInstance(model).createDatabaseUploader();
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
                                      String archiverName, String archiverContact, URI lobFolder, File targetArchive) {
        this.model.updateArchiveMetaData(
                dbName,
                description,
                owner,
                dataOriginTimespan,
                archiverName,
                archiverContact,
                lobFolder,
                targetArchive);
    }

    public void initializeWorkflow(Workflow workflow, RootStage stage) {
        this.model.clearSiardArchive();
        this.workflow = workflow;
        stage.openDialog(View.forWorkflow(workflow));
    }

    public void initializeExport(RootStage stage) {
        this.workflow = Workflow.EXPORT;
        stage.openDialog(View.OPEN_SIARD_ARCHIVE_DIALOG);
    }

    public void initializeUpload(RootStage stage) {
        this.workflow = Workflow.UPLOAD;
        stage.openDialog(View.EXPORT_SELECT_TABLES);
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

    public String getCurrentTableSearch() {
        return this.model.getCurrentTableSearch();
    }

    public TableSearchBase getCurrentTableSearchBase() {
        return this.model.getCurrentTableSearchBase();
    }

    public void setCurrentTableSearch(String s) {
        this.model.setCurrentTableSearch(s);
    }

    public TableSearchButton getCurrentTableSearchButton() {
        return this.model.getCurrentTableSearchButton();
    }

    public void setCurrentTableSearchButton(MFXButton button, boolean active) {
        this.model.setCurrentTableSearchButton(button, active);
    }

    public void setDatabaseType(String databaseType) {
        model.setDatabaseType(databaseType);
    }

    public void populate(TreeItem root) {
        this.model.populate(root);
    }

    public void provideArchiveObject(ArchiveVisitor visitor) {
        this.model.provideArchiveObject(visitor);
    }

    public void provideDatabaseArchiveProperties(SiardArchiveVisitor visitor) {
        this.model.provideDatabaseArchiveProperties(visitor);
    }

    public void provideDatabaseArchiveProperties(SiardArchiveVisitor visitor, DatabaseObject databaseObject) {
        this.model.provideDatabaseArchiveProperties(visitor, databaseObject);
    }

    public void provideDatabaseArchiveMetaDataProperties(SiardArchiveMetaDataVisitor visitor) {
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

    public String getCurrentMetaSearch() {
        return this.model.getCurrentMetaSearch();
    }

    public void setSiardArchive(String name, Archive archive) {
        this.model.setSiardArchive(name, archive);
    }


    public void setCurrentMetaSearch(String s) {
        this.model.setCurrentMetaSearch(s);
    }

    public Set<MetaSearchHit> aggregatedMetaSearch(String s) {
        return this.model.aggregatedMetaSearch(s);
    }

    public ArchiveBrowserPresenter getCurrentPreviewPresenter() {
        return this.model.getCurrentPreviewPresenter();
    }

    public void setCurrentPreviewPresenter(ArchiveBrowserPresenter archiveBrowserPresenter) {
        this.model.setCurrentPreviewPresenter(archiveBrowserPresenter);
    }

    public void setCurrentTableSearchBase(TableView<Map> tableView, LinkedHashSet<Map> maps) {
        this.model.setCurrentTableSearchBase(tableView, maps);
    }

    public void saveArchiveOnlyMetaData(File targetArchive) throws IOException {
        ((ArchiveImpl) this.tmpArchive).isMetaDataDifferent("1",
                                                            "2"); // hacky way to tell the archive that it has changed. it won't save it otherwise
        this.getSiardArchive().save(targetArchive);

    }
}
