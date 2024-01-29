package ch.admin.bar.siardsuite.util.preferences;

import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.assertions.api.Assertions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

class UserPreferencesTest {

    private static final Options OPTIONS = Options.builder()
            .loginTimeout(1234)
            .queryTimeout(4321)
            .build();

    private static final RecentFile RECENT_FILE_1 = new RecentFile(new File("/i/am/file1"));
    private static final RecentFile RECENT_FILE_2 = new RecentFile(new File("/i/am/file2"));
    private static final RecentFile RECENT_FILE_3 = new RecentFile(new File("/i/am/file3"));
    private static final RecentFile RECENT_FILE_4 = new RecentFile(new File("/i/am/file4"));

    private static final RecentDbConnection RECENT_CONNECTION_1 = RecentDbConnection.builder()
            .name("RECENT_CONNECTION_1")
            .dbmsProduct("dbmsid_1")
            .connectionOptions("options_1")
            .host("host_1")
            .port("port_1")
            .dbName("dbname_1")
            .user("user_1")
            .file("file_1")
            .build();

    private static final RecentDbConnection RECENT_CONNECTION_2 = RecentDbConnection.builder()
            .name("RECENT_CONNECTION_2")
            .dbmsProduct("dbmsid_2")
            .connectionOptions("options_2")
            .host("host_2")
            .port("port_2")
            .dbName("dbname_2")
            .user("user_2")
            .file("file_2")
            .build();

    private static final RecentDbConnection RECENT_CONNECTION_3 = RecentDbConnection.builder()
            .name("RECENT_CONNECTION_3")
            .dbmsProduct("dbmsid_3")
            .connectionOptions("options_3")
            .host("host_3")
            .port("port_3")
            .dbName("dbname_3")
            .user("user_3")
            .file("file_3")
            .build();

    private static final RecentDbConnection RECENT_CONNECTION_4 = RecentDbConnection.builder()
            .name("RECENT_CONNECTION_4")
            .dbmsProduct("dbmsid_4")
            .connectionOptions("options_4")
            .host("host_4")
            .port("port_4")
            .dbName("dbname_4")
            .user("user_4")
            .file("file_4")
            .build();

    private final Preferences preferencesMock = new PreferencesMockBuilder().build();
    private final UserPreferences userPreferences = new UserPreferences(() -> preferencesMock);

    @Test
    public void getStoredOptions_withPreviouslyStoredOptions_expectPreviouslyStored() {
        // given
        userPreferences.push(OPTIONS);

        // when
        val options = userPreferences.getStoredOptions();

        // then
        Assertions.assertThat(options).isEqualTo(OPTIONS);
    }

    @Test
    public void getStoredOptions_withNoStoredOptions_expectDefault() {
        // given

        // when
        val options = userPreferences.getStoredOptions();

        // then
        Assertions.assertThat(options).isEqualTo(Options.builder()
                .loginTimeout(0)
                .queryTimeout(0)
                .build());
    }

    @Test
    public void getRecentFiles_withNoStoredFiles_expectEmpty() {
        // given

        // when
        val files = userPreferences.getRecentFiles();

        // then
        Assertions.assertThat(files).isEmpty();
    }

    @Test
    public void getRecentFiles_withPreviouslyStoredFiles_expectPreviouslyStored() {
        // given
        userPreferences.push(RECENT_FILE_1);
        userPreferences.push(RECENT_FILE_2);
        userPreferences.push(RECENT_FILE_3);
        userPreferences.push(RECENT_FILE_4);

        // when
        val files = userPreferences.getRecentFiles(); // last stored first and cropped to max

        // then
        Assertions.assertThat(files.stream()
                        .map(recentFileStorageData -> {
                            // Test needs to run on windows and on unix-os
                            val osIndependentFile = new File(recentFileStorageData.getStoredData().getValue().getAbsolutePath().replace("C:", ""));
                            return new RecentFile(osIndependentFile);
                        })
                        .collect(Collectors.toList()))
                .containsExactly(
                        RECENT_FILE_4,
                        RECENT_FILE_3,
                        RECENT_FILE_2
                );
    }

    @Test
    public void getRecentConnections_withNoStoredConnections_expectEmpty() {
        // given

        // when
        val connections = userPreferences.getStoredConnections();

        // then
        Assertions.assertThat(connections).isEmpty();
    }

    @Test
    public void getRecentConnections_withPreviouslyStoredConnections_expectPreviouslyStored() {
        // given
        userPreferences.push(RECENT_CONNECTION_1);
        userPreferences.push(RECENT_CONNECTION_2);
        userPreferences.push(RECENT_CONNECTION_3);
        userPreferences.push(RECENT_CONNECTION_4);

        // when
        val connections = userPreferences.getStoredConnections(); // last stored first and cropped to max

        // then
        Assertions.assertThat(connections.stream()
                        .map(StorageData::getStoredData)
                        .collect(Collectors.toList()))
                .containsExactly(
                        RECENT_CONNECTION_4,
                        RECENT_CONNECTION_3,
                        RECENT_CONNECTION_2
                );
    }

    private static class PreferencesMockBuilder {
        private final Map<String, String> valuesByQualifiedKey = new HashMap<>();

        public Preferences build() {
            return getSubNodeMock("");
        }

        @SneakyThrows
        private Preferences getSubNodeMock(final String qualifiedNodeName) {
            val nodeMock = Mockito.mock(Preferences.class);

            // get sub node
            Mockito.when(nodeMock.node(Mockito.anyString())).then(getSubNodeCall -> {
                val subNodeName = getSubNodeCall.getArgument(0);
                return getSubNodeMock(qualifiedNodeName + "." + subNodeName);
            });

            // get value
            Mockito.when(nodeMock.get(Mockito.anyString(), Mockito.anyString())).then(getValueCall -> {
                val propertyName = getValueCall.getArgument(0);

                return Optional.ofNullable(valuesByQualifiedKey.get(qualifiedNodeName + "." + propertyName))
                        .orElse(getValueCall.getArgument(1));
            });

            // get children names childrenNames
            Mockito.when(nodeMock.childrenNames()).then(getValueCall -> {
                val childNames = valuesByQualifiedKey.entrySet().stream()
                        .filter(entry -> entry.getKey().startsWith(qualifiedNodeName))
                        .map(entry -> entry.getKey().replace(qualifiedNodeName + ".", ""))
                        .map(key -> key.split("\\."))
                        .filter(split -> split.length > 1) // if length == 1, then it's not a child but a stored value
                        .map(split -> split[0])
                        .collect(Collectors.toSet());

                return childNames.toArray(new String[0]);
            });

            // put value
            Mockito.doAnswer(putValueCall -> {
                String valueName = putValueCall.getArgument(0);
                String value = putValueCall.getArgument(1);

                valuesByQualifiedKey.put(qualifiedNodeName + "." + valueName, value);

                return null;
            }).when(nodeMock).put(Mockito.anyString(), Mockito.anyString());

            // remove node removeNode
            Mockito.doAnswer(removeNodeCall -> {
                val keysToRemove = valuesByQualifiedKey.keySet().stream()
                        .filter(key -> key.startsWith(qualifiedNodeName))
                        .collect(Collectors.toSet());

                keysToRemove.forEach(valuesByQualifiedKey::remove);

                return null;
            }).when(nodeMock).removeNode();

            return nodeMock;
        }
    }
}