package ch.admin.bar.siardsuite.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class UserPreferences {

    private static final int max_stack_size = 4;

    public static Preferences node(NodePath nodePath) {
        return Preferences.userRoot().node(nodePath.path());
    }

    public static void clear() throws BackingStoreException {
        for (String childName : Preferences.userRoot().childrenNames()) {
            Preferences.userRoot().node(childName).removeNode();
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
                        name -> node(nodePath).node(name).get(comparisonKeyIndex.index(), ""),
                        comparator
                ))
                .collect(Collectors.toList());
    }

    public enum NodePath {
        RECENT_FILES("0"),
        DATABASE_CONNECTION("1");

        private final String path;

        NodePath(String path) {
            this.path = path;
        }

        public String path() {
            return path;
        }
    }

    public enum KeyIndex {
        DATABASE_SERVER("0"),
        DATABASE_NAME("1"),
        USER_NAME("2"),
        USER_PASSWORD("3"),
        CONNECTION_URL("4"),
        STORAGE_DATE("5"),
        ABSOLUTE_PATH("6"),
        TIMESTAMP("7");

        private final String index;

        KeyIndex(String index) {
            this.index = index;
        }

        public String index() {
            return index;
        }
    }

}
