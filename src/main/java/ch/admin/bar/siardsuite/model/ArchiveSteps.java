package ch.admin.bar.siardsuite.model;

import java.util.List;

public class ArchiveSteps {

  private static final List<Step> steps = List.of(
          new Step("archive.step.name.dbms", View.ARCHIVE_DB.getName(), 1 ),
          new Step("archive.step.name.connection", View.ARCHIVE_CONNECTION.getName(), 2),
          new Step("archive.step.name.preview", View.ARCHIVE_DB.getName(), 3),
          new Step("archive.step.name.metadata", View.ARCHIVE_DB.getName(), 4),
          new Step("archive.step.name.download", View.ARCHIVE_DB.getName(), 5)
  );

  public static List<Step> getSteps() {
    return steps;
  }
}
