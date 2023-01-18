package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.DEFAULT;
import static ch.admin.bar.siardsuite.util.SiardEvent.UPDATE_STEPPER_DBTYPE_EVENT;

public class ArchiveChooseDbmsPresenter extends StepperPresenter {

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
    public BorderPane borderPane;
    @FXML
    private ButtonBox buttonsBox;

    private final ToggleGroup toggleGroup = new ToggleGroup();

    private boolean next = true;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;
    }

    @Override
    public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
        this.init(controller, model, stage);

        I18n.bind(title.textProperty(), "archiveDb.view.title");
        I18n.bind(text.textProperty(), "archiveDb.view.text");
        this.errorMessage.setVisible(false);
        I18n.bind(errorMessage.textProperty(), "archiveDb.view.error");


        this.model.getDatabaseTypes().forEach(this::createRadioToVBox);

        this.buttonsBox = new ButtonBox().make(DEFAULT);
        this.borderPane.setBottom(buttonsBox);
        this.setListeners(stepper);
    }

    private void createRadioToVBox(String s) {
        VBox vBox = next ? leftVBox : rightVBox;
        next = !next;
        MFXRadioButton radioButton = new MFXRadioButton(s);
        radioButton.setToggleGroup(toggleGroup);
        vBox.getChildren().add(radioButton);
        VBox.setMargin(radioButton, new Insets(0, 0, 25, 0));
    }

    private void setListeners(MFXStepper stepper) {
        this.buttonsBox.next().setOnAction((event) -> {
            MFXRadioButton selected = (MFXRadioButton) toggleGroup.getSelectedToggle();
            if (selected != null) {
                controller.setDatabaseType(selected.getText());
                this.errorMessage.setVisible(false);
                stepper.next();
                stepper.fireEvent(new SiardEvent(UPDATE_STEPPER_DBTYPE_EVENT));
                //fire event
            } else {
                this.errorMessage.setVisible(true);
            }
        });
        this.buttonsBox.previous().setOnAction((event) -> stage.openDialog(View.ARCHIVE_DB_DIALOG));
        this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG));
    }

}
