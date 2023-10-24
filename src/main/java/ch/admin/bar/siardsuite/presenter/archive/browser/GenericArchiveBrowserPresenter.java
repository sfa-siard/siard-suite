package ch.admin.bar.siardsuite.presenter.archive.browser;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.IconButton;
import ch.admin.bar.siardsuite.component.TwoStatesButton;
import ch.admin.bar.siardsuite.component.rendering.TreeItemsExplorer;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.presenter.tree.DetailsPresenter;
import ch.admin.bar.siardsuite.util.DeactivatableListener;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.fxml.FXMLLoadHelper;
import ch.admin.bar.siardsuite.util.fxml.LoadedFxml;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.val;

import java.util.Optional;

import static ch.admin.bar.siardsuite.util.CastHelper.tryCast;
import static ch.admin.bar.siardsuite.util.I18n.bind;
import static ch.admin.bar.siardsuite.util.OptionalHelper.ifPresentOrElse;

/**
 * Presentes an archive - either when archiving a database (always only metadata) or when a SIARD Archive
 * file was opened to browse the archive content
 */
public class GenericArchiveBrowserPresenter {

    private static final I18nKey META_SEARCH = I18nKey.of("tableContainer.metaSearchButton");
    private static final I18nKey TABLE_SEARCH = I18nKey.of("tableContainer.tableSearchButton");

    @FXML
    public VBox container;
    @FXML
    private BorderPane borderPane;

    @FXML
    private Label title;
    @FXML
    private Text text;

    @FXML
    private TreeView<TreeAttributeWrapper> treeView;
    @FXML
    public VBox leftTreeBox;
    @FXML
    private StackPane rightTableBox;

    @FXML
    private IconButton saveChangesButton;
    @FXML
    private IconButton dropChangesButton;
    @FXML
    private TwoStatesButton tableSearchButton;
    @FXML
    private MFXButton metaSearchButton;
    @FXML
    private Label titleTableContainer;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private AnchorPane contentPane;

    private Optional<ChangeableDataPresenter> currentChangeableDataPresenter = Optional.empty();


    private RootStage rootStage;
    private Controller controller;

    public void init(
            final RootStage rootStage,
            final Controller controller,
            final DisplayableText title,
            final DisplayableText text,
            final Node footerNode,
            final TreeItem<TreeAttributeWrapper> rootTreeItem
    ) {
        this.rootStage = rootStage;
        this.controller = controller;

        this.title.setText(title.getText());
        this.text.setText(text.getText());

        this.borderPane.setBottom(footerNode);
        this.leftTreeBox.prefHeightProperty().bind(container.heightProperty());

        this.treeView.setRoot(rootTreeItem);
        I18n.localeProperty()
                .addListener((observable, oldValue, newValue) -> this.treeView.refresh());

        val explorer = TreeItemsExplorer.from(rootTreeItem);

        metaSearchButton.setOnAction(event -> rootStage.openSearchMetaDataDialog(
                explorer,
                treeItem -> treeView.getSelectionModel().select(treeItem)
        ));
        treeView.getSelectionModel()
                .selectedItemProperty()
                .addListener(new DeactivatableListener<>(this::onSelectedTreeItemChanged));

        bind(metaSearchButton, DisplayableText.of(META_SEARCH));
        bind(tableSearchButton, DisplayableText.of(TABLE_SEARCH));

        treeView.getSelectionModel().select(rootTreeItem);
    }

    private void onSelectedTreeItemChanged(final DeactivatableListener.Change<TreeItem<TreeAttributeWrapper>> change) {
        ifPresentOrElse(currentChangeableDataPresenter,
                changeableDataPresenter -> onSelectedTreeItemChanged(change, changeableDataPresenter),
                () -> refreshContentPane(change.getNewValue().getValue()));
    }

