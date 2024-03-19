package ch.admin.bar.siardsuite.ui.component;

import ch.admin.bar.siardsuite.ui.common.Icon;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IconButton extends Label {

    private Icon icon;

    public IconButton() {
        this.setMaxHeight(42);
        this.setPrefWidth(42);
        this.setAlignment(Pos.CENTER);
        this.setCursor(Cursor.HAND);
    }

    public IconButton(final Icon icon) {
        this();
        setIcon(icon);
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        this.setGraphic(new ImageView(icon.getUrl().toString()));
    }

    public final void setOnAction(Runnable value) {
        this.setOnMouseClicked(event -> value.run());
    }
}