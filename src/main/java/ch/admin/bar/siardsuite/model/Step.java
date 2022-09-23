package ch.admin.bar.siardsuite.model;

public class Step {

  private String key;
  private String contentView;

  private StepButton[] buttons;
  private Integer position;

  public Step(String key, String contentView, Integer position, StepButton... buttons) {
    this.key = key;
    this.contentView = contentView;
    this.position = position;
    this.buttons = buttons;
  }

  public String getKey() {
    return key;
  }

  public String getContentView() {
    return contentView;
  }

  public StepButton[] getButtons() {
    return buttons;
  }
  public Integer getPosition() {
    return position;
  }
}
