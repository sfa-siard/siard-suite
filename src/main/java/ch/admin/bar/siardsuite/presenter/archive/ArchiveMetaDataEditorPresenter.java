package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.component.SiardTooltip;
import ch.admin.bar.siardsuite.model.Failure;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.model.database.SiardArchiveMetaData;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.presenter.ValidationProperties;
import ch.admin.bar.siardsuite.presenter.ValidationProperty;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.DEFAULT;
import static ch.admin.bar.siardsuite.util.SiardEvent.ARCHIVE_METADATA_UPDATED;
import static ch.admin.bar.siardsuite.util.SiardEvent.ERROR_OCCURED;

public class ArchiveMetaDataEditorPresenter extends StepperPresenter implements SiardArchiveMetaDataVisitor {


    @FXML
    Text titleText = new Text();
    @FXML
    Text descriptionText = new Text();
    @FXML
    Text titleWhat = new Text();
    @FXML
    Text titleWho = new Text();
    @FXML
    TextField name;
    @FXML
    public Label descriptionLabel;
    @FXML
    TextArea description;
    @FXML
    TextField owner;
    @FXML
    TextField dataOriginTimespan;
    @FXML
    TextField archiverName;
    @FXML
    public Label archiverContactLabel;
    @FXML
    TextArea archiverContact;
    @FXML
    protected ButtonBox buttonsBox;
    @FXML
    public MFXButton infoButton;
    @FXML
    private Tooltip tooltip;
    @FXML
    public Label nameLabel;
    @FXML
    public Label ownerLabel;
    @FXML
    public Label dataOriginTimespanLabel;
    @FXML
    public Label archiverLabel;
    @FXML
    public Label ownerValidationMsg;
    @FXML
    public Label dataOriginTimespanValidationMsg;
    @FXML
    public Label lobExportLabel;
    @FXML
    public TextField lobExportLocation;
    @FXML
    public MFXButton lobFolderButton;

    @Override
    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;

        this.buttonsBox = new ButtonBox().make(DEFAULT);


        this.borderPane.setBottom(buttonsBox);
        this.tooltip = new SiardTooltip("archiveMetadata.view.tooltip");

