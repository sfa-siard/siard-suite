package ch.admin.bar.siardsuite.presenter.search;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.ui.CloseDialogButton;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
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
    private List<Map> rows;
    private final List<Map> hits = new ArrayList<>();

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

        if (model.getCurrentDatabaseTable() != null) {
            tableView = model.getCurrentDatabaseTable().getKey();
            rows = model.getCurrentDatabaseTable().getValue();
        }

        if (tableView != null) {
            // ability to search whole table
            // tableView.fireEvent(new SiardEvent(SiardEvent.EXPAND_DATABASE_TABLE));
            hits.addAll(tableView.getItems());
            if (rows != null) {
                if (rows.size() < tableView.getItems().size()) {
                    model.setCurrentDatabaseTable(tableView, tableView.getItems());
                }
            }
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
                final List<Map> hits = this.hits.stream().filter(row -> contains(row, s)).toList();
                tableView.setItems(FXCollections.observableArrayList(hits));
                stage.closeDialog();
            } else {
                stage.closeDialog();
            }
        }
        model.setCurrentTableSearch(s);
    }

}
