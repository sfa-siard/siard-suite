package ch.admin.bar.siardsuite.presenter.upload;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

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

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.controller = controller;
        this.model = model;
        this.stage = stage;
    }

    @Override
    public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
        this.init(controller, model, stage);

        // for now: assume that everything was ok...
        I18n.bind(title.textProperty(), "upload.result.success.title");
        I18n.bind(summary.textProperty(), "upload.result.success.message");
        I18n.bind(toStartScreen.textProperty(), "button.home");
        this.toStartScreen.setOnAction(event -> this.stage.navigate(START));
    }
}
