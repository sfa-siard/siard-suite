package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.TreeContentViewModel;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TablePresenter extends TreePresenter {
  @FXML
  public Label titleTableContainer;
  @FXML
  public Label label1;
  @FXML
  public Label text1;
  @FXML
  public Label label2;
  @FXML
  public Label text2;
  @FXML
  public TableView tableView;

  @Override
  public void init(Controller controller, TreeContentViewModel model, RootStage stage) {
    this.model = model.getModel();
    this.controller = controller;
    this.stage = stage;

    this.titleTableContainer.textProperty().bind(I18n.createStringBinding(model.getTitle()));
    this.label1.textProperty().bind(I18n.createStringBinding("tableContainer.labelSchema"));
    this.label2.textProperty().bind(I18n.createStringBinding("tableContainer.labelDescSchema"));

    this.text1.setText("SCOTT");
    this.text2.setText("-");

    initTable();

  }

  private void initTable() {
    TableColumn<String, String> col1 = new TableColumn<>();
    TableColumn<String, String> col2 = new TableColumn<>();
    TableColumn<String, String> col3 = new TableColumn<>();
    TableColumn<String, String> col4 = new TableColumn<>();
    col1.textProperty().bind(I18n.createStringBinding("tableContainer.table.column.row"));
    col2.textProperty().bind(I18n.createStringBinding("tableContainer.table.column.tablename"));
    col3.textProperty().bind(I18n.createStringBinding("tableContainer.table.column.columns"));
    col4.textProperty().bind(I18n.createStringBinding("tableContainer.table.column.data"));


    tableView.getColumns().addAll(col1, col2, col3, col4);


  }
}
