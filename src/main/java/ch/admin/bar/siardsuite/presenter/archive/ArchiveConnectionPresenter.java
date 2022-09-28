package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ArchiveConnectionPresenter extends StepperPresenter {

  @FXML
  public Label errorMessage;
  public Text title;
  public Text text;
  @FXML
  private MFXButton nextButton;
  @FXML
  private MFXButton previousButton;
  @FXML
  private MFXButton cancelButton;
  @FXML
  private HBox buttonsBox;

  @Override
  public void init(Controller controller, Model model, RootStage stage) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;
  }

  @Override
  public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;

    title.textProperty().bind(I18n.createStringBinding("archiveConnection.view.title"));
    text.textProperty().bind(I18n.createStringBinding("archiveConnection.view.text"));

    this.nextButton = new MFXButton();
    this.nextButton.textProperty().bind(I18n.createStringBinding("button.next"));
    this.nextButton.getStyleClass().setAll("button", "primary");
    this.nextButton.setManaged(true);
    this.previousButton = new MFXButton();
    this.previousButton.textProperty().bind(I18n.createStringBinding("button.back"));
    this.previousButton.getStyleClass().setAll("button", "secondary");
    this.previousButton.setManaged(true);
    this.cancelButton = new MFXButton();
    this.cancelButton.textProperty().bind(I18n.createStringBinding("button.cancel"));
    this.cancelButton.getStyleClass().setAll("button", "secondary");
    this.cancelButton.setManaged(true);

    this.buttonsBox.getChildren().addAll(this.previousButton, this.cancelButton, this.nextButton);
    this.setListeners(stepper);
  }

  private void setListeners(MFXStepper stepper) {
    this.nextButton.setOnAction((event) -> {
//        controller.setDatabaseType(selected.getText());
//        this.errorMessage.setVisible(false);
//        stepper.next();
    });
    this.previousButton.setOnAction((event) -> {
      stepper.previous();
    });
    this.cancelButton.setOnAction((event) -> {
        // AbortDialog
    });
  }
}
