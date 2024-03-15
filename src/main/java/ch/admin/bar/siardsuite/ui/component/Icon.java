package ch.admin.bar.siardsuite.ui.component;

import ch.admin.bar.siardsuite.SiardApplication;
import javafx.scene.image.Image;

// provides a set of icons
public class Icon {
    public static final Image loading;
    public static final Image ok;
    public static final Image error;
    public static final Image siardDb;
    public static final Image siardDbRed;
    public static final Image archive;
    public static final Image archiveRed;
    public static final Image export;
    public static final Image exportRed;
    public static final Image db;

    static {
        loading = new Image(String.valueOf(SiardApplication.class.getResource("icons/loading.png")));
        ok = new Image(String.valueOf(SiardApplication.class.getResource("icons/ok_check.png")));
        error = new Image(String.valueOf(SiardApplication.class.getResource("icons/x-circle-red.png")));
        siardDb = new Image(String.valueOf(SiardApplication.class.getResource("icons/siard-db.png")));
        siardDbRed = new Image(String.valueOf(SiardApplication.class.getResource("icons/siard-db_red.png")));
        archive = new Image(String.valueOf(SiardApplication.class.getResource("icons/archive.png")));
        archiveRed = new Image(String.valueOf(SiardApplication.class.getResource("icons/archive_red.png")));
        export = new Image(String.valueOf(SiardApplication.class.getResource("icons/export.png")));
        exportRed = new Image(String.valueOf(SiardApplication.class.getResource("icons/export_red.png")));
        db = new Image(String.valueOf(SiardApplication.class.getResource(
                "icons/server.png")), 16.0, 16.0, true, false);
    }
}
