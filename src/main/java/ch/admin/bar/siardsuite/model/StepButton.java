package ch.admin.bar.siardsuite.model;

public class StepButton {

  private String textKey;

  private String styleClass;

  public StepButton(String textKey, String styleClass) {
    this.textKey = textKey;
    this.styleClass = styleClass;
  }

  public String getTextKey() {
    return textKey;
  }

  public String getStyleClass() {
    return styleClass;
  }
}
