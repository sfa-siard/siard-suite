package ch.admin.bar.siardsuite.presenter.connection.fields;

import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.util.OptionalHelper;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.TranslatableText;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class StringFormField extends VBox {

    private static final String TITLE_STYLE_CLASS = "form-label";
    private static final String FIELD_STYLE_CLASS = "form-field";
    private static final String VALIDATION_STYLE_CLASS = "error-text";

    private final Label title;
    private final TextField value;
    private final Label validationMsg;

    private final BooleanProperty hasChanged = new SimpleBooleanProperty(false);
    private final Set<ReadWriteStringProperty.Validator> validators;

    @Builder
    public StringFormField(
            @NonNull final DisplayableText title,
            @Nullable final DisplayableText prompt,
            @Nullable final String initialValue,
            @Singular final Set<ReadWriteStringProperty.Validator> validators,
            @Nullable final Double prefWidth
    ) {
        this.validators = Optional.ofNullable(validators).orElse(new HashSet<>());

        this.title = new Label();
        this.title.textProperty()
                .bind(title.bindable());
        this.title.getStyleClass().add(TITLE_STYLE_CLASS);
        VBox.setMargin(this.title, new Insets(0, 0, 10, 0));

        this.value = new TextField();
        this.value.getStyleClass().add(FIELD_STYLE_CLASS);
        Optional.ofNullable(prompt)
                .ifPresent(translatableText -> this.value.promptTextProperty().bind(translatableText.bindable()));
        this.value.getStyleClass().add(FIELD_STYLE_CLASS);
        Optional.ofNullable(initialValue)
                .ifPresent(this.value::setText);

        Optional.ofNullable(prefWidth)
                .ifPresent(this.value::setPrefWidth);

        validationMsg = new Label();
        validationMsg.getStyleClass().add(VALIDATION_STYLE_CLASS);
        hideValidationLabel();

        this.value.textProperty()
                .addListener((observable, oldValue, newValue) -> hasChanged.set(true));

        this.getChildren().setAll(this.title, this.value, validationMsg);
    }

    public boolean hasValidValue() {
        val currentValue = value.getText();
        val failedValidator = validators.stream()
                .filter(validator -> !validator.getIsValidCheck().test(currentValue))
                .findAny();

        OptionalHelper.ifPresentOrElse(
                failedValidator,
                validator -> showValidationLabel(validator.getMessage()),
                this::hideValidationLabel
        );

        return !failedValidator.isPresent();
    }

    public String getValue() {
        return value.getText();
    }

    private void showValidationLabel(final DisplayableText message) {
        validationMsg.setText(message.getText());
        validationMsg.setVisible(true);
        validationMsg.setManaged(true);
    }

    private void hideValidationLabel() {
        validationMsg.setVisible(false);
        validationMsg.setManaged(false); // otherwise, the VBox still does reserve space for the label
    }
}
