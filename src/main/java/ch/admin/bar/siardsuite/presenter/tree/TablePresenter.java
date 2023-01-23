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

    @Override
  public void init(Controller controller, Model model, RootStage stage, TreeAttributeWrapper wrapper) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;


    this.nameLabel.textProperty().bind(I18n.createStringBinding(wrapper.getType().getNameLabel()));
    this.infoLabel.textProperty().bind(I18n.createStringBinding(wrapper.getType().getInfoLabel()));

    model.provideDatabaseArchiveProperties(this, wrapper.getDatabaseObject());
    model.populate(tableView, wrapper.getDatabaseObject(), wrapper.getType());
    tableView.setColumnResizePolicy((param -> true));
    tableView.resize(1400, 400);
    model.setCurrentTableSearchBase(tableView, new LinkedHashSet<>(tableView.getItems()));
    if (model.getCurrentTableSearchButton() != null && model.getCurrentTableSearchButton().button() != null) {
      model.setCurrentTableSearchButton(model.getCurrentTableSearchButton().button(), false);
      model.getCurrentTableSearchButton().button().setStyle("-fx-font-weight: normal;");
    }
  }

  @Override
  public void visit(String archiveName, boolean onlyMetaData, List<DatabaseSchema> schemas) {}

  @Override
  public void visit(String schemaName, String schemaDescription, List<DatabaseTable> tables) {
    nameText.setText(schemaName);
    infoText.setText(schemaDescription == null || schemaDescription.isEmpty() ? "(...)" : schemaDescription);
  }

  @Override
  public void visit(String tableName, String numberOfRows, List<DatabaseColumn> columns, List<DatabaseRow> rows) {
    nameText.setText(tableName);
    infoText.setText(numberOfRows);
  }

  @Override
  public void visit(String columnName) {}

  @Override
  public void visit(SiardArchive archive) {}

}
