package ch.admin.bar.siardsuite.util.preferences;

import ch.admin.bar.siardsuite.util.I18n;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Slf4j
public class UserPreferences {

    public static final UserPreferences INSTANCE = new UserPreferences(Preferences::userRoot);

    private static final int max_stack_size = 30;

    private final PreferencesWrapper preferencesWrapper;

    private Preferences node(NodePath nodePath) {
        return preferencesWrapper.root().node(nodePath.name());
    }

    public void push(final RecentDbConnection recentDbConnection) {
        val preferences = node(NodePath.DATABASE_CONNECTION).node(recentDbConnection.getName());

        preferences.put(KeyIndex.DATABASE_SYSTEM.name(), recentDbConnection.getDbmsProduct());
        preferences.put(KeyIndex.CONNECTION_OPTIONS.name(), recentDbConnection.getConnectionOptions());

        preferences.put(KeyIndex.DATABASE_SERVER.name(), recentDbConnection.getHost());
        preferences.put(KeyIndex.PORT_NUMBER.name(), recentDbConnection.getPort());
        preferences.put(KeyIndex.DATABASE_NAME.name(), recentDbConnection.getDbName());
        preferences.put(KeyIndex.USER_NAME.name(), recentDbConnection.getUser());

        preferences.put(KeyIndex.FILE.name(), recentDbConnection.getFile());

        preferences.put(KeyIndex.STORAGE_DATE.name(), I18n.getLocaleDate(LocalDate.now().toString()));
        preferences.put(KeyIndex.TIMESTAMP.name(), String.valueOf(Clock.systemDefaultZone().millis()));

        cropToMaxElements(NodePath.DATABASE_CONNECTION, 3);
    }

    public void push(final RecentFile recentFile) {
        val node = node(NodePath.RECENT_FILES).node(String.valueOf(recentFile.hashCode()));

        node.put(KeyIndex.ABSOLUTE_PATH.name(), recentFile.getValue().getAbsolutePath());
        node.put(KeyIndex.TIMESTAMP.name(), String.valueOf(Clock.systemDefaultZone().millis()));

        cropToMaxElements(NodePath.RECENT_FILES, 3);
    }

    public List<StorageData<RecentFile>> getRecentFiles() {
        val recentFiles = node(NodePath.RECENT_FILES);

        try {
            return childrenNodeNamesNewestToOldest(NodePath.RECENT_FILES).stream()
                    .map(name -> {
                        val node = recentFiles.node(name);

                        val file = new File(node.get(KeyIndex.ABSOLUTE_PATH.name(), ""));

                        return StorageData.<RecentFile>builder()
                                .storedAt(readTimestampFromNode(node))
                                .storedData(new RecentFile(file))
                                .build();
                    })
                    .collect(Collectors.toList());

        } catch (BackingStoreException e) {
            throw new RuntimeException("Exception while reading stored connections in user preferences", e);
        }
    }

    public List<StorageData<RecentDbConnection>> getStoredConnections() {
        val connectionNode = node(NodePath.DATABASE_CONNECTION);

        try {
            return childrenNodeNamesNewestToOldest(NodePath.DATABASE_CONNECTION).stream()
                    .map(name -> {
                        val node = connectionNode.node(name);

                        return StorageData.<RecentDbConnection>builder()
                                .storedAt(readTimestampFromNode(node))
                                .storedData(RecentDbConnection.builder()
                                        .name(name)
                                        .dbmsProduct(node.get(KeyIndex.DATABASE_SYSTEM.name(), ""))
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

    public Options getStoredOptions() {
        val optionsNode = node(NodePath.OPTIONS);

        return Options.builder()
                .queryTimeout(Integer.parseInt(optionsNode.get(KeyIndex.QUERY_TIMEOUT.name(), "0")))
                .loginTimeout(Integer.parseInt(optionsNode.get(KeyIndex.LOGIN_TIMEOUT.name(), "0")))
                .build();
    }

    public void push(final Options options) {
        val node = node(NodePath.OPTIONS);
        node.put(KeyIndex.QUERY_TIMEOUT.name(), options.getQueryTimeout() + "");
        node.put(KeyIndex.LOGIN_TIMEOUT.name(), options.getLoginTimeout() + "");
    }

    private Preferences push(NodePath nodePath, KeyIndex comparisonKeyIndex, Comparator<String> comparator, String path) throws BackingStoreException {
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

    private void remove(NodePath nodePath, String path) throws BackingStoreException {
        node(nodePath).node(path).removeNode();
    }

    private List<String> sortedChildrenNames(NodePath nodePath, KeyIndex comparisonKeyIndex, Comparator<String> comparator) throws BackingStoreException {
        return Arrays.stream(node(nodePath).childrenNames())
                .sorted(Comparator.comparing(
                        name -> node(nodePath).node(name).get(comparisonKeyIndex.name(), ""),
                        Comparator.reverseOrder()
                ))
                .collect(Collectors.toList());
    }

    private void cropToMaxElements(final NodePath nodePath, final int maxNr) {
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

    private List<String> childrenNodeNamesOldestToNewest(NodePath nodePath) throws BackingStoreException {
        val parentNode = node(nodePath);
        return Arrays.stream(parentNode.childrenNames())
                .sorted(Comparator.comparing(
                        name -> Long.parseLong(parentNode.node(name).get(KeyIndex.TIMESTAMP.name(), "0")),
                        Comparator.naturalOrder()
                ))
                .collect(Collectors.toList());
    }

    private List<String> childrenNodeNamesNewestToOldest(NodePath nodePath) throws BackingStoreException {
        val parentNode = node(nodePath);
        return Arrays.stream(parentNode.childrenNames())
                .sorted(Comparator.comparing(
                        name -> Long.parseLong(parentNode.node(name).get(KeyIndex.TIMESTAMP.name(), "0")),
                        Comparator.reverseOrder()
                ))
                .collect(Collectors.toList());
    }

    private ZonedDateTime readTimestampFromNode(Preferences node) {
        val timestampInMs = Long.parseLong(node.get(KeyIndex.TIMESTAMP.name(), "0"));
        val instant = Instant.ofEpochMilli(timestampInMs);
        return instant.atZone(ZoneId.systemDefault());
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

    public interface PreferencesWrapper {
        Preferences root();
    }
}
