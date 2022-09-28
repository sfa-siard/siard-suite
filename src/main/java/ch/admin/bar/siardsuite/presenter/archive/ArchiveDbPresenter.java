package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;

public class ArchiveDbPresenter extends StepperPresenter {

  @FXML
  public Text title;
  @FXML
  public Text text;
  @FXML
  public VBox leftVBox;
  @FXML
  public VBox rightVBox;
  @FXML
  public Label errorMessage;
  @FXML
  private MFXButton nextButton;
  @FXML
  private MFXButton previousButton;
  @FXML
  private MFXButton cancelButton;
  @FXML
  private HBox buttonsBox;

  private final ToggleGroup toggleGroup = new ToggleGroup();

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

    this.title.textProperty().bind(I18n.createStringBinding("archiveDb.view.title"));
    this.text.textProperty().bind(I18n.createStringBinding("archiveDb.view.text"));
    this.errorMessage.setVisible(false);
    this.errorMessage.textProperty().bind(I18n.createStringBinding("archiveDb.view.error"));

    List.of("MS Access", "DB/2", "H2 Database", "MySQL").forEach(s -> createRadioToVBox(s, leftVBox));
    List.of("Oracle", "PostgreSQL", "Microsoft SQL Server").forEach(s -> createRadioToVBox(s, rightVBox));


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

  private void createRadioToVBox(String s, VBox vBox) {
    MFXRadioButton radioButton = new MFXRadioButton(s);
    radioButton.setToggleGroup(toggleGroup);
    vBox.getChildren().add(radioButton);
    VBox.setMargin(radioButton, new Insets(0, 0, 25, 0));
  }

  private void setListeners(MFXStepper stepper) {
    this.nextButton.setOnAction((event) -> {
      MFXRadioButton selected = (MFXRadioButton) toggleGroup.getSelectedToggle();
      if (selected != null) {
        controller.setDatabaseType(selected.getText());
        this.errorMessage.setVisible(false);
        stepper.next();
      } else {
        this.errorMessage.setVisible(true);
      }
    });
    this.previousButton.setOnAction((event) -> stage.openDialog(View.ARCHIVE_DB_DIALOG.getName()));
    this.cancelButton.setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG.getName()));
  }

}
