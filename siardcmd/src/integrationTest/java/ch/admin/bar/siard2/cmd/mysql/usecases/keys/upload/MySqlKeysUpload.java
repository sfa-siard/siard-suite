package ch.admin.bar.siard2.cmd.mysql.usecases.keys.upload;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.mysql.usecases.types.download.defaulttypes.MySql8DefaultDataTypesIT;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.assertions.SiardArchiveAssertions;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Assert;

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
        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + jdbcUrl,
                "-u:" + "it_user",
                "-p:" + "it_password",
                "-s:" + expectedArchive.getPathToArchiveFile()
        });
        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + jdbcUrl,
                "-u:" + "it_user",
                "-p:" + "it_password",
                "-s:" + actualArchive.getPathToArchiveFile()
        });

        // then
        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        SiardArchiveAssertions.builder()
                .expectedArchive(expectedArchive)
                .actualArchive(actualArchive.preserveArchive())
                .assertionModifier(SiardArchiveAssertions.IGNORE_FOREIGN_KEY_DELETE_ACTION)
                .assertionModifier(SiardArchiveAssertions.IGNORE_FOREIGN_KEY_UPDATE_ACTION)
                .assertEqual();
    }
}
