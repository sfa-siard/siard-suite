package ch.admin.bar.siardsuite.component.rendering;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.util.I18nKey;
import ch.admin.bar.siardsuite.util.OptionalHelper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FormRenderer<T> {

    private static final String TITLE_LABEL_STYLE_CLASS = "table-container-label";
    private static final String VALIDATION_LABEL_STYLE_CLASS = "validation-text";

    private static final String FIELD_STYLE_CLASS = "table-container-rendered-field";
    private static final String FORM_FIELD_STYLE_CLASS = "form-field";

    private final RenderableForm<T> renderableForm;
    private final Controller controller;
    private final T data;

    private final List<EditableFormField> editableFormFields = new ArrayList<>();

    @Getter
    private final BooleanProperty hasChanged = new SimpleBooleanProperty(false);

    public FormRenderer(RenderableForm<T> renderableForm, Controller controller) {
        this.renderableForm = renderableForm;
        this.controller = controller;
        this.data = renderableForm.getDataExtractor().apply(controller);
    }

    public VBox renderForm() {
        val vbox = new VBox();
        vbox.getChildren().setAll(renderableForm.getGroups().stream()
                .map(renderableGroup -> createGroup(renderableGroup, data))
                .collect(Collectors.toList()));

        vbox.setSpacing(40);
        VBox.setVgrow(vbox, Priority.ALWAYS);
        return vbox;
    }

    private VBox createGroup(final RenderableFormGroup<T> group, final T data) {
        val vbox = new VBox();
        vbox.getChildren().setAll(
                group.getProperties().stream()
                        .map(renderableProperty -> {
                            if (renderableProperty instanceof ReadWriteStringProperty) {
                                return createField((ReadWriteStringProperty<T>) renderableProperty, data);
                            }

                            if (renderableProperty instanceof ReadOnlyStringProperty) {
                                return createField((ReadOnlyStringProperty<T>) renderableProperty, data);
                            }

                            if (renderableProperty instanceof RenderableTable) {
                                return TableRenderer.<T, Object>builder()
                                        .data(data)
                                        .renderableTable((RenderableTable<T, Object>) renderableProperty)
                                        .build()
                                        .render();
                            }

                            throw new IllegalArgumentException(String.format(
                                    "Property type %s ins not supported yet.",
                                    renderableProperty.getClass().getName()
                            ));
                        })
                        .collect(Collectors.toList()));
        vbox.setSpacing(10);
        VBox.setVgrow(vbox, Priority.ALWAYS);

        return vbox;
    }

    private VBox createField(final ReadWriteStringProperty<T> property, final T data) {
        val formField = new EditableFormField<T>(property, data, hasChanged);
        editableFormFields.add(formField);

        return formField;
    }

    private VBox createField(final ReadOnlyStringProperty<T> property, final T data) {
        val vbox = new VBox();

        val titleLabel = new Label();
        titleLabel.setText(I18n.get(property.getTitle()));
        titleLabel.getStyleClass().add(TITLE_LABEL_STYLE_CLASS);

        val valueLabel = new Label();
        val value = property.getValueExtractor().apply(data);
        valueLabel.setText(value);
        valueLabel.getStyleClass().add(FIELD_STYLE_CLASS);

        vbox.getChildren().setAll(titleLabel, valueLabel);


        return vbox;
    }

    @SneakyThrows // TODO
    public void saveChanges() {
        val invalidFields = this.editableFormFields.stream()
                .filter(editableFormField -> !editableFormField.hasValidValue())
                .collect(Collectors.toList());

        if (invalidFields.isEmpty()) {
            this.hasChanged.set(false);

            this.editableFormFields.stream()
                    .filter(EditableFormField::hasChanges)
                    .forEach(EditableFormField::save);

            this.renderableForm.getSaveAction().doAfterSaveChanges(controller, data);
        }
    }

    public void dropChanges() {
        this.editableFormFields.forEach(EditableFormField::reset);
        this.hasChanged.set(false);
    }

    private static class EditableFormField<T> extends VBox {

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
            title.setText(I18n.get(property.getTitle()) + titleSuffix);
            title.getStyleClass().add(TITLE_LABEL_STYLE_CLASS);

            value = new TextField();
            value.getStyleClass()
                    .add(FORM_FIELD_STYLE_CLASS);

            validationMsg = new Label();
            validationMsg.getStyleClass().add(VALIDATION_LABEL_STYLE_CLASS);
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
            val originalValue = property.getValueExtractor().apply(data);
            value.setText(originalValue);
            hideValidationLabel();
        }

        public boolean hasChanges() {
            val originalValue = property.getValueExtractor().apply(data);
            return !Objects.equals(originalValue, value.getText());
        }

        public void save() {
            property.getValuePersistor()
                    .accept(data, value.getText());
        }

        private void showValidationLabel(final I18nKey message) {
            validationMsg.setText(I18n.get(message));
            validationMsg.setVisible(true);
            validationMsg.setManaged(true);
        }

        private void hideValidationLabel() {
            validationMsg.setVisible(false);
            validationMsg.setManaged(false); // otherwise, the VBox still does reserve space for the label
        }
    }
}
