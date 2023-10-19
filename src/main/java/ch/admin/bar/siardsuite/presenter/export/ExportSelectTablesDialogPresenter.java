package ch.admin.bar.siardsuite.presenter.export;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.service.TableExporterService;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import lombok.val;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExportSelectTablesDialogPresenter extends DialogPresenter {
    @FXML
    public Label title;

    @FXML
    public MFXButton closeButton;

    @FXML
    public Text text;

    @FXML
    public HBox buttonBox;

    @FXML
    public MFXButton cancelButton;

    @FXML
    public MFXButton saveButton;

    @FXML
    public TreeView tableSelector;

    @Override
    public void init(Controller controller, RootStage stage) {
        this.controller = controller;
        this.stage = stage;

        this.title.textProperty().bind(I18n.createStringBinding("export.select-tables.dialog.title"));
        this.text.textProperty().bind(I18n.createStringBinding("export.select-tables.dialog.text"));

        EventHandler<ActionEvent> closeEvent = event -> this.stage.closeDialog();
        this.closeButton.setOnAction(closeEvent);

        this.cancelButton.setOnAction(closeEvent);
        this.cancelButton.textProperty().bind(I18n.createStringBinding("button.cancel"));

        this.saveButton.setOnAction(this::handleSaveClicked);
        this.saveButton.textProperty().bind(I18n.createStringBinding("export.choose-location.text"));
        this.tableSelector.setRoot(createTree());
        this.tableSelector.setShowRoot(false);
        tableSelector.setCellFactory((Callback<TreeView<String>, TreeCell<String>>) param -> new CheckBoxTreeCell<>());
    }

    private void handleSaveClicked(ActionEvent actionEvent) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(I18n.get("export.choose-location.text"));
        File file = directoryChooser.showDialog(stage);
        if (Objects.nonNull(file)) {
            try {
                val namesOfSelectedTables = findCheckedItems().stream()
                        .map(TreeItem::getValue)
                        .collect(Collectors.toSet());

                TableExporterService.builder()
                        .exportDir(file)
                        .schemas(this.controller.getSiardArchive().schemas())
                        .shouldBeExportedFilter(databaseTable -> namesOfSelectedTables.contains(databaseTable.getName()))
                        .build()
                        .export();

                this.stage.closeDialog();
                this.stage.openDialog(View.EXPORT_SUCCESS);
            } catch (Exception e) {
                // TODO: show failure message
                e.printStackTrace();
            }
        }
    }

    private CheckBoxTreeItem<String> createTree() {
        val root = new CheckBoxTreeItem<>("root");
        root.setExpanded(true);

        val items = controller.getSiardArchive().getSchemas().stream()
                .map(schema -> {
                    val schemaItem = new CheckBoxTreeItem<>(schema.name());
                    schemaItem.setExpanded(true);

                    val tableItems = schema.getTables().stream()
                            .map(table -> new CheckBoxTreeItem<>(table.getName()))
                            .collect(Collectors.toList());

                    schemaItem.getChildren().setAll(tableItems);

                    return schemaItem;
                })
                .collect(Collectors.toList());
        root.getChildren().setAll(items);

        return root;
    }

    private List<CheckBoxTreeItem<String>> findCheckedItems() {
        return findCheckedItems((CheckBoxTreeItem<String>) this.tableSelector.getRoot())
                .collect(Collectors.toList());
    }

    private Stream<CheckBoxTreeItem<String>> findCheckedItems(CheckBoxTreeItem<String> item) {
        val checkedItem = Stream.of(item)
                .filter(CheckBoxTreeItem::isSelected);

        val checkedChildItems = item.getChildren().stream()
                .map(child -> (CheckBoxTreeItem<String>) child)
                .flatMap(this::findCheckedItems);

        return Stream.concat(
                checkedItem,
                checkedChildItems
        );
    }
}


