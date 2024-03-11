package ch.admin.bar.siardsuite.framework.general;

import ch.admin.bar.siardsuite.database.model.LoadDatabaseInstruction;
import ch.admin.bar.siardsuite.database.model.UploadDatabaseInstruction;

import java.sql.SQLException;

public interface DbInteractionService {
    void execute(LoadDatabaseInstruction instruction) throws SQLException;

    void execute(UploadDatabaseInstruction instruction) throws SQLException;

    void cancelRunning();
}
