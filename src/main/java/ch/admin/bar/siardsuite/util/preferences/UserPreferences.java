package ch.admin.bar.siardsuite.util.preferences;

import ch.admin.bar.siardsuite.database.model.DbmsId;
import ch.admin.bar.siardsuite.util.I18n;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.KeyIndex.LOGIN_TIMEOUT;
import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.KeyIndex.QUERY_TIMEOUT;
import static ch.admin.bar.siardsuite.util.preferences.UserPreferences.NodePath.OPTIONS;

@Slf4j
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

    public static void push(final DbConnection dbConnection) {
        val preferences = node(NodePath.DATABASE_CONNECTION).node(dbConnection.getName());

        preferences.put(KeyIndex.DATABASE_SYSTEM.name(), dbConnection.getDbmsProduct().getValue());
        preferences.put(KeyIndex.CONNECTION_OPTIONS.name(), dbConnection.getConnectionOptions());

        preferences.put(KeyIndex.DATABASE_SERVER.name(), dbConnection.getHost());
        preferences.put(KeyIndex.PORT_NUMBER.name(), dbConnection.getPort());
        preferences.put(KeyIndex.DATABASE_NAME.name(), dbConnection.getDbName());
        preferences.put(KeyIndex.USER_NAME.name(), dbConnection.getUser());

        preferences.put(KeyIndex.FILE.name(), dbConnection.getFile());

        preferences.put(KeyIndex.STORAGE_DATE.name(), I18n.getLocaleDate(LocalDate.now().toString()));
        preferences.put(KeyIndex.TIMESTAMP.name(), String.valueOf(Clock.systemDefaultZone().millis()));

        cropToMaxElements(NodePath.DATABASE_CONNECTION, 3);
    }

    public static List<StorageData<DbConnection>> getStoredConnections() {
        val connectionNode = node(NodePath.DATABASE_CONNECTION);

        try {
            return childrenNodeNamesNewestToOldest(NodePath.DATABASE_CONNECTION).stream()
                    .map(name -> {
                        val node = connectionNode.node(name);

                        return StorageData.<DbConnection>builder()
                                .storedAtTime(node.get(KeyIndex.TIMESTAMP.name(), ""))
                                .storedAtDate(node.get(KeyIndex.STORAGE_DATE.name(), ""))
                                .storedData(DbConnection.builder()
                                        .name(name)
                                        .dbmsProduct(DbmsId.of(node.get(KeyIndex.DATABASE_SYSTEM.name(), "")))
                                        .connectionOptions(node.get(KeyIndex.CONNECTION_OPTIONS.name(), ""))
                                        .host(node.get(KeyIndex.DATABASE_SERVER.name(), ""))
                                        .port(node.get(KeyIndex.PORT_NUMBER.name(), ""))
                                        .dbName(node.get(KeyIndex.DATABASE_NAME.name(), ""))
                                        .user(node.get(KeyIndex.USER_NAME.name(), ""))
                                        .file(node.get(KeyIndex.FILE.name(), ""))
                                        .build())
                                .build();
                    })
                    .collect(Collectors.toList());

        } catch (BackingStoreException e) {
            throw new RuntimeException("Exception while reading stored connections in user preferences", e);
        }
    }

    public static Options getStoredOptions() {
        val optionsNode = node(OPTIONS);

        return Options.builder()
                .queryTimeout(Integer.parseInt(optionsNode.get(QUERY_TIMEOUT.name(), "0")))
                .loginTimeout(Integer.parseInt(optionsNode.get(LOGIN_TIMEOUT.name(), "0")))
                .build();
    }

    public static void push(final Options options) {
        val node = UserPreferences.node(OPTIONS);
        node.put(QUERY_TIMEOUT.name(), options.getQueryTimeout() + "");
        node.put(LOGIN_TIMEOUT.name(), options.getLoginTimeout() + "");
    }

    @Value
    @Builder
    public static class StorageData<T> {
        String storedAtDate;
        String storedAtTime;
        T storedData;
    }

    public static Preferences push(NodePath nodePath, KeyIndex comparisonKeyIndex, Comparator<String> comparator, String path) throws BackingStoreException {
        final List<String> childrenNames = sortedChildrenNames(nodePath, comparisonKeyIndex, comparator);
        List<String> list = new ArrayList<>();
        list.add(path);
        childrenNames.removeAll(list);
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
                        Comparator.reverseOrder()
                ))
                .collect(Collectors.toList());
    }

    private static void cropToMaxElements(final NodePath nodePath, final int maxNr) {
        val parentNode = node(nodePath);

        try {
            val sortedChildNodeNames = childrenNodeNamesOldestToNewest(nodePath);

            val nrToRemove = new AtomicInteger(sortedChildNodeNames.size() - maxNr);
            for (val childNodeName : sortedChildNodeNames) {
                if (nrToRemove.getAndDecrement() <= 0) {
                    return;
                }

                val childNode = parentNode.node(childNodeName);
                log.debug("Removing node {} from parent {} in user preferences", childNode, nodePath);
                childNode.removeNode();
            }
        } catch (BackingStoreException e) {
            log.error("Failed to crop path {} to max {} child nodes because: {}", nodePath, maxNr, e.getMessage());
        }
    }

    public static List<String> childrenNodeNamesOldestToNewest(NodePath nodePath) throws BackingStoreException {
        val parentNode = node(nodePath);
        return Arrays.stream(parentNode.childrenNames())
                .sorted(Comparator.comparing(
                        name -> Long.parseLong(parentNode.node(name).get(KeyIndex.TIMESTAMP.name(), "0")),
                        Comparator.naturalOrder()
                ))
                .collect(Collectors.toList());
    }

    public static List<String> childrenNodeNamesNewestToOldest(NodePath nodePath) throws BackingStoreException {
        val parentNode = node(nodePath);
        return Arrays.stream(parentNode.childrenNames())
                .sorted(Comparator.comparing(
                        name -> Long.parseLong(parentNode.node(name).get(KeyIndex.TIMESTAMP.name(), "0")),
                        Comparator.reverseOrder()
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
        FILE,
        CONNECTION_URL,
        CONNECTION_OPTIONS,
        STORAGE_DATE,
        ABSOLUTE_PATH,
        TIMESTAMP,
        EXPORT_PATH,
        QUERY_TIMEOUT,
        LOGIN_TIMEOUT
    }
}
