package ch.admin.bar.siardsuite.model;

import java.util.List;

public class ArchiveSteps {

  private static final List<Step> steps = List.of(
          new Step("archive.step.name.dbms", View.ARCHIVE_PREVIEW.getName(), 1, true),
          new Step("archive.step.name.connection", View.ARCHIVE_CONNECTION.getName(), 2, true),
          new Step("", View.ARCHIVE_LOADING_PREVIEW.getName(), 3, false),
          new Step("archive.step.name.preview", View.ARCHIVE_PREVIEW.getName(), 3, true),
          new Step("archive.step.name.metadata", View.ARCHIVE_DB.getName(), 4, true),
          new Step("archive.step.name.download", View.ARCHIVE_DB.getName(), 5, true)
  );

  public static List<Step> getSteps() {
    return steps;
  }
}
