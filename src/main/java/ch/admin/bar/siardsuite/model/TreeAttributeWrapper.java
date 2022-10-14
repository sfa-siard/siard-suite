package ch.admin.bar.siardsuite.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TreeAttributeWrapper {

  private StringProperty name;
  private int id;
  private TreeContentView type;

  public TreeAttributeWrapper(String name, int id, TreeContentView type) {
    this.name = new SimpleStringProperty(name);
    this.id = id;
    this.type = type;
  }

  public StringProperty getName() {
    return name;
  }

  public int getId() {
    return id;
  }

  public TreeContentView getType() {
    return type;
  }

  @Override
  public String toString() {
    return name.get();
  }
}
