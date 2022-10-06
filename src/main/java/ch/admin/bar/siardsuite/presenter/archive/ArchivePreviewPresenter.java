package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.component.StepperButtonBox;
import ch.admin.bar.siardsuite.model.*;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.presenter.tree.TreePresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;


public class ArchivePreviewPresenter extends StepperPresenter {
  @FXML
  public Label title;
  @FXML
  public Text text;
  @FXML
  public VBox leftVBox;
  @FXML
  public BorderPane borderPane;
  @FXML
  public TreeView treeView;
  @FXML
  public Label titleTableContainer;
  @FXML
  public MFXButton tableSearchButton;
  @FXML
  public MFXButton metaSearchButton;

  @FXML
  public VBox tableContainerContent;

  @FXML
  private StepperButtonBox buttonsBox;

  private final Node db = new ImageView(new Image(String.valueOf(SiardApplication.class.getResource("icons/server.png")), 16.0, 16.0, true, false));

  @Override
  public void init(Controller controller, Model model, RootStage stage) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;
  }

  @Override
  public void init(Controller controller, Model model, RootStage stage, MFXStepper stepper) {
    this.model = model;
    this.controller = controller;
    this.stage = stage;

    this.title.textProperty().bind(I18n.createStringBinding("archivePreview.view.title"));
    this.text.textProperty().bind(I18n.createStringBinding("archivePreview.view.text"));
    this.metaSearchButton.textProperty().bind(I18n.createStringBinding("tableContainer.metaSearchButton"));
    this.tableSearchButton.textProperty().bind(I18n.createStringBinding("tableContainer.tableSearchButton"));

    initTreeView();

    this.buttonsBox = new StepperButtonBox().make(StepperButtonBox.DEFAULT);
    this.borderPane.setBottom(buttonsBox);
    this.setListeners(stepper);
  }

  private void initTreeView() {
    int i = 0;
    var root = new TreeItem(new TreeAttributeWrapper("dbname.siard", i, TreeContentView.ROOT), db);
    var rootSchema = new TreeItem(new TreeAttributeWrapper("Schemas (1)", i++, TreeContentView.SCHEMAS));
    var root2Schema = new TreeItem(new TreeAttributeWrapper("Schemas (2)", i++, TreeContentView.SCHEMAS));
    var schema = new TreeItem(new TreeAttributeWrapper("scott", i++, TreeContentView.TABLE));
    var schema2 = new TreeItem(new TreeAttributeWrapper("noscott", i++, TreeContentView.TABLE));
    var tableNode2 = new TreeItem(new TreeAttributeWrapper("Tabellen (4)", i++, TreeContentView.TABLE));
    var table = new TreeItem(new TreeAttributeWrapper("BONUS", i++, TreeContentView.TABLE));
    var column = new TreeItem(new TreeAttributeWrapper("Spalten (3)", i++, TreeContentView.COLUMNS));
    column.getChildren().add(new TreeItem(new TreeAttributeWrapper("DEPTNO", i++, TreeContentView.COLUMN)));
    table.getChildren().add(column);
    ObservableList<TreeItem<String>> tables2 = FXCollections.observableArrayList(
            table,
            new TreeItem(new TreeAttributeWrapper("DEPT", i++, TreeContentView.TABLE)),
            new TreeItem(new TreeAttributeWrapper("EMP", i++, TreeContentView.TABLE)),
            new TreeItem(new TreeAttributeWrapper("SALGARDE", i++, TreeContentView.TABLE)));
    tableNode2.getChildren().addAll(tables2);
    schema2.getChildren().add(tableNode2);
    rootSchema.getChildren().add(schema);
    root2Schema.getChildren().add(schema2);
    root.getChildren().add(rootSchema);
    root.getChildren().add(root2Schema);
    treeView.setRoot(root);


    expandChildren(root);
//    updateTableContainerContent((TreeAttributeWrapper) root.getValue());
  }

  private void expandChildren(TreeItem<String> root) {
    root.setExpanded(true);
    for (TreeItem<String> child : root.getChildren()) {
      child.setExpanded(true);
      if (child.getChildren().isEmpty()) {
      } else {
        expandChildren(child);
      }
    }
  }

  private void setListeners(MFXStepper stepper) {

    MultipleSelectionModel<TreeItem> selection = treeView.getSelectionModel();
    selection.selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
      updateTableContainerContent((TreeAttributeWrapper) newValue.getValue());
    }));


    this.buttonsBox.next().setOnAction((event) -> {
      stepper.next();
    });
    this.buttonsBox.previous().setOnAction((event) -> {
      stepper.previous();
      stepper.previous();
      this.stage.setHeight(950);
    });
    this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG.getName()));
  }

  private void updateTableContainerContent(TreeAttributeWrapper wrapper) {
    try {
      FXMLLoader loader = new FXMLLoader(SiardApplication.class.getResource(wrapper.type().getName()));
      Node container = loader.load();
      tableContainerContent.getChildren().setAll(container);

      loader.<TreePresenter>getController().init(this.controller, new TreeContentViewModel(wrapper.type().getTitle(),
              this.model), this.stage);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
