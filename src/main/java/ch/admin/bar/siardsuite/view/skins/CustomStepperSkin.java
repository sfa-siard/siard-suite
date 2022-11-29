package ch.admin.bar.siardsuite.view.skins;

import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.TextUtils;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
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
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class CustomStepperSkin extends SkinBase<MFXStepper> {
  private final StackPane contentPane;
  private final HBox stepperBar;
  private final Group progressBarGroup;
  private final Rectangle bar;
  private final Rectangle track = this.buildRectangle("track");
  private boolean buttonWasPressed = false;

  public CustomStepperSkin(MFXStepper stepper, Stage stage) {
    super(stepper);
    this.track.setHeight(2.0);
    this.bar = this.buildRectangle("bar");
    this.bar.setHeight(2.0);
    this.progressBarGroup = new Group(this.track, this.bar);
    this.progressBarGroup.setManaged(false);
    this.stepperBar = new HBox(this.progressBarGroup);
    this.stepperBar.setSpacing(40.0);
    this.stepperBar.alignmentProperty().bind(stepper.alignmentProperty());
    this.stepperBar.getChildren().addAll(stepper.getStepperToggles().stream().filter(Node::isVisible).toList());
    this.stepperBar.getStyleClass().setAll("stepper-bar");
    this.stepperBar.setMaxHeight(52.0);
    this.stepperBar.setMinHeight(52.0);
    layoutWidth(stepper);
    this.stepperBar.setMaxSize(stepper.getPrefWidth(), 52.0);
    this.progressBarGroup.layoutYProperty().bind(Bindings.createDoubleBinding(() ->
            this.snapPositionY(this.stepperBar.getHeight() / 2.0 - (this.bar.getHeight() / 2)),
            this.stepperBar.heightProperty()));
    this.contentPane = new StackPane();
    this.contentPane.getStyleClass().setAll("content-pane");
    BorderPane container = new BorderPane();
    container.getStylesheets().setAll(stepper.getUserAgentStylesheet());
    container.setTop(this.stepperBar);
    container.setCenter(this.contentPane);
    this.getChildren().add(container);
    this.setListeners(stage);
  }

  private void layoutWidth(MFXStepper stepper) {
    double toggleWidth = stepper.getStepperToggles()
            .stream()
            .filter(Node::isVisible)
            .mapToDouble(
                    t -> this.stepperBar.spacingProperty().get() + (TextUtils.computeTextWidth(Font.font("Roboto Regular", 13.0), I18n.get(t.textProperty().get())) + (t.getLabelTextGap() + 22))
            ).sum();
    this.bar.setWidth(toggleWidth);
    this.track.setWidth(toggleWidth);
  }

  private void setListeners(Stage stage) {
    MFXStepper stepper = this.getSkinnable();

    stage.addEventHandler(SiardEvent.UPDATE_LANGUAGE_EVENT, event -> {
      stepper.requestLayout();
      layoutWidth(stepper);
    });

    stepper.addEventFilter(MFXStepper.MFXStepperEvent.FORCE_LAYOUT_UPDATE_EVENT, (event) -> {
      stepper.requestLayout();
      this.computeProgress();
    });
    stepper.addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> stepper.requestFocus());
    stepper.addEventFilter(MFXStepperToggle.MFXStepperToggleEvent.STATE_CHANGED, (event) -> this.computeProgress());
    stepper.getStepperToggles().addListener((ListChangeListener<? super MFXStepperToggle>) (invalidated) -> {
      stepper.reset();
      this.stepperBar.getChildren().setAll(stepper.getStepperToggles());
      this.stepperBar.getChildren().add(0, this.progressBarGroup);
      stepper.next();
      AnimationUtils.PauseBuilder.build().setDuration(250.0).setOnFinished((event) -> stepper.requestLayout()).getAnimation().play();
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

    this.manageScene();
  }

  private void manageScene() {
    MFXStepper stepper = this.getSkinnable();
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
    MFXStepper stepper = this.getSkinnable();
    if (stepper.isLastToggle()) {
    } else {
      MFXStepperToggle stepperToggle = stepper.getCurrentStepperNode();
      if (stepperToggle != null) {
        Bounds bounds = stepperToggle.getGraphicBounds();
        if (bounds != null) {
          double minX = this.snapSizeX(stepperToggle.localToParent(bounds).getMinX());
        }
      }
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
    return Math.max(super.computeMinWidth(height, topInset, leftInset, bottomInset, rightInset) + this.getSkinnable().getExtraSpacing() * 2.0, 300.0);
  }

  protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    return topInset + this.stepperBar.getHeight() + bottomInset;
  }

  protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
    return this.getSkinnable().prefWidth(height);
  }

  protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    return this.getSkinnable().prefHeight(width);
  }

  public void dispose() {
    super.dispose();
  }

  protected void layoutChildren(double x, double y, double w, double h) {
    super.layoutChildren(x, y, w, h);
    this.progressBarGroup.resize(w, 2.0);
  }
}
