package ch.admin.bar.siardsuite.component;

import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.util.I18n;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class ButtonBox extends HBox {

  public enum Type {
    DEFAULT, CANCEL, DOWNLOAD_FINISHED, FAILED, TO_START, OPEN_PREVIEW;
  }

  @FXML
  final MFXButton nextButton = new MFXButton();
  @FXML
  final MFXButton previousButton = new MFXButton();
  @FXML
  final MFXButton cancelButton = new MFXButton();

  public ButtonBox make(Type type) {
    return switch (type) {
      case CANCEL -> new CancelButtonBox();
      case DOWNLOAD_FINISHED -> new DownloadFinishedButtonBox();
      case DEFAULT -> new DefaultButtonBox();
      case FAILED -> new FailedButtonBox();
      case TO_START -> new StartButtonBox();
      case OPEN_PREVIEW -> new OpenPreviewButtonBox();
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

  private static class CancelButtonBox extends ButtonBox {

    public CancelButtonBox() {
      this.cancelButton.textProperty().bind(I18n.createStringBinding("button.cancel"));
      this.cancelButton.getStyleClass().setAll("button", "secondary");
      this.cancelButton.setManaged(true);
      this.getChildren().addAll(this.cancelButton);
      this.initialize();
    }
  }

  private static class DefaultButtonBox extends ButtonBox {
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

  private class DownloadFinishedButtonBox extends ButtonBox {

    DownloadFinishedButtonBox() {
      this.nextButton.textProperty().bind(I18n.createStringBinding("button.home"));
      this.nextButton.getStyleClass().setAll("button", "primary");
      this.nextButton.setManaged(true);
      this.cancelButton.textProperty().bind(I18n.createStringBinding("button.view-archive"));
      this.cancelButton.getStyleClass().setAll("button", "secondary");
      this.cancelButton.setManaged(true);
      this.getChildren().addAll(this.nextButton, this.cancelButton);
      this.initialize();
    }
  }


  private class FailedButtonBox extends ButtonBox {

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

  private class StartButtonBox extends ButtonBox {

    StartButtonBox() {
      this.nextButton.textProperty().bind(I18n.createStringBinding("button.home"));
      this.nextButton.getStyleClass().setAll("button", "primary");
      this.nextButton.setManaged(true);
      this.getChildren().add(this.nextButton);
      this.initialize();
    }
  }

  private static class OpenPreviewButtonBox extends ButtonBox {
    public OpenPreviewButtonBox() {
      this.nextButton.textProperty().bind(I18n.createStringBinding("button.home"));
      this.nextButton.getStyleClass().setAll("button", "primary" );
      this.nextButton.setManaged(true);
      this.previousButton.textProperty().bind(I18n.createStringBinding("button.export"));
      this.previousButton.getStyleClass().setAll("button", "secondary", "export-icon", "icon-text-btn");
      this.previousButton.setManaged(true);
      this.cancelButton.textProperty().bind(I18n.createStringBinding("button.upload"));
      this.cancelButton.getStyleClass().setAll("button", "secondary", "upload-icon", "icon-text-btn");
      this.cancelButton.setManaged(true);
      this.getChildren().addAll(this.previousButton, this.cancelButton, this.nextButton);
      this.initialize();
    }
  }
}
