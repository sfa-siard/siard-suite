package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.cmd.utils.VersionsExplorer;
import picocli.CommandLine;

public class SiardVersionProvider implements CommandLine.IVersionProvider {

    @Override
    public String[] getVersion() {
        return new String[]{
                "siard " + VersionsExplorer.INSTANCE.getAppVersion(),
                "SIARD Suite, SIARD format version " + VersionsExplorer.INSTANCE.getSiardVersion()
        };
    }
}
