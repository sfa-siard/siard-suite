package ch.admin.bar.siardsuite.model;

public class Step {

  private String key;
  private String contentView;
  private Integer position;

  public Step(String key, String contentView, Integer position) {
    this.key = key;
    this.contentView = contentView;
    this.position = position;
  }

  public String getKey() {
    return key;
  }

  public String getContentView() {
    return contentView;
  }

  public Integer getPosition() {
    return position;
  }
}
