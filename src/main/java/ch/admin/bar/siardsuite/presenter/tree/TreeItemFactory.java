package ch.admin.bar.siardsuite.presenter.tree;

import ch.admin.bar.siardsuite.model.TreeAttributeWrapper;
import ch.admin.bar.siardsuite.model.TreeContentView;
import ch.admin.bar.siardsuite.model.database.DatabaseObject;
import ch.admin.bar.siardsuite.util.I18n;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.scene.control.TreeItem;

import java.util.Collection;

public class TreeItemFactory {

    /**
     * Create a tree item
     *
     * @param label    - the label key from messages_xx.properties
     * @param view     - the {@link TreeContentView}
     * @param dbObject - the {@link DatabaseObject}
     * @param elements - collection of child elements - used to indicate the number of children in the root label
     * @return
     */
    public static TreeItem<TreeAttributeWrapper> create(String label,
                                                        TreeContentView view,
                                                        DatabaseObject dbObject,
                                                        Collection elements) {

        return create(label, view, dbObject, String.valueOf(elements.size()));
    }

    public static TreeItem<TreeAttributeWrapper> create(String label, TreeContentView view, DatabaseObject dbObject,
                                                        String numberOfElements) {
        final TreeItem<TreeAttributeWrapper> item = new TreeItem<>();
        item.valueProperty()
                .bind(createTreeAtributeWrapperBinding(label,
                        view,
                        dbObject,
                        numberOfElements));
        return item;
    }


    public static ObjectBinding<TreeAttributeWrapper> createTreeAtributeWrapperBinding(final String key,
                                                                                       final TreeContentView type,
                                                                                       DatabaseObject databaseObject,
                                                                                       final Object... args) {
        return Bindings.createObjectBinding(() -> new TreeAttributeWrapper(I18n.get(key, args), type, databaseObject),
                I18n.locale);
    }
}
