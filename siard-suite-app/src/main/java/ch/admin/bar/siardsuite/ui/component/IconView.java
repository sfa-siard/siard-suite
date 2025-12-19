package ch.admin.bar.siardsuite.ui.component;

import ch.admin.bar.siardsuite.ui.common.Icon;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconView extends ImageView {

  public enum IconType {
    LOADING(Icon.LOADING.toImage(), "loading-icon"),
    OK(Icon.OK.toImage(), ""),
    ERROR(Icon.CIRCLE_ERROR.toImage(), "");

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
