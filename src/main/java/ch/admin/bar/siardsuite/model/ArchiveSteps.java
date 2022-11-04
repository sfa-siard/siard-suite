package ch.admin.bar.siardsuite.model;

import java.util.List;

public class ArchiveSteps {

    public static final List<Step> steps;
    public static final List<Step> savedConnectionSteps;

    private final static Step selectDbms = new Step("archive.step.name.dbms", View.ARCHIVE_DB.getName(), 1, true);
    private final static Step dbConnection = new Step("archive.step.name.databaseConnectionURL",
                                                      View.ARCHIVE_CONNECTION.getName(),
                                                      2,
                                                      true);
    private final static Step dbLoading = new Step("", View.ARCHIVE_LOADING_PREVIEW.getName(), 3, false);
    private final static Step dbPreview = new Step("archive.step.name.preview",
                                                   View.ARCHIVE_PREVIEW.getName(),
                                                   3,
                                                   true);
    private final static Step editMetaData = new Step("archive.step.name.metadata",
                                                      View.ARCHIVE_METADATA_EDITOR.getName(),
                                                      4,
                                                      true);
    private final static Step dbDownload = new Step("archive.step.name.download",
                                                    View.ARCHIVE_DOWNLOAD.getName(),
                                                    5,
                                                    true);

    static {
        steps = List.of(
                selectDbms,
                dbConnection,
                dbLoading,
                dbPreview,
                editMetaData,
                dbDownload
        );

        savedConnectionSteps = List.of(
                dbConnection,
                dbLoading,
                dbPreview,
                editMetaData,
                dbDownload
        );
    }
}
