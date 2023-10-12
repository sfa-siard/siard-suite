package ch.admin.bar.siardsuite.component.rendering;

import ch.admin.bar.siardsuite.component.rendering.model.ReadOnlyStringProperty;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableForm;
import ch.admin.bar.siardsuite.component.rendering.model.RenderableFormGroup;
import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.util.I18nKey;
import ch.admin.bar.siardsuite.util.MetaSearchTerm;
import javafx.scene.control.TreeItem;
import lombok.Value;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class FormsExplorerTest {
    private static final DummyData DATA_A = new DummyData("Dummy A");
    private static final DummyData DATA_B = new DummyData("Dummy B");
    private static final DummyData DATA_C = new DummyData("Dummy C");

    private static final I18nKey PROPERTY_A1 = I18nKey.of("form.a");
    private static final I18nKey PROPERTY_B1 = I18nKey.of("form.b");
    private static final I18nKey PROPERTY_C1 = I18nKey.of("form.c");

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
            .name("treeitem.a.name")
            .viewTitle(I18nKey.of("treeitem.a.viewTitle"))
            .type(TreeContentView.FORM_RENDERER)
            .renderableForm(FORM_A)
            .build());

    private static final TreeItem<TreeAttributeWrapper> TREE_ITEM_B = new TreeItem<>(TreeAttributeWrapper.builder()
            .name("treeitem.b.name")
            .viewTitle(I18nKey.of("treeitem.b.viewTitle"))
            .type(TreeContentView.FORM_RENDERER)
            .renderableForm(FORM_B)
            .build());

    private static final TreeItem<TreeAttributeWrapper> TREE_ITEM_C = new TreeItem<>(TreeAttributeWrapper.builder()
            .name("treeitem.c.name")
            .viewTitle(I18nKey.of("treeitem.c.viewTitle"))
            .type(TreeContentView.FORM_RENDERER)
            .renderableForm(FORM_C)
            .build());

    @Test
    public void find_withTwoChildren_expectSearchResultsWithCorrectPath() {
        // given
        TREE_ITEM_A.getChildren().setAll(TREE_ITEM_B, TREE_ITEM_C);
        TREE_ITEM_B.getChildren().setAll();
        TREE_ITEM_C.getChildren().setAll();

        val explorer = FormsExplorer.from(TREE_ITEM_A);

        // when
        val result = explorer.find(new MetaSearchTerm("Dummy"));

        //then
        Assertions.assertThat(result).containsExactlyInAnyOrder(
                FormsExplorer.Result.builder()
                        .propertyName(PROPERTY_A1)
                        .pathToTreeItem(Arrays.asList())
                        .treeItem(TREE_ITEM_A)
                        .build(),
                FormsExplorer.Result.builder()
                        .propertyName(PROPERTY_B1)
                        .pathToTreeItem(Arrays.asList(TREE_ITEM_A.getValue().getName()))
                        .treeItem(TREE_ITEM_B)
                        .build(),
                FormsExplorer.Result.builder()
                        .propertyName(PROPERTY_C1)
                        .pathToTreeItem(Arrays.asList(TREE_ITEM_A.getValue().getName()))
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

        val explorer = FormsExplorer.from(TREE_ITEM_A);

        // when
        val result = explorer.find(new MetaSearchTerm("Dummy"));

        //then
        Assertions.assertThat(result).containsExactlyInAnyOrder(
                FormsExplorer.Result.builder()
                        .propertyName(PROPERTY_A1)
                        .pathToTreeItem(Arrays.asList())
                        .treeItem(TREE_ITEM_A)
                        .build(),
                FormsExplorer.Result.builder()
                        .propertyName(PROPERTY_B1)
                        .pathToTreeItem(Arrays.asList(TREE_ITEM_A.getValue().getName()))
                        .treeItem(TREE_ITEM_B)
                        .build(),
                FormsExplorer.Result.builder()
                        .propertyName(PROPERTY_C1)
                        .pathToTreeItem(Arrays.asList(TREE_ITEM_A.getValue().getName(), TREE_ITEM_B.getValue().getName()))
                        .treeItem(TREE_ITEM_C)
                        .build()
        );
    }

    @Value
    private static class DummyData {
        String data;
    }
}