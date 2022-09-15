package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.util.I18n;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Locale;

public class RootPresenter implements Presenter {

  public AnchorPane rootPane;
  private Controller controller;

  private Stage stage;

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

    this.stage.titleProperty().bind(I18n.createStringBinding("window.title"));

    this.archive.setOnAction(event -> System.out.println("archive button pressed"));
    this.upload.setOnAction(event -> System.out.println("uplaod button pressed"));
    this.export.setOnAction(event -> System.out.println("export button pressed"));
    this.open.setOnAction(event -> System.out.println("open button pressed"));
  }
}
