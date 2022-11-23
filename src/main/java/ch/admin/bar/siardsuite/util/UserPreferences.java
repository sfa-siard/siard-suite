package ch.admin.bar.siardsuite.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class UserPreferences {

    private static final Preferences root = Preferences.userRoot();
    private static final int max_stack_size = 30;

    public static Preferences node(NodePath nodePath) {
        return root.node(nodePath.name());
    }

    public static void clear() throws BackingStoreException {
        for (String childName : root.childrenNames()) {
            root.node(childName).removeNode();
        }
    }

    public static Preferences push(NodePath nodePath, KeyIndex comparisonKeyIndex, Comparator<String> comparator, String path) throws BackingStoreException {
        final List<String> childrenNames = sortedChildrenNames(nodePath, comparisonKeyIndex, comparator);
        childrenNames.removeAll(List.of(path));
        childrenNames.add(0, path);
        for (String childName : childrenNames) {
            if (childrenNames.indexOf(childName) > max_stack_size - 1) {
                remove(nodePath, childName);
            }
        }
        return node(nodePath).node(path);
    }

    public static void remove(NodePath nodePath, String path) throws BackingStoreException {
        node(nodePath).node(path).removeNode();
    }

    public static List<String> sortedChildrenNames(NodePath nodePath, KeyIndex comparisonKeyIndex, Comparator<String> comparator) throws BackingStoreException {
        return Arrays.stream(node(nodePath).childrenNames())
                .sorted(Comparator.comparing(
                        name -> node(nodePath).node(name).get(comparisonKeyIndex.name(), ""),
                        comparator
                ))
                .collect(Collectors.toList());
    }

    public enum NodePath {
        RECENT_FILES,
        DATABASE_CONNECTION,
        OPTIONS
    }

    public enum KeyIndex {
        DATABASE_SERVER,
        DATABASE_SYSTEM,
        PORT_NUMBER,
        DATABASE_NAME,
        USER_NAME,
        USER_PASSWORD,
        CONNECTION_URL,
        STORAGE_DATE,
        ABSOLUTE_PATH,
        TIMESTAMP,
        EXPORT_PATH
    }
}
