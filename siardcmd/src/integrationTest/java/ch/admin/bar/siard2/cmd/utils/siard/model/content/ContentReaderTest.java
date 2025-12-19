package ch.admin.bar.siard2.cmd.utils.siard.model.content;

import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.siard.utils.Unzipper;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

public class ContentReaderTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void read_expectNoExceptions() throws IOException {
        // given
        val unzipper = new Unzipper(
                TestResourcesResolver.resolve(SiardProjectExamples.SAMPLE_DATALINK_2_2_SIARD),
                temporaryFolder.getRoot());

        val contentReader = new ContentReader(unzipper.unzip());

        // when
        val result = contentReader.read();

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTables()).isNotEmpty();

        result.getTables().forEach(table -> {
            Assertions.assertThat(table.getTableContent().getRows()).isNotEmpty();
            table.getTableContent().getRows().forEach(tableRow -> Assertions.assertThat(tableRow.getCells()).isNotEmpty());
        });
    }
}
