package ch.admin.bar.siardsuite.component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;

import static ch.admin.bar.siardsuite.util.ResourcesResolver.*;

@Getter
@Setter
public class IconButton extends Label {

    private Icon icon;

    public IconButton() {
        this.setMaxHeight(42);
        this.setPrefWidth(42);
        this.setAlignment(Pos.CENTER);
        this.setCursor(Cursor.HAND);

        //this.setIcon(Icon.DOWNLOAD);
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        this.setGraphic(new ImageView(icon.getUrl().toString()));
    }

    public final void setOnAction(Runnable value) {
        this.setOnMouseClicked(event -> value.run());
    }

    @Getter
    public enum Icon {
        DOWNLOAD(resolve("ch/admin/bar/siardsuite/icons/download.png")),
        LEFT_ARROW(resolve("ch/admin/bar/siardsuite/icons/left_arrow.png"));

        private URL url;

        Icon(URL file) {
            this.url = file;
        }
    }
}