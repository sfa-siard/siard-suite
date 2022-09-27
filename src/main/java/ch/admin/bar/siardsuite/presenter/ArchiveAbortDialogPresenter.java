package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ArchiveAbortDialogPresenter extends DialogPresenter {

  @FXML
  public Text text;
  @FXML
  public MFXButton closeButton; // seems redundant
  @FXML
  public HBox buttonBox;

  @Override
  public void init(Controller controller, Model model, RootStage stage) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;

    setTitle("archiveAbortDialog.title");
    this.text.textProperty().bind(I18n.createStringBinding("archiveAbortDialog.text"));

    final MFXButton cancelArchiveButton = new MFXButton();
    cancelArchiveButton.textProperty().bind(I18n.createStringBinding("button.cancel.archive"));
    cancelArchiveButton.getStyleClass().setAll("button", "primary");
    cancelArchiveButton.setManaged(true);

    cancelArchiveButton.setOnAction(event -> {
      stage.closeDialog();
      stage.navigate(View.START.getName());
    });

    closeButton.setOnAction(event -> stage.closeDialog());

    final MFXButton proceedArchiveButton = getCancelButton();
    proceedArchiveButton.textProperty().bind(I18n.createStringBinding("button.proceed.archive"));

    buttonBox.getChildren().addAll(proceedArchiveButton, cancelArchiveButton);
  }
}
