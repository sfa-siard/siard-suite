package ch.admin.bar.siardsuite.component;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconView extends ImageView {

  public enum IconType {
    LOADING(Icon.loading, "loading-icon"),
    OK(Icon.ok, ""),
    ERROR(Icon.error, "");

    private final Image icon;
    private final String styleClass;

    IconType(Image icon, String style) {
      this.icon = icon;
      this.styleClass = style;
    }
  }

  public IconView(Integer pos, IconType type) {
    this.setImage(type.icon);
    this.setFitHeight(14.0);
    this.setFitWidth(14.0);
    this.getStyleClass().addAll(type.styleClass, "icon-button");
    this.setId("dataLoader" + pos);
  }

}
