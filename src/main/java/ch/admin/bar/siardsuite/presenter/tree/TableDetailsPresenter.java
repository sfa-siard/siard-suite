package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.database.*;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.view.TableSize;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static ch.admin.bar.siardsuite.util.I18n.bind;

public class TableDetailsPresenter extends DetailsPresenter implements SiardArchiveVisitor {

    @FXML
    private Label nameLabel;
    @FXML
    private Label nameText;
    @FXML
    private Label infoLabel;
    @FXML
    private Label infoText;
    @FXML
    private TableView<Map> tableView = new TableView<>();
    @FXML
    private VBox tableContainer;

    private TreeAttributeWrapper wrapper;


    @Override
    public void init(Controller controller, Model model, RootStage stage, TreeAttributeWrapper wrapper) {
        this.wrapper = wrapper;
        super.init(controller, model, stage, wrapper);

        model.provideDatabaseArchiveProperties(this, wrapper.getDatabaseObject());
        model.populate(tableView, wrapper.getDatabaseObject(), wrapper.getType());
        model.setCurrentTableSearchBase(tableView, new LinkedHashSet<>(tableView.getItems()));
        if (model.getCurrentTableSearchButton() != null && model.getCurrentTableSearchButton().button() != null) {
            model.setCurrentTableSearchButton(model.getCurrentTableSearchButton().button(), false);
            model.getCurrentTableSearchButton().button().setStyle("-fx-font-weight: normal;");
        }

        tableContainer.prefHeightProperty().bind(stage.heightProperty().subtract(500.0));
        tableView.autosize();
        new TableSize(tableView).resize();
    }

    @Override
    protected void bindLabels() {
        bind(this.nameLabel, wrapper.getType().getNameLabel());
        bind(this.infoLabel, wrapper.getType().getInfoLabel());
    }


    @Override
    public void visit(String archiveName, boolean onlyMetaData, List<DatabaseSchema> schemas, List<User> users,
                      List<Privilige> priviliges) {
    }

    @Override
    public void visitSchema(String schemaName, String schemaDescription, List<DatabaseTable> tables,
                            List<DatabaseView> views, List<DatabaseType> types, List<Routine> routines) {
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
}
