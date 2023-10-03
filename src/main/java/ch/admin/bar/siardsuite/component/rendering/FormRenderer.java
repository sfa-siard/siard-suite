package ch.admin.bar.siardsuite.component.rendering;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.util.I18n;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FormRenderer<T> {

    private static final String TITLE_STYLE_CLASS = "table-container-label";
    private static final String FIELD_STYLE_CLASS = "table-container-rendered-field";
    private static final String FORM_FIELD_STYLE_CLASS = "form-field";

    private final RenderableForm<T> renderableForm;
    private final Controller controller;
    private final T data;

    private final List<ResetableStringPropertyHandler> resetableStringPropertyHandlers = new ArrayList<>();

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
        val vbox = new VBox();

        val titleLabel = new Label();
        titleLabel.setText(I18n.get(property.getTitle()));
        titleLabel.getStyleClass().add(TITLE_STYLE_CLASS);

        val valueTextField = new TextField();

        val propertyValue = property.getValueExtractor().apply(data);
        val resetableStringPropertyHandler = new ResetableStringPropertyHandler<>(
                propertyValue,
                property,
                () -> property.getValuePersistor().accept(data, valueTextField.getText()));
        valueTextField.textProperty().bindBidirectional(resetableStringPropertyHandler.getStringProperty());
        this.resetableStringPropertyHandlers.add(resetableStringPropertyHandler);

        valueTextField.getStyleClass()
                .add(FORM_FIELD_STYLE_CLASS);
        valueTextField.textProperty()
                .addListener((observable, oldValue, newValue) -> this.hasChanged.set(true));

        vbox.getChildren().setAll(titleLabel, valueTextField);


        return vbox;
    }

    private VBox createField(final ReadOnlyStringProperty<T> property, final T data) {
        val vbox = new VBox();

        val titleLabel = new Label();
        titleLabel.setText(I18n.get(property.getTitle()));
        titleLabel.getStyleClass().add(TITLE_STYLE_CLASS);

        val valueLabel = new Label();
        val value = property.getValueExtractor().apply(data);
        valueLabel.setText(value);
        valueLabel.getStyleClass().add(FIELD_STYLE_CLASS);

        vbox.getChildren().setAll(titleLabel, valueLabel);


        return vbox;
    }

    public void saveChanges() {
        this.hasChanged.set(false);
        val changedValues = this.resetableStringPropertyHandlers.stream()
                .filter(ResetableStringPropertyHandler::hasChanges)
                .collect(Collectors.toList());

        resetableStringPropertyHandlers.removeAll(changedValues);

        val resetedOrigValue = changedValues.stream()
                .map(resetableStringPropertyHandler -> resetableStringPropertyHandler.toBuilder()
                        .originalValue(resetableStringPropertyHandler.getStringProperty().getValue())
                        .build())
                .collect(Collectors.toList());

        resetableStringPropertyHandlers.addAll(resetedOrigValue);

        changedValues.forEach(ResetableStringPropertyHandler::save);

        this.renderableForm.getSaveAction().accept(controller, data);

    }

    public void dropChanges() {
        this.resetableStringPropertyHandlers.forEach(ResetableStringPropertyHandler::reset);
        this.hasChanged.set(false);
    }


    @Getter
    @Builder(toBuilder = true)
    @RequiredArgsConstructor
    private static class ResetableStringPropertyHandler<T> {
        private final StringProperty stringProperty;
        private final ReadWriteStringProperty<T> renderableProperty;
        private final String originalValue;
        private final Runnable save;

        public ResetableStringPropertyHandler(
                String originalValue,
                @NonNull final ReadWriteStringProperty<T> renderableProperty,
                @NonNull Runnable save) {
            this.originalValue = Optional.ofNullable(originalValue).orElse("");
            this.stringProperty = new SimpleStringProperty(originalValue);
            this.renderableProperty = renderableProperty;
            this.save = save;
        }

        public void reset() {
            this.stringProperty.set(originalValue);
        }

        public boolean hasChanges() {
            return !Objects.equals(originalValue, stringProperty.getValue());
        }

        public void save() {
            this.save.run();
        }
    }
}
