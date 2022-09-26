package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class Presenter {

  protected Model model;
  protected Controller controller;
  protected RootStage stage;

  public abstract void init(Controller controller, Model model, RootStage stage);

}
