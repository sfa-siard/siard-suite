package ch.admin.bar.siardsuite.component.rendering;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableLazyLoadingTable;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableTable;
import ch.admin.bar.siardsuite.presenter.archive.browser.ChangeableDataPresenter.SaveChangesReport;
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
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class FormRenderer<T> {

    private static final String TITLE_STYLE_CLASS = "table-container-label";
    private static final String VALIDATION_STYLE_CLASS = "validation-text";
    private static final String FIELD_STYLE_CLASS = "rendered-field";

    private final RenderableForm<T> renderableForm;
    private final T data;

    private final List<EditableFormField> editableFormFields = new ArrayList<>();
    private final List<SearchableFormEntry> searchableFormEntries = new ArrayList<>();

    @Getter private final BooleanProperty hasChanged = new SimpleBooleanProperty(false);
    @Getter private final BooleanProperty hasSearchableData = new SimpleBooleanProperty(false);

    public FormRenderer(RenderableForm<T> renderableForm) {
        this.renderableForm = renderableForm;
        this.data = renderableForm.getDataSupplier().get();
    }

    public VBox renderForm() {
        val vbox = new VBox();
        val groups = renderableForm.getGroups().stream()
                .map(renderableGroup -> createGroup(renderableGroup, data))
                .collect(Collectors.toList());

        vbox.getChildren().setAll(groups);
        vbox.setSpacing(40); // space between groups
        VBox.setVgrow(vbox, Priority.ALWAYS);

        return vbox;
    }

    private VBox createGroup(final RenderableFormGroup<T> group, final T data) {
        val vbox = new VBox();
        val fields = group.getProperties().stream()
                .map(renderableProperty -> {
                    if (renderableProperty instanceof ReadWriteStringProperty) {
                        return createField((ReadWriteStringProperty<T>) renderableProperty, data);
                    }

                    if (renderableProperty instanceof ReadOnlyStringProperty) {
                        return createField((ReadOnlyStringProperty<T>) renderableProperty, data);
                    }

                    if (renderableProperty instanceof RenderableTable) {
                        hasSearchableData.set(true);
                        val renderer = TableRenderer.<T, Object>builder()
                                .data(data)
                                .renderableTable((RenderableTable<T, Object>) renderableProperty)
                                .build();
                        searchableFormEntries.add(renderer);

                        return renderer
                                .render();
                    }

                    if (renderableProperty instanceof RenderableLazyLoadingTable) {
                        return LazyLoadingTableRenderer.<T, Object>builder()
                                .dataHolder(data)
                                .renderableTable((RenderableLazyLoadingTable<T, Object>) renderableProperty)
                                .build()
                                .render();
                    }

                    throw new IllegalArgumentException(String.format(
                            "Property type %s ins not supported yet.",
                            renderableProperty.getClass().getName()
                    ));
                })
                .collect(Collectors.toList());

        vbox.getChildren().setAll(fields);
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
        titleLabel.getStyleClass().add(TITLE_STYLE_CLASS);

        val value = Optional.ofNullable(property.getValueExtractor())
                .map(getter -> {
                    try {
                        return getter.apply(data);
                    } catch(Exception ex) {
                        System.out.println(property);
                        return "ERROR"; // FIXME
                    }

                }) // FIXME
                .orElse("NULL"); // FIXME

        val valueTextField = new TextField();
        valueTextField.setText(value);
        valueTextField.setEditable(false);
        valueTextField.getStyleClass().add(FIELD_STYLE_CLASS);

        vbox.getChildren().setAll(titleLabel, valueTextField);

        return vbox;
    }

    public void dropChanges() {
        this.editableFormFields.forEach(EditableFormField::reset);
        this.hasChanged.set(false);
    }

    public SaveChangesReport saveChanges() {
        val invalidFields = this.editableFormFields.stream()
                .filter(editableFormField -> !editableFormField.hasValidValue())
                .collect(Collectors.toList());

        if (!invalidFields.isEmpty()) {
            return new SaveChangesReport(I18n.get(I18nKey.of("storage.failed.validationErrors")));
        }

        val failedFields = this.editableFormFields.stream()
                .filter(editableFormField -> !editableFormField.save())
                .collect(Collectors.toList());

        try {
            this.renderableForm.getAfterSaveAction()
                    .doAfterSaveChanges(data);

            if (failedFields.isEmpty()) {
                hasChanged.set(false);
                return new SaveChangesReport();
            }
        } catch (Exception e) {
            log.error("Storage failed because of after-storage-action because {}",
                    e.getMessage());
        }
        return new SaveChangesReport(I18n.get(I18nKey.of("storage.failed.unknownError")));
    }

    public void applySearchTerm(final String searchTerm) {
        searchableFormEntries.forEach(entry -> entry.applySearchTerm(searchTerm));
    }

    public void clearSearchTerm() {
        searchableFormEntries.forEach(SearchableFormEntry::clearSearchTerm);
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
            val originalValue = property.getValueExtractor().apply(data);
            value.setText(originalValue);
            hideValidationLabel();
        }

        public boolean hasChanges() {
            val originalValue = property.getValueExtractor().apply(data);
            return !Objects.equals(originalValue, value.getText());
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
                showValidationLabel(I18nKey.of("storage.singleField.failed.unknownError"));
                return false;
            }
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
