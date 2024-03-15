package ch.admin.bar.siardsuite.presenter.archive.browser.dialogues;

import ch.admin.bar.siardsuite.component.CloseDialogButton;
import ch.admin.bar.siardsuite.component.SearchButton;
import ch.admin.bar.siardsuite.component.rendering.TreeItemsExplorer;
import ch.admin.bar.siardsuite.framework.ServicesFacade;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.Tuple;
import ch.admin.bar.siardsuite.util.MetaSearchTerm;
import ch.admin.bar.siardsuite.framework.view.FXMLLoadHelper;
import ch.admin.bar.siardsuite.framework.view.LoadedView;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.framework.DialogCloser;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SearchMetadataDialogPresenter {

    private static final I18nKey TITLE = I18nKey.of("search.metadata.dialog.title");
    private static final I18nKey TEXT = I18nKey.of("search.metadata.dialog.text");
    private static final I18nKey NO_DATA = I18nKey.of("search.metadata.dialog.nodata");

    @FXML
    protected Label title;
    @FXML
    protected Text text;
    @FXML
    protected MFXButton closeButton;
    @FXML
    protected TextField searchField;
    @FXML
    protected VBox searchHitsBox;
    @FXML
    protected HBox buttonBox;

    protected DialogCloser dialogCloser;
    private Consumer<TreeItem<TreeAttributeWrapper>> onSelected;
    private TreeItemsExplorer treeItemsExplorer;

    public void init(
            final DialogCloser dialogCloser,
            final TreeItemsExplorer treeItemsExplorer,
            final Consumer<TreeItem<TreeAttributeWrapper>> onSelected
    ) {
        this.dialogCloser = dialogCloser;
        this.onSelected = onSelected;
        this.treeItemsExplorer = treeItemsExplorer;

        title.setText(DisplayableText.of(TITLE).getText());
        text.setText((DisplayableText.of(TEXT).getText()));

        closeButton.setOnAction(event -> dialogCloser.closeDialog());
        buttonBox.getChildren().add(new CloseDialogButton(dialogCloser));
        buttonBox.getChildren().add(new SearchButton(event -> search(new MetaSearchTerm(searchField.getText()))));
    }

    private void search(final MetaSearchTerm searchTerm) {
        val results = treeItemsExplorer.find(searchTerm);

        if (!results.isEmpty()) {
            val grouped = results.stream()
                    .collect(Collectors.groupingBy(result -> new ResultTarget(
                            joinPath(result.getPathToTreeItem()),
                            result.getTreeItem())));

            val boxes = grouped.entrySet().stream()
                    .map(entry -> createSearchHitBox(
                            entry.getKey(),
                            entry.getValue().size()))
                    .collect(Collectors.toList());

            searchHitsBox.getChildren().setAll(boxes);
        } else {
            searchHitsBox.getChildren().setAll(createNoSearchHitsLabel());
        }
    }

    private Label createNoSearchHitsLabel() {
        final Label label = new Label(DisplayableText.of(NO_DATA).getText());
        label.setStyle("-fx-text-fill: #2a2a2a82");

        return label;
    }

    private HBox createSearchHitBox(
            final ResultTarget resultTarget,
            final int nrOfMatches
    ) {
        final HBox searchHitBox = new HBox();

        final Label nameLabel = new Label(resultTarget.getPath());
        nameLabel.getStyleClass().add("name-label");
        HBox.setHgrow(nameLabel, Priority.ALWAYS); // FIXME ?

        final Label numberLabel = new Label(String.valueOf(nrOfMatches));
        numberLabel.getStyleClass().add("number-label");

        searchHitBox.getChildren().addAll(numberLabel, nameLabel);
        searchHitBox.getStyleClass().add("search-hit-hbox");
        VBox.setMargin(searchHitBox, new Insets(5, 0, 5, 0));

        searchHitBox.setOnMouseClicked(event -> {
            dialogCloser.closeDialog();
            onSelected.accept(resultTarget.getTreeItem());
        });

        return searchHitBox;
    }

    private String joinPath(final List<String> path) {
        val editedPath = replaceOrAddFirstElement(path, "SIARD");
        return String.join(" > ", editedPath);
    }

    private List<String> replaceOrAddFirstElement(final List<String> path, String firstElement) {
        if (path.isEmpty()) {
            return Collections.singletonList(firstElement);
        }

        val copiedPath = path.subList(0, path.size());
        copiedPath.set(0, firstElement);

        return copiedPath;
    }

    @Value
    private static class ResultTarget {
        @NonNull
        String path;

        @EqualsAndHashCode.Exclude
        @NonNull
        TreeItem<TreeAttributeWrapper> treeItem;
    }

    public static LoadedView<SearchMetadataDialogPresenter> load(
            final DialogCloser dialogCloser,
            final TreeItemsExplorer treeItemsExplorer,
            final Consumer<TreeItem<TreeAttributeWrapper>> onSelected
    ) {
        val loaded = FXMLLoadHelper.<SearchMetadataDialogPresenter>load("fxml/search/search-metadata-dialog.fxml");
        loaded.getController().init(dialogCloser, treeItemsExplorer, onSelected);

        return loaded;
    }

    public static LoadedView<SearchMetadataDialogPresenter> load(
            final Tuple<TreeItemsExplorer, Consumer<TreeItem<TreeAttributeWrapper>>> data,
            final ServicesFacade servicesFacade
    ) {
        val loaded = FXMLLoadHelper.<SearchMetadataDialogPresenter>load("fxml/search/search-metadata-dialog.fxml");
        loaded.getController().init(
                servicesFacade.dialogs(),
                data.getValue1(),
                data.getValue2()
        );

        return loaded;
    }
}
