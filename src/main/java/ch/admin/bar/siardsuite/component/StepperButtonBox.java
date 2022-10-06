package ch.admin.bar.siardsuite.component;

import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.util.I18n;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class StepperButtonBox extends HBox {

  public static final String DEFAULT = "default";
  public static final String CANCEL = "cancel";
  @FXML
  final MFXButton nextButton = new MFXButton();
  @FXML
  final MFXButton previousButton = new MFXButton();
  @FXML
  final MFXButton cancelButton = new MFXButton();

  public StepperButtonBox make(String type) {
    if (type.equals(CANCEL)) {
      return new CancelButtonBox();
    } else {
      return new DefaultButtonBox();
    }
  }

//  public StepperButtonBox() {
//    new DefaultButtonBox();
//  }

  void initialize() {
    this.getStylesheets().add((Objects.requireNonNull(SiardApplication.class.getResource("css/root.css")).toString()));
    this.getStyleClass().add("btn-box");
    this.setSpacing(20.0);
    this.setMinHeight(60);
    this.setMaxHeight(1.7976931348623157E308);
    this.setMaxWidth(1.7976931348623157E308);
  }

  public MFXButton next() {
    return nextButton;
  }

  public MFXButton previous() {
    return previousButton;
  }

  public MFXButton cancel() {
    return cancelButton;
  }

  public static class CancelButtonBox extends StepperButtonBox {

    public CancelButtonBox() {
      this.cancelButton.textProperty().bind(I18n.createStringBinding("button.cancel"));
      this.cancelButton.getStyleClass().setAll("button", "secondary");
      this.cancelButton.setManaged(true);
      this.getChildren().addAll(this.cancelButton);
      this.initialize();
    }
  }

  public static class DefaultButtonBox extends StepperButtonBox {
    public DefaultButtonBox() {
      this.nextButton.textProperty().bind(I18n.createStringBinding("button.next"));
      this.nextButton.getStyleClass().setAll("button", "primary");
      this.nextButton.setManaged(true);
      this.previousButton.textProperty().bind(I18n.createStringBinding("button.back"));
      this.previousButton.getStyleClass().setAll("button", "secondary");
      this.previousButton.setManaged(true);
      this.cancelButton.textProperty().bind(I18n.createStringBinding("button.cancel"));
      this.cancelButton.getStyleClass().setAll("button", "secondary");
      this.cancelButton.setManaged(true);
      this.getChildren().addAll(this.previousButton, this.cancelButton, this.nextButton);
      this.initialize();
    }
  }
}
