package ch.admin.bar.siard2.cmd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfigRecordsTest {

    private static final String URL = "jdbc:postgresql://host/db";

    @Test
    void archiveConfig_constructsValidParameters(@TempDir Path tempDir) {
        // when
        ArchiveConfig config = new ArchiveConfig(URL,
                                                 "u",
                                                 "p",
                                                 tempDir.resolve("out.siard"),
                                                 null,
                                                 false,
                                                 false,
                                                 300,
                                                 75,
                                                 null,
                                                 null,
                                                 null,
                                                 null);

        // then
        assertThat(config.jdbcUrl()).isEqualTo(URL);
        assertThat(config.siardFile()).isEqualTo(tempDir.resolve("out.siard"));
    }

    @Test
    void archiveConfig_rejectsMissingOutputs() {
        // when/then
        assertThatThrownBy(() -> new ArchiveConfig(URL,
                                                   "u",
                                                   "p",
                                                   null,
                                                   null,
                                                   false,
                                                   false,
                                                   300,
                                                   75,
                                                   null,
                                                   null,
                                                   null,
                                                   null)).isInstanceOf(IllegalArgumentException.class)
                                                         .hasMessageContaining("siardFile");
    }

    @Test
    void archiveConfig_rejectsLobFolderWithoutSiardFile(@TempDir Path tempDir) {
        // when/then
        assertThatThrownBy(() -> new ArchiveConfig(URL,
                                                   "u",
                                                   "p",
                                                   null,
                                                   tempDir.resolve("meta.xml"),
                                                   false,
                                                   false,
                                                   300,
                                                   75,
                                                   null,
                                                   tempDir.resolve("lobs"),
                                                   null,
                                                   null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void restoreConfig_constructsWithSchemaMapping(@TempDir Path tempDir) {
        // when
        RestoreConfig config = new RestoreConfig(URL,
                                                 "u",
                                                 "p",
                                                 tempDir.resolve("in.siard"),
                                                 true,
                                                 300,
                                                 75,
                                                 Map.of("HR", "HR_COPY"));

        // then
        assertThat(config.schemaMapping()).containsEntry("HR", "HR_COPY");
    }
}
