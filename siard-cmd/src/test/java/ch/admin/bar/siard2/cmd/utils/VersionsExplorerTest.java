package ch.admin.bar.siard2.cmd.utils;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VersionsExplorerTest {

    @Test
    public void getSiardVersion_expectNoException() {
        // given

        // when
        String siardVersion = VersionsExplorer.INSTANCE.getSiardVersion();

        // then
        Assertions.assertNotNull(siardVersion);
    }

    @Test
    public void getAppVersion_expectNoException() {
        // given

        // when
        String appVersion = VersionsExplorer.INSTANCE.getAppVersion();

        // then
        Assertions.assertNotNull(appVersion);
    }
}