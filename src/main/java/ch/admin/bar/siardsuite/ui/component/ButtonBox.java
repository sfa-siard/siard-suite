package ch.admin.bar.siardsuite.ui.component;

import ch.admin.bar.siardsuite.SiardApplication;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.util.I18n;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import lombok.Builder;
import lombok.Value;
import lombok.val;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class ButtonBox extends HBox {

    public enum Type {
        DEFAULT, CANCEL, FAILED, TO_START, OPEN_PREVIEW
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
            I18n.bind(this.cancelButton.textProperty(), "button.cancel");
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
    
    private static class FailedButtonBox extends ButtonBox {

        FailedButtonBox() {
            I18n.bind(this.nextButton.textProperty(), "button.close");
            this.nextButton.getStyleClass().setAll("button", "primary");
            this.nextButton.setManaged(true);
            I18n.bind(this.cancelButton.textProperty(), "button.back");
            this.cancelButton.getStyleClass().setAll("button", "secondary");
            this.cancelButton.setManaged(true);
            this.getChildren().addAll(this.cancelButton, this.nextButton);
            this.initialize();
        }
    }

    private static class StartButtonBox extends ButtonBox {

        StartButtonBox() {
            I18n.bind(this.nextButton.textProperty(), "button.home");
            this.nextButton.getStyleClass().setAll("button", "primary");
            this.nextButton.setManaged(true);
            this.getChildren().add(this.nextButton);
            this.initialize();
        }
    }

    private static class OpenPreviewButtonBox extends ButtonBox {
        public OpenPreviewButtonBox() {
            I18n.bind(this.nextButton.textProperty(), "button.home");
            this.nextButton.getStyleClass().setAll("button", "primary");
            this.nextButton.setManaged(true);
            I18n.bind(this.previousButton.textProperty(), "button.export");
            this.previousButton.getStyleClass().setAll("button", "secondary", "export-icon", "icon-text-btn");
            this.previousButton.setManaged(true);
            I18n.bind(this.cancelButton.textProperty(), "button.upload");
            this.cancelButton.getStyleClass().setAll("button", "secondary", "upload-icon", "icon-text-btn");
            this.cancelButton.setManaged(true);
            this.getChildren().addAll(this.previousButton, this.cancelButton, this.nextButton);
            this.initialize();
        }
    }

    public static ButtonBox create(final ButtonDescriber... describers) {
        val buttons = Arrays.stream(describers)
                .map(buttonDescriber -> {
                    val button = new MFXButton();
                    button.textProperty().bind(DisplayableText.of(buttonDescriber.getTitle()).bindable());

                    switch (buttonDescriber.getStyle()) {
                        case PRIMARY:
                            button.getStyleClass().setAll("button", "primary");
                            break;

                        case SECONDARY:
                            button.getStyleClass().setAll("button", "secondary");
                            break;
                    }

                    button.setOnAction(actionEvent -> buttonDescriber.getOnAction().run());

                    return button;
                })
                .collect(Collectors.toList());

        val buttonBox = new ButtonBox();
        buttonBox.getChildren().addAll(buttons);
        buttonBox.initialize();

        return buttonBox;
    }

    public enum ButtonStyle {
        SECONDARY,
        PRIMARY
    }

    @Value
    @Builder
    public static class ButtonDescriber {
        I18nKey title;
        ButtonStyle style;
        Runnable onAction;
    }
}
