package ch.admin.bar.siard2.cmd.utils;


import org.junit.Assert;
import org.junit.Test;

public class VersionsExplorerTest {

    @Test
    public void getSiardVersion_expectNoException() {
        // given

        // when
        String siardVersion = VersionsExplorer.INSTANCE.getSiardVersion();

        // then
        Assert.assertNotNull(siardVersion);
    }

    @Test
    public void getAppVersion_expectNoException() {
        // given

        // when
        String appVersion = VersionsExplorer.INSTANCE.getAppVersion();

        // then
        Assert.assertNotNull(appVersion);
    }
}