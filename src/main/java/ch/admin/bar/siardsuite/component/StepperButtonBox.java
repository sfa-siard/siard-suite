package ch.admin.bar.siardsuite.component;

import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.util.I18n;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class StepperButtonBox extends HBox {

  @FXML
  private final MFXButton nextButton = new MFXButton();
  @FXML
  private final MFXButton previousButton = new MFXButton();
  @FXML
  private final MFXButton cancelButton = new MFXButton();

  public StepperButtonBox() {
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

  public StepperButtonBox(String cancel) {
    this.cancelButton.textProperty().bind(I18n.createStringBinding("button.cancel"));
    this.cancelButton.getStyleClass().setAll("button", "secondary");
    this.cancelButton.setManaged(true);
    this.getChildren().addAll(this.cancelButton);
    this.initialize();
  }



  private void initialize() {
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

}
