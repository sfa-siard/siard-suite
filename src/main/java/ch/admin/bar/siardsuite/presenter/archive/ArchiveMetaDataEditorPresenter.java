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
        this.titleText.textProperty().bind(I18n.createStringBinding("archiveMetadata.view.title"));
        this.descriptionText.textProperty().bind(I18n.createStringBinding("archiveMetadata.view.description"));
        this.titleWhat.textProperty().bind(I18n.createStringBinding("archiveMetadata.view.titleWhat"));
        this.titleWho.textProperty().bind(I18n.createStringBinding("archiveMetadata.view.titleWho"));

        this.nameLabel.textProperty().bind(I18n.createStringBinding("archiveMetadata.view.databaseName"));

        this.descriptionLabel.textProperty()
                        .bind(I18n.createStringBinding("archiveMetadata.view.databaseDescription"));
        this.ownerLabel.textProperty()
                  .bind(I18n.createStringBinding("archiveMetadata.view.deliveringOffice"));
        this.dataOriginTimespanLabel.textProperty().bind(I18n.createStringBinding("archiveMetadata.view.databaseCreationDate"));
        this.archiverLabel.textProperty().bind(I18n.createStringBinding("archiveMetadata.view.archiverName"));
        archiverContactLabel.textProperty()
                            .bind(I18n.createStringBinding("archiveMetadata.view.archiverContact"));
        this.errorMessage.textProperty().bind(I18n.createStringBinding("archiveMetadata.view.error"));
    }

    private File showFileChoserToSelectTargetArchive(String databaseName) {
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
                File targetArchive = this.showFileChoserToSelectTargetArchive(this.name.getText());
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
        name.setText(this.model.getDatabaseName().getValue());
        model.provideDatabaseArchiveMetaDataProperties(this);
    }
    @Override
    public void visit(String siardFormatVersion, String databaseName, String databaseProduct, String databaseConnectionURL,
                      String databaseUsername, String databaseDescription, String databseOwner, String databaseCreationDate,
                      LocalDate archivingDate, String archiverName, String archiverContact, File targetArchive) {
        description.setText(databaseDescription);
        owner.setText(databseOwner);
        this.dataOriginTimespan.setText(databaseCreationDate);
        this.archiverName.setText(archiverName);
        this.archiverContact.setText(archiverContact);
    }

    @Override
    public void visit(SiardArchiveMetaData metaData) {}

}
