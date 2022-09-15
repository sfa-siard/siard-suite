package ch.admin.bar.siardsuite;


import ch.admin.bar.siardsuite.view.ArchiveStage;
import javafx.scene.control.Alert;

import java.io.IOException;

public class Controller {

  public Controller() {
    // add model
  }

  public boolean navigateSomewhere() {


    try {
      new ArchiveStage(this);
      return true;
    } catch (IOException e) {

      System.out.println(e.getMessage());

      return false;
    }
  }
}