    private void onSelectedTreeItemChanged(
            final DeactivatableListener.Change<TreeItem<TreeAttributeWrapper>> change,
            final ChangeableDataPresenter changeableDataPresenter
    ) {
        if (!changeableDataPresenter.hasChanged().get()) {
            refreshContentPane(change.getNewValue().getValue());
            return;
        }

        this.rootStage.openUnsavedChangesDialogue(result -> {
            switch (result) {
                case CANCEL:
                    change.getDeactivatableListener().deactivate();
                    change.getOldValue().ifPresent(previousSelectedItem ->
                                    treeView.getSelectionModel()
                                            .select(previousSelectedItem));
                    change.getDeactivatableListener().activate();
                    break;
                case DROP_CHANGES:
                    changeableDataPresenter.dropChanges();
                    refreshContentPane(change.getNewValue().getValue());
                    break;
                case SAVE_CHANGES:
                    val report = changeableDataPresenter.saveChanges();
                    ifPresentOrElse(
                            report.getFailedMessage(),
                            errorMessage -> {
                                showErrorMessage(errorMessage);
                                change.getDeactivatableListener().deactivate();
                                change.getOldValue().ifPresent(previousSelectedItem ->
                                        treeView.getSelectionModel()
                                                .select(previousSelectedItem));
                                change.getDeactivatableListener().activate();
                            },
                            () -> refreshContentPane(change.getNewValue().getValue())
                    );
                    break;
            }
        });
    }

    private void refreshContentPane(TreeAttributeWrapper wrapper) {
        hideSaveAndDropButtons();
        hideErrorMessage();

        this.titleTableContainer.setText(wrapper.getViewTitle().getText());

        val newContent = FXMLLoadHelper.load(wrapper.getType().getViewName());
        contentPane.getChildren().clear();
        contentPane.getChildren().add(newContent.getNode());
        contentPane.prefWidthProperty().bind(rightTableBox.widthProperty());

        tryCast(newContent.getController(), DetailsPresenter.class)
                .ifPresent(detailsPresenter -> refreshContentPane(wrapper, detailsPresenter));

        ifPresentOrElse(
                tryCast(newContent.getController(), ChangeableDataPresenter.class),
                detailsPresenter -> refreshContentPane(wrapper, detailsPresenter),
                () -> refreshForNonChangeableContent(wrapper)
        );

        ifPresentOrElse(
                tryCast(newContent.getController(), SearchableTableContainer.class),
                this::refreshContentPane,
                () -> tableSearchButton.setVisible(false));
    }

    private void refreshContentPane(
            final TreeAttributeWrapper wrapper,
            final DetailsPresenter detailsPresenter
    ) {
        detailsPresenter.init(
                this.controller,
                this.rootStage,
                wrapper);
    }

    private void refreshForNonChangeableContent(final TreeAttributeWrapper wrapper) {
        this.currentChangeableDataPresenter = Optional.empty();
        hideSaveAndDropButtons();
        hideErrorMessage();
        this.titleTableContainer.setText(wrapper.getViewTitle().getText());
    }

    private void refreshContentPane(
            final TreeAttributeWrapper wrapper,
            final ChangeableDataPresenter changeableDataPresenter
    ) {
        this.currentChangeableDataPresenter = Optional.of(changeableDataPresenter);

        changeableDataPresenter
                .hasChanged()
                .addListener((observable, oldValue, hasChanges) -> {
                    if (hasChanges) {
                        showSaveAndDropButtons();
                        this.titleTableContainer.setText(wrapper.getViewTitle().getText());
                    } else {
                        hideSaveAndDropButtons();
                        this.titleTableContainer.setText(wrapper.getViewTitle().getText());
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

    private void refreshContentPane(final SearchableTableContainer searchableTableContainer) {
        tableSearchButton.visibleProperty()
                .bind(searchableTableContainer.hasSearchableData());
        tableSearchButton.setState(TwoStatesButton.State.NORMAL);
        tableSearchButton.setNormalStateAction(event ->
                rootStage.openSearchTableDialog(optionalSearchTerm -> ifPresentOrElse(optionalSearchTerm,
                        searchableTableContainer::applySearchTerm,
                        () -> tableSearchButton.setState(TwoStatesButton.State.NORMAL)
                )));
        tableSearchButton.setBoldStateAction(event -> searchableTableContainer.clearSearchTerm());
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

    public static LoadedFxml<GenericArchiveBrowserPresenter> load(
            final RootStage rootStage,
            final Controller controller,
            final DisplayableText title,
            final DisplayableText text,
            final Node footer,
            final TreeItem<TreeAttributeWrapper> rootTreeItem
    ) {
        val loaded = FXMLLoadHelper.<GenericArchiveBrowserPresenter>load("fxml/archive-browser.fxml");
        loaded.getController()
                .init(rootStage,
                        controller,
                        title,
                        text,
                        footer,
                        rootTreeItem);

        return loaded;
    }
}
