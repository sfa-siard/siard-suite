package ch.admin.bar.siardsuite.service.database;

import ch.admin.bar.siard2.cmd.MetaDataToDb;
import ch.admin.bar.siard2.cmd.PrimaryDataToDb;
import ch.admin.bar.siardsuite.service.database.model.UploadDatabaseInstruction;
import ch.admin.bar.siardsuite.service.preferences.UserPreferences;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.val;

public class DatabaseUploadService extends Service<String> {

    private final DatabaseConnectionFactory connectionFactory;
    private final UserPreferences userPreferences;

    private final UploadDatabaseInstruction instruction;

    public DatabaseUploadService(DatabaseConnectionFactory connectionFactory, UserPreferences userPreferences, UploadDatabaseInstruction instruction) {
        this.connectionFactory = connectionFactory;
        this.userPreferences = userPreferences;
        this.instruction = instruction;

        this.setOnSucceeded(instruction.getOnSuccess());
        this.setOnFailed(instruction.getOnFailure());
        this.valueProperty().addListener(instruction.getOnStepCompleted());
        this.progressProperty().addListener(instruction.getOnProgress());
    }

    @Override
    protected Task<String> createTask() {
        return new DatabaseUploadTask(
                connectionFactory,
                userPreferences,
                instruction
        );
    }

    @RequiredArgsConstructor
    private static class DatabaseUploadTask extends Task<String> {

        private final DatabaseConnectionFactory connectionFactory;
        private final UserPreferences userPreferences;

        private final UploadDatabaseInstruction instruction;

        @Override
        protected String call() throws Exception {
            val connection = connectionFactory.getOrCreateConnection(instruction.getConnectionData());
            val timeout = userPreferences.getStoredOptions().getQueryTimeout();

            // TODO overwrite and metadataonly?
            boolean isOverwrite = true;
            boolean metaDataOnly = false;

            val metaDataToDb = MetaDataToDb.newInstance(
                    connection.getMetaData(),
                    instruction.getArchive().getMetaData(),
                    instruction.getSchemaNameMappings());
            metaDataToDb.setQueryTimeout(timeout);

            if (!isOverwrite) {
                if ((metaDataToDb.tablesDroppedByUpload() == 0)) {
                    metaDataToDb.typesDroppedByUpload();
                }
            }

            updateValue("Metadata");
            updateProgress(0, 100);
            metaDataToDb.upload(new SiardCmdProgressListener(this::updateProgress));

            if (!metaDataOnly) {
                val primaryDataToDb = PrimaryDataToDb.newInstance(
                        connection,
                        instruction.getArchive(),
                        metaDataToDb.getArchiveMapping(),
                        metaDataToDb.supportsArrays(),
                        metaDataToDb.supportsDistincts(),
                        metaDataToDb.supportsUdts());
                primaryDataToDb.setQueryTimeout(timeout);

                updateValue("Dataload");
                updateProgress(0, 100);
                primaryDataToDb.upload(new SiardCmdProgressListener(this::updateProgress));
            }
            return null;
        }
    }
}
