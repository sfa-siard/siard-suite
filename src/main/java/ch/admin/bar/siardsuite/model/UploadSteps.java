package ch.admin.bar.siardsuite.model;

import java.util.ArrayList;
import java.util.List;

public class UploadSteps {
    public static final List<Step> steps;

    private final static Step selectDbms = new Step("upload.step.name.dbms", View.UPLOAD_CHOOSE_DBMS, 1, true);
    private final static Step dbConnection = new Step("upload.step.name.databaseConnection",
                                                      View.UPLOAD_DB_CONNECTION,
                                                      2,
                                                      true);
    private final static Step uploadArchiveInProgress = new Step("upload.step.name.uploading", View.UPLOADING, 3, false);
    private final static Step uploadArchiveResult = new Step("upload.step.name.result", View.UPLOAD_RESULT, 3, true);

    static {
        steps = new ArrayList<>();
        steps.add(selectDbms);
        steps.add(dbConnection);
        steps.add(uploadArchiveInProgress);
        steps.add(uploadArchiveResult);
    }
}
