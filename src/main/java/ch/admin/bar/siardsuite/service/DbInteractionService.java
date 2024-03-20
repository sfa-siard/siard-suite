package ch.admin.bar.siardsuite.service;

import ch.admin.bar.siardsuite.service.database.DatabaseConnectionFactory;
import ch.admin.bar.siardsuite.service.database.DatabaseLoadService;
import ch.admin.bar.siardsuite.service.database.DatabaseUploadService;
import ch.admin.bar.siardsuite.service.database.model.LoadDatabaseInstruction;
import ch.admin.bar.siardsuite.service.database.model.UploadDatabaseInstruction;
import ch.admin.bar.siardsuite.service.preferences.UserPreferences;
import lombok.RequiredArgsConstructor;

/**
 * Defines a service for interacting with a database, providing methods to execute specific instructions.
 */
@RequiredArgsConstructor
public class DbInteractionService {

    private DatabaseLoadService databaseLoadService;
    private DatabaseUploadService databaseUploadService;

    private final ArchiveHandler archiveHandler;
    private final UserPreferences userPreferences;
    private final DatabaseConnectionFactory databaseConnectionFactory;

    /**
     * Executes the specified instruction to load a database.
     *
     * @param instruction The instruction to load a database.
     */
    public void execute(LoadDatabaseInstruction instruction) {
        this.databaseLoadService = new DatabaseLoadService(
                databaseConnectionFactory,
                archiveHandler,
                userPreferences,
                instruction);

        this.databaseLoadService.start();
    }

    /**
     * Executes the specified instruction to upload a database.
     *
     * @param instruction The instruction to upload a database.
     */
    public void execute(UploadDatabaseInstruction instruction) {
        this.databaseUploadService = new DatabaseUploadService(
                databaseConnectionFactory,
                userPreferences,
                instruction);

        this.databaseUploadService.start();
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

        databaseConnectionFactory.disconnect();
    }
}
