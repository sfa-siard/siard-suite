package ch.admin.bar.siardsuite.presenter.search;

import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.CloseDialogButton;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.admin.bar.siardsuite.model.database.DatabaseObject.contains;

public class SearchTableDialogPresenter extends DialogPresenter {

    @FXML
    protected Label title;
    @FXML
    protected Text text;
    @FXML
    protected MFXButton closeButton; // seems redundant
    @FXML
    protected TextField searchField;
    @FXML
    protected HBox buttonBox;
    private TableView<Map> tableView;
    private LinkedHashSet<Map> rows;
    private final LinkedHashSet<Map> hits = new LinkedHashSet<>();

    @Override
    public void init(Controller controller, Model model, RootStage stage) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        I18n.bind(title.textProperty(), "search.table.dialog.title");
        I18n.bind(text.textProperty(), "search.table.dialog.text");

        if (model.getCurrentTableSearch() != null) {
            searchField.setText(model.getCurrentTableSearch());
        }

        if (model.getCurrentTableSearchBase() != null) {
            tableView = model.getCurrentTableSearchBase().tableView();
            rows = model.getCurrentTableSearchBase().rows();
        }

        if (tableView != null) {
            // ability to search whole table:
            // tableView.fireEvent(new SiardEvent(SiardEvent.EXPAND_DATABASE_TABLE));
            hits.addAll(tableView.getItems());
            if (rows != null) {
                rows.addAll(tableView.getItems());
            }
        }

        closeButton.setOnAction(event -> stage.closeDialog());
        buttonBox.getChildren().add(new CloseDialogButton(this.stage));

        final MFXButton searchButton = new MFXButton();
        I18n.bind(searchButton.textProperty(), "search.table.dialog.search");
        searchButton.getStyleClass().add("primary");
        searchButton.setOnAction(event -> tableSearch(searchField.getText()));
        buttonBox.getChildren().add(searchButton);
    }

    private void tableSearch(String s) {
        if (tableView != null) {
            if (!s.isEmpty() && !s.equals(" ")) {
                final List<Map> hits = this.hits.stream().filter(row -> contains(row, s)).collect(Collectors.toList());
                tableView.setItems(FXCollections.observableArrayList(hits));
                stage.closeDialog();
            } else {
                stage.closeDialog();
            }
            model.setCurrentTableSearch(s);
            model.setCurrentTableSearchButton(model.getCurrentTableSearchButton().button(), true);
            model.getCurrentTableSearchButton().button().setStyle("-fx-font-weight: bold;");
        }
    }

}
