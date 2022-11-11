package ch.admin.bar.siardsuite.component;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class LabelIcon extends Label {

  public LabelIcon(String text, Integer pos, IconView.IconType type) {
    this.setText(text);
    this.getStyleClass().add("view-text");
    this.setContentDisplay(ContentDisplay.RIGHT);
    ImageView imageView = new IconView(pos, type);
    if (type.equals(IconView.IconType.LOADING)) {
      new Spinner(imageView).play();
    }
    this.setGraphic(imageView);
  }

}
