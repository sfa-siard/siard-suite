package ch.admin.bar.siardsuite.model;

import java.util.List;

public class ArchiveSteps {

  private static final List<StepButton> defaultButtons = List.of(
          new StepButton("button.back", "secondary"),
          new StepButton("button.cancel", "secondary"),
          new StepButton("button.next", "primary"));


  private static final List<Step> steps = List.of(
          new Step("archive.step.name.dbms", View.ARCHIVE.getName(), 1 ),
          new Step("archive.step.name.connection", View.ARCHIVE.getName(), 2),
          new Step("archive.step.name.preview", View.ARCHIVE.getName(), 3),
          new Step("archive.step.name.metadata", View.ARCHIVE.getName(), 4),
          new Step("archive.step.name.download", View.ARCHIVE.getName(), 5)
  );

  public static List<Step> getSteps() {
    return steps;
  }
}
