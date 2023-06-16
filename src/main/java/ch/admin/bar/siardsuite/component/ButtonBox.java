package ch.admin.bar.siardsuite.component;

import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.util.I18n;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class ButtonBox extends HBox {

    public enum Type {
        DEFAULT, CANCEL, DOWNLOAD_FINISHED, FAILED, TO_START, OPEN_PREVIEW
    }

    @FXML
    final MFXButton nextButton = new MFXButton();
    @FXML
    final MFXButton previousButton = new MFXButton();
    @FXML
    final MFXButton cancelButton = new MFXButton();

    public ButtonBox make(Type type) {
        switch (type) {
            case CANCEL:
                return new CancelButtonBox();
            case DOWNLOAD_FINISHED:
                return new DownloadFinishedButtonBox();
            case DEFAULT:
                return new DefaultButtonBox();
            case FAILED:
                return new FailedButtonBox();
            case TO_START:
                return new StartButtonBox();
            case OPEN_PREVIEW:
                return new OpenPreviewButtonBox();
            default:
                throw new IllegalArgumentException();
        }
    }

    protected void initialize() {
        this.getStylesheets()
            .add((Objects.requireNonNull(SiardApplication.class.getResource("css/root.css")).toString()));
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

    public ButtonBox append(MFXButton button) {
        button.getStyleClass().setAll("button", "secondary");
        button.setManaged(true);
        this.getChildren().add(button);
        return this;
    }

    private static class CancelButtonBox extends ButtonBox {

        public CancelButtonBox() {
            I18n.bind(this.cancelButton.textProperty(),"button.cancel");
            this.cancelButton.getStyleClass().setAll("button", "secondary");
            this.cancelButton.setManaged(true);
            this.getChildren().addAll(this.cancelButton);
            this.initialize();
        }
    }

    private static class DefaultButtonBox extends ButtonBox {
        public DefaultButtonBox() {
            I18n.bind(nextButton, "button.next");
            this.nextButton.getStyleClass().setAll("button", "primary");
            this.nextButton.setManaged(true);
            I18n.bind(previousButton, "button.back");
            this.previousButton.getStyleClass().setAll("button", "secondary");
            this.previousButton.setManaged(true);
            I18n.bind(cancelButton, "button.cancel");
            this.cancelButton.getStyleClass().setAll("button", "secondary");
            this.cancelButton.setManaged(true);
            this.getChildren().addAll(this.previousButton, this.cancelButton, this.nextButton);
            this.initialize();
        }
    }

    private static class DownloadFinishedButtonBox extends ButtonBox {

        DownloadFinishedButtonBox() {
            I18n.bind(this.nextButton.textProperty(),"button.home");
            this.nextButton.getStyleClass().setAll("button", "primary");
            this.nextButton.setManaged(true);
            I18n.bind(this.cancelButton.textProperty(),"button.view-archive");
            this.cancelButton.getStyleClass().setAll("button", "secondary");
            this.cancelButton.setManaged(true);
            this.getChildren().addAll(this.nextButton, this.cancelButton);
            this.initialize();
        }
    }


    private static class FailedButtonBox extends ButtonBox {

        FailedButtonBox() {
            I18n.bind(this.nextButton.textProperty(),"button.close");
            this.nextButton.getStyleClass().setAll("button", "primary");
            this.nextButton.setManaged(true);
            I18n.bind(this.cancelButton.textProperty(),"button.back");
            this.cancelButton.getStyleClass().setAll("button", "secondary");
            this.cancelButton.setManaged(true);
            this.getChildren().addAll(this.cancelButton, this.nextButton);
            this.initialize();
        }
    }

    private static class StartButtonBox extends ButtonBox {

        StartButtonBox() {
            I18n.bind(this.nextButton.textProperty(),"button.home");
            this.nextButton.getStyleClass().setAll("button", "primary");
            this.nextButton.setManaged(true);
            this.getChildren().add(this.nextButton);
            this.initialize();
        }
    }

    private static class OpenPreviewButtonBox extends ButtonBox {
        public OpenPreviewButtonBox() {
            I18n.bind(this.nextButton.textProperty(),"button.home");
            this.nextButton.getStyleClass().setAll("button", "primary");
            this.nextButton.setManaged(true);
            I18n.bind(this.previousButton.textProperty(),"button.export");
            this.previousButton.getStyleClass().setAll("button", "secondary", "export-icon", "icon-text-btn");
            this.previousButton.setManaged(true);
            I18n.bind(this.cancelButton.textProperty(),"button.upload");
            this.cancelButton.getStyleClass().setAll("button", "secondary", "upload-icon", "icon-text-btn");
            this.cancelButton.setManaged(true);
            this.getChildren().addAll(this.previousButton, this.cancelButton, this.nextButton);
            this.initialize();
        }
    }
}
