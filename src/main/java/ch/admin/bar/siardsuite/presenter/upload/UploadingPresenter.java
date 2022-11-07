package ch.admin.bar.siardsuite.presenter.upload;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.ui.Icon;
import ch.admin.bar.siardsuite.ui.Spinner;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import static ch.admin.bar.siardsuite.model.View.UPLOAD_ABORT_DIALOG;

public class UploadingPresenter  extends StepperPresenter {
    @FXML
    public Label title;
    @FXML
    public ImageView loader;
    @FXML
    public Label progress;
    @FXML
    public MFXButton cancel;

    private Spinner loadingSpinner;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.controller = controller;
        this.model = model;
        this.stage = stage;

        this.loader.setImage(Icon.loading);
        loadingSpinner = new Spinner(this.loader);

        I18n.bind(title.textProperty(), "upload.inProgress.title");
        I18n.bind(progress.textProperty(), "upload.records.uploaded.message");
        I18n.bind(cancel.textProperty(), "button.cancel");

        cancel.setOnAction(event -> stage.openDialog(UPLOAD_ABORT_DIALOG)); // TODO: how to cancel the upload task
    }

    @Override
    public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
        this.init(controller, model, stage);
        stepper.addEventHandler(SiardEvent.UPLOAD_CONNECTION_UPDATED, uploadDatabase(stepper));
    }

    private EventHandler<SiardEvent> uploadDatabase(MFXStepper stepper) {
        return event -> {
            System.out.println("now - upload the archive into the database");
            loadingSpinner.play();
        };
    }
}
