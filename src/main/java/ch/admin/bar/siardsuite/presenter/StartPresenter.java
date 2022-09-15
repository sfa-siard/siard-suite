package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.util.I18n;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class StartPresenter implements Presenter {

  private Model model;
  private Controller controller;
  private Stage stage;
  @FXML
  public HBox windowHeader;
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

    this.archive.setOnAction(event -> navigate("archive-view.fxml"));
    this.upload.setOnAction(event -> System.out.println("uplaod button pressed"));
    this.export.setOnAction(event -> System.out.println("export button pressed"));
    this.open.setOnAction(event -> System.out.println("open button pressed"));
  }

  private void navigate(String view) {
    Platform.runLater(() -> {

      boolean loaded;
      loaded = controller.navigate(view);

      if (loaded) {
        this.stage.close();
      }
    });
  }
}
