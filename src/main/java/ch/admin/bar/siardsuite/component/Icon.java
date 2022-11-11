package ch.admin.bar.siardsuite.component;

import ch.admin.bar.siardsuite.SiardApplication;
import javafx.scene.image.Image;

// provides a set of icons
public class Icon {
  public static Image loading;
  public static Image ok;
  public static Image error;

  static {
    loading = new Image(String.valueOf(SiardApplication.class.getResource("icons/loading.png")));
    ok = new Image(String.valueOf(SiardApplication.class.getResource("icons/ok_check.png")));
    error = new Image(String.valueOf(SiardApplication.class.getResource("icons/x-circle-red.png")));
  }
}
