package ch.admin.bar.siardsuite.presenter.connection.fields;

import ch.admin.bar.siardsuite.util.Validator;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
public class StringFormField extends FormField<String> {

    private final TextInputControl value;

    @Builder
    public StringFormField(
            @NonNull final DisplayableText title,
            @Nullable final DisplayableText hint,
            @Nullable final DisplayableText prompt,
            @Nullable final String initialValue,
            @Singular final Set<Validator<String>> validators,
            @Nullable final Double prefWidth,
            @Nullable final Consumer<String> onNewUserInput,
            @Nullable final Boolean deactivable,
            @Nullable final InputType inputType
    ) {
        super(title, hint, validators, deactivable);

        if (InputType.PASSWORD.equals(inputType)) {
            this.value = new PasswordField();
        } else {
            this.value = new TextField();
        }

        this.value.getStyleClass().add(FIELD_STYLE_CLASS);
        Optional.ofNullable(prompt)
                .ifPresent(translatableText -> this.value.promptTextProperty().bind(translatableText.bindable()));
        this.value.getStyleClass().add(FIELD_STYLE_CLASS);
        Optional.ofNullable(initialValue)
                .ifPresent(this.value::setText);
        Optional.ofNullable(prefWidth)
                .ifPresent(this.value::setPrefWidth);
        Optional.ofNullable(onNewUserInput)
                .ifPresent(stringConsumer -> this.value.focusedProperty().addListener((observable, oldValue, newValue) -> {
                            if (!newValue && oldValue && !hasInvalidValueAndIfSoShowValidationMessage()) {
                                stringConsumer.accept(this.value.getText());
                            }
                        }
                ));

        this.getChildren().setAll(this.title, this.value, validationMsg);
    }

    @Override
    public String getValue() {
        return value.getText();
    }

    @Override
    public void setValue(final String text) {
        value.setText(text);
    }

    @Override
    protected Node getContentNode() {
        return this.value;
    }

    public enum InputType {
        PLAIN_TEXT,
        PASSWORD
    }
}
