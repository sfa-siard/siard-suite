package ch.admin.bar.siardsuite.view.skins;

import ch.admin.bar.siardsuite.util.I18n;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.TextUtils;
import io.github.palexdev.materialfx.validation.MFXValidator;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;

public class CustomStepperToggleSkin extends SkinBase<MFXStepperToggle> {
  private final HBox container;
  private final MFXTextField label;
  private final Button icon;

  public CustomStepperToggleSkin(MFXStepperToggle stepperToggle) {
    super(stepperToggle);

    this.label = MFXTextField.asLabel();
    this.label.textProperty().bind(I18n.createStringBinding(stepperToggle.getText()));
    this.label.setManaged(false);
    this.icon = (Button) stepperToggle.getIcon();
    this.icon.setMaxWidth(22);
    this.container = new HBox(stepperToggle.getIcon(), this.label);
    this.container.getStyleClass().setAll("custom-stepper-toggle");

    this.getChildren().addAll(new Node[]{this.container});
    this.setListeners();
  }

  private void setListeners() {
    MFXStepperToggle stepperToggle = (MFXStepperToggle) this.getSkinnable();
    MFXValidator validator = stepperToggle.getValidator();

    this.label.visibleProperty().bind(stepperToggle.textProperty().isEmpty().not());
  }

  protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    return topInset + this.container.prefHeight(0.0) + bottomInset;
  }

  protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
    return leftInset + this.icon.getMaxWidth() + TextUtils.computeTextWidth(this.label.getFont(), this.label.getText()) + ((MFXStepperToggle) this.getSkinnable()).getLabelTextGap() * 2.0 + rightInset;
  }

  protected void layoutChildren(double x, double y, double w, double h) {
    super.layoutChildren(x, y, w, h);
    MFXStepperToggle stepperToggle = (MFXStepperToggle) this.getSkinnable();
    double lw = this.snapSizeX(w - this.icon.getMaxWidth() - ((MFXStepperToggle) this.getSkinnable()).getLabelTextGap());
    double lh = this.snapSizeY(h);
    double lx = 0.0;
    this.label.setTranslateX(0.0);
    lx = this.snapPositionX(this.icon.getMaxWidth() + stepperToggle.getLabelTextGap());
    this.label.resizeRelocate(lx, -0.1, lw, lh);
  }
}


