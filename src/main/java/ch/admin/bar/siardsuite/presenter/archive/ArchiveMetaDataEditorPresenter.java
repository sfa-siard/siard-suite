package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.SiardTooltip;
import ch.admin.bar.siardsuite.component.StepperButtonBox;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;

import static ch.admin.bar.siardsuite.component.StepperButtonBox.Type.DEFAULT;

public class ArchiveMetaDataEditorPresenter extends StepperPresenter implements ArchiveMetaDataVisitor {

    @FXML
    Text titleText;
    @FXML
    Text descriptionText;
    @FXML
    Text titleWhat;
    @FXML
    Text titleWho;
    @FXML
    MFXTextField name;
    @FXML
    MFXTextField description;
    @FXML
    MFXTextField owner;
    @FXML
    MFXTextField timeOfOrigin;
    @FXML
    MFXTextField archiverName;
    @FXML
    MFXTextField archiverContact;
    @FXML
    protected StepperButtonBox buttonsBox;
    @FXML
    public MFXButton infoButton;
    @FXML
    private Tooltip tooltip;
    @FXML
    public Label errorMessage;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        this.buttonsBox = new StepperButtonBox().make(DEFAULT);
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

        this.name.floatingTextProperty().bind(I18n.createStringBinding("archiveMetadata.view.databaseName"));

        this.description.floatingTextProperty()
                        .bind(I18n.createStringBinding("archiveMetadata.view.databaseDescription"));
        this.owner.floatingTextProperty()
                  .bind(I18n.createStringBinding("archiveMetadata.view.deliveringOffice"));
        this.timeOfOrigin.floatingTextProperty().bind(I18n.createStringBinding("archiveMetadata.view.timeOfOrigin"));
        this.archiverName.floatingTextProperty().bind(I18n.createStringBinding("archiveMetadata.view.archiverName"));
        this.archiverContact.floatingTextProperty()
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
            if (this.owner.getText().isBlank() || this.timeOfOrigin.getText().isBlank()) {
                this.errorMessage.setVisible(true);
            } else {
                this.errorMessage.setVisible(false);
                File targetArchive = this.showFileChoserToSelectTargetArchive(this.name.getText());
                if (targetArchive != null) {
                    this.controller.updateArchiveMetaData(this.descriptionText.getText(),
                                                          this.owner.getText(),
                                                          this.timeOfOrigin.getText(),
                                                          this.archiverName.getText(),
                                                          this.archiverContact.getText(),
                                                          targetArchive);
                    stepper.next();
                    stepper.fireEvent(getUpdateEvent(SiardEvent.ARCHIVE_METADATA_UPDATED));
                }
            }
        });
        this.buttonsBox.previous().setOnAction((event) -> stepper.previous());
        this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG.getName()));

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
        this.name.setText(this.model.getDatabaseName().getValue());
        if(this.model.getArchive().hasArchiveMetaData()) {
            this.controller.provideArchiveMetaData(this);
        }
    }
    @Override
    public void visit(String description, String owner, String timeOfOrigin,
                      String archiverName, String archiverContact) {
        this.descriptionText.setText(description);
        this.owner.setText(owner);
        this.timeOfOrigin.setText(timeOfOrigin);
        this.archiverName.setText(archiverName);
        this.archiverContact.setText(archiverContact);
    }
}
