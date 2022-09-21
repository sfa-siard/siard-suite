package ch.admin.bar.siardsuite.model;

import java.util.List;

public class ArchiveViewSteps {

  private static final List<ViewStep> steps = List.of(
          new ViewStep("archive.step.name.dbms", View.START.getName(), 1),
          new ViewStep("archive.step.name.connection", View.ARCHIVE.getName(), 2),
          new ViewStep("archive.step.name.preview", View.ARCHIVE.getName(), 3),
          new ViewStep("archive.step.name.metadata", View.ARCHIVE.getName(), 4),
          new ViewStep("archive.step.name.download", View.ARCHIVE.getName(), 5)
  );

  public static List<ViewStep> getSteps() {
    return steps;
  }
}
