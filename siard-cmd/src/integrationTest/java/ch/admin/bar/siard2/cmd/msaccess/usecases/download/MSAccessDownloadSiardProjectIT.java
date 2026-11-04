package ch.admin.bar.siard2.cmd.msaccess.usecases.download;

import ch.admin.bar.siard2.cmd.SiardCliTest;
import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.assertions.SiardArchiveAssertions;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import org.junit.jupiter.api.extension.RegisterExtension;

public class MSAccessDownloadSiardProjectIT {

    @RegisterExtension
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Test
    public void shouldDownloadNationsDb() throws SQLException, IOException, ClassNotFoundException {

        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.MS_ACCESS_NATIONS);
        val actualArchive = siardArchivesHandler.prepareEmpty();

        String path = this.getClass()
                          .getResource("/msaccess/usecases.download/nations.accdb")
                          .getPath();
        var siardFromDb = SiardCliTest.execute("archive",
                "--overwrite",
                "--user=Admin", // the user determines the schema that is used. don't change it.
                "--password=pw",
                "--jdbc-url=" + "jdbc:access:" + path,
                "--siard-file=" + actualArchive.getPathToArchiveFile());

        Assertions.assertEquals(0, siardFromDb.exitCode());

        SiardArchiveAssertions.builder()
                              .assertionModifier(SiardArchiveAssertions.IGNORE_DBNAME) // FIXME ?
                              .actualArchive(actualArchive)
                              .expectedArchive(expectedArchive.preserveArchive())
                              .assertEqual();
    }
}
