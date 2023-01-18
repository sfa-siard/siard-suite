package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.ButtonBox;
import ch.admin.bar.siardsuite.component.SiardTooltip;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.model.database.SiardArchiveMetaData;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.visitor.SiardArchiveMetaDataVisitor;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.DEFAULT;
import static ch.admin.bar.siardsuite.util.SiardEvent.ARCHIVE_METADATA_UPDATED;

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
    public Label errorMessage;
    @FXML
    public Label nameLabel;
    @FXML
    public Label ownerLabel;
    @FXML
    public Label dataOriginTimespanLabel;
    @FXML
    public Label archiverLabel;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        this.buttonsBox = new ButtonBox().make(DEFAULT);
        this.borderPane.setBottom(buttonsBox);
        this.tooltip = new SiardTooltip("archiveMetadata.view.tooltip");
        this.bindTexts();
    }

    @Override
    public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
        this.init(controller, model, stage);
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
        I18n.bind(archiverContactLabel.textProperty(), "archiveMetadata.view.archiverContact");
        I18n.bind(this.errorMessage.textProperty(), "archiveMetadata.view.error");
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
            if (this.owner.getText().equals("") || this.dataOriginTimespan.getText().equals("")) {
                this.errorMessage.setVisible(true);
            } else {
                this.errorMessage.setVisible(false);
                File targetArchive = this.showFileChooserToSelectTargetArchive(this.name.getText());
                if (targetArchive != null) {
                    this.model.updateArchiveMetaData(
                            this.name.getText(),
                            this.description.getText(),
                            this.owner.getText(),
                            this.dataOriginTimespan.getText(),
                            this.archiverName.getText(),
                            this.archiverContact.getText(),
                            targetArchive);
                    stepper.next();
                    stepper.fireEvent(new SiardEvent(ARCHIVE_METADATA_UPDATED));
                }
            }
        });
        this.buttonsBox.previous().setOnAction((event) -> stepper.previous());
        this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG));

        infoButton.setOnMouseMoved(event -> {
            Bounds boundsInScreen = infoButton.localToScreen(infoButton.getBoundsInLocal());
            tooltip.show(infoButton,
                    (boundsInScreen.getMaxX() - boundsInScreen.getWidth() / 2) - tooltip.getWidth() / 2,
                    boundsInScreen.getMaxY() - boundsInScreen.getHeight() - tooltip.getHeight());
        });

        infoButton.setOnMouseExited(event -> tooltip.hide());

        stepper.addEventHandler(SiardEvent.ARCHIVE_LOADED, event -> initFields());
    }

    private void initFields() {
        model.provideDatabaseArchiveMetaDataProperties(this);
    }

    private String removePlaceholder(String value) {
        return (value.equals("(...)") ? "" : value);
    }

    @Override
    public void visit(String siardFormatVersion, String databaseName, String databaseProduct, String databaseConnectionURL,
                      String databaseUsername, String databaseDescription, String databseOwner, String databaseCreationDate,
                      LocalDate archivingDate, String archiverName, String archiverContact, File targetArchive) {
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

}
