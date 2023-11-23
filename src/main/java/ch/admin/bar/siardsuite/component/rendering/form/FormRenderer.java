package ch.admin.bar.siardsuite.component.rendering.form;

import ch.admin.bar.siardsuite.component.rendering.SaveChangesReport;
import ch.admin.bar.siardsuite.component.rendering.SearchableFormEntry;
import ch.admin.bar.siardsuite.component.rendering.model.*;
import ch.admin.bar.siardsuite.component.rendering.table.LazyLoadingTableRenderer;
import ch.admin.bar.siardsuite.component.rendering.table.TableRenderer;
import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.view.ErrorDialogOpener;
import javafx.beans.property.BooleanProperty;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FormRenderer<T> {

    private static final I18nKey VALIDATION_ERRORS = I18nKey.of("storage.failed.validationErrors");
    private static final I18nKey UNKNOWN_ERROR = I18nKey.of("storage.failed.unknownError");
    private static final I18nKey SINGLE_FIELD_UNKNOWN_ERROR = I18nKey.of("storage.singleField.failed.unknownError");

    private static final String TITLE_STYLE_CLASS = "table-container-label";
    private static final String VALIDATION_STYLE_CLASS = "validation-text";
    private static final String FIELD_STYLE_CLASS = "rendered-field";

    @Getter
    private final RenderableForm<T> renderableForm;
    private final T data;

    private final ErrorDialogOpener errorDialogOpener;

    private final List<EditableFormField> editableFormFields = new ArrayList<>();
    private final List<SearchableFormEntry> searchableFormEntries = new ArrayList<>();

    private final BooleanProperty hasChanged;

    @Getter
    private final VBox rendered;

    @Builder
    public FormRenderer(
            @NonNull final RenderableForm<T> renderableForm,
            @NonNull final ErrorDialogOpener errorDialogOpener,
            @NonNull final BooleanProperty hasChanged
    ) {
        this.renderableForm = renderableForm;
        this.errorDialogOpener = errorDialogOpener;
        this.hasChanged = hasChanged;

        this.data = renderableForm.getDataSupplier().get();
        this.rendered = renderForm();
    }

    private VBox renderForm() {
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
                                .errorDialogOpener(errorDialogOpener)
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
        if (renderableForm.isReadOnlyForm()) {
            return new ReadOnlyFormField<>(
                    property.getTitle(),
                    property.getValueExtractor(),
                    data);
        }

        val formField = new EditableFormField<>(property, data, hasChanged);
        editableFormFields.add(formField);

        return formField;
    }

    private VBox createField(final ReadOnlyStringProperty<T> property, final T data) {
        return new ReadOnlyFormField<>(property, data);
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
            return new SaveChangesReport(DisplayableText.of(VALIDATION_ERRORS).getText());
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
        return new SaveChangesReport(DisplayableText.of(UNKNOWN_ERROR).getText());
    }

    public void applySearchTerm(final String searchTerm) {
        searchableFormEntries.forEach(entry -> entry.applySearchTerm(searchTerm));
    }

    public void clearSearchTerm() {
        searchableFormEntries.forEach(SearchableFormEntry::clearSearchTerm);
    }

    public boolean hasSearchableData() {
        return !searchableFormEntries.isEmpty();
    }


}
