package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.database.*;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class TablePresenter extends TreePresenter implements SiardArchiveVisitor {

    @FXML
    public Label nameLabel;
    @FXML
    public Label nameText;
    @FXML
    public Label infoLabel;
    @FXML
    public Label infoText;
    @FXML
    public TableView<Map> tableView = new TableView<>();
    @FXML
    public VBox tableContainer;

    @Override
    public void init(Controller controller, Model model, RootStage stage, TreeAttributeWrapper wrapper) {
        this.model = model;
        this.controller = controller;
        this.stage = stage;

        I18n.bind(this.nameLabel.textProperty(), wrapper.getType().getNameLabel());
        I18n.bind(this.infoLabel.textProperty(), wrapper.getType().getInfoLabel());

        model.provideDatabaseArchiveProperties(this, wrapper.getDatabaseObject());
        model.populate(tableView, wrapper.getDatabaseObject(), wrapper.getType());
        model.setCurrentTableSearchBase(tableView, new LinkedHashSet<>(tableView.getItems()));
        if (model.getCurrentTableSearchButton() != null && model.getCurrentTableSearchButton().button() != null) {
            model.setCurrentTableSearchButton(model.getCurrentTableSearchButton().button(), false);
            model.getCurrentTableSearchButton().button().setStyle("-fx-font-weight: normal;");
        }

        tableContainer.prefHeightProperty().bind(stage.heightProperty().subtract(500.0));
        tableView.autosize();
        autoResizeColumns(tableView);
    }


    @Override
    public void visit(String archiveName, boolean onlyMetaData, List<DatabaseSchema> schemas, List<User> users,
                      List<Privilige> priviliges) {
    }

    @Override
    public void visitSchema(String schemaName, String schemaDescription, List<DatabaseTable> tables,
                            List<DatabaseView> views, List<DatabaseType> types) {
        nameText.setText(schemaName);
        infoText.setText(schemaDescription == null || schemaDescription.isEmpty() ? "(...)" : schemaDescription);
    }

    @Override
    public void visit(String tableName, String numberOfRows, List<DatabaseColumn> columns, List<DatabaseRow> rows) {
        nameText.setText(tableName);
        infoText.setText(numberOfRows);
    }

    @Override
    public void visit(String columnName) {
    }

    @Override
    public void visit(SiardArchive archive) {
    }

    private void autoResizeColumns(TableView<?> table) {
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.getColumns().stream().forEach((column) ->
                                            {
                                                Text t = new Text(column.getText());
                                                double max = t.getLayoutBounds().getWidth();
                                                for (int i = 0; i < table.getItems().size(); i++) {
                                                    if (column.getCellData(i) != null) {
                                                        t = new Text(column.getCellData(i).toString());
                                                        double calcwidth = t.getLayoutBounds().getWidth();
                                                        if (calcwidth > max) {
                                                            max = calcwidth;
                                                        }
                                                    }
                                                }
                                                column.setPrefWidth(max + 10.0d);
                                            });
    }
}
