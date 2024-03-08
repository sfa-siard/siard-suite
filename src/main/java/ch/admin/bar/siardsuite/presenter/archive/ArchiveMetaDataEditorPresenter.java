package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.component.SiardTooltip;
import ch.admin.bar.siardsuite.framework.general.ServicesFacade;
import ch.admin.bar.siardsuite.framework.steps.StepperNavigator;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.model.database.SiardArchive;
import ch.admin.bar.siardsuite.presenter.ValidationProperties;
import ch.admin.bar.siardsuite.presenter.ValidationProperty;
import ch.admin.bar.siardsuite.presenter.archive.model.SiardArchiveWithConnectionData;
import ch.admin.bar.siardsuite.presenter.archive.model.UserDefinedMetadata;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.view.ErrorHandler;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.DEFAULT;

public class ArchiveMetaDataEditorPresenter {

    private static final I18nKey TOOLTIP = I18nKey.of("archiveMetadata.view.tooltip");
    private static final I18nKey TITLE = I18nKey.of("archiveMetadata.view.title");
    private static final I18nKey DESCRIPTION = I18nKey.of("archiveMetadata.view.description");
    private static final I18nKey WHAT = I18nKey.of("archiveMetadata.view.titleWhat");
    private static final I18nKey WHO = I18nKey.of("archiveMetadata.view.titleWho");
    private static final I18nKey DATABASE_NAME = I18nKey.of("archiveMetadata.view.databaseName");
    private static final I18nKey DATABASE_DESCRIPTION = I18nKey.of("archiveMetadata.view.databaseDescription");
    private static final I18nKey DELIVERING_OFFICE = I18nKey.of("archiveMetadata.view.deliveringOffice");
    private static final I18nKey DATABASE_CREATION_DATE = I18nKey.of("archiveMetadata.view.databaseCreationDate");
    private static final I18nKey ARCHIVER_NAME = I18nKey.of("archiveMetadata.view.archiverName");
    private static final I18nKey ARCHIVER_CONTACT = I18nKey.of("archiveMetadata.view.archiverContact");
    private static final I18nKey EXPORT_LOCATION_LOB = I18nKey.of("archiveMetadata.view.exportLocationLob");
    private static final I18nKey EXPORT_VIEWS_AS_TABLES = I18nKey.of("archiveMetadata.view.exportViewsAsTables");
    private static final I18nKey SAVE_ARCHIVE = I18nKey.of("button.save.archive");

    @FXML
    protected BorderPane borderPane;

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


    private ErrorHandler errorHandler;

    @Deprecated
    private Controller controller;

    private SiardArchive siardArchive;

    public void init(
            final SiardArchiveWithConnectionData siardArchiveWithConnectionData,
            final StepperNavigator<UserDefinedMetadata> navigator,
            final ServicesFacade servicesFacade
    ) {
        this.errorHandler = servicesFacade.errorHandler();
        this.controller = servicesFacade.controller();
        this.siardArchive = siardArchiveWithConnectionData.getSiardArchive();

        this.bindTexts();

        this.buttonsBox = new ButtonBox().make(DEFAULT);
        this.borderPane.setBottom(buttonsBox);
        this.buttonsBox.previous().setOnAction(event -> navigator.previous());
        this.buttonsBox.cancel().setOnAction((event) -> servicesFacade
                .dialogs()
                .openDialog(View.ARCHIVE_ABORT_DIALOG));
        this.buttonsBox.next().setOnAction((event) -> {
            if (this.validateProperties()) {
                File targetArchive = this.showFileChooserToSelectTargetArchive(this.name.getText());
                URI lobFolder = null;
                try {
                    lobFolder = new URI(lobExportLocation.getText()
                            .replace("\\",
                                    "/")); // replace backslashes with slashes that work on all platforms
                } catch (URISyntaxException e) {
                    errorHandler.handle(e);
                }

                if (targetArchive != null) {
                    updateMetaData(targetArchive, lobFolder);
                    navigator.next(UserDefinedMetadata.builder()
                            .dbName(this.name.getText())
                            .description(this.description.getText())
                            .owner(this.owner.getText())
                            .dataOriginTimespan(this.dataOriginTimespan.getText())
                            .archiverName(this.archiverName.getText())
                            .archiverContact(this.archiverContact.getText())
                            .lobFolder(lobFolder)
                            .saveAt(targetArchive)
                            .exportViewsAsTables(exportViewsAsTables.isSelected())
                            .dbmsConnectionData(siardArchiveWithConnectionData.getDbmsConnectionData()) // TODO!
                            .build());
                }
            }
        });

        val saveArchiveButton = new MFXButton();
        saveArchiveButton.textProperty().bind(DisplayableText.of(SAVE_ARCHIVE).bindable());
        saveArchiveButton.setOnAction(event -> this.saveOnlyMetaData(controller));

        this.buttonsBox.append(saveArchiveButton);
        initFields(siardArchive);

        new SiardTooltip(TOOLTIP).showOnMouseOn(infoButton);
    }

