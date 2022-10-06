package ch.admin.bar.siardsuite.model;

public record TreeAttributeWrapper(String name, int id, TreeContentView type) {
  @Override
  public String toString() {
    return name;
  }
}
