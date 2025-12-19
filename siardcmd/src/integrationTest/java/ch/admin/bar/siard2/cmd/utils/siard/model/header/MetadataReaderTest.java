package ch.admin.bar.siard2.cmd.utils.siard.model.header;

import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.siard.utils.Unzipper;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

public class MetadataReaderTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void read_expectNoExceptions() throws IOException {
        // given
        val unzipper = new Unzipper(
                TestResourcesResolver.resolve(SiardProjectExamples.SAMPLE_DATALINK_2_2_SIARD),
                temporaryFolder.getRoot());

        val metadataReader = new MetadataReader(unzipper.unzip());

        // when
        val result = metadataReader.read();

        // then
        Assertions.assertThat(result).isNotNull();

        Assertions.assertThat(result.getSchemas()).isNotEmpty();
        result.getSchemas().forEach(schema -> {
            Assertions.assertThat(schema.getTypes()).isNotEmpty();
            Assertions.assertThat(schema.getTables()).isNotEmpty();
            schema.getTables().forEach(table -> Assertions.assertThat(table.getColumns()).isNotEmpty());
        });
    }
}