    private void bindTexts() {
        this.titleText.textProperty().bind(DisplayableText.of(TITLE).bindable());
        this.descriptionText.textProperty().bind(DisplayableText.of(DESCRIPTION).bindable());
        this.titleWhat.textProperty().bind(DisplayableText.of(WHAT).bindable());
        this.titleWho.textProperty().bind(DisplayableText.of(WHO).bindable());
        this.nameLabel.textProperty().bind(DisplayableText.of(DATABASE_NAME).bindable());
        this.descriptionLabel.textProperty().bind(DisplayableText.of(DATABASE_DESCRIPTION).bindable());
        this.ownerLabel.textProperty().bind(DisplayableText.of(DELIVERING_OFFICE).bindable());
        this.dataOriginTimespanLabel.textProperty().bind(DisplayableText.of(DATABASE_CREATION_DATE).bindable());
        this.archiverLabel.textProperty().bind(DisplayableText.of(ARCHIVER_NAME).bindable());
        this.archiverContactLabel.textProperty().bind(DisplayableText.of(ARCHIVER_CONTACT).bindable());
        this.lobExportLabel.textProperty().bind(DisplayableText.of(EXPORT_LOCATION_LOB).bindable());
        this.exportViewsAsTables.textProperty().bind(DisplayableText.of(EXPORT_VIEWS_AS_TABLES).bindable());
    }

    private File showFileChooserToSelectTargetArchive(String databaseName) { // TODO Move to dialogs
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(I18n.get("export.choose-location.text"));
        fileChooser.setInitialFileName(databaseName + ".siard");
        final FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("SIARD files", "*.siard");
        fileChooser.getExtensionFilters().add(extensionFilter);
        return fileChooser.showSaveDialog(titleText.getScene().getWindow());
    }


    /*
    Most of this function is needed to hack around the old siard-api and to prevent another databaseLoad.
     */
    private void setupMetaArchive(final Controller controller) {
        try {
            File targetArchive = this.showFileChooserToSelectTargetArchive(this.name.getText());
            File tmp = new File(controller.getSiardArchive().getTmpPath().getFileName());
            // Copy the tmp-archive created with the downloadTask to the user-selected export folder.
            copyFileUsingStream(tmp, targetArchive);
            // Create a new archive-instance pointing to the copied archive-file.
            final Archive archive = ArchiveImpl.newInstance();
            archive.open(targetArchive);
            controller.setSiardArchive(this.name.getText(), archive);

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
        this.siardArchive.addArchiveMetaData(
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

    private void saveOnlyMetaData(Controller controller) {
        if (this.validateProperties()) {
            setupMetaArchive(controller);
            try {
                controller.saveArchiveOnlyMetaData();
            } catch (IOException e) {
                errorHandler.handle(e);
            }
        }
    }

    private void initFields(final SiardArchive siardArchive) {
        val metadata = siardArchive.getMetaData();

        this.name.setText(metadata.getDatabaseName());
        this.description.setText(metadata.getDatabaseDescription());
        this.owner.setText(removePlaceholder(metadata.getDataOwner()));
        this.dataOriginTimespan.setText(removePlaceholder(metadata.getDataOriginTimespan()));
        this.archiverName.setText(metadata.getArchiverName());
        this.archiverContact.setText(metadata.getArchiverContact());
        Optional.ofNullable(metadata.getLobFolder())
                .ifPresent(lobFolder -> this.lobExportLocation.setText(lobFolder.toString()));
    }

    private String removePlaceholder(String value) {
        return (value.equals("(...)") ? "" : value);
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

    public static LoadedFxml<ArchiveMetaDataEditorPresenter> load(
            final SiardArchiveWithConnectionData siardArchiveWithConnectionData,
            final StepperNavigator<UserDefinedMetadata> navigator,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<ArchiveMetaDataEditorPresenter>load("fxml/archive/archive-metadata-editor.fxml");
        loaded.getController().init(siardArchiveWithConnectionData, navigator, servicesFacade);

        return loaded;
    }
}
