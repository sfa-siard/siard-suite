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

public class ArchiveMetaDataPresenter extends StepperPresenter implements ArchiveMetaDataVisitor {

    @FXML
    Text title;
    @FXML
    Text description;
    @FXML
    Text titleWhat;
    @FXML
    Text titleWho;
    @FXML
    MFXTextField databaseName;
    @FXML
    MFXTextField databaseDescription;
    @FXML
    MFXTextField dbDeliveringOffice;
    @FXML
    MFXTextField dbTimeOfOrigin;
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

        this.buttonsBox = new StepperButtonBox().make(StepperButtonBox.DEFAULT);
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
        this.title.textProperty().bind(I18n.createStringBinding("archiveMetadata.view.title"));
        this.description.textProperty().bind(I18n.createStringBinding("archiveMetadata.view.description"));
        this.titleWhat.textProperty().bind(I18n.createStringBinding("archiveMetadata.view.titleWhat"));
        this.titleWho.textProperty().bind(I18n.createStringBinding("archiveMetadata.view.titleWho"));

        this.databaseName.floatingTextProperty().bind(I18n.createStringBinding("archiveMetadata.view.databaseName"));

        this.databaseDescription.floatingTextProperty()
                                .bind(I18n.createStringBinding("archiveMetadata.view.databaseDescription"));
        this.dbDeliveringOffice.floatingTextProperty()
                               .bind(I18n.createStringBinding("archiveMetadata.view.deliveringOffice"));
        this.dbTimeOfOrigin.floatingTextProperty().bind(I18n.createStringBinding("archiveMetadata.view.timeOfOrigin"));
        this.archiverName.floatingTextProperty().bind(I18n.createStringBinding("archiveMetadata.view.archiverName"));
        this.archiverContact.floatingTextProperty()
                            .bind(I18n.createStringBinding("archiveMetadata.view.archiverContact"));
        this.errorMessage.textProperty().bind(I18n.createStringBinding("archiveMetadata.view.error"));
    }

    private void setListeners(MFXStepper stepper) {
        this.buttonsBox.next().setOnAction((event) -> {
            if (this.dbDeliveringOffice.getText().isBlank() || this.dbTimeOfOrigin.getText().isBlank()) {
                this.errorMessage.setVisible(true);
            } else {
                this.errorMessage.setVisible(false);
                this.controller.updateArchiveMetaData(this.description.getText(),
                                                      this.dbDeliveringOffice.getText(),
                                                      this.dbTimeOfOrigin.getText(),
                                                      this.archiverName.getText(),
                                                      this.archiverContact.getText());
                stepper.next();
                stepper.fireEvent(getUpdateEvent(SiardEvent.ARCHIVE_METADATA_UPDATED));
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
        this.databaseName.setText(this.model.getDatabaseName().getValue());
        if(this.model.getArchive().hasArchiveMetaData()) {
            this.controller.provideArchiveMetaData(this);
        }
    }
    @Override
    public void visit(String description, String owner, String timeOfOrigin,
                      String archiverName, String archiverContact) {
        this.description.setText(description);
        this.dbDeliveringOffice.setText(owner);
        this.dbTimeOfOrigin.setText(timeOfOrigin);
        this.archiverName.setText(archiverName);
        this.archiverContact.setText(archiverContact);
    }
}
