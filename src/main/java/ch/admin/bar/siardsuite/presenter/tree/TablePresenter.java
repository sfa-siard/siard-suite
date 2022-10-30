package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.util.Map;

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
  public TableView<Map> tableView = new TableView<>();

  @Override
  public void init(Controller controller, Model model, RootStage stage, TreeAttributeWrapper wrapper) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;

    this.titleTableContainer.textProperty().bind(I18n.createStringBinding(wrapper.getType().getTitle()));
    this.label1.textProperty().bind(I18n.createStringBinding("tableContainer.labelSchema"));
    this.label2.textProperty().bind(I18n.createStringBinding("tableContainer.labelDescSchema"));

    this.text1.setText("Test");
    this.text2.setText("-");

    model.getArchive().populate(tableView, wrapper.getDatabaseObject(), wrapper.getType());
  }

}
