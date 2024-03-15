package ch.admin.bar.siardsuite.service;

import ch.admin.bar.siardsuite.service.database.DatabaseConnectionFactory;
import ch.admin.bar.siardsuite.service.database.DatabaseLoadService;
import ch.admin.bar.siardsuite.service.database.DatabaseUploadService;
import ch.admin.bar.siardsuite.service.database.model.LoadDatabaseInstruction;
import ch.admin.bar.siardsuite.service.database.model.UploadDatabaseInstruction;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * Defines a service for interacting with a database, providing methods to execute specific instructions.
 */
@RequiredArgsConstructor
public class DbInteractionService {

    private DatabaseLoadService databaseLoadService;
    private DatabaseUploadService databaseUploadService;

    private final ArchiveHandler archiveHandler;

    /**
     * Executes the specified instruction to load a database.
     *
     * @param instruction The instruction to load a database.
     */
    public void execute(LoadDatabaseInstruction instruction) {
        val archive = instruction.getSaveAt()
                .map(archiveHandler::init)
                .orElseGet(archiveHandler::init);

        this.databaseLoadService = DatabaseConnectionFactory.INSTANCE
                .createDatabaseLoader(
                        instruction.getConnectionData(),
                        instruction.getOnSuccess(),
                        archive,
                        instruction.isLoadOnlyMetadata(),
                        instruction.isViewsAsTables());

        this.databaseLoadService.setOnFailed(instruction.getOnFailure());

        this.databaseLoadService.start();

        this.databaseLoadService.valueProperty().addListener(instruction.getOnStepCompleted());
        this.databaseLoadService.progressProperty().addListener(instruction.getOnProgress());
    }

    /**
     * Executes the specified instruction to upload a database.
     *
     * @param instruction The instruction to upload a database.
     */
    public void execute(UploadDatabaseInstruction instruction) {
        this.databaseUploadService = DatabaseConnectionFactory.INSTANCE
                .createDatabaseUploader(
                        instruction.getConnectionData(),
                        instruction.getArchive(),
                        instruction.getSchemaNameMappings());
        this.databaseUploadService.setOnSucceeded(instruction.getOnSuccess());
        this.databaseUploadService.setOnFailed(instruction.getOnFailure());

        this.databaseUploadService.start();

        this.databaseUploadService.valueProperty().addListener(instruction.getOnStepCompleted());
        this.databaseUploadService.progressProperty().addListener(instruction.getOnProgress());
    }

    /**
     * Cancels any running database operation.
     */
    public void cancelRunning() {
        if (databaseLoadService != null && databaseLoadService.isRunning()) {
            this.databaseLoadService.cancel();
        }

        if (databaseUploadService != null && databaseUploadService.isRunning()) {
            this.databaseUploadService.cancel();
        }

        DatabaseConnectionFactory.disconnect();
    }
}
