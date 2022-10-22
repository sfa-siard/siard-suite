package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.StepperButtonBox;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.ui.Icon;
import ch.admin.bar.siardsuite.ui.Spinner;
import ch.admin.bar.siardsuite.ui.SystemFileBrowser;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

import static ch.admin.bar.siardsuite.component.StepperButtonBox.Type.DOWNLOAD_FINISHED;

public class ArchiveDownloadPresenter extends StepperPresenter {

    @FXML
    public Label title;

    @FXML
    public ImageView loader;
    @FXML
    public Label recordsLoaded;
    @FXML
    public Label filePath;
    @FXML
    public Label pathTitle;
    @FXML
    public Label openLink;

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

        this.buttonsBox.next().setOnAction(event -> this.stage.navigate(View.OPEN_SIARD_ARCHIVE_PREVIEW));
        this.buttonsBox.cancel().setOnAction(event -> this.stage.navigate(View.START));
        this.openLink.setOnMouseClicked(openArchiveDirectory());

        this.bindTexts();
    }

    @Override
    public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
        this.init(controller, model, stage);
        createListeners(stepper);
    }

    private void createListeners(MFXStepper stepper) {
        stepper.addEventHandler(SiardEvent.ARCHIVE_METADATA_UPDATED, downloadAndArchiveDatabse(stepper));
    }

    private void bindTexts() {
        this.title.textProperty().bind(I18n.createStringBinding("archiveDownload.view.title.inProgress"));
    }

    private EventHandler<SiardEvent> downloadAndArchiveDatabse(MFXStepper stepper) {
        return event -> {
            loadingSpinner.play();
            controller.loadDatabase(this.model.getArchive().getArchiveMetaData().getTargetArchive(),
                                    false); // TODO: way to many chainings
            controller.onDatabaseLoadSuccess(handleDownloadSuccess(stepper));
            controller.onDatabaseLoadFailed(handleDownloadFailure(stepper));
        };
    }


    private EventHandler<WorkerStateEvent> handleDownloadFailure(MFXStepper stepper) {
        return e -> {
            // TODO: show error page
            System.out.println("download of database failed...");
            controller.closeDbConnection();
            stepper.previous();
        };
    }

    private EventHandler<WorkerStateEvent> handleDownloadSuccess(MFXStepper stepper) {
        return e -> {
            this.title.textProperty().bind(I18n.createStringBinding("archiveDownload.view.title.success"));
            loadingSpinner.stop();
            this.loader.setImage(Icon.ok);
            controller.closeDbConnection();
            stepper.fireEvent(getUpdateEvent(SiardEvent.DATABASE_DOWNLOADED));
        };
    }

    private EventHandler<MouseEvent> openArchiveDirectory() {
        return event -> {
            try {
                new SystemFileBrowser(model.getArchive().getArchiveMetaData().getTargetArchive()).show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
