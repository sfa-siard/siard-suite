package ch.admin.bar.siardsuite.framework.general;

import ch.admin.bar.siardsuite.database.model.LoadDatabaseInstruction;
import ch.admin.bar.siardsuite.database.model.UploadDatabaseInstruction;

import java.sql.SQLException;

/**
 * Defines a service for interacting with a database, providing methods to execute specific instructions.
 */
public interface DbInteractionService {
    /**
     * Executes the specified instruction to load a database.
     *
     * @param instruction The instruction to load a database.
     */
    void execute(LoadDatabaseInstruction instruction);

    /**
     * Executes the specified instruction to upload a database.
     *
     * @param instruction The instruction to upload a database.
     */
    void execute(UploadDatabaseInstruction instruction);

    /**
     * Cancels any running database operation.
     */
    void cancelRunning();
}
