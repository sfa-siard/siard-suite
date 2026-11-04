package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.cmd.SiardCliTest.Result;
import org.junit.jupiter.api.Test;

import static ch.admin.bar.siard2.cmd.SiardCliTest.execute;
import static org.assertj.core.api.Assertions.assertThat;

class SiardCliOutputTest {

    @Test
    void version_printsNameAppAndSiardVersionAndExits0() {
        // when
        Result result = execute("--version");

        // then
        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(result.out()).contains("SIARD").contains("2.2");
    }

    @Test
    void normalRun_printsNoBannerAndNoLogLines() {
        // when
        Result result = execute("archive", "--help");

        // then stdout holds only command results: no credits banner, no log lines
        assertThat(result.out()).doesNotContain("Developed by").doesNotContain("Specified by");
        assertThat(result.out()).doesNotContainPattern("\\d{2}:\\d{2}:\\d{2}\\.\\d{3}");
    }

    @Test
    void failure_writesDiagnosticsToStderrOnly() {
        // when
        Result result = execute("frobnicate");

        // then
        assertThat(result.err()).isNotBlank();
        assertThat(result.out()).doesNotContain("frobnicate");
    }
}
