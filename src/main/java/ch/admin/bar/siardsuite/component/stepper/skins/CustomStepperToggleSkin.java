package ch.admin.bar.siardsuite.component.stepper.skins;

import ch.admin.bar.siardsuite.util.I18n;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import io.github.palexdev.materialfx.utils.TextUtils;
import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class CustomStepperToggleSkin extends SkinBase<MFXStepperToggle> {
  private final HBox container;
  private final TextField label;
  private final Button icon;

  private Boolean visible;

  public CustomStepperToggleSkin(MFXStepperToggle stepperToggle, Boolean visible) {
    super(stepperToggle);
      this.visible = visible;
      this.label = new TextField();
      this.label.setEditable(false);
      this.icon = (Button) stepperToggle.getIcon();

      if (visible) {
        this.container = new HBox(stepperToggle.getIcon(), this.label);
        I18n.bind(this.label.textProperty(), stepperToggle.getText());
        this.label.setManaged(false);
        this.icon.setMaxWidth(22);
        this.container.getStyleClass().setAll("custom-stepper-toggle");
        this.container.setLayoutY(0);
        this.container.setMinHeight(52.0);
        this.label.setLayoutY(0);
        this.label.setMinHeight(52.0);
        this.getChildren().addAll(this.container);
        this.setListeners();
      } else {
        this.icon.setMaxWidth(0);
        this.container = new HBox();
      }
  }

  public void refresh() {
    layoutChildren(0,0,0,0);
  }

  private void setListeners() {
    MFXStepperToggle stepperToggle = this.getSkinnable();

    this.label.visibleProperty().bind(stepperToggle.textProperty().isEmpty().not());
  }

  protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
    return topInset + this.container.prefHeight(0.0) + bottomInset;
  }

  protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
    if (this.visible) {
      return leftInset + this.icon.getMaxWidth() + TextUtils.computeTextWidth(this.label.getFont(), this.label.getText()) + this.getSkinnable().getLabelTextGap() * 2.0 + rightInset;
    } else return 0;
  }

  protected void layoutChildren(double x, double y, double w, double h) {
    super.layoutChildren(x, y, w, h);
    MFXStepperToggle stepperToggle = this.getSkinnable();
    double lw = this.snapSize(w - this.icon.getMaxWidth() - this.getSkinnable().getLabelTextGap());
    double lh = 52.0;
    double lx = this.snapPosition(this.icon.getMaxWidth() + stepperToggle.getLabelTextGap());
    this.label.setTranslateX(0.0);
    this.label.resizeRelocate(lx, 0.0, lw, lh);
  }
}


