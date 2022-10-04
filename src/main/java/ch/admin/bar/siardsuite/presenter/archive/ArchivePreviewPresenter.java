package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.component.StepperButtonBox;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXTreeItem;
import io.github.palexdev.materialfx.controls.MFXTreeView;
import io.github.palexdev.materialfx.controls.base.AbstractMFXTreeItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
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

import java.util.ArrayList;
import java.util.List;


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
  public Label labelFormat;
  public Label textFormat;
  public Label labelDb;
  public Label textDb;

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

    this.titleTableContainer.textProperty().bind((I18n.createStringBinding("tableContainer.title.siardFile")));
    this.metaSearchButton.textProperty().bind(I18n.createStringBinding("tableContainer.metaSearchButton"));
    this.tableSearchButton.textProperty().bind(I18n.createStringBinding("tableContainer.tableSearchButton"));


    TreeItem root2 = new TreeItem("dbname.siard", db );
    TreeItem root2Schema = new TreeItem("Schemas (1)");
    TreeItem schema2 = new TreeItem("scott");
    TreeItem tableNode2 = new TreeItem("Tabellen (4)");
    ObservableList<TreeItem> tables2 = FXCollections.observableArrayList(new TreeItem("BONUS"), new TreeItem("DEPT"), new TreeItem("EMP"), new TreeItem( "SALGARDE"));
    tableNode2.getChildren().addAll(tables2);
    schema2.getChildren().add(tableNode2);
    root2Schema.getChildren().add(schema2);
    root2.getChildren().add(root2Schema);
    treeView.setRoot(root2);

    this.labelFormat.textProperty().bind(I18n.createStringBinding("tableContainer.label.format"));
    this.labelDb.textProperty().bind(I18n.createStringBinding("tableContainer.label.format"));
    this.textFormat.setText("2.1");
    this.textDb.setText("2.1");

    this.buttonsBox = new StepperButtonBox().make(StepperButtonBox.DEFAULT);
    this.borderPane.setBottom(buttonsBox);
    this.setListeners(stepper);
  }

  private void setListeners(MFXStepper stepper) {

    MultipleSelectionModel<TreeItem> selection = treeView.getSelectionModel();
    selection.selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
      System.out.println("should display selection");
    }));

    this.buttonsBox.next().setOnAction((event) -> {
        stepper.next();
    });
    this.buttonsBox.previous().setOnAction((event) -> {
      stepper.previous();
      this.stage.setHeight(950);
    });
    this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG.getName()));
  }
}
