package ch.admin.bar.siardsuite.component.rendering.form;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ThrowingExtractor;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.TranslatableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.NonNull;
import lombok.val;

public class ReadOnlyFormField<T> extends VBox {
    private static final String TITLE_STYLE_CLASS = "table-container-label";
    private static final String FIELD_STYLE_CLASS = "rendered-field";

    private static final I18nKey READ_VALUE_ERROR = I18nKey.of("table.readValue.error");

    private final Label titleLabel;
    private final TextField valueTextField;

    public ReadOnlyFormField(
            @NonNull final DisplayableText title,
            @NonNull final ThrowingExtractor<T, String> valueExtractor,
            @NonNull final T data) {

        titleLabel = new Label();
        titleLabel.textProperty()
                .bind(I18n.bind(title));
        titleLabel.getStyleClass().add(TITLE_STYLE_CLASS);

        valueTextField = new TextField();
        valueTextField.setEditable(false);
        valueTextField.getStyleClass().add(FIELD_STYLE_CLASS);
        try {
            val value = valueExtractor.extract(data);
            valueTextField.setText(value);
        } catch (Exception e) {
            valueTextField.setText(TranslatableText.of(READ_VALUE_ERROR).getText());
            valueTextField.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
        }

        this.getChildren().setAll(titleLabel, valueTextField);
    }

    public ReadOnlyFormField(
            @NonNull final ReadOnlyStringProperty<T> property,
            @NonNull final T data) {
        this(property.getTitle(), property.getValueExtractor(), data);
    }
}
