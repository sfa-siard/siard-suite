package ch.admin.bar.siardsuite.model;

import java.util.List;

public class UploadSteps {
    public static final List<Step> steps;
    public  static  final List<Step> savedConnectionSteps;

    private final static Step selectDbms = new Step("upload.step.name.dbms", View.UPLOAD_CHOOSE_DBMS, 1, true);
    private final static Step dbConnection = new Step("upload.step.name.databaseConnection",
                                                      View.UPLOAD_DB_CONNECTION,
                                                      2,
                                                      true);
    private final static Step uploadArchiveInProgress = new Step("upload.step.name.uploading", View.UPLOADING, 3, true);
    private final static Step uploadArchiveResult = new Step("upload.step.name.result", View.UPLOAD_RESULT, 4, true);

    static {
        steps = List.of(selectDbms, dbConnection, uploadArchiveInProgress, uploadArchiveResult);
        savedConnectionSteps = List.of(dbConnection, uploadArchiveInProgress, uploadArchiveResult);
    }
}
