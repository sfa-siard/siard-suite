package ch.admin.bar.siard2.cmd.mysql.usecases.keys.upload;

import ch.admin.bar.siard2.cmd.SiardCliTest;
import ch.admin.bar.siard2.cmd.mysql.usecases.types.download.defaulttypes.MySql8DefaultDataTypesIT;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.assertions.SiardArchiveAssertions;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Assertions;

public class MySqlKeysUpload {

    /**
     * Output of {@link MySql8DefaultDataTypesIT}
     */
    public final static String CREATE_IT_USER_SQL_SCRIPT = "mysql/usecases/keys/upload/create-it-user.sql";

    @SneakyThrows
    public static void executeTest(SiardArchivesHandler siardArchivesHandler, String jdbcUrl, String siardArchivePath) {
        // given
        val expectedArchive = siardArchivesHandler.prepareResource(siardArchivePath);
        val actualArchive = siardArchivesHandler.prepareEmpty();

        // when
        var siardToDb = SiardCliTest.execute("restore",
                "--overwrite",
                "--jdbc-url=" + jdbcUrl,
                "--user=" + "it_user",
                "--password=" + "it_password",
                "--siard-file=" + expectedArchive.getPathToArchiveFile());
        var siardFromDb = SiardCliTest.execute("archive",
                "--overwrite",
                "--jdbc-url=" + jdbcUrl,
                "--user=" + "it_user",
                "--password=" + "it_password",
                "--siard-file=" + actualArchive.getPathToArchiveFile());

        // then
        Assertions.assertEquals(0, siardToDb.exitCode());
        Assertions.assertEquals(0, siardFromDb.exitCode());

        SiardArchiveAssertions.builder()
                              .expectedArchive(expectedArchive)
                              .actualArchive(actualArchive.preserveArchive())
                              .assertionModifier(SiardArchiveAssertions.IGNORE_FOREIGN_KEY_DELETE_ACTION)
                              .assertionModifier(SiardArchiveAssertions.IGNORE_FOREIGN_KEY_UPDATE_ACTION)
                              .assertEqual();
    }
}
