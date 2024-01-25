package ch.admin.bar.siardsuite.presenter.connection.fields;

import ch.admin.bar.siardsuite.component.IconButton;
import ch.admin.bar.siardsuite.component.SiardTooltip;
import ch.admin.bar.siardsuite.util.OptionalHelper;
import ch.admin.bar.siardsuite.util.Validator;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
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

    protected final HBox title;
    protected final Label validationMsg;

    private final MFXToggleButton activationToggle;

    private final Collection<Validator<T>> validators;

    public FormField(
            @NonNull final DisplayableText title,
            @Nullable final DisplayableText hint,
            @Nullable final Collection<Validator<T>> validators,
            @Nullable final Boolean deactivable
            ) {
        this.validators = Optional.ofNullable(validators).orElse(new ArrayList<>());

        val titleLabel = new Label();
        titleLabel.textProperty()
                .bind(title.bindable());
        titleLabel.getStyleClass().add(TITLE_STYLE_CLASS);
        titleLabel.setAlignment(Pos.CENTER_LEFT);
        titleLabel.setMaxHeight(Double.MAX_VALUE);

        activationToggle = new MFXToggleButton();
        activationToggle.setSelected(true);
        activationToggle.setOnAction(event -> {
            val contentNode = getContentNode();
            contentNode.setVisible(!contentNode.isVisible());
            contentNode.setManaged(!contentNode.isManaged());

            if (!activationToggle.isSelected()) {
                hideValidationLabel();
            }
        });

        if (deactivable != null && deactivable) {
            this.title = new HBox(activationToggle, titleLabel);
        } else {
            this.title = new HBox(titleLabel);
        }
        this.title.setFillHeight(true);

        Optional.ofNullable(hint).ifPresent(displayableText -> {
            val iconButton = new IconButton(IconButton.Icon.INFO);

            titleLabel.setContentDisplay(ContentDisplay.RIGHT);
            titleLabel.setGraphic(iconButton);

            new SiardTooltip(displayableText).showOnMouseOn(iconButton);
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

    public boolean isActivated() {
        return activationToggle.isSelected();
    }

    public abstract T getValue();

    public abstract void setValue(T newValue);

    protected abstract Node getContentNode();

    private Optional<Validator<T>> findFailingValidator() {
        if (!isActivated()) {
            return Optional.empty();
        }

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