        this.bindTexts();
    }

    @Override
    public void init(Controller controller, RootStage stage, MFXStepper stepper) {
        this.init(controller, stage);

        this.buttonsBox.previous().setOnAction((event) -> stepper.previous());
        this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG));
        MFXButton saveArchiveButton = new MFXButton();
        I18n.bind(saveArchiveButton, "button.save.archive");
        saveArchiveButton.setOnAction(event -> {
            this.saveOnlyMetaData(stepper);
        });

        this.buttonsBox.append(saveArchiveButton);
        this.setListeners(stepper);
    }

    private void bindTexts() {
        I18n.bind(this.titleText.textProperty(), "archiveMetadata.view.title");
        I18n.bind(this.descriptionText.textProperty(), "archiveMetadata.view.description");
        I18n.bind(this.titleWhat.textProperty(), "archiveMetadata.view.titleWhat");
        I18n.bind(this.titleWho.textProperty(), "archiveMetadata.view.titleWho");

        I18n.bind(this.nameLabel.textProperty(), "archiveMetadata.view.databaseName");

        I18n.bind(this.descriptionLabel.textProperty(), "archiveMetadata.view.databaseDescription");
        I18n.bind(this.ownerLabel.textProperty(), "archiveMetadata.view.deliveringOffice");
        I18n.bind(this.dataOriginTimespanLabel.textProperty(), "archiveMetadata.view.databaseCreationDate");
        I18n.bind(this.archiverLabel.textProperty(), "archiveMetadata.view.archiverName");
        I18n.bind(this.archiverContactLabel.textProperty(), "archiveMetadata.view.archiverContact");
        I18n.bind(this.lobExportLabel.textProperty(), "archiveMetadata.view.exportLocationLob");
    }

    private File showFileChooserToSelectTargetArchive(String databaseName) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(I18n.get("open.siard.archive.dialog.choose.file.title"));
        fileChooser.setInitialFileName(databaseName + ".siard");
        final FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("SIARD files", "*.siard");
        fileChooser.getExtensionFilters().add(extensionFilter);
        return fileChooser.showSaveDialog(stage);
    }

    private void setListeners(MFXStepper stepper) {
        this.buttonsBox.next().setOnAction((event) -> {
            if (this.validateProperties()) {
                File targetArchive = this.showFileChooserToSelectTargetArchive(this.name.getText());
                File lobFolder = new File(lobExportLocation.getText());

                if (targetArchive != null) {
                    this.controller.updateArchiveMetaData(
                            this.name.getText(),
                            this.description.getText(),
                            this.owner.getText(),
                            this.dataOriginTimespan.getText(),
                            this.archiverName.getText(),
                            this.archiverContact.getText(),
                            lobFolder.toURI() != null ? lobFolder.toURI() : null,
                            targetArchive);
                    stepper.next();
                    stepper.fireEvent(new SiardEvent(ARCHIVE_METADATA_UPDATED));
                }
            }

        });

        infoButton.setOnMouseMoved(event -> {
            Bounds boundsInScreen = infoButton.localToScreen(infoButton.getBoundsInLocal());
            tooltip.show(infoButton,
                         (boundsInScreen.getMaxX() - boundsInScreen.getWidth() / 2) - tooltip.getWidth() / 2,
                         boundsInScreen.getMaxY() - boundsInScreen.getHeight() - tooltip.getHeight());
        });

        infoButton.setOnMouseExited(event -> tooltip.hide());

        lobFolderButton.setOnAction(event -> {
            final DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle(I18n.get("export.choose-location.text"));
            File file = directoryChooser.showDialog(stage);
            this.lobExportLocation.setText(file.getAbsolutePath());
        });

        stepper.addEventHandler(SiardEvent.ARCHIVE_LOADED, event -> initFields());
    }

    private void saveOnlyMetaData(MFXStepper stepper) {
        if (this.validateProperties()) {
            File targetArchive = this.showFileChooserToSelectTargetArchive(this.name.getText());
            File lobFolder = new File(lobExportLocation.getText());

            if (targetArchive != null) {
                this.controller.updateArchiveMetaData(
                        this.name.getText(),
                        this.description.getText(),
                        this.owner.getText(),
                        this.dataOriginTimespan.getText(),
                        this.archiverName.getText(),
                        this.archiverContact.getText(),
                        lobFolder.toURI() != null ? lobFolder.toURI() : null,
                        targetArchive);
                try {
                    this.controller.saveArchiveOnlyMetaData(targetArchive);
                } catch (IOException e) {
                    fail(stepper, e, ERROR_OCCURED);
                }
                //stepper.fireEvent(new SiardEvent(ARCHIVE_METADATA_UPDATED));
            }
        }
    }

    private void fail(MFXStepper stepper, Throwable e, EventType<SiardEvent> event) {
        e.printStackTrace();
        this.stage.openDialog(View.ERROR_DIALOG);
        controller.cancelDownload();
        controller.failure(new Failure(e));
        stepper.fireEvent(new SiardEvent(event));
    }


    private void initFields() {
        controller.provideDatabaseArchiveMetaDataProperties(this);
    }

    private String removePlaceholder(String value) {
        return (value.equals("(...)") ? "" : value);
    }

    @Override
    public void visit(String siardFormatVersion, String databaseName, String databaseProduct,
                      String databaseConnectionURL,
                      String databaseUsername, String databaseDescription, String databseOwner,
                      String databaseCreationDate,
                      LocalDate archivingDate, String archiverName, String archiverContact, File targetArchive,
                      URI lobFolder) {
        name.setText(databaseName);
        description.setText(databaseDescription);
        owner.setText(removePlaceholder(databseOwner));
        this.dataOriginTimespan.setText(removePlaceholder(databaseCreationDate));
        this.archiverName.setText(archiverName);
        this.archiverContact.setText(archiverContact);
    }

    @Override
    public void visit(SiardArchiveMetaData metaData) {
    }

    private boolean validateProperties() {
        List<ValidationProperty> validationProperties = Arrays.asList(new ValidationProperty(owner,
                                                                                             ownerValidationMsg,
                                                                                             "archiveMetaData.owner.missing"),
                                                                      new ValidationProperty(dataOriginTimespan,
                                                                                             dataOriginTimespanValidationMsg,
                                                                                             "archiveMetaData.timespan.missing"));

        return new ValidationProperties(validationProperties).validate();
    }
}
