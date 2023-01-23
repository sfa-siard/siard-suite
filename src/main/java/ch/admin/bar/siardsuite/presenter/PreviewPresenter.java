package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.model.database.*;
import ch.admin.bar.siardsuite.presenter.tree.TreePresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.visitor.SiardArchiveVisitor;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PreviewPresenter extends StepperPresenter implements SiardArchiveVisitor {

  private final StringProperty archiveName = new SimpleStringProperty();
  private boolean onlyMetaData = false;
  private List<DatabaseSchema> schemas = new ArrayList<>();
  private String schemaName = "";
  private List<DatabaseTable> tables = new ArrayList<>();
  private String tableName = "";
  private List<DatabaseColumn> columns = new ArrayList<>();
  private String columnName = "";
  private String numberOfRows = "";
  protected final Node db = new ImageView(new Image(String.valueOf(SiardApplication.class.getResource("icons/server.png")), 16.0, 16.0, true, false));
  @FXML
  protected TreeView<TreeAttributeWrapper> treeView;
  @FXML
  protected MFXButton tableSearchButton;
  @FXML
  protected MFXButton metaSearchButton;
  @FXML
  protected AnchorPane tableContainerContent;
  @FXML
  public Label titleTableContainer;



  @Override
  public void init(Controller controller, Model model, RootStage stage) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;

    model.setCurrentPreviewPresenter(this);
    this.tableSearchButton.setVisible(false);
    this.metaSearchButton.textProperty().bind(I18n.createStringBinding("tableContainer.metaSearchButton"));
    this.tableSearchButton.textProperty().bind(I18n.createStringBinding("tableContainer.tableSearchButton"));

    setListeners();
  }

  @Override
  public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
    this.init(controller, model, stage);
  }

  protected void initTreeView() {
    model.provideDatabaseArchiveObject(this);
    model.provideDatabaseArchiveProperties(this);

    final TreeItem<TreeAttributeWrapper> rootItem = new TreeItem<>(new TreeAttributeWrapper(archiveName.get(), TreeContentView.ROOT, null), db);

    final TreeItem<TreeAttributeWrapper> schemasItem = new TreeItem<>();
    schemasItem.valueProperty().bind(I18n.createTreeAtributeWrapperBinding("archive.tree.view.node.schemas", TreeContentView.SCHEMAS, null, schemas.size()));
    TreeItem<TreeAttributeWrapper> schemaItem;

    TreeItem<TreeAttributeWrapper> tablesItem;
    TreeItem<TreeAttributeWrapper> tableItem;

    TreeItem<TreeAttributeWrapper> columnsItem;
    TreeItem<TreeAttributeWrapper> columnItem;

    TreeItem<TreeAttributeWrapper> rowsItem;

    for (DatabaseSchema schema : schemas) {
      model.provideDatabaseArchiveProperties(this, schema);

      schemaItem = new TreeItem<>(new TreeAttributeWrapper(schemaName, TreeContentView.SCHEMA, schema));

      tablesItem = new TreeItem<>();
      tablesItem.valueProperty().bind(I18n.createTreeAtributeWrapperBinding("archive.tree.view.node.tables", TreeContentView.TABLES, schema, tables.size()));

      for (DatabaseTable table : tables) {
        model.provideDatabaseArchiveProperties(this, table);

        tableItem = new TreeItem<>(new TreeAttributeWrapper(tableName, TreeContentView.TABLE, table));


        columnsItem = new TreeItem<>();
        columnsItem.valueProperty().bind(I18n.createTreeAtributeWrapperBinding("archive.tree.view.node.columns", TreeContentView.COLUMNS, table, columns.size()));

        for (DatabaseColumn column : columns) {
          model.provideDatabaseArchiveProperties(this, column);

          columnItem = new TreeItem<>(new TreeAttributeWrapper(columnName, TreeContentView.COLUMN, column));
          columnsItem.getChildren().add(columnItem);
        }

        if (!onlyMetaData) {
          rowsItem = new TreeItem<>();
          rowsItem.valueProperty().bind(I18n.createTreeAtributeWrapperBinding("archive.tree.view.node.rows", TreeContentView.ROWS, table, numberOfRows));

          tableItem.getChildren().add(rowsItem);
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
    if (!root.valueProperty().getValue().getType().getViewTitle().equals("tableContainer.title.tables")) {
      for (TreeItem<TreeAttributeWrapper> child : root.getChildren()) {
        child.setExpanded(true);
        expandChildren(child);
      }
    }
  }

  protected void setListeners() {
    MultipleSelectionModel<TreeItem<TreeAttributeWrapper>> selection = treeView.getSelectionModel();
    selection.selectedItemProperty().addListener(((observable, oldValue, newValue) -> updateTableContainerContent(newValue.getValue())));
    tableSearchButton.setOnAction(event -> {
      if (model.getCurrentTableSearchButton() != null && tableSearchButton.equals(model.getCurrentTableSearchButton().button()) && model.getCurrentTableSearchButton().active()) {
        model.setCurrentTableSearchButton(tableSearchButton, false);
        tableSearchButton.setStyle("-fx-font-weight: normal;");
        model.getCurrentTableSearchBase().tableView().setItems(FXCollections.observableArrayList(model.getCurrentTableSearchBase().rows()));
      } else {
        model.setCurrentTableSearchButton(tableSearchButton, false);
        stage.openDialog(View.SEARCH_TABLE_DIALOG);
      }
    });
    metaSearchButton.setOnAction(event -> stage.openDialog(View.SEARCH_METADATA_DIALOG));
  }

  protected void updateTableContainerContent(TreeAttributeWrapper wrapper) {
    try {
      FXMLLoader loader = new FXMLLoader(SiardApplication.class.getResource(wrapper.getType().getViewName()));
      Node container = loader.load();
      tableContainerContent.getChildren().setAll(container);
      loader.<TreePresenter>getController().init(this.controller, model, this.stage, wrapper);
      tableSearchButton.setVisible(wrapper.getType().getHasTableSearch());
      this.titleTableContainer.textProperty().bind(I18n.createStringBinding(wrapper.getType().getViewTitle()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public AnchorPane getTableContainerContent() {
    return tableContainerContent;
  }

  @Override
  public void visit(String archiveName, boolean onlyMetaData, List<DatabaseSchema> schemas) {
    this.archiveName.setValue(archiveName);
    this.onlyMetaData = onlyMetaData;
    this.schemas = schemas;
  }

  @Override
  public void visit(String schemaName, String schemaDescription, List<DatabaseTable> tables) {
    this.schemaName = schemaName;
    this.tables = tables;
  }

  @Override
  public void visit(String tableName, String numberOfRows, List<DatabaseColumn> columns, List<DatabaseRow> rows) {
    this.tableName = tableName;
    this.columns = columns;
    this.numberOfRows = numberOfRows;
  }

  @Override
  public void visit(String columnName) {
    this.columnName = columnName;
  }

  @Override
  public void visit(SiardArchive archive) {}

}
