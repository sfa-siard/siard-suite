package ch.admin.bar.siardsuite.presenter.connection.fields;

import ch.admin.bar.siardsuite.component.IconButton;
import ch.admin.bar.siardsuite.component.SiardToolip;
import ch.admin.bar.siardsuite.component.SiardTooltip;
import ch.admin.bar.siardsuite.util.OptionalHelper;
import ch.admin.bar.siardsuite.util.Validator;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public abstract class FormField<T> extends VBox {
    protected static final String TITLE_STYLE_CLASS = "form-label";
    protected static final String FIELD_STYLE_CLASS = "form-field";
    protected static final String VALIDATION_STYLE_CLASS = "error-text";

    protected final Label title;
    protected final Label validationMsg;

    private final Collection<Validator<T>> validators;

    public FormField(
            @NonNull final DisplayableText title,
            @Nullable final DisplayableText hint,
            @Nullable final Collection<Validator<T>> validators
            ) {
        this.validators = Optional.ofNullable(validators).orElse(new ArrayList<>());

        this.title = new Label();
        this.title.textProperty()
                .bind(title.bindable());
        this.title.getStyleClass().add(TITLE_STYLE_CLASS);

        Optional.ofNullable(hint).ifPresent(displayableText -> {
            val temp = new SiardTooltip(displayableText.getText());
            val iconButton = new IconButton(IconButton.Icon.INFO);

            this.title.setContentDisplay(ContentDisplay.RIGHT);
            this.title.setGraphic(iconButton);

            new SiardToolip(iconButton, temp).setup();
        });

        VBox.setMargin(this.title, new Insets(0, 0, 10, 0));

        validationMsg = new Label();
        validationMsg.getStyleClass().add(VALIDATION_STYLE_CLASS);
        hideValidationLabel();
    }

    public boolean hasValidValue() {
        return !findFailingValidator().isPresent();
    }

    public boolean hasInvalidValue() {
        return !hasValidValue();
    }

    public boolean hasInvalidValueAndIfSoShowValidationMessage() {
        val failingValidator = findFailingValidator();

        OptionalHelper.ifPresentOrElse(
                failingValidator,
                validator -> showValidationLabel(validator.getMessage()),
                this::hideValidationLabel
        );

        return failingValidator.isPresent();
    }

    public abstract T getValue();

    public abstract void setValue(T newValue);

    private Optional<Validator<T>> findFailingValidator() {
        val currentValue = getValue();
        return validators.stream()
                .filter(validator -> !validator.getIsValidCheck().test(currentValue))
                .findAny();
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
