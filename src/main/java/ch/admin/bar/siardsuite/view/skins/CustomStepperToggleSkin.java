package ch.admin.bar.siardsuite.view.skins;

import ch.admin.bar.siardsuite.util.I18n;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.MFXValidator;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class CustomStepperToggleSkin extends SkinBase<MFXStepperToggle> {
  private final StackPane container;
  private final Circle circle;
  private final MFXTextField label;


  public CustomStepperToggleSkin(MFXStepperToggle stepperToggle) {
    super(stepperToggle);
    this.circle = new Circle(0.0, Color.TRANSPARENT);
    this.circle.setId("circle");
    this.circle.radiusProperty().bind(stepperToggle.sizeProperty());
    this.container = new StackPane(new Node[]{this.circle, stepperToggle.getIcon()});
    this.label = MFXTextField.asLabel();
    this.label.textProperty().bind(I18n.createStringBinding(stepperToggle.getText()));
    this.label.setManaged(false);
    this.getChildren().addAll(new Node[]{this.container, this.label});
    this.setListeners();
  }

  private void setListeners() {
    MFXStepperToggle stepperToggle = (MFXStepperToggle)this.getSkinnable();
    MFXValidator validator = stepperToggle.getValidator();


    this.label.visibleProperty().bind(stepperToggle.textProperty().isEmpty().not());
  }


  protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    return topInset + this.container.prefWidth(-1.0) + ((MFXStepperToggle)this.getSkinnable()).getLabelTextGap() * 2.0 + this.label.getHeight() * 2.0 + bottomInset;
  }

  protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
    return leftInset + Math.max(this.circle.getRadius() * 2.0, this.label.getWidth()) + rightInset;
  }

  protected void layoutChildren(double x, double y, double w, double h) {
    super.layoutChildren(x, y, w, h);
    MFXStepperToggle stepperToggle = (MFXStepperToggle)this.getSkinnable();
    double lw = this.snapSizeX(this.label.prefWidth(-1.0));
    double lh = this.snapSizeY(this.label.prefHeight(-1.0));
    double lx = 0.0;
    double ly = this.snapPositionY(this.circle.getBoundsInParent().getCenterY() - lh / 2.0);
    // Customize text right
      this.label.setTranslateX(0.0);
      lx = this.snapPositionY(this.circle.getBoundsInParent().getMaxX() + stepperToggle.getLabelTextGap());
      this.label.resizeRelocate(lx, ly, lw, lh);


    double ix = this.snapPositionX(this.circle.getBoundsInParent().getMaxX());
    double iy = this.snapPositionY(this.circle.getBoundsInParent().getMinY() - 6.0);
  }
}

