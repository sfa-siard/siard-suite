package ch.admin.bar.siard2.cmd.utils.siard.utils;

import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

public class UnzipperTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void unzip_expectUnzippedArchive() throws IOException {
        // given
        val siardArchive = TestResourcesResolver.resolve(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_ORACLE18_2_2);
        val unzipper = new Unzipper(siardArchive, temporaryFolder.getRoot());

        // when
        val unzippedSiardArchive = unzipper.unzip();

        // then
        Assertions.assertThat(unzippedSiardArchive).exists();
        Assertions.assertThat(unzippedSiardArchive.listFiles()).hasSize(2); // header & content folders
    }

}