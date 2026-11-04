package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.cmd.SiardCliTest.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static ch.admin.bar.siard2.cmd.SiardCliTest.execute;
import static org.assertj.core.api.Assertions.assertThat;

class ArchiveCommandParseTest {

    private static final String UNREACHABLE_PG_URL = "jdbc:postgresql://localhost:1/db";

    @Test
    void archiveHelp_listsOptionsAndExits0() {
        // when
        Result result = execute("archive", "--help");

        // then
        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(result.out()).contains("--jdbc-url")
                                .contains("--siard-file")
                                .contains("--export-xml");
    }

    @Test
    void unknownOption_namesOptionAndExits2() {
        // when
        Result result = execute("archive", "--jdbcurl=x");

        // then
        assertThat(result.exitCode()).isEqualTo(2);
        assertThat(result.err()).contains("--jdbcurl");
        assertThat(result.err()).contains("Possible solutions: --jdbc-url");
    }

    @Test
    void missingJdbcUrl_namesOptionAndExits2(@TempDir Path tempDir) {
        // when
        Result result = execute("archive",
                                "--user=u", "--password=p",
                                "--siard-file=" + tempDir.resolve("out.siard"));

        // then
        assertThat(result.exitCode()).isEqualTo(2);
        assertThat(result.err()).contains("--jdbc-url");
    }

    @Test
    void unsupportedJdbcScheme_namesSchemeAndExits2(@TempDir Path tempDir) {
        // when
        Result result = execute("archive",
                                "--jdbc-url=jdbc:foo:bar", "--user=u", "--password=p",
                                "--siard-file=" + tempDir.resolve("out.siard"));

        // then
        assertThat(result.exitCode()).isEqualTo(2);
        assertThat(result.err()).contains("jdbc:foo");
    }

    @Test
    void neitherSiardFileNorExportXml_exits2() {
        // when
        Result result = execute("archive",
                                "--jdbc-url=" + UNREACHABLE_PG_URL, "--user=u", "--password=p");

        // then
        assertThat(result.exitCode()).isEqualTo(2);
        assertThat(result.err()).contains("--siard-file")
                                .contains("--export-xml");
    }

    @Test
    void missingImportXml_namesFileAndExits2(@TempDir Path tempDir) {
        // given
        Path missing = tempDir.resolve("missing.xml");

        // when
        Result result = execute("archive",
                                "--jdbc-url=" + UNREACHABLE_PG_URL, "--user=u", "--password=p",
                                "--siard-file=" + tempDir.resolve("out.siard"),
                                "--import-xml=" + missing);

        // then
        assertThat(result.exitCode()).isEqualTo(2);
        assertThat(result.err()).contains("missing.xml");
    }

    @Test
    void existingSiardFileWithoutOverwrite_suggestsOverwriteAndExits1(@TempDir Path tempDir) throws IOException {
        // given
        Path existing = Files.createFile(tempDir.resolve("existing.siard"));

        // when
        Result result = execute("archive",
                                "--jdbc-url=" + UNREACHABLE_PG_URL, "--user=u", "--password=p",
                                "--siard-file=" + existing);

        // then
        assertThat(result.exitCode()).isEqualTo(1);
        assertThat(result.err()).contains("--overwrite");
    }
}
