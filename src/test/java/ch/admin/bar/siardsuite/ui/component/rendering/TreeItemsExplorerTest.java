package ch.admin.bar.siardsuite.ui.component.rendering;

import ch.admin.bar.siardsuite.ui.component.rendering.TreeItemsExplorer;
import ch.admin.bar.siardsuite.ui.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.ui.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.ui.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import ch.admin.bar.siardsuite.ui.common.MetaSearchTerm;
import javafx.scene.control.TreeItem;
import lombok.Value;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class TreeItemsExplorerTest {
    private static final DummyData DATA_A = new DummyData("Dummy A");
    private static final DummyData DATA_B = new DummyData("Dummy B");
    private static final DummyData DATA_C = new DummyData("Dummy C");

    private static final DisplayableText PROPERTY_A1 = DisplayableText.of("form.a");
    private static final DisplayableText PROPERTY_B1 = DisplayableText.of("form.b");
    private static final DisplayableText PROPERTY_C1 = DisplayableText.of("form.c");

    private static final RenderableForm<DummyData> FORM_A = RenderableForm.<DummyData>builder()
            .dataSupplier(() -> DATA_A)
            .group(RenderableFormGroup.<DummyData>builder()
                    .property(new ReadOnlyStringProperty<>(
                            PROPERTY_A1,
                            DummyData::getData
                    ))
                    .build())
            .build();

    private static final RenderableForm<DummyData> FORM_B = RenderableForm.<DummyData>builder()
            .dataSupplier(() -> DATA_B)
            .group(RenderableFormGroup.<DummyData>builder()
                    .property(new ReadOnlyStringProperty<>(
                            PROPERTY_B1,
                            DummyData::getData
                    ))
                    .build())
            .build();

    private static final RenderableForm<DummyData> FORM_C = RenderableForm.<DummyData>builder()
            .dataSupplier(() -> DATA_C)
            .group(RenderableFormGroup.<DummyData>builder()
                    .property(new ReadOnlyStringProperty<>(
                            PROPERTY_C1,
                            DummyData::getData
                    ))
                    .build())
            .build();

    private static final TreeItem<TreeAttributeWrapper> TREE_ITEM_A = new TreeItem<>(TreeAttributeWrapper.builder()
            .name(DisplayableText.of("treeitem.a.name"))
            .viewTitle(DisplayableText.of("treeitem.a.viewTitle"))
            .renderableForm(FORM_A)
            .build());

    private static final TreeItem<TreeAttributeWrapper> TREE_ITEM_B = new TreeItem<>(TreeAttributeWrapper.builder()
            .name(DisplayableText.of("treeitem.b.name"))
            .viewTitle(DisplayableText.of("treeitem.b.viewTitle"))
            .renderableForm(FORM_B)
            .build());

    private static final TreeItem<TreeAttributeWrapper> TREE_ITEM_C = new TreeItem<>(TreeAttributeWrapper.builder()
            .name(DisplayableText.of("treeitem.c.name"))
            .viewTitle(DisplayableText.of("treeitem.c.viewTitle"))
            .renderableForm(FORM_C)
            .build());

    @Test
    public void find_withTwoChildren_expectSearchResultsWithCorrectPath() {
        // given
        TREE_ITEM_A.getChildren().setAll(TREE_ITEM_B, TREE_ITEM_C);
        TREE_ITEM_B.getChildren().setAll();
        TREE_ITEM_C.getChildren().setAll();

        val explorer = TreeItemsExplorer.from(TREE_ITEM_A);

        // when
        val result = explorer.find(new MetaSearchTerm("Dummy"));

        //then
        Assertions.assertThat(result).containsExactlyInAnyOrder(
                TreeItemsExplorer.Result.builder()
                        .propertyName(PROPERTY_A1)
                        .pathToTreeItem(buildPath(TREE_ITEM_A))
                        .treeItem(TREE_ITEM_A)
                        .build(),
                TreeItemsExplorer.Result.builder()
                        .propertyName(PROPERTY_B1)
                        .pathToTreeItem(buildPath(
                                TREE_ITEM_A,
                                TREE_ITEM_B))
                        .treeItem(TREE_ITEM_B)
                        .build(),
                TreeItemsExplorer.Result.builder()
                        .propertyName(PROPERTY_C1)
                        .pathToTreeItem(buildPath(
                                TREE_ITEM_A,
                                TREE_ITEM_C))
                        .treeItem(TREE_ITEM_C)
                        .build()
        );
    }

    @Test
    public void find_withChildOfChild_expectSearchResultsWithCorrectPath() {
        // given
        TREE_ITEM_A.getChildren().setAll(TREE_ITEM_B);
        TREE_ITEM_B.getChildren().setAll(TREE_ITEM_C);
        TREE_ITEM_C.getChildren().setAll();

        val explorer = TreeItemsExplorer.from(TREE_ITEM_A);

        // when
        val result = explorer.find(new MetaSearchTerm("Dummy"));

        //then
        Assertions.assertThat(result).containsExactlyInAnyOrder(
                TreeItemsExplorer.Result.builder()
                        .propertyName(PROPERTY_A1)
                        .pathToTreeItem(buildPath(TREE_ITEM_A))
                        .treeItem(TREE_ITEM_A)
                        .build(),
                TreeItemsExplorer.Result.builder()
                        .propertyName(PROPERTY_B1)
                        .pathToTreeItem(buildPath(
                                TREE_ITEM_A,
                                TREE_ITEM_B))
                        .treeItem(TREE_ITEM_B)
                        .build(),
                TreeItemsExplorer.Result.builder()
                        .propertyName(PROPERTY_C1)
                        .pathToTreeItem(buildPath(
                                TREE_ITEM_A,
                                TREE_ITEM_B,
                                TREE_ITEM_C))
                        .treeItem(TREE_ITEM_C)
                        .build()
        );
    }

    private List<String> buildPath(TreeItem<TreeAttributeWrapper>... items) {
        return Arrays.stream(items)
                .map(treeItem -> treeItem.getValue().getName().getText())
                .collect(Collectors.toList());
    }

    @Value
    private static class DummyData {
        String data;
    }
}