package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.*;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.model.database.SiardArchiveMetaData;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.enums.StepperToggleState;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.*;
import static ch.admin.bar.siardsuite.model.View.START;
import static ch.admin.bar.siardsuite.util.SiardEvent.DATABASE_DOWNLOADED;

public class ArchiveDownloadPresenter extends StepperPresenter implements SiardArchiveMetaDataVisitor {

    @FXML
    public Label title;
    @FXML
    public Label resultTitle;
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
    public Label subtitle1;
    @FXML
    public ScrollPane resultBox;
    @FXML
    public VBox scrollBox;
    @FXML
    private ButtonBox buttonsBox;

    private Spinner loadingSpinner;
    private File targetArchive;
    private String databaseName;
    private long total;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.controller = controller;
        this.model = model;
        this.stage = stage;
    }

    @Override
    public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
        this.init(controller, model, stage);
        this.loader.setImage(Icon.loading);
        loadingSpinner = new Spinner(this.loader);
        this.bindTexts();
        this.buttonsBox = new ButtonBox().make(CANCEL);
        addButtons(stepper);
        setListeners(stepper);
    }

    private void setListeners(MFXStepper stepper) {
        stepper.addEventHandler(SiardEvent.ARCHIVE_METADATA_UPDATED, downloadAndArchiveDatabase(stepper));

        if (this.buttonsBox.cancel() != null) {
            if (this.resultTitle.isVisible()) {
                this.buttonsBox.cancel().setOnAction((event) -> {
                    try {
                        final Archive archive = ArchiveImpl.newInstance();
                        archive.open(targetArchive);
                        model.setArchive(targetArchive.getName(), archive);
                        stage.navigate(View.OPEN_SIARD_ARCHIVE_PREVIEW);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG));
            }
        }
        if (this.buttonsBox.next() != null) {
            this.buttonsBox.next().setOnAction((event) -> stage.navigate(START));
        }
    }

    private void bindTexts() {
        I18n.bind(title.textProperty(), "archiveDownload.view.title.inProgress");
        I18n.bind(recordsLoaded.textProperty(), "archiveDownload.view.message.inProgress", 6666);
        I18n.bind(pathTitle.textProperty(), "archiveDownload.view.pathTitle");
        I18n.bind(openLink.textProperty(), "archiveDownload.view.openLink");
    }

    private EventHandler<SiardEvent> downloadAndArchiveDatabase(MFXStepper stepper) {
        return event -> {
            loadingSpinner.play();
            model.provideDatabaseArchiveMetaDataProperties(this);
            this.openLink.setOnMouseClicked(openArchiveDirectory(targetArchive));
            this.archivePath.setText(targetArchive.getAbsolutePath());
            this.subtitle1.setText(this.databaseName);
            try {
                controller.loadDatabase(targetArchive, false, handleDownloadSuccess(stepper), handleDownloadFailure(stepper));
                controller.addDatabaseLoadingValuePropertyListener((o1, oldValue1, newValue1) -> {
                    AtomicInteger pos1 = new AtomicInteger();
                    newValue1.forEach(p -> addTableData(p.getKey(), p.getValue(), pos1.getAndIncrement()));
                });
                // TODO CR 459 Progress is no processable with current api data
//                controller.addDatabaseLoadingProgressPropertyListener((o, oldValue, newValue) -> {
//                    double pos = (newValue.doubleValue() * 100) * scrollBox.getChildren().size();
//                    LabelIcon label = (LabelIcon) scrollBox.getChildren().get((int) pos);
//                    label.setGraphic(new IconView(newValue.intValue(), IconView.IconType.OK));
//                });
            } catch (SQLException e) {
                // TODO should notify user about any error - Toast it # CR 458
                throw new RuntimeException(e);
            }
        };
    }


    private EventHandler<WorkerStateEvent> handleDownloadSuccess(MFXStepper stepper) {
        return e -> {
            stepper.getStepperToggles().get(stepper.getCurrentIndex()).setState(StepperToggleState.COMPLETED);
            stepper.updateProgress();
            loadingSpinner.hide();
            this.title.setVisible(false);
            this.resultTitle.setVisible(true);
            this.fileSystemBox.setVisible(true);
            I18n.bind(resultTitle.textProperty(), "archiveDownload.view.title.success");
            resultTitle.getStyleClass().setAll("ok-circle-icon", "h2", "label-icon-left");
            setResultData();
            controller.closeDbConnection();
            stepper.fireEvent(new SiardEvent(DATABASE_DOWNLOADED));
            this.buttonsBox = new ButtonBox().make(DOWNLOAD_FINISHED);
            addButtons(stepper);
        };
    }

    private EventHandler<WorkerStateEvent> handleDownloadFailure(MFXStepper stepper) {
        return e -> {
            stepper.getStepperToggles().get(stepper.getCurrentIndex()).setState(StepperToggleState.ERROR);
            stepper.updateProgress();
            this.title.setVisible(false);
            loadingSpinner.hide();
            this.resultTitle.setVisible(true);
            this.subtitle1.setVisible(false);
            this.resultBox.setVisible(false);
            I18n.bind(resultTitle.textProperty(), "archiveDownload.view.title.failed");
            I18n.bind(recordsLoaded.textProperty(), "archiveDownload.view.message.failed");
            resultTitle.getStyleClass().setAll("x-circle-icon", "h2", "label-icon-left");
            controller.closeDbConnection();
            this.buttonsBox = new ButtonBox().make(TO_START);
            addButtons(stepper);
        };
    }

    private void addButtons(MFXStepper stepper) {
        this.borderPane.setBottom(buttonsBox);
        setListeners(stepper);
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

    private void setResultData() {
        I18n.bind(recordsLoaded.textProperty(), "archiveDownload.view.message.success", total);
    }

    private void addTableData(String tableName, Long rows, Integer pos) {
        LabelIcon label = new LabelIcon(tableName, pos, IconView.IconType.LOADING);
        I18n.bind(label.textProperty(), "upload.result.success.table.rows", tableName, rows);
        scrollBox.getChildren().add(label);
        total += rows;
    }

    @Override
    public void visit(String siardFormatVersion, String databaseName, String databaseProduct, String databaseConnectionURL,
                      String databaseUsername, String databaseDescription, String databaseOwner, String databaseCreationDate,
                      LocalDate archivingDate, String archiverName, String archiverContact, File targetArchive) {
        this.targetArchive = targetArchive;
        this.databaseName = databaseName;
    }

    @Override
    public void visit(SiardArchiveMetaData metaData) {
    }
}
