package ch.admin.bar.siardsuite.presenter.connection.fields;

import ch.admin.bar.siardsuite.util.OptionalHelper;
import ch.admin.bar.siardsuite.util.Validator;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
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
public class StringFormField extends FormField {

    private static final String FIELD_STYLE_CLASS = "form-field";

    private final TextField value;

    @Builder
    public StringFormField(
            @NonNull final DisplayableText title,
            @Nullable final DisplayableText hint,
            @Nullable final DisplayableText prompt,
            @Nullable final String initialValue,
            @Singular final Set<Validator<String>> validators,
            @Nullable final Double prefWidth
    ) {
        super(title, hint, validators);

        this.value = new TextField();
        this.value.getStyleClass().add(FIELD_STYLE_CLASS);
        Optional.ofNullable(prompt)
                .ifPresent(translatableText -> this.value.promptTextProperty().bind(translatableText.bindable()));
        this.value.getStyleClass().add(FIELD_STYLE_CLASS);
        Optional.ofNullable(initialValue)
                .ifPresent(this.value::setText);
        Optional.ofNullable(prefWidth)
                .ifPresent(this.value::setPrefWidth);

        this.getChildren().setAll(this.title, this.value, validationMsg);
    }

    public String getValue() {
        return value.getText();
    }
}
