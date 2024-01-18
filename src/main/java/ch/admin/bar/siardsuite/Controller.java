package ch.admin.bar.siardsuite;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siardsuite.database.DatabaseConnectionFactory;
import ch.admin.bar.siardsuite.database.DatabaseInteractionService;
import ch.admin.bar.siardsuite.database.DatabaseLoadService;
import ch.admin.bar.siardsuite.database.DatabaseProperties;
import ch.admin.bar.siardsuite.database.DatabaseUploadService;
import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.database.model.LoadDatabaseInstruction;
import ch.admin.bar.siardsuite.database.model.UploadDatabaseInstruction;
import ch.admin.bar.siardsuite.model.Failure;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.presenter.tree.SiardArchiveMetaDataDetailsVisitor;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.visitor.ArchiveVisitor;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Controller {

    private final Model model;
    private Archive tmpArchive;
    private DatabaseLoadService databaseLoadService;
    private DatabaseUploadService databaseUploadService;
    private Workflow workflow;
    public String recentDatabaseConnection;

    private final DatabaseInteractionService databaseInteractionService;

    @Setter
    @Getter
    private Optional<DbmsConnectionData> tempConnectionData = Optional.empty();

    public Controller(Model model) {
        this.model = model;
        this.databaseInteractionService = new DatabaseInteractionService(new DatabaseConnectionFactory(), model);
    }

    public void loadDatabase(final LoadDatabaseInstruction instruction) {
        databaseInteractionService.execute(instruction);
    }

    public void uploadDatabase(final UploadDatabaseInstruction instruction) {
        databaseInteractionService.execute(instruction);
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

    public Workflow getWorkflow() {
        return workflow;
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
