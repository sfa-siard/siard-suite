package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.component.StepperButtonBox;
import ch.admin.bar.siardsuite.model.DataTable;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ArchiveLoadingPreviewPresenter extends StepperPresenter {
  @FXML
  public Label title;
  @FXML
  public Text text;
  @FXML
  public VBox leftVBox;
  @FXML
  public BorderPane borderPane;
  @FXML
  public ImageView loader;
  @FXML
  public Label subtitle;
  @FXML
  public VBox scrollBox;
  @FXML
  private StepperButtonBox buttonsBox;

  List<DataTable> data;

  private final Image loading = new Image(String.valueOf(SiardApplication.class.getResource("icons/loading.png")));
  private  Image ok = new Image(String.valueOf(SiardApplication.class.getResource("icons/ok_check.png")));

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

    this.title.textProperty().bind(I18n.createStringBinding("archiveLoadingPreview.view.title"));
    this.text.textProperty().bind(I18n.createStringBinding("archiveLoadingPreview.view.text"));
    this.subtitle.textProperty().bind(I18n.createStringBinding("archiveLoadingPreview.view.subtitle"));
    this.loader.setImage(loading);
    addLoaderTransition(this.loader);

    this.buttonsBox = new StepperButtonBox().make(StepperButtonBox.CANCEL);
    this.borderPane.setBottom(buttonsBox);
    this.setListeners(stepper);
  }

  private void addTableData(String tableName, Integer pos) {
    Label label = new Label(tableName);
    label.getStyleClass().add("view-text");
    label.setContentDisplay(ContentDisplay.RIGHT);
    ImageView imageView = getImageView(pos, loading);
    addLoaderTransition(imageView);
    label.setGraphic(imageView);
    scrollBox.getChildren().add(label);
  }

  private ImageView getImageView(Integer pos, Image image) {
    ImageView imageView = new ImageView(image);
    imageView.setFitHeight(14.0);
    imageView.setFitWidth(14.0);
    imageView.getStyleClass().addAll("loading-icon", "icon-button");
    imageView.setId("dataLoader" + pos);
    return imageView;
  }

  private void addLoaderTransition(ImageView loader) {
    RotateTransition rt = new RotateTransition(Duration.millis(1000), loader);
    rt.setByAngle(360);
    rt.setCycleCount(Animation.INDEFINITE);
    rt.setInterpolator(Interpolator.LINEAR);
    rt.play();
  }

  private void setListeners(MFXStepper stepper) {
    stepper.addEventHandler(SiardEvent.UPDATE_STEPPER_DBLOAD_EVENT, event -> {
      scrollBox.getChildren().clear();
      controller.loadDatabase();

      model.getDatabaseLoadService().valueProperty().addListener((o, oldValue, newValue)  ->  {
        AtomicInteger pos = new AtomicInteger();
        newValue.forEach(t -> addTableData(t.table(), pos.getAndIncrement()));
      });
      model.getDatabaseLoadService().progressProperty().addListener((o, oldValue, newValue) -> {
        double pos = newValue.doubleValue() * (scrollBox.getChildren().size()-1);
        if (pos >= 1) {
          Label label = (Label) scrollBox.getChildren().get((int) pos);
          label.setGraphic(getImageView(newValue.intValue(), ok));
        }
      });

      model.getDatabaseLoadService().setOnSucceeded(e -> {
        controller.closeDbConnection();
        stepper.next();
        this.stage.setHeight(950);
      });

      model.getDatabaseLoadService().setOnFailed(e -> {
        controller.closeDbConnection();
        stepper.previous();
        this.stage.setHeight(1080.00);
      });

    });


    this.buttonsBox.previous().setOnAction((event) -> {
      controller.closeDbConnection();
      stepper.previous();
      this.data.clear();
      this.stage.setHeight(1080.00);
    });
    this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG.getName()));
  }
}