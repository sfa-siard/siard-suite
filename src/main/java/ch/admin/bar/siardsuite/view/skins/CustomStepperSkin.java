package ch.admin.bar.siardsuite.view.skins;

import ch.admin.bar.siardsuite.util.I18n;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import io.github.palexdev.materialfx.effects.ripple.RippleClipType;
import io.github.palexdev.materialfx.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.factories.RippleClipTypeFactory;



import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

public class CustomStepperSkin extends SkinBase<MFXStepper> {
  private final StackPane contentPane;
  private final HBox stepperBar;
  private final HBox buttonsBox;
  private final MFXButton nextButton;
  private final MFXButton previousButton;
  private final Group progressBarGroup;
  private final double height = 2.0;
  private final Rectangle bar;
  private final Rectangle track = this.buildRectangle("track");
  private Timeline progressAnimation;
  private boolean buttonWasPressed = false;

  public CustomStepperSkin(MFXStepper stepper) {
    super(stepper);
    this.track.setHeight(2.0);
    this.track.widthProperty().bind(stepper.widthProperty());
    this.bar = this.buildRectangle("bar");
    this.bar.setHeight(2.0);
    this.progressBarGroup = new Group(new Node[]{this.track, this.bar});
    this.progressBarGroup.setManaged(false);
    this.progressAnimation = new Timeline();
    this.progressAnimation.setOnFinished((event) -> {
      this.buttonWasPressed = false;
    });
    this.stepperBar = new HBox(new Node[]{this.progressBarGroup});
    this.stepperBar.spacingProperty().bind(stepper.spacingProperty());
    this.stepperBar.alignmentProperty().bind(stepper.alignmentProperty());
    this.stepperBar.getChildren().addAll(stepper.getStepperToggles());
    this.stepperBar.setMaxHeight(53.0);
    this.stepperBar.setPrefSize(-1.0, 53.0);
    this.progressBarGroup.layoutYProperty().bind(Bindings.createDoubleBinding(() -> {
      return this.snapPositionY(this.stepperBar.getHeight() / 2.0 - 3.5);
    }, new Observable[]{this.stepperBar.heightProperty()}));
    this.nextButton = new MFXButton();
    this.nextButton.textProperty().bind(I18n.createStringBinding("button.next"));
    this.nextButton.getStyleClass().setAll("button", "primary");
    this.nextButton.setManaged(false);
    this.previousButton = new MFXButton();
    this.previousButton.textProperty().bind(I18n.createStringBinding("button.back"));
    this.previousButton.getStyleClass().setAll("button", "secondary");
    this.previousButton.setManaged(false);
    this.buttonsBox = new HBox(20.0, new Node[]{this.previousButton, this.nextButton});
    this.buttonsBox.getStyleClass().setAll(new String[]{"btn-box"});
    this.buttonsBox.setAlignment(Pos.CENTER_LEFT);
    this.buttonsBox.setMinHeight(70.0);
    this.contentPane = new StackPane();
    this.contentPane.getStyleClass().setAll(new String[]{"content-pane"});
    BorderPane container = new BorderPane();
    container.getStylesheets().setAll(new String[]{stepper.getUserAgentStylesheet()});
    container.setTop(this.stepperBar);
    container.setCenter(this.contentPane);
    container.setBottom(this.buttonsBox);
    this.getChildren().add(container);
    this.setListeners();
  }

