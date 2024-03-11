package ch.admin.bar.siardsuite.framework.general;

import ch.admin.bar.siardsuite.database.model.DbmsConnectionData;
import ch.admin.bar.siardsuite.database.model.LoadDatabaseInstruction;
import ch.admin.bar.siardsuite.database.model.UploadDatabaseInstruction;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.io.File;
import java.sql.SQLException;

public interface DbInteractionService {
    void execute(LoadDatabaseInstruction instruction) throws SQLException;

    void execute(UploadDatabaseInstruction instruction) throws SQLException;

    void cancelRunning();
}
