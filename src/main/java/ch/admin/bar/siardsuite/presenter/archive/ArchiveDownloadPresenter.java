package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.StepperButtonBox;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.ui.Icon;
import ch.admin.bar.siardsuite.ui.Spinner;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import static ch.admin.bar.siardsuite.component.StepperButtonBox.Type.DOWNLOAD_FINISHED;

public class ArchiveDownloadPresenter extends StepperPresenter {

    @FXML
    public Label title;

    @FXML
    public ImageView loader;

    @FXML
    private StepperButtonBox buttonsBox;

    private Spinner loadingSpinner;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.controller = controller;
        this.model = model;
        this.stage = stage;

        this.loader.setImage(Icon.loading);
        loadingSpinner = new Spinner(this.loader);
        this.buttonsBox = new StepperButtonBox().make(DOWNLOAD_FINISHED);
        this.borderPane.setBottom(buttonsBox);

        this.bindTexts();
    }

    @Override
    public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
        this.init(controller, model, stage);
        createListeners(stepper);
    }

    private void createListeners(MFXStepper stepper) {
        this.buttonsBox.next().setOnAction(event -> this.stage.navigate(View.OPEN_SIARD_ARCHIVE_PREVIEW));
        this.buttonsBox.cancel().setOnAction(event -> this.stage.navigate(View.START));

        stepper.addEventHandler(SiardEvent.ARCHIVE_METADATA_UPDATED, event -> {
            loadingSpinner.play();
            controller.loadDatabase(this.model.getArchive().getArchiveMetaData().getTargetArchive(),
                                    false); // TODO: way to many chainings


            controller.onDatabaseLoadSuccess(e -> {
                loadingSpinner.stop();
                this.loader.setImage(Icon.ok);
                controller.closeDbConnection();
                stepper.fireEvent(getUpdateEvent(SiardEvent.DATABASE_DOWNLOADED));
            });


            controller.onDatabaseLoadFailed(e -> {
                System.out.println("download of database failed...");
                controller.closeDbConnection();
                stepper.previous();
                this.stage.setHeight(1080.00);
            });
        });
    }

    private void bindTexts() {
        this.title.textProperty().bind(I18n.createStringBinding("archiveDownload.view.title"));
    }

}
