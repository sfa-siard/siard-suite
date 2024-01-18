package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siardsuite.database.model.LoadDatabaseInstruction;
import ch.admin.bar.siardsuite.database.model.UploadDatabaseInstruction;
import ch.admin.bar.siardsuite.model.Model;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class DatabaseInteractionService {

    private final DatabaseConnectionFactory connectionFactory;

    @Deprecated // TODO remove model
    private final Model model;

    public InstructionExecutionHandle execute(final LoadDatabaseInstruction instruction) {
        val tempArchive = model.initArchive();
        val connection = connectionFactory.createConnection(instruction.getConnectionData());

        val dbLoadService = new DatabaseLoadService(
                connection,
                model,
                tempArchive,
                instruction.isLoadOnlyMetadata(),
                instruction.isViewsAsTables());

        dbLoadService.setOnSucceeded(event -> {
            instruction.getOnSuccess().handle(event);
            close(connection);
        });
        dbLoadService.setOnFailed(event -> {
            instruction.getOnFailure().handle(event);
            close(connection);
        });

        dbLoadService.start();

        dbLoadService.valueProperty().addListener((observable, oldValue, newValue) -> {
            instruction.getOnStepCompleted().changed(observable, oldValue, newValue);
        });

        dbLoadService.progressProperty().addListener((observable, oldValue, newValue) -> {
            instruction.getOnProgress().changed(observable, oldValue, newValue);
        });

        return () -> {
            dbLoadService.cancel();
            close(connection);
        };
    }

    public InstructionExecutionHandle execute(final UploadDatabaseInstruction instruction) {
        val connection = connectionFactory.createConnection(instruction.getConnectionData());

        val dbUploadService = new DatabaseUploadService(
                connection,
                model);

        dbUploadService.setOnSucceeded(event -> {
            instruction.getOnSuccess().handle(event);
            close(connection);
        });
        dbUploadService.setOnFailed(event -> {
            instruction.getOnFailure().handle(event);
            close(connection);
        });

        dbUploadService.start();

        dbUploadService.valueProperty().addListener(instruction.getOnStepCompleted());
        dbUploadService.progressProperty().addListener(instruction.getOnProgress());

        return () -> {
            dbUploadService.cancel();
            close(connection);
        };
    }

    public interface InstructionExecutionHandle {
        void cancel();
    }

    private static void close(final Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("Failed to close connection {} because: {}", connection, e);
        }
    }
}
