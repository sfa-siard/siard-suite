package ch.admin.bar.siardsuite.util;

import java.util.prefs.BackingStoreException;

public class Preferences {

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
