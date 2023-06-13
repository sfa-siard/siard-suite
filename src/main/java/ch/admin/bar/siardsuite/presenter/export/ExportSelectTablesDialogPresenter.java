package ch.admin.bar.siardsuite.presenter.export;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.DialogPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        TreeItem root = new CheckBoxTreeItem("root");
        this.controller.populate(root);
        root.setExpanded(true);

        this.tableSelector.setRoot(root);
        this.tableSelector.setShowRoot(false);
        tableSelector.setCellFactory((Callback<TreeView<String>, TreeCell<String>>) param -> new CheckBoxTreeCell<>());

    }

    private void handleSaveClicked(ActionEvent actionEvent) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(I18n.get("export.choose-location.text"));
        File file = directoryChooser.showDialog(stage);
        if (Objects.nonNull(file)) {
            try {
                List<String> tables = new ArrayList<>();
                this.findCheckedItems((CheckBoxTreeItem<String>) this.tableSelector.getRoot(), tables);

                this.controller.getSiardArchive().export(tables, file);
                this.stage.closeDialog();
                this.stage.openDialog(View.EXPORT_SUCCESS);
            } catch (Exception e) {
                // TODO: show failure message
                e.printStackTrace();
            }
        }
    }

    // copied from https://stackoverflow.com/questions/42828781/how-to-get-checked-items-in-checkboxtreeview-in-javafx
    private void findCheckedItems(CheckBoxTreeItem<String> item, List<String> checkedItems) {
        if (item.isSelected()) {
            checkedItems.add(item.getValue());
        }
        for (TreeItem<?> child : item.getChildren()) {
            findCheckedItems((CheckBoxTreeItem<String>) child, checkedItems);
        }
    }
}


