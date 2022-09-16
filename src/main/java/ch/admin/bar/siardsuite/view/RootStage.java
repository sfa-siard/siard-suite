package ch.admin.bar.siardsuite.view;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.Presenter;
import ch.admin.bar.siardsuite.presenter.RootPresenter;
import ch.admin.bar.siardsuite.util.View;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class RootStage extends Stage {
  private Model model;
  private Controller controller;

  @FXML
  private BorderPane rootPane;

  public RootStage(Model model, Controller controller) throws IOException {
    this.model = model;
    this.controller = controller;

    FXMLLoader loader = new FXMLLoader(SiardApplication.class.getResource(View.ROOT.getName()));
    rootPane = loader.load();
    loader.<RootPresenter>getController().init(controller, this.model,this);

    // Load start view
    loadView(model.getCurrentView());

    Scene scene = new Scene(rootPane);
    scene.setFill(Color.WHITESMOKE);
    this.initStyle(StageStyle.UNDECORATED);
    this.setScene(scene);
    this.show();
  }

  public void loadView(String viewName) throws IOException {
    FXMLLoader loader = new FXMLLoader(SiardApplication.class.getResource(viewName));
    Parent container = loader.load();
    rootPane.setCenter(container);
    loader.<Presenter>getController().init(this.controller, this.model, this);
  }

}
