package ch.admin.bar.siardsuite.ui.presenter.connection.fields;

import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.ui.common.Validator;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class BooleanFormField extends FormField<Boolean> {

    private final CheckBox checkBox;

    @Builder
    public BooleanFormField(
            @NonNull final DisplayableText title,
            @Nullable final DisplayableText hint,
            @Singular final Set<Validator<Boolean>> validators,
            @Nullable final Boolean initialValue,
            @Nullable final Consumer<Boolean> onNewUserInput
    ) {
        super(title, hint, validators, null);

        this.checkBox = new CheckBox();
        this.checkBox.textProperty()
                     .bind(title.bindable());
        
        Optional.ofNullable(initialValue)
                .ifPresent(this.checkBox::setSelected);
        
        Optional.ofNullable(onNewUserInput)
                .ifPresent(consumer -> this.checkBox.selectedProperty()
                                                    .addListener((observable, oldValue, newValue) -> {
                                                        if (!oldValue.equals(newValue)) {
                                                            consumer.accept(newValue);
                                                        }
                                                    }));

        this.getChildren()
            .setAll(this.checkBox, validationMsg);
    }

    @Override
    public Boolean getValue() {
        return checkBox.isSelected();
    }

    @Override
    public void setValue(final Boolean value) {
        checkBox.setSelected(value);
    }

    @Override
    protected Node getContentNode() {
        return this.checkBox;
    }
}
