package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.component.ArchiveBrowserView;
import ch.admin.bar.siardsuite.component.IconButton;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.tree.ChangeableDataPresenter;
import ch.admin.bar.siardsuite.presenter.tree.DetailsPresenter;
import ch.admin.bar.siardsuite.util.CastHelper;
import ch.admin.bar.siardsuite.util.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.OptionalHelper;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lombok.val;

import java.io.IOException;

import static ch.admin.bar.siardsuite.util.I18n.bind;
import static ch.admin.bar.siardsuite.util.OptionalHelper.ifPresentOrElse;

/**
 * Presentes an archive - either when archiving a database (always only metadata) or when a SIARD Archive
 * file was opened to browse the archive content
 */
public class ArchiveBrowserPresenter extends StepperPresenter {

    @FXML
    protected TreeView<TreeAttributeWrapper> treeView;
    @FXML
    protected IconButton saveChangesButton;
    @FXML
    protected IconButton dropChangesButton;
    @FXML
    protected MFXButton tableSearchButton;
    @FXML
    protected MFXButton metaSearchButton;
    @FXML
    protected AnchorPane contentPane;
    @FXML
    public Label titleTableContainer;
    @FXML
    public Label errorMessageLabel;
    @FXML
    public StackPane rightTableBox;


    @Override
    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;


        ArchiveBrowserView archiveTreeView = new ArchiveBrowserView(controller.getSiardArchive(), treeView);
        archiveTreeView.init();
        this.refreshContentPane(archiveTreeView.rootItem().getValue());

        setListeners();
        controller.setCurrentPreviewPresenter(this);
        tableSearchButton.setVisible(false);
        bind(metaSearchButton, "tableContainer.metaSearchButton");
        bind(tableSearchButton, "tableContainer.tableSearchButton");
    }

    @Override
    public void init(Controller controller, RootStage stage, MFXStepper stepper) {
        this.init(controller, stage);
    }

    protected void setListeners() {
        MultipleSelectionModel<TreeItem<TreeAttributeWrapper>> selection = treeView.getSelectionModel();
        selection.selectedItemProperty()
                .addListener(((observable, oldValue, newValue) -> refreshContentPane(newValue.getValue())));
        tableSearchButton.setOnAction(event -> {

            if (controller.getCurrentTableSearchButton() != null &&
                    tableSearchButton.equals(controller.getCurrentTableSearchButton().button()) &&
                    controller.getCurrentTableSearchButton().active()
            ) {
                controller.setCurrentTableSearchButton(tableSearchButton, false);
                tableSearchButton.setStyle("-fx-font-weight: normal;");

                controller.getCurrentTableSearchBase()
                        .tableView()
                        .setItems(FXCollections.observableArrayList(controller.getCurrentTableSearchBase().rows()));
            } else {
                controller.setCurrentTableSearchButton(tableSearchButton, false);
                stage.openDialog(View.SEARCH_TABLE_DIALOG);
            }
        });
        metaSearchButton.setOnAction(event -> stage.openDialog(View.SEARCH_METADATA_DIALOG));
    }

    // Refresh the content view based on the selected item in the tree (e.g. tables, users...)
    protected void refreshContentPane(TreeAttributeWrapper wrapper) {
        hideSaveAndDropButtons();
        hideErrorMessage();

        this.titleTableContainer.setText(I18n.get(wrapper.getType().getViewTitle()));

        val newContent = FXMLLoadHelper.load(wrapper.getType().getViewName());
        contentPane.getChildren().setAll(newContent.getNode());
        contentPane.prefWidthProperty().bind(rightTableBox.widthProperty());

        CastHelper.tryCast(newContent.getController(), DetailsPresenter.class)
                .ifPresent(detailsPresenter -> refreshContentPane(wrapper, detailsPresenter));

        OptionalHelper.ifPresentOrElse(
                CastHelper.tryCast(newContent.getController(), ChangeableDataPresenter.class),
                detailsPresenter -> refreshContentPane(wrapper, detailsPresenter),
                () -> refreshForNonChangeableContent(wrapper)
        );

        CastHelper.tryCast(newContent.getController(), ChangeableDataPresenter.class)
                .ifPresent(detailsPresenter -> refreshContentPane(wrapper, detailsPresenter));
    }

    @Deprecated // TODO: Refactore table search to avoid this
    public AnchorPane getContentPane() {
        return contentPane;
    }

    private void refreshContentPane(
            final TreeAttributeWrapper wrapper,
            final DetailsPresenter detailsPresenter
    ) {
        detailsPresenter.init(
                this.controller,
                this.stage,
                wrapper);
        tableSearchButton.setVisible(wrapper.getType().getHasTableSearch());
    }

    private void refreshForNonChangeableContent(final TreeAttributeWrapper wrapper) {
        hideSaveAndDropButtons();
        hideErrorMessage();
        this.titleTableContainer.setText(I18n.get(wrapper.getType().getViewTitle()));
    }

    private void refreshContentPane(
            final TreeAttributeWrapper wrapper,
            final ChangeableDataPresenter changeableDataPresenter
    ) {
        changeableDataPresenter
                .hasChanged()
                .addListener((observable, oldValue, hasChanges) -> {
            if (hasChanges) {
                showSaveAndDropButtons();
                this.titleTableContainer.setText(I18n.get(wrapper.getType().getViewTitle()) + " (edited)");
            } else {
                hideSaveAndDropButtons();
                this.titleTableContainer.setText(I18n.get(wrapper.getType().getViewTitle()));
            }
        });

        this.saveChangesButton.setOnAction(() -> {
            val report = changeableDataPresenter.saveChanges();
            ifPresentOrElse(
                    report.getFailedMessage(),
                    this::showErrorMessage,
                    this::hideErrorMessage
            );
        });

        this.dropChangesButton.setOnAction(() -> {
            hideErrorMessage();
            changeableDataPresenter.dropChanges();
        });
    }

    private void showSaveAndDropButtons() {
        this.saveChangesButton.setVisible(true);
        this.saveChangesButton.setManaged(true);
        this.dropChangesButton.setVisible(true);
        this.dropChangesButton.setManaged(true);
    }

    private void hideSaveAndDropButtons() {
        this.saveChangesButton.setVisible(false);
        this.saveChangesButton.setManaged(false);
        this.dropChangesButton.setVisible(false);
        this.dropChangesButton.setManaged(false);
    }

    private void showErrorMessage(final String message) {
        this.errorMessageLabel.setText(message);
        this.errorMessageLabel.setVisible(true);
        this.errorMessageLabel.setManaged(true);
    }

    private void hideErrorMessage() {
        this.errorMessageLabel.setVisible(false);
        this.errorMessageLabel.setManaged(false);
    }
}