  private void setListeners() {
    MFXStepper stepper = (MFXStepper) this.getSkinnable();
    stepper.addEventFilter(MFXStepper.MFXStepperEvent.FORCE_LAYOUT_UPDATE_EVENT, (event) -> {
      stepper.requestLayout();
      this.computeProgress();
    });
    stepper.addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> {
      stepper.requestFocus();
    });
    stepper.addEventFilter(MFXStepperToggle.MFXStepperToggleEvent.STATE_CHANGED, (event) -> {
      this.computeProgress();
    });
    stepper.getStepperToggles().addListener((ListChangeListener<? super MFXStepperToggle>) (invalidated) -> {
      stepper.reset();
      this.stepperBar.getChildren().setAll(stepper.getStepperToggles());
      this.stepperBar.getChildren().add(0, this.progressBarGroup);
      stepper.next();
      AnimationUtils.PauseBuilder.build().setDuration(250.0).setOnFinished((event) -> {
        stepper.requestLayout();
      }).getAnimation().play();
    });
    stepper.currentContentProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        this.contentPane.getChildren().setAll(new Node[]{newValue});
      } else {
        this.contentPane.getChildren().clear();
      }

    });
    stepper.lastToggleProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        this.computeProgress();
      }

    });
    this.nextButton.setOnAction((event) -> {
      this.buttonWasPressed = true;
      stepper.next();
    });
    this.previousButton.setOnAction((event) -> {
      this.buttonWasPressed = true;
      stepper.previous();
    });
    this.manageScene();
  }

  private void manageScene() {
    MFXStepper stepper = (MFXStepper) this.getSkinnable();
    Scene scene = stepper.getScene();
    if (scene != null) {
      stepper.next();
    }

    NodeUtils.waitForScene(stepper, () -> {
      if (stepper.getCurrentIndex() == -1) {
        stepper.next();
      }

    }, true, false);
    stepper.needsLayoutProperty().addListener((observable, oldValue, newValue) -> {
      if (!this.buttonWasPressed) {
        stepper.requestLayout();
      }

      this.computeProgress();
    });
  }

  private void computeProgress() {
    MFXStepper stepper = (MFXStepper) this.getSkinnable();
    if (stepper.isLastToggle()) {
      this.updateProgressBar(stepper.getWidth());
    } else {
      MFXStepperToggle stepperToggle = stepper.getCurrentStepperNode();
      if (stepperToggle != null) {
        Bounds bounds = stepperToggle.getGraphicBounds();
        if (bounds != null) {
          double minX = this.snapSizeX(stepperToggle.localToParent(bounds).getMinX());
          this.updateProgressBar(minX + 10.0);
        }
      } else {
        this.updateProgressBar(0.0);
      }

    }
  }

  private void updateProgressBar(double width) {
    MFXStepper stepper = (MFXStepper) this.getSkinnable();
    if (stepper.isAnimated() && this.buttonWasPressed) {
      KeyFrame kf = new KeyFrame(Duration.millis(stepper.getAnimationDuration()), new KeyValue[]{new KeyValue(this.bar.widthProperty(), width, MFXAnimationFactory.INTERPOLATOR_V2)});
      this.progressAnimation.getKeyFrames().setAll(new KeyFrame[]{kf});
      this.progressAnimation.playFromStart();
    } else {
      this.bar.setWidth(width);
      this.buttonWasPressed = false;
    }
  }

  protected Rectangle buildRectangle(String styleClass) {
    Rectangle rectangle = new Rectangle();
    rectangle.getStyleClass().setAll(new String[]{styleClass});
    rectangle.setStroke(Color.TRANSPARENT);
    rectangle.setStrokeLineCap(StrokeLineCap.ROUND);
    rectangle.setStrokeLineJoin(StrokeLineJoin.ROUND);
    rectangle.setStrokeType(StrokeType.INSIDE);
    rectangle.setStrokeWidth(0.0);
    return rectangle;
  }

  protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
    return Math.max(super.computeMinWidth(height, topInset, leftInset, bottomInset, rightInset) + ((MFXStepper) this.getSkinnable()).getExtraSpacing() * 2.0, 300.0);
  }

  protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    return topInset + this.stepperBar.getHeight() + this.buttonsBox.getHeight() + bottomInset;
  }

  protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
    return ((MFXStepper) this.getSkinnable()).prefWidth(height);
  }

  protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    return ((MFXStepper) this.getSkinnable()).prefHeight(width);
  }

  public void dispose() {
    super.dispose();
    if (this.progressAnimation.getStatus() != Animation.Status.STOPPED) {
      this.progressAnimation.stop();
    }

    this.progressAnimation = null;
  }

  protected void layoutChildren(double x, double y, double w, double h) {
    super.layoutChildren(x, y, w, h);
    this.progressBarGroup.resize(w, 2.0);
    double bw = 148;
    double bh = 42.0;
    double pbx = this.snapPositionX(15.0);
    double nbx = this.snapPositionX(bw + 30.0);
    double by = this.snapPositionY(this.buttonsBox.getHeight() / 2.0 - bh / 2.0);
    this.previousButton.resizeRelocate(pbx, by, bw, bh);
    this.nextButton.resizeRelocate(nbx, by, bw, bh);
  }
}
