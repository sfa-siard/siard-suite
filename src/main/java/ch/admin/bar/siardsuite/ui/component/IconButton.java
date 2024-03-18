package ch.admin.bar.siardsuite.ui.component;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;

import static ch.admin.bar.siardsuite.util.ResourcesResolver.resolve;

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

    @Getter
    public enum Icon {
        DOWNLOAD(resolve("ch/admin/bar/siardsuite/icons/download.png")),
        LEFT_ARROW(resolve("ch/admin/bar/siardsuite/icons/left_arrow.png")),
        SELECT_FILE(resolve("ch/admin/bar/siardsuite/icons/select_file_icon_gray.png")),
        INFO(resolve("ch/admin/bar/siardsuite/icons/info.png")),
        SERVER(resolve("ch/admin/bar/siardsuite/icons/server.png")),
        CIRCLE_WARN(resolve("ch/admin/bar/siardsuite/icons/circle-warn.png")),
        CIRCLE_ERROR(resolve("ch/admin/bar/siardsuite/icons/x-circle-red.png")),
        USER(resolve("ch/admin/bar/siardsuite/icons/user.png"));


        private URL url;

        Icon(URL file) {
            this.url = file;
        }

        public ImageView toImageView() {
            return new ImageView(this.url.toString());
        }

        public Image toImage() {
            return new Image(this.url.toString());
        }
    }
}