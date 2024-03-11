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
     * @throws SQLException If an error occurs during the execution of the load database instruction.
     */
    void execute(LoadDatabaseInstruction instruction) throws SQLException;

    /**
     * Executes the specified instruction to upload a database.
     *
     * @param instruction The instruction to upload a database.
     * @throws SQLException If an error occurs during the execution of the upload database instruction.
     */
    void execute(UploadDatabaseInstruction instruction) throws SQLException;

    /**
     * Cancels any running database operation.
     */
    void cancelRunning();
}
