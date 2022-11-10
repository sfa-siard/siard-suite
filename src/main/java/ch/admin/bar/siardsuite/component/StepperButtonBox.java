package ch.admin.bar.siardsuite.component;

import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.util.I18n;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class StepperButtonBox extends HBox {

  public enum Type {
    DEFAULT, CANCEL, DOWNLOAD_FINISHED, FAILED;
  }

  @FXML
  final MFXButton nextButton = new MFXButton();
  @FXML
  final MFXButton previousButton = new MFXButton();
  @FXML
  final MFXButton cancelButton = new MFXButton();

  public StepperButtonBox make(Type type) {
    return switch (type) {
      case CANCEL -> new CancelButtonBox();
      case DOWNLOAD_FINISHED -> new DownloadFinishedButtonBox();
      case DEFAULT -> new DefaultButtonBox();
      case FAILED -> new FailedButtonBox();
    };
  }

  protected void initialize() {
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

  private static class CancelButtonBox extends StepperButtonBox {

    public CancelButtonBox() {
      this.cancelButton.textProperty().bind(I18n.createStringBinding("button.cancel"));
      this.cancelButton.getStyleClass().setAll("button", "secondary");
      this.cancelButton.setManaged(true);
      this.getChildren().addAll(this.cancelButton);
      this.initialize();
    }
  }

  private static class DefaultButtonBox extends StepperButtonBox {
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

  private class DownloadFinishedButtonBox extends StepperButtonBox {

    DownloadFinishedButtonBox() {
      this.nextButton.textProperty().bind(I18n.createStringBinding("button.view-archive"));
      this.nextButton.getStyleClass().setAll("button", "primary");
      this.nextButton.setManaged(true);
      this.cancelButton.textProperty().bind(I18n.createStringBinding("button.home"));
      this.cancelButton.getStyleClass().setAll("button", "secondary");
      this.cancelButton.setManaged(true);
      this.getChildren().addAll(this.cancelButton, this.nextButton);
      this.initialize();
    }
  }


  private class FailedButtonBox extends StepperButtonBox {

    FailedButtonBox() {
      this.nextButton.textProperty().bind(I18n.createStringBinding("button.close"));
      this.nextButton.getStyleClass().setAll("button", "primary");
      this.nextButton.setManaged(true);
      this.cancelButton.textProperty().bind(I18n.createStringBinding("button.back"));
      this.cancelButton.getStyleClass().setAll("button", "secondary");
      this.cancelButton.setManaged(true);
      this.getChildren().addAll(this.cancelButton, this.nextButton);
      this.initialize();
    }
  }
}
