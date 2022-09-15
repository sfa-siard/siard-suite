package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.util.I18n;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Locale;

public class RootPresenter implements Presenter {

  private Controller controller;
  private Stage stage;

  private double xOffset;
  private double yOffset;
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

  public void init(Controller controller, Stage stage) {

    this.controller = controller;
    this.stage = stage;

    windowHeader.setOnMousePressed(event -> {
      xOffset = stage.getX() - event.getScreenX();
      yOffset = stage.getY() - event.getScreenY();
    });
    windowHeader.setOnMouseDragged(event -> {
      stage.setX(event.getScreenX() + xOffset);
      stage.setY(event.getScreenY() + yOffset);
    });

    this.stage.titleProperty().bind(I18n.createStringBinding("window.title"));

    this.archive.setOnAction(event -> navigate());
    this.upload.setOnAction(event -> System.out.println("uplaod button pressed"));
    this.export.setOnAction(event -> System.out.println("export button pressed"));
    this.open.setOnAction(event -> System.out.println("open button pressed"));
  }

  private void navigate() {
    Platform.runLater(() -> {

      boolean loaded;
      loaded = controller.navigateSomewhere();

      if (loaded) {
        this.stage.close();
      }
    });
  }
}
