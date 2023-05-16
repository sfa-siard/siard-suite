package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.database.DatabaseObject;
import ch.admin.bar.siardsuite.util.I18n;
import javafx.scene.control.TreeItem;

import java.util.Collection;

public class TreeItemFactory {

    /**
     * Create a tree item
     *
     * @param label    - the label key from messages_xx.properties
     * @param view     - the {@link TreeContentView}
     * @param dbObject - the {@link DatabaseObject}
     * @param elements - elementes to display
     * @return
     */
    public static TreeItem<TreeAttributeWrapper> create(String label,
                                                        TreeContentView view,
                                                        DatabaseObject dbObject,
                                                        Collection elements) {

        final TreeItem<TreeAttributeWrapper> item = new TreeItem<>();
        item.valueProperty()
            .bind(I18n.createTreeAtributeWrapperBinding(label,
                                                        view,
                                                        dbObject,
                                                        elements.size()));
        return item;
    }
}
