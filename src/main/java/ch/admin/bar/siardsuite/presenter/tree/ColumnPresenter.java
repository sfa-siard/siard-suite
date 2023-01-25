package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ColumnPresenter extends TreePresenter {

  @FXML
  public VBox container;
  @FXML
  public VBox labels;
  @FXML
  public VBox texts;


  @Override
  public void init(Controller controller, Model model, RootStage stage, TreeAttributeWrapper wrapper) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;

    initLabels();

    model.populate(texts, wrapper.getDatabaseObject(), wrapper.getType());
  }

  private void initLabels() {
    for (Node child : this.labels.getChildren()) {
      if (child.getId().contains("label")) {
        I18n.bind(((Label) child).textProperty(),"tableContainer.column." + child.getId());
        child.getStyleClass().add("table-container-label-small");
      }
    }
  }

}
