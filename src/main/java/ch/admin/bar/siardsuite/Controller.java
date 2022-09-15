package ch.admin.bar.siardsuite;


import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.view.RootStage;

import java.io.IOException;

public class Controller {

  private Model model;

  public Controller(Model model) {
    this.model = model;
  }

  public void navigate(String view, RootStage stage) {
    String currentView = model.getCurrentView();
    model.setCurrentView(view);
    try {
      stage.loadView(view);
    } catch (IOException e) {
      model.setCurrentView(currentView);
    }
  }
}
