package ch.admin.bar.siardsuite.ui.common;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.val;

import java.net.URL;

import static ch.admin.bar.siardsuite.util.ResourcesResolver.resolve;

@Getter
public enum Icon {
    DOWNLOAD(resolve("ch/admin/bar/siardsuite/icons/download.png")),
    LEFT_ARROW(resolve("ch/admin/bar/siardsuite/icons/left_arrow.png")),
    SELECT_FILE(resolve("ch/admin/bar/siardsuite/icons/select_file_icon_gray.png")),
    INFO(resolve("ch/admin/bar/siardsuite/icons/info.png")),
    SERVER(resolve("ch/admin/bar/siardsuite/icons/server.png")),
    CIRCLE_WARN(resolve("ch/admin/bar/siardsuite/icons/circle-warn.png")),
    CIRCLE_ERROR(resolve("ch/admin/bar/siardsuite/icons/x-circle-red.png")),
    USER(resolve("ch/admin/bar/siardsuite/icons/user.png")),
    LOADING(resolve("ch/admin/bar/siardsuite/icons/loading.png")),
    OK(resolve("ch/admin/bar/siardsuite/icons/ok_check.png")),
    SIARD_DB(resolve("ch/admin/bar/siardsuite/icons/siard-db.png")),
    SIARD_DB_RED(resolve("ch/admin/bar/siardsuite/icons/siard-db_red.png")),
    ARCHIVE(resolve("ch/admin/bar/siardsuite/icons/archive.png")),
    ARCHIVE_RED(resolve("ch/admin/bar/siardsuite/icons/archive_red.png")),
    EXPORT(resolve("ch/admin/bar/siardsuite/icons/export.png")),
    EXPORT_RED(resolve("ch/admin/bar/siardsuite/icons/export_red.png")),
    DB(resolve("ch/admin/bar/siardsuite/icons/server.png"));

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

    public Image toResizedImageOfHeight(final double height) {
        val image = toImage();
        val origHeight = image.getHeight();
        val origWidth = image.getWidth();

        val ratio = (float)origHeight / (float)height;

        return new Image(this.url.toString(), origWidth * ratio, height, true, false);
    }
}
