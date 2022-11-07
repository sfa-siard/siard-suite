package ch.admin.bar.siardsuite.presenter.search;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.model.MetaSearchHit;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.presenter.tree.TreePresenter;
import ch.admin.bar.siardsuite.ui.CloseDialogButton;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.io.IOException;
import java.util.Set;

public class SearchMetadataDialogPresenter extends DialogPresenter {

    @FXML
    protected Label title;
    @FXML
    protected Text text;
    @FXML
    protected MFXButton closeButton; // seems redundant
    @FXML
    protected MFXTextField searchField;
    @FXML
    protected VBox searchHitsBox;
    @FXML
    protected HBox buttonBox;

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        title.textProperty().bind(I18n.createStringBinding("search.metadata.dialog.title"));
        text.textProperty().bind(I18n.createStringBinding("search.metadata.dialog.text"));

        closeButton.setOnAction(event -> stage.closeDialog());
        buttonBox.getChildren().add(new CloseDialogButton(this.stage));

        final MFXButton searchButton = new MFXButton();
        searchButton.textProperty().bind(I18n.createStringBinding("search.metadata.dialog.search"));
        searchButton.getStyleClass().add("primary");
        searchButton.setOnAction(event -> metaSearch(searchField.getText()));
        buttonBox.getChildren().add(searchButton);
    }

    private void metaSearch(String s) {
        searchHitsBox.getChildren().clear();
        final Set<MetaSearchHit> hits = model.aggregatedMetaSearch(s);
        if (hits.size() > 0) {
            for (MetaSearchHit hit : hits) {
                searchHitsBox.getChildren().add(getSearchHitBox(hit));
            }
        } else {
            showNoSearchHits();
        }
    }

    private void showNoSearchHits() {
        final Label label = new Label(I18n.get("search.metadata.dialog.nodata"));
        searchHitsBox.getChildren().add(label);
        label.setStyle("-fx-text-fill: #2a2a2a82");
    }

    private HBox getSearchHitBox(MetaSearchHit hit) {
        final HBox searchHitBox = new HBox();

        if (hit != null) {
            final Label nameLabel = new Label(hit.displayName());
            nameLabel.getStyleClass().add("name-label");

            final Label numberLabel = new Label(String.valueOf(hit.nodeIds().size()));
            numberLabel.getStyleClass().add("number-label");

            searchHitBox.getChildren().addAll(numberLabel, nameLabel);
            searchHitBox.getStyleClass().add("search-hit-hbox");
            VBox.setMargin(searchHitBox, new Insets(5, 0, 5, 0));

            searchHitBox.setOnMouseClicked(event -> show(hit));
        }

        return searchHitBox;
    }

    private void show(MetaSearchHit hit) {
        stage.closeDialog();
        try {
            FXMLLoader loader = new FXMLLoader(SiardApplication.class.getResource(hit.treeContentView().getViewName()));
            Node node = loader.load();
            model.getCurrentPreviewPresenter().getTableContainerContent().getChildren().setAll(node);
            loader.<TreePresenter>getController().init(controller, model, stage, new TreeAttributeWrapper(null, hit.treeContentView(), hit.databaseObject()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
