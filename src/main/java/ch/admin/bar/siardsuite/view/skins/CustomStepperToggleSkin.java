package ch.admin.bar.siardsuite.view.skins;

import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.SiardEvent;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.TextUtils;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class CustomStepperToggleSkin extends SkinBase<MFXStepperToggle> {
  private final HBox container;
  private final MFXTextField label;
  private final Button icon;

  private Boolean visible;

  public CustomStepperToggleSkin(MFXStepperToggle stepperToggle, Boolean visible, Stage stage) {
    super(stepperToggle);
      this.visible = visible;
      this.label = MFXTextField.asLabel();
      this.icon = (Button) stepperToggle.getIcon();

      if (visible) {
        this.container = new HBox(stepperToggle.getIcon(), this.label);
        this.label.textProperty().bind(I18n.createStringBinding(stepperToggle.getText()));
        this.label.setManaged(false);
        this.icon.setMaxWidth(22);
        this.container.getStyleClass().setAll("custom-stepper-toggle");
        this.getChildren().addAll(new Node[]{this.container});
        this.setListeners(stage);
      } else {
        this.icon.setMaxWidth(0);
        this.container = new HBox();
      }

  }

  private void setListeners(Stage stage) {
    MFXStepperToggle stepperToggle = this.getSkinnable();

    this.label.visibleProperty().bind(stepperToggle.textProperty().isEmpty().not());

    stage.addEventHandler(SiardEvent.UPDATE_LANGUAGE_EVENT, event -> layoutChildren(0,0,0,0));
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
    double lw = this.snapSizeX(w - this.icon.getMaxWidth() - this.getSkinnable().getLabelTextGap());
    double lh = this.snapSizeY(h);
    double lx;
    this.label.setTranslateX(0.0);
    lx = this.snapPositionX(this.icon.getMaxWidth() + stepperToggle.getLabelTextGap());
    this.label.resizeRelocate(lx, -0.1, lw, lh);
  }
}


