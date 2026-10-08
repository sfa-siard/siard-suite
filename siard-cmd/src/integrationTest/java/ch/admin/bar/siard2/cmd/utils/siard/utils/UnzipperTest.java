package ch.admin.bar.siard2.cmd.utils.siard.utils;

import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

public class UnzipperTest {

    @TempDir
    public File temporaryFolder;

    @Test
    public void unzip_expectUnzippedArchive() throws IOException {
        // given
        val siardArchive = TestResourcesResolver.resolve(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_ORACLE18_2_2);
        val unzipper = new Unzipper(siardArchive, temporaryFolder);

        // when
        val unzippedSiardArchive = unzipper.unzip();

        // then
        Assertions.assertThat(unzippedSiardArchive)
                  .exists();
        Assertions.assertThat(unzippedSiardArchive.listFiles())
                  .hasSize(2); // header & content folders
    }

}