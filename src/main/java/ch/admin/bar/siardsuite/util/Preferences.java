package ch.admin.bar.siardsuite.util;

import java.util.List;
import java.util.prefs.BackingStoreException;

public class Preferences {

    public static int max_cache_size = 30;
    public static final String[] cache = new String[max_cache_size];
    public static void clear(PreferencesNode node) {
        try {
            java.util.prefs.Preferences.userRoot().node(node.toString()).clear();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public static java.util.prefs.Preferences get(PreferencesNode node) {
        return java.util.prefs.Preferences.userRoot().node(node.toString());
    }

    public static void add(PreferencesNode node, String value) {
        final int k = List.of(cache).indexOf(value);
        final int l = (k < 0) ? max_cache_size - 1 : k;
        if (l > 0) {
            for (int j = 0; j < l; j++) {
                get(node).put(String.valueOf(j+1), cache[j]);
            }
        }
        get(node).put("0", value);
    }

    public static void remove(PreferencesNode node, String value)  {
        final int l = List.of(cache).indexOf(value);
        if (l >= 0) {
            get(node).remove(String.valueOf(l));
            for (int j = l + 1; j < max_cache_size; j++) {
                get(node).put(String.valueOf(j-1), cache[j]);
            }
        }
    }

    public enum PreferencesNode {
        RECENT_FILES("recent_files"),
        DATABASE_CONNECTION("database_connection");

        private final String name;

        PreferencesNode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
