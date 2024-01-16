package ch.admin.bar.siardsuite.presenter.connection.fields;

import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.TranslatableText;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Builder;
import lombok.NonNull;

public abstract class FormField<T extends Node> extends VBox {
    protected static final String TITLE_STYLE_CLASS = "form-label";
    protected static final String FIELD_STYLE_CLASS = "form-field";
    protected static final String VALIDATION_STYLE_CLASS = "error-text";

    private final Label title;
    protected final T value;
    private final Label validationMsg;

    private final BooleanProperty hasChanged = new SimpleBooleanProperty(false);

    public FormField(
            @NonNull final TranslatableText title,
            @NonNull final T value
    ) {

        this.title = new Label();
        this.title.textProperty()
                .bind(title.bindable());
        this.title.getStyleClass().add(TITLE_STYLE_CLASS);
        VBox.setMargin(this.title, new Insets(0, 0, 10, 0));

        this.value = value;

        validationMsg = new Label();
        validationMsg.getStyleClass().add(VALIDATION_STYLE_CLASS);
        hideValidationLabel();

        this.getChildren().setAll(this.title, this.value, validationMsg);
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
