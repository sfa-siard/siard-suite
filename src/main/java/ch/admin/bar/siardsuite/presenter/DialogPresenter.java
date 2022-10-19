package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

public class DialogPresenter extends Presenter {

  @FXML
  protected HBox windowHeader;

  @Override
  public void init(Controller controller, Model model, RootStage stage) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;

    allowStageRepositioning(windowHeader);
  }

  protected MFXButton getCancelButton() {
    final MFXButton cancelButton = new MFXButton();
    cancelButton.textProperty().bind(I18n.createStringBinding("button.cancel"));
    cancelButton.getStyleClass().setAll("button", "secondary");
    cancelButton.setManaged(true);
    cancelButton.setOnAction(event -> stage.closeDialog());
    return cancelButton;
  }

}
