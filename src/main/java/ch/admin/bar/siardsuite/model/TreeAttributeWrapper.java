package ch.admin.bar.siardsuite.model;

import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.model.database.DatabaseObject;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;


@Value
public class TreeAttributeWrapper {

  String name;
  TreeContentView type;
  DatabaseObject databaseObject;

  Optional<RenderableForm> renderableForm;

  @Builder
  public TreeAttributeWrapper(
          @NonNull String name,
          @NonNull TreeContentView type,
          DatabaseObject databaseObject,
          RenderableForm renderableForm
  ) {
    this.name = name;
    this.type = type;
    this.databaseObject = databaseObject;
    this.renderableForm = Optional.ofNullable(renderableForm);
  }

  public TreeAttributeWrapper(String name, TreeContentView type, DatabaseObject databaseObject) {
    // constructor is needed because of compatibility reasons
    this.name = name;
    this.type = type;
    this.databaseObject = databaseObject;
    this.renderableForm = Optional.empty();
  }

  @Override
  public String toString() {
    return name;
  }
}
