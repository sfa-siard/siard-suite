package ch.admin.bar.siardsuite.view;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.Presenter;
import ch.admin.bar.siardsuite.presenter.RootPresenter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
//import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class RootStage extends Stage {
  private final Model model;
  private final Controller controller;

  @FXML
  private final BorderPane rootPane;
  @FXML
  private final BorderPane dialogPane;

  public RootStage(Model model, Controller controller) throws IOException {
    this.model = model;
    this.controller = controller;

    FXMLLoader loader = new FXMLLoader(SiardApplication.class.getResource(View.ROOT.getName()));
    rootPane = loader.load();
    loader.<RootPresenter>getController().init(controller, this.model,this);

    // load start view
    navigate(model.getCurrentView());

    // prepare for dialogs
    loader = new FXMLLoader(SiardApplication.class.getResource(View.DIALOG.getName()));
    dialogPane = loader.load();
    loader.<DialogPresenter>getController().init(controller, this.model,this);
    dialogPane.setVisible(false);

    // set overall stack pane
    StackPane stackPane = new StackPane(rootPane, dialogPane);

    Scene scene = new Scene(stackPane);
//    scene.setFill(Color.WHITESMOKE);
    scene.getRoot().getStyleClass().add("root");
    scene.setFill(null);
    this.initStyle(StageStyle.TRANSPARENT);
    this.setScene(scene);
    this.show();
  }

  private void setCenter(BorderPane borderPane, String viewName) {
    FXMLLoader loader = new FXMLLoader(SiardApplication.class.getResource(viewName));
    try {
      borderPane.setCenter(loader.load());
      loader.<Presenter>getController().init(this.controller, this.model, this);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void navigate(String viewName) {
    model.setCurrentView(viewName);
    setCenter(rootPane, viewName);
  }

  public void openDialog(String viewName) {
    setCenter(dialogPane, viewName);
    dialogPane.setVisible(true);
  }

  public void closeDialog() {
    dialogPane.setVisible(false);
  }

}
