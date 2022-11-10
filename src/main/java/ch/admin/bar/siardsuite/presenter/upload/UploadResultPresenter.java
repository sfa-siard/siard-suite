package ch.admin.bar.siardsuite.presenter.upload;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.StepperButtonBox;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import static ch.admin.bar.siardsuite.component.StepperButtonBox.Type.FAILED;
import static ch.admin.bar.siardsuite.model.View.START;

public class UploadResultPresenter extends StepperPresenter {
    @FXML
    public Label title;
    @FXML
    public ImageView resultIcon;
    @FXML
    public Label summary;
    @FXML
    public MFXButton toStartScreen;
    @FXML
    public BorderPane borderPane;
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
                // for now: assume that everything was ok...
                I18n.bind(title.textProperty(), "upload.result.success.title");
                I18n.bind(summary.textProperty(), "upload.result.success.message");
                I18n.bind(toStartScreen.textProperty(), "button.home");
                title.getStyleClass().add("ok-check-icon");
                title.setStyle("-fx-text-fill: #1BCB95");
                this.toStartScreen.setOnAction(e -> this.stage.navigate(START));
            };
    }

    private EventHandler<SiardEvent> showFailed(MFXStepper stepper) {
        return event -> {
            // stepper Progress failed
            // stepper update
            I18n.bind(title.textProperty(), "upload.result.failed.title");
            I18n.bind(summary.textProperty(), "upload.result.failed.message");
            title.getStyleClass().add("x-circle-icon");
            title.setStyle("-fx-text-fill: red");
            this.buttonsBox = new StepperButtonBox().make(FAILED);
            this.borderPane.setBottom(buttonsBox);
            this.setListeners(stepper);
        };
    }

    private void setListeners(MFXStepper stepper) {
        this.buttonsBox.next().setOnAction((event) -> stage.navigate(START));
        this.buttonsBox.cancel().setOnAction((event) -> stepper.previous());
    }
}
