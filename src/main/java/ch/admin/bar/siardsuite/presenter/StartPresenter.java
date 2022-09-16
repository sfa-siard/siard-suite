package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.View;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import static ch.admin.bar.siardsuite.util.I18n.createStringBinding;

public class StartPresenter extends Presenter {

  @FXML
  private Button archive;
  @FXML
  private Button upload;
  @FXML
  private Button export;
  @FXML
  private Button open;

  public void init(Controller controller, Model model, Stage stage) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;

    this.archive.setOnAction(event -> navigate(View.ARCHIVE.getName()));
    this.upload.setOnAction(event -> System.out.println("uplaod button pressed"));
    this.export.setOnAction(event -> System.out.println("export button pressed"));
    this.open.setOnAction(event -> System.out.println("open button pressed"));

    this.archive.textProperty().bind(createStringBinding("button.archive"));
    this.upload.textProperty().bind(createStringBinding("button.upload"));
    this.export.textProperty().bind(createStringBinding("button.export"));
    this.open.textProperty().bind(createStringBinding("button.open"));
  }
}
