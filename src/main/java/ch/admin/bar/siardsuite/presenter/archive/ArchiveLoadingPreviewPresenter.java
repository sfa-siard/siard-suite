package ch.admin.bar.siardsuite.presenter.archive;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.StepperButtonBox;
import ch.admin.bar.siardsuite.model.Model;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.presenter.StepperPresenter;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.view.RootStage;
import io.github.palexdev.materialfx.controls.MFXStepper;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

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
  private StepperButtonBox buttonsBox;


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
    addLoaderTransition();

    this.buttonsBox = new StepperButtonBox();
    this.borderPane.setBottom(buttonsBox);
    this.setListeners(stepper);
  }

  private void addLoaderTransition() {
    RotateTransition rt = new RotateTransition(Duration.millis(1000), this.loader);
    rt.setByAngle(360);
    rt.setCycleCount(Animation.INDEFINITE);
    rt.setInterpolator(Interpolator.LINEAR);
    rt.play();
  }

  private void setListeners(MFXStepper stepper) {

    this.buttonsBox.next().setOnAction((event) -> {
      stepper.next();
      this.stage.setHeight(950);
    });
    this.buttonsBox.previous().setOnAction((event) -> {
      stepper.previous();
      this.stage.setHeight(1080.00);
    });
    this.buttonsBox.cancel().setOnAction((event) -> stage.openDialog(View.ARCHIVE_ABORT_DIALOG.getName()));
  }
}
