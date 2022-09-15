package ch.admin.bar.siardsuite;


import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.view.ArchiveStage;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.scene.control.Alert;

import java.io.IOException;

public class Controller {

  private Model model;

  public Controller(Model model) {
    this.model = model;
  }

  public boolean navigate(String view) {
    String currentView = model.getCurrentView();
    model.setCurrentView(view);
    try {
      new RootStage(this.model, this);
      return true;
    } catch (IOException e) {

      System.out.println(e.getMessage());
      model.setCurrentView(currentView);
      return false;
    }

  }
}
