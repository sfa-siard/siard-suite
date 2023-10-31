package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.component.SiardToolip;
import ch.admin.bar.siardsuite.component.SiardTooltip;
import ch.admin.bar.siardsuite.model.Failure;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.presenter.ValidationProperties;
import ch.admin.bar.siardsuite.presenter.ValidationProperty;
import ch.admin.bar.siardsuite.presenter.tree.SiardArchiveMetaDataDetailsVisitor;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.DEFAULT;
import static ch.admin.bar.siardsuite.util.SiardEvent.ARCHIVE_METADATA_UPDATED;

public class ArchiveMetaDataEditorPresenter extends StepperPresenter implements SiardArchiveMetaDataDetailsVisitor {

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
    public CheckBox exportViewsAsTables;

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
        saveArchiveButton.setOnAction(event -> this.saveOnlyMetaData(stepper));

        this.buttonsBox.append(saveArchiveButton);
        initFields();
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
        I18n.bind(this.exportViewsAsTables.textProperty(), "archiveMetadata.view.exportViewsAsTables");
    }

    private File showFileChooserToSelectTargetArchive(String databaseName) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(I18n.get("export.choose-location.text"));
        fileChooser.setInitialFileName(databaseName + ".siard");
        final FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("SIARD files", "*.siard");
        fileChooser.getExtensionFilters().add(extensionFilter);
        return fileChooser.showSaveDialog(stage);
    }

    private void setListeners(MFXStepper stepper) {
        this.buttonsBox.next().setOnAction((event) -> {
            if (this.validateProperties()) {
                File targetArchive = this.showFileChooserToSelectTargetArchive(this.name.getText());
                URI lobFolder = null;
                try {
                    lobFolder = new URI(lobExportLocation.getText()
                                                         .replace("\\",
                                                                  "/")); // replace backslashes with slashes that work on all platforms
                } catch (URISyntaxException e) {
                    fail(stepper, e);
                }

                if (targetArchive != null) {
                    updateMetaData(targetArchive, lobFolder);

                    stepper.next();
                    stepper.fireEvent(new SiardEvent(ARCHIVE_METADATA_UPDATED));
                }
            }
        });

        new SiardToolip(infoButton, tooltip).setup();
    }

    /*
    Most of this function is needed to hack around the old siard-api and to prevent another databaseLoad.
     */
    private void setupMetaArchive() {
        try {
            File targetArchive = this.showFileChooserToSelectTargetArchive(this.name.getText());
            File tmp = new File(this.controller.getSiardArchive().getTmpPath().getFileName());
            // Copy the tmp-archive created with the downloadTask to the user-selected export folder.
            copyFileUsingStream(tmp, targetArchive);
            // Create a new archive-instance pointing to the copied archive-file.
            final Archive archive = ArchiveImpl.newInstance();
            archive.open(targetArchive);
            this.controller.setSiardArchive(this.name.getText(), archive);

            URI lobFolder = new URI(lobExportLocation.getText());

            if (targetArchive != null) {
                updateMetaData(targetArchive, lobFolder);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    private void updateMetaData(File targetArchive, URI lobFolder) {
        this.controller.updateArchiveMetaData(
                this.name.getText(),
                this.description.getText(),
                this.owner.getText(),
                this.dataOriginTimespan.getText(),
                this.archiverName.getText(),
                this.archiverContact.getText(),
                lobFolder,
                targetArchive,
                exportViewsAsTables.isSelected());
    }

    private static void copyFileUsingStream(File source, File dest) throws IOException {
        try (InputStream is = Files.newInputStream(source.toPath()); OutputStream os = Files.newOutputStream(dest.toPath())) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    private void saveOnlyMetaData(MFXStepper stepper) {
        if (this.validateProperties()) {
            setupMetaArchive();
            try {
                this.controller.saveArchiveOnlyMetaData();
            } catch (IOException e) {
                fail(stepper, e);
            }
        }
    }

    private void fail(MFXStepper stepper, Throwable e) {
        e.printStackTrace();
        this.stage.openDialog(View.ERROR_DIALOG);
        controller.cancelDownload();
        controller.failure(new Failure(e));
        stepper.fireEvent(new SiardEvent(SiardEvent.ERROR_OCCURED));
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
                      String databaseUsername, String databaseDescription, String databaseOwner,
                      String databaseCreationDate,
                      LocalDate archivingDate, String archiverName, String archiverContact, File targetArchive,
                      URI lobFolder, boolean viewsAsTables) {
        this.name.setText(databaseName);
        this.description.setText(databaseDescription);
        this.owner.setText(removePlaceholder(databaseOwner));
        this.dataOriginTimespan.setText(removePlaceholder(databaseCreationDate));
        this.archiverName.setText(archiverName);
        this.archiverContact.setText(archiverContact);
        if (lobFolder != null) this.lobExportLocation.setText(lobFolder.toString());
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
