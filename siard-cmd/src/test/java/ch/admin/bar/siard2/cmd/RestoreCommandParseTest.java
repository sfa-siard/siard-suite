package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.cmd.SiardCliTest.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static ch.admin.bar.siard2.cmd.SiardCliTest.execute;
import static org.assertj.core.api.Assertions.assertThat;

class RestoreCommandParseTest {

    @Test
    void restoreHelp_listsOptionsAndExits0() {
        // when
        Result result = execute("restore", "--help");

        // then
        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(result.out()).contains("--siard-file").contains("--map-schema");
    }

    @Test
    void malformedMapSchema_explainsSyntaxAndExits2(@TempDir Path tempDir) {
        // when
        Result result = execute("restore",
                                "--jdbc-url=jdbc:postgresql://localhost:1/db",
                                "--user=u",
                                "--password=p",
                                "--siard-file=" + tempDir.resolve("in.siard"),
                                "--map-schema=BADVALUE");

        // then
        assertThat(result.exitCode()).isEqualTo(2);
        assertThat(result.err()).contains("archive-schema=db-schema");
    }
}
