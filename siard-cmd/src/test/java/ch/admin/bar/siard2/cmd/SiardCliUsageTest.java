package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.cmd.SiardCliTest.Result;
import org.junit.jupiter.api.Test;

import static ch.admin.bar.siard2.cmd.SiardCliTest.execute;
import static org.assertj.core.api.Assertions.assertThat;

class SiardCliUsageTest {

    @Test
    void bareSiard_printsUsageAndExits2() {
        // when
        Result result = execute();

        // then
        assertThat(result.exitCode()).isEqualTo(2);
        assertThat(result.out()).contains("Usage:");
    }

    @Test
    void unknownSubcommand_namesSubcommandListsValidOnesAndExits2() {
        // when
        Result result = execute("frobnicate");

        // then
        assertThat(result.exitCode()).isEqualTo(2);
        assertThat(result.err()).contains("frobnicate");
        assertThat(result.err()).contains("archive").contains("restore");
    }

    @Test
    void topLevelHelp_printsUsageAndExits0() {
        // when
        Result result = execute("--help");

        // then
        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(result.out()).contains("archive").contains("restore");
    }
}
