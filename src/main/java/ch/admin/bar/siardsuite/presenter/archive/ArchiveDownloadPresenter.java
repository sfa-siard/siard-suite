package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.StepperButtonBox;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.model.database.DatabaseArchiveMetaData;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.ui.Icon;
import ch.admin.bar.siardsuite.ui.Spinner;
import ch.admin.bar.siardsuite.ui.SystemFileBrowser;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.visitor.DatabaseArchiveMetaDataVisitor;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

import java.io.File;
import java.io.IOException;

import static ch.admin.bar.siardsuite.component.StepperButtonBox.Type.DOWNLOAD_FINISHED;

public class ArchiveDownloadPresenter extends StepperPresenter implements DatabaseArchiveMetaDataVisitor {

    @FXML
    public Label title;
    @FXML
    public Label resultTitle;
    @FXML
    public ImageView resultIcon;
    @FXML
    public ImageView loader;
    @FXML
    public Label recordsLoaded;
    @FXML
    public Label archivePath;
    @FXML
    public Label pathTitle;
    @FXML
    public Label openLink;
    @FXML
    public VBox fileSystemBox;
    @FXML
    private StepperButtonBox buttonsBox;

    private Spinner loadingSpinner;
    private File targetArchive;

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

        this.bindTexts();
    }

    @Override
    public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
        this.init(controller, model, stage);
        createListeners(stepper);
    }

    private void createListeners(MFXStepper stepper) {
        stepper.addEventHandler(SiardEvent.ARCHIVE_METADATA_UPDATED, downloadAndArchiveDatabase(stepper));
    }

    private void bindTexts() {
        this.title.textProperty().bind(I18n.createStringBinding("archiveDownload.view.title.inProgress"));
        this.resultTitle.textProperty().bind(I18n.createStringBinding("archiveDownload.view.title.success"));
    }

    private EventHandler<SiardEvent> downloadAndArchiveDatabase(MFXStepper stepper) {
        return event -> {
            loadingSpinner.play();
            model.provideDatabaseArchiveMetaDataProperties(this);
            this.openLink.setOnMouseClicked(openArchiveDirectory(targetArchive));
            this.archivePath.setText(targetArchive.getAbsolutePath());
            controller.loadDatabase(targetArchive, false, handleDownloadSuccess(stepper), handleDownloadFailure());

        };
    }

    private EventHandler<WorkerStateEvent> handleDownloadFailure() {
        return e -> {
            this.title.setVisible(false);
            loadingSpinner.hide();
            this.resultTitle.setVisible(true);
            this.resultTitle.textProperty().bind(I18n.createStringBinding("archiveDownload.view.title.failed"));
            this.resultTitle.setTextFill(Paint.valueOf("#FF0000"));
            this.resultIcon.setImage(Icon.error);
            controller.closeDbConnection();
        };
    }

    private EventHandler<WorkerStateEvent> handleDownloadSuccess(MFXStepper stepper) {
        return e -> {
            this.title.setVisible(false);
            this.resultTitle.setVisible(true);
            this.fileSystemBox.setVisible(true);
            loadingSpinner.hide();
            controller.closeDbConnection();
            stepper.fireEvent(getUpdateEvent(SiardEvent.DATABASE_DOWNLOADED));
        };
    }

    private EventHandler<MouseEvent> openArchiveDirectory(File file) {
        return event -> {
            try {
                new SystemFileBrowser(file).show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public void visit(String siardFormatVersion, String databaseName, String databaseProduct, String databaseConnectionURL,
                      String databaseUsername, String databaseDescription, String databaseOwner, String databaseCreationDate,
                      String archivingDate, String archiverName, String archiverContact, File targetArchive) {
        this.targetArchive = targetArchive;
    }

    @Override
    public void visit(DatabaseArchiveMetaData metaData) {}

}
