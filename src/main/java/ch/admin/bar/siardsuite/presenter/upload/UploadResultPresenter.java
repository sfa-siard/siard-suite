package ch.admin.bar.siardsuite.presenter.upload;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.StepperButtonBox;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.enums.StepperToggleState;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

import static ch.admin.bar.siardsuite.component.StepperButtonBox.Type.FAILED;
import static ch.admin.bar.siardsuite.component.StepperButtonBox.Type.TO_START;
import static ch.admin.bar.siardsuite.model.View.START;

public class UploadResultPresenter extends StepperPresenter {
    @FXML
    public Label title;
    @FXML
    public Label summary;
    @FXML
    public BorderPane borderPane;
    @FXML
    public Label subtitle1;
    @FXML
    public ScrollPane resultBox;
    @FXML
    private StepperButtonBox buttonsBox;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.controller = controller;
        this.model = model;
        this.stage = stage;
    }

    @Override
    public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
        this.init(controller, model, stage);

        stepper.addEventHandler(SiardEvent.UPLOAD_SUCCEDED, showResult(stepper));
        stepper.addEventHandler(SiardEvent.UPLOAD_FAILED, showFailed(stepper));
    }

    private EventHandler<SiardEvent> showResult(MFXStepper stepper) {
            return event -> {
                stepper.getStepperToggles().get(stepper.getCurrentIndex()).setState(StepperToggleState.COMPLETED);
                stepper.updateProgress();
                this.subtitle1.setVisible(true);
                this.resultBox.setVisible(true);
                I18n.bind(title.textProperty(), "upload.result.success.title");
                I18n.bind(summary.textProperty(), "upload.result.success.message");
                title.getStyleClass().setAll("ok-circle-icon", "h2", "label-icon-left");
                this.buttonsBox = new StepperButtonBox().make(TO_START);
                addButtons(stepper);
            };
    }

    private EventHandler<SiardEvent> showFailed(MFXStepper stepper) {
        return event -> {
            stepper.getStepperToggles().get(stepper.getCurrentIndex()).setState(StepperToggleState.ERROR);
            stepper.updateProgress();
            I18n.bind(title.textProperty(), "upload.result.failed.title");
            I18n.bind(summary.textProperty(), "upload.result.failed.message");
            title.getStyleClass().setAll("x-circle-icon","h2", "label-icon-left");
            this.buttonsBox = new StepperButtonBox().make(FAILED);
            addButtons(stepper);
        };
    }

    private void addButtons(MFXStepper stepper) {
        this.borderPane.setBottom(buttonsBox);
        setListeners(stepper);
    }

    private void setListeners(MFXStepper stepper) {
        this.buttonsBox.next().setOnAction((event) -> stage.navigate(START));
        if (this.buttonsBox.cancel() != null) {
            this.buttonsBox.cancel().setOnAction((event) -> {
                stepper.previous();
                stepper.previous();
            });
        }
    }
}
