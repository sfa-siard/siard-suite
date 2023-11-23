package ch.admin.bar.siardsuite.component.rendering.form;

import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.OptionalHelper;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.TranslatableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class EditableFormField<T> extends VBox {

    private static final I18nKey SINGLE_FIELD_UNKNOWN_ERROR = I18nKey.of("storage.singleField.failed.unknownError");

    private static final String TITLE_STYLE_CLASS = "table-container-label";
    private static final String VALIDATION_STYLE_CLASS = "validation-text";
    private static final String FIELD_STYLE_CLASS = "rendered-field";

    private static final I18nKey READ_VALUE_ERROR = I18nKey.of("table.readValue.error");

    private final Label title;
    private final TextField value;
    private final Label validationMsg;

    private final ReadWriteStringProperty<T> property;
    private final T data;

    public EditableFormField(
            final ReadWriteStringProperty<T> property,
            final T data,
            final BooleanProperty hasChanged) {
        this.property = property;
        this.data = data;

        title = new Label();
        val titleSuffix = property.getValueValidators().stream()
                .map(validator -> validator.getTitleSuffix().orElse(""))
                .collect(Collectors.joining());

        title.textProperty()
                .bind(Bindings
                        .concat(I18n.bind(property.getTitle()))
                        .concat(titleSuffix));
        title.getStyleClass().add(TITLE_STYLE_CLASS);

        value = new TextField();
        value.getStyleClass().add(FIELD_STYLE_CLASS);

        validationMsg = new Label();
        validationMsg.getStyleClass().add(VALIDATION_STYLE_CLASS);
        hideValidationLabel();

        reset();
        value.textProperty()
                .addListener((observable, oldValue, newValue) -> hasChanged.set(true));

        this.getChildren().setAll(title, value, validationMsg);
    }

    public boolean hasValidValue() {
        val currentValue = value.getText();
        val failedValidator = this.property.getValueValidators().stream()
                .filter(validator -> !validator.getIsValidCheck().test(currentValue))
                .findAny();

        OptionalHelper.ifPresentOrElse(
                failedValidator,
                validator -> showValidationLabel(validator.getMessage()),
                this::hideValidationLabel
        );

        return !failedValidator.isPresent();
    }

    public void reset() {
        try {
            val originalValue = property.getValueExtractor().extract(data);
            value.setText(originalValue);
            value.setEditable(true);

        } catch (Exception e) {
            value.setEditable(false);
            value.setText(TranslatableText.of(READ_VALUE_ERROR).getText());
            value.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
        }
        hideValidationLabel();

    }

    public boolean hasChanges() {
        try {
            val originalValue = property.getValueExtractor().extract(data);
            return !Objects.equals(originalValue, value.getText());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean save() {
        if (!hasChanges()) {
            return true;
        }

        val currentValue = value.getText();

        try {
            property.getValuePersistor()
                    .persist(data, currentValue);
            return true;
        } catch (Exception e) {
            log.error("Storage failed for field {} with value {} because {}",
                    title.getText(),
                    currentValue,
                    e.getMessage());
            showValidationLabel(DisplayableText.of(SINGLE_FIELD_UNKNOWN_ERROR));
            return false;
        }
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
