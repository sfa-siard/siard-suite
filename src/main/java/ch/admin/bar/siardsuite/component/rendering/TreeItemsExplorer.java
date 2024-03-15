package ch.admin.bar.siardsuite.component.rendering;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.ReadWriteStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.util.MetaSearchTerm;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import javafx.scene.control.TreeItem;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class which helps to explore {@link TreeItem} structures.
 */
@RequiredArgsConstructor
public class TreeItemsExplorer {

    private final Set<FormField2TreeItemRelation> formFields;

    public Collection<Result> find(final MetaSearchTerm searchTerm) {
        return formFields.stream()
                .filter(formField -> searchTerm.matches(formField.getValueSupplier().get()))
                .map(formField2TreeItemRelation -> Result.builder()
                        .pathToTreeItem(formField2TreeItemRelation.getPathToTreeItem())
                        .propertyName(formField2TreeItemRelation.getPropertyTitle())
                        .treeItem(formField2TreeItemRelation.getTreeItem())
                        .build())
                .collect(Collectors.toList());
    }

    public static TreeItemsExplorer from(final TreeItem<TreeAttributeWrapper> rootItem) {
        val relations = extractForms(rootItem, new ArrayList<>())
                .collect(Collectors.toSet());

        return new TreeItemsExplorer(relations);
    }

    private static Stream<FormField2TreeItemRelation> extractForms(
            final TreeItem<TreeAttributeWrapper> treeItem,
            final List<String> pathToTreeItem
    ) {
        val updatedPathToTreeItem = copyAndAddElement(
                pathToTreeItem,
                treeItem.getValue().getName().getText());

        val formFields = findFormFields(treeItem.getValue().getRenderableForm());

        val itemForm = formFields.stream()
                .map(field -> FormField2TreeItemRelation.builder()
                        .treeItem(treeItem)
                        .pathToTreeItem(updatedPathToTreeItem)
                        .propertyTitle(field.getTitle())
                        .valueSupplier(field.getValueSupplier())
                        .build());

        val childForms = treeItem.getChildren().stream()
                .flatMap(childItem -> extractForms(
                        childItem,
                        updatedPathToTreeItem));

        return Stream.concat(itemForm, childForms);
    }

    private static <T> List<T> copyAndAddElement(List<T> origList, T additionalElement) {
        return Stream.concat(
                        origList.stream(),
                        Stream.of(additionalElement))
                .collect(Collectors.toList());
    }

    private static <T> List<FormField> findFormFields(
            final RenderableForm<T> form
    ) {
        val data = form.getDataSupplier().get();

        return Stream.concat(
                        findReadOnlyStringProperties(form).stream()
                                .map(property -> new FormField(
                                        property.getTitle(),
                                        () -> property.getValueExtractor().apply(data))),
                        findReadWriteStringProperties(form).stream()
                                .map(property -> new FormField(
                                        property.getTitle(),
                                        () -> property.getValueExtractor().apply(data))))
                .collect(Collectors.toList());
    }

    private static <T> List<ReadOnlyStringProperty<T>> findReadOnlyStringProperties(RenderableForm<T> form) {
        return form.getGroups().stream()
                .flatMap(group -> group.getProperties().stream())
                .filter(renderableProperty -> renderableProperty instanceof ReadOnlyStringProperty)
                .map(renderableProperty -> (ReadOnlyStringProperty<T>) renderableProperty)
                .collect(Collectors.toList());
    }

    private static <T> List<ReadWriteStringProperty<T>> findReadWriteStringProperties(RenderableForm<T> form) {
        return form.getGroups().stream()
                .flatMap(group -> group.getProperties().stream())
                .filter(renderableProperty -> renderableProperty instanceof ReadWriteStringProperty)
                .map(renderableProperty -> (ReadWriteStringProperty<T>) renderableProperty)
                .collect(Collectors.toList());
    }

    @Value
    @Builder
    public static class Result {
        @NonNull TreeItem<TreeAttributeWrapper> treeItem;
        @NonNull List<String> pathToTreeItem;
        @NonNull DisplayableText propertyName;
    }

    @Value
    private static class FormField {
        DisplayableText title;
        Supplier<String> valueSupplier;
    }

    @Value
    @Builder
    private static class FormField2TreeItemRelation {
        Supplier<String> valueSupplier;
        TreeItem<TreeAttributeWrapper> treeItem;
        DisplayableText propertyTitle;
        List<String> pathToTreeItem;
    }
}
