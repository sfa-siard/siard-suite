package ch.admin.bar.siardsuite.model;

import java.util.ArrayList;
import java.util.List;

public class ArchiveSteps {

    public static final List<Step> steps;

    private final static Step selectDbms = new Step("archive.step.name.dbms", View.ARCHIVE_DB, 1, true);
    private final static Step dbConnection = new Step("archive.step.name.databaseConnectionURL",
                                                      View.ARCHIVE_CONNECTION,
                                                      2,
                                                      true);
    private final static Step dbLoading = new Step("", View.ARCHIVE_LOADING_PREVIEW, 3, false);
    private final static Step dbPreview = new Step("archive.step.name.preview",
                                                   View.ARCHIVE_PREVIEW,
                                                   3,
                                                   true);
    private final static Step editMetaData = new Step("archive.step.name.metadata",
                                                      View.ARCHIVE_METADATA_EDITOR,
                                                      4,
                                                      true);
    private final static Step dbDownload = new Step("archive.step.name.download",
                                                    View.ARCHIVE_DOWNLOAD,
                                                    5,
                                                    true);

    static {
        steps = new ArrayList<>();
        steps.add(selectDbms);
        steps.add(dbConnection);
        steps.add(dbLoading);
        steps.add(dbPreview);
        steps.add(editMetaData);
        steps.add(dbDownload);
    }
}
