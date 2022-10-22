package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.StepperButtonBox;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.ui.Icon;
import ch.admin.bar.siardsuite.ui.Spinner;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.OS;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

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

        this.openLink.setOnMouseClicked(event -> {
            try {
                openFileBrowser();
            } catch (IOException e) {
                System.out.println("unable to open native file browser");
            }
        });

        stepper.addEventHandler(SiardEvent.ARCHIVE_METADATA_UPDATED, event -> {
            loadingSpinner.play();
            controller.loadDatabase(this.model.getArchive().getArchiveMetaData().getTargetArchive(),
                                    false); // TODO: way to many chainings


            controller.onDatabaseLoadSuccess(e -> {
                this.title.textProperty().bind(I18n.createStringBinding("archiveDownload.view.title.success"));
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

    // TODO: fix the deprecation warning and move to its own class
    private void openFileBrowser() throws IOException {
        if (OS.UNSUPPORTED) throw new UnsupportedOperationException("Open file browser is not supported on your OS");

        // TODO: the archive is not set in the metadata...
        //String filePath = this.model.getArchive().getArchiveMetaData().getTargetArchive().getAbsolutePath();
        String filePath = "/home/mburri/projects/siard-suite/siard-suite/";
        if (OS.IS_WINDOWS) Runtime.getRuntime().exec("explorer /select, "+ filePath);
        if (OS.IS_UNIX) Runtime.getRuntime().exec("xdg-open "+ filePath);
        if (OS.IS_MAC) Runtime.getRuntime().exec("open -R " + filePath);
    }


    private void bindTexts() {
        this.title.textProperty().bind(I18n.createStringBinding("archiveDownload.view.title.inProgress"));
    }

}
