package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.TreeContentViewModel;
import ch.admin.bar.siardsuite.model.database.DatabaseArchive;
import ch.admin.bar.siardsuite.model.database.DatabaseColumn;
import ch.admin.bar.siardsuite.model.database.DatabaseSchema;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import ch.admin.bar.siardsuite.presenter.tree.TreePresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class PreviewPresenter extends Presenter {
  protected DatabaseArchive archive;
  protected final Node db = new ImageView(new Image(String.valueOf(SiardApplication.class.getResource("icons/server.png")), 16.0, 16.0, true, false));

  @FXML
  protected BorderPane borderPane;
  @FXML
  protected TreeView<TreeAttributeWrapper> treeView;
  @FXML
  protected MFXButton tableSearchButton;
  @FXML
  protected MFXButton metaSearchButton;
  @FXML
  protected VBox tableContainerContent;

  @Override
  public void init(Controller controller, Model model, RootStage stage) {
    this.model = model;
    archive = model.getArchive();
    this.controller = controller;
    this.stage = stage;

    this.metaSearchButton.textProperty().bind(I18n.createStringBinding("tableContainer.metaSearchButton"));
    this.tableSearchButton.textProperty().bind(I18n.createStringBinding("tableContainer.tableSearchButton"));

    initTreeView();

    setListeners();
  }

  /* Cantor's pairing function */
  private int pair(final int a, final int b) {
    if (a < 0 || b < 0) {
      throw new IllegalArgumentException("Pairing is defined only for natural numbers.");
    }
    return (int) (0.5 * (a + b) * (a + b + 1) + b);
  }

  /* Cantor's unpairing function: unpair(pair(a, b), 0) = a and unpair(pair(a, b), 1) = b */
  private int unpair(final int c, final int i) {
    if (c < 0 || i < 0 || i > 1) {
      throw new IllegalArgumentException("Unpairing is defined only for natural numbers as codes of pairs and for coordinates 0 and 1.");
    }
    final int u = (int) Math.floor(0.5 * (Math.sqrt(8 * c + 1) - 1));
    final int v = (int) (0.5 * (Math.pow(u, 2) + u));
    int r;
    if (i == 1) {
      r = c - v;
    } else {
      r = u - (c - v);
    }
    return r;
  }

  protected void initTreeView() {
    final TreeItem<TreeAttributeWrapper> rootItem = new TreeItem<>(new TreeAttributeWrapper(archive.getArchiveName().get(), pair(0, 0), TreeContentView.ROOT), db);

    final List<DatabaseSchema> schemas = archive.getSchemas();
    final TreeItem<TreeAttributeWrapper> schemasItem = new TreeItem<>();
    schemasItem.valueProperty().bind(I18n.createTreeAtributeWrapperBinding("preview.view.tree.schemas", pair(1, 0), TreeContentView.SCHEMAS, schemas.size()));
    TreeItem<TreeAttributeWrapper> schemaItem;

    List<DatabaseTable> tables;
    TreeItem<TreeAttributeWrapper> tablesItem;
    TreeItem<TreeAttributeWrapper> tableItem;

    List<DatabaseColumn> columns;
    TreeItem<TreeAttributeWrapper> columnsItem;
    TreeItem<TreeAttributeWrapper> columnItem;

    for (DatabaseSchema schema : schemas) {
      schemaItem = new TreeItem<>(new TreeAttributeWrapper(schema.getName().get(), pair(2, schemas.indexOf(schema)), TreeContentView.SCHEMA_TABLE));

      tables = schema.getTables();
      tablesItem = new TreeItem<>();
      tablesItem.valueProperty().bind(I18n.createTreeAtributeWrapperBinding("preview.view.tree.tables", pair(3, 0), TreeContentView.TABLES, tables.size()));

      for (DatabaseTable table : tables) {
        tableItem = new TreeItem<>(new TreeAttributeWrapper(table.getName().get(), pair(4, tables.indexOf(table)), TreeContentView.TABLE));

        columns = table.getColumns();
        columnsItem = new TreeItem<>();
        columnsItem.valueProperty().bind(I18n.createTreeAtributeWrapperBinding("preview.view.tree.columns", pair(5, 0), TreeContentView.COLUMNS, columns.size()));

        for (DatabaseColumn column : columns) {
          columnItem = new TreeItem<>(new TreeAttributeWrapper(column.getName().get(), pair(6, columns.indexOf(column)), TreeContentView.COLUMN));
            // more levels
          columnsItem.getChildren().add(columnItem);
        }

        tableItem.getChildren().add(columnsItem);
        tablesItem.getChildren().add(tableItem);
      }

      schemaItem.getChildren().add(tablesItem);
      schemasItem.getChildren().add(schemaItem);
    }

    rootItem.getChildren().add(schemasItem);
    treeView.setRoot(rootItem);

    expandChildren(rootItem);

    updateTableContainerContent(rootItem.getValue());
  }

  protected void expandChildren(TreeItem<TreeAttributeWrapper> root) {
    root.setExpanded(true);
    for (TreeItem<TreeAttributeWrapper> child : root.getChildren()) {
      child.setExpanded(true);
      expandChildren(child);
    }
  }

  protected void setListeners() {
    MultipleSelectionModel<TreeItem<TreeAttributeWrapper>> selection = treeView.getSelectionModel();
    selection.selectedItemProperty().addListener(((observable, oldValue, newValue) -> updateTableContainerContent(newValue.getValue())));
  }

  protected void updateTableContainerContent(TreeAttributeWrapper wrapper) {
    try {
      FXMLLoader loader = new FXMLLoader(SiardApplication.class.getResource(wrapper.getType().getName()));
      Node container = loader.load();
      tableContainerContent.getChildren().setAll(container);

      loader.<TreePresenter>getController().init(this.controller, new TreeContentViewModel(wrapper.getType().getTitle(),
              this.model), this.stage);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
