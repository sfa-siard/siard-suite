package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.model.Model;

import ch.admin.bar.siardsuite.model.View;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

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
  @FXML
  private Text openHint;
  @FXML
  private Text archiveHint;
  @FXML
  private Text uploadHint;

  public void init(Controller controller, Model model, Stage stage) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;

    this.archive.setOnAction(event -> {
      try {
        showDialog("archive-db-dialog.fxml");
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
    this.upload.setOnAction(event -> System.out.println("uplaod button pressed"));
    this.export.setOnAction(event -> System.out.println("export button pressed"));
    this.open.setOnAction(event -> System.out.println("open button pressed"));

    this.archive.textProperty().bind(createStringBinding("button.archive"));
    this.upload.textProperty().bind(createStringBinding("button.upload"));
    this.export.textProperty().bind(createStringBinding("button.export"));
    this.open.textProperty().bind(createStringBinding("button.open"));

    this.openHint.textProperty().bind(createStringBinding("text.open.hint"));
    this.archiveHint.textProperty().bind(createStringBinding("text.archive.hint"));
    this.uploadHint.textProperty().bind(createStringBinding("text.upload.hint"));
  }

  private void showDialog(String s) throws IOException {
    FXMLLoader loader = new FXMLLoader(SiardApplication.class.getResource(View.ARCHIVE_DB_DIALOG.getName()));
    Parent container = loader.load();
    Stage dialog = new Stage();
    dialog.initStyle(StageStyle.UNDECORATED);
    loader.<ArchiveDbDialogPresenter>getController().init(this.controller, this.model, this.stage, dialog);
    Scene scene = new Scene(container);
    dialog.setScene(scene);
    dialog.show();
  }
}
