package ch.admin.bar.siardsuite.model;

import ch.admin.bar.siardsuite.model.database.DatabaseObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TreeAttributeWrapper {

  private final StringProperty name;
  private final int id;
  private final TreeContentView type;
  private final DatabaseObject databaseObject;

  public TreeAttributeWrapper(String name, int id, TreeContentView type, DatabaseObject databaseObject) {
    this.name = new SimpleStringProperty(name);
    this.id = id;
    this.type = type;
    this.databaseObject = databaseObject;
  }

  public TreeContentView getType() {
    return type;
  }

  public DatabaseObject getDatabaseObject() {
    return databaseObject;
  }

  @Override
  public String toString() {
    return name.get();
  }
}
