package ch.admin.bar.siardsuite.presenter.search;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.database.DatabaseObject;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.presenter.tree.TreePresenter;
import ch.admin.bar.siardsuite.ui.CloseDialogButton;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.admin.bar.siardsuite.model.database.DatabaseObject.contains;

public class SearchTableDialogPresenter extends DialogPresenter {

    @FXML
    protected Label title;
    @FXML
    protected Text text;
    @FXML
    protected MFXButton closeButton; // seems redundant
    @FXML
    protected MFXTextField searchField;
    @FXML
    protected HBox buttonBox;
    private TableView<Map> tableView;
    private DatabaseObject databaseObject;
    private final List<Map> rows = new ArrayList<>();

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        title.textProperty().bind(I18n.createStringBinding("search.table.dialog.title"));
        text.textProperty().bind(I18n.createStringBinding("search.table.dialog.text"));

        if (model.getCurrentTableSearch() != null) {
            searchField.setText(model.getCurrentTableSearch());
        }

        tableView = model.getCurrentDatabaseTable().getKey();
        databaseObject = model.getCurrentDatabaseTable().getValue();

        if (tableView != null) {
            // ability to search whole table
            // tableView.fireEvent(new SiardEvent(SiardEvent.EXPAND_DATABASE_TABLE));
            rows.addAll(tableView.getItems());
        }

        closeButton.setOnAction(event -> stage.closeDialog());
        buttonBox.getChildren().add(new CloseDialogButton(this.stage));

        final MFXButton searchButton = new MFXButton();
        searchButton.textProperty().bind(I18n.createStringBinding("search.table.dialog.search"));
        searchButton.getStyleClass().add("primary");
        searchButton.setOnAction(event -> tableSearch(searchField.getText()));
        buttonBox.getChildren().add(searchButton);
    }

    private void tableSearch(String s) {
        if (tableView != null) {
            if (!s.isEmpty() && !s.isBlank()) {
                final List<Map> hits = rows.stream().filter(row -> contains(row, s)).toList();
                tableView.setItems(FXCollections.observableArrayList(hits));
                stage.closeDialog();
            } else {
                stage.closeDialog();
            }
        }
        model.setCurrentTableSearch(s);
    }

    private void initTable() {
        if (model.getCurrentPreviewPresenter() != null && model.getCurrentDatabaseTable().getValue() != null) {
            try {
                FXMLLoader loader = new FXMLLoader(SiardApplication.class.getResource(TreeContentView.ROWS.getViewName()));
                Node node = loader.load();
                model.getCurrentPreviewPresenter().getTableContainerContent().getChildren().setAll(node);
                loader.<TreePresenter>getController().init(controller, model, stage, new TreeAttributeWrapper(null, TreeContentView.ROWS, model.getCurrentDatabaseTable().getValue()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
