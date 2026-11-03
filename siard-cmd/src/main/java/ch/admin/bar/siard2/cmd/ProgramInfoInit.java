package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.cmd.utils.VersionsExplorer;
import ch.enterag.utils.ProgramInfo;

import java.util.Arrays;

/**
 * Ensures the {@link ProgramInfo} singleton exists before engine work.
 * MetaDataFromDb requires it for the producer information written into archives;
 * creating it does not print the banner. Idempotent — skips initialization when
 * an embedding application (e.g. the GUI) already provided its own.
 */
final class ProgramInfoInit {

    private ProgramInfoInit() {
    }

    static void ensure() {
        try {
            ProgramInfo.getProgramInfo();
        } catch (RuntimeException notInitialized) {
            ProgramInfo.getProgramInfo("SIARD Suite",
                    VersionsExplorer.INSTANCE.getSiardVersion(),
                    "siard",
                    VersionsExplorer.INSTANCE.getAppVersion(),
                    "Program to store database content in a .siard file and to load it back",
                    "Swiss Federal Archives, Berne, Switzerland, 2008-2026",
                    Arrays.asList("Hartwig Thomas, Enter AG, Rüti ZH, Switzerland",
                            "Andreas Voss, Swiss Federal Archives, Berne, Switzerland",
                            "Anders Bo Nielsen, Danish National Archives, Denmark",
                            "Claire Röthlisberger-Jourdan, KOST, Berne, Switzerland"),
                    Arrays.asList("Hartwig Thomas, Enter AG, Rüti ZH, Switzerland",
                            "Simon Jutz, Cytex GmbH, Zurich, Switzerland"),
                    Arrays.asList("Claudia Matthys, POOL Computer AG, Zurich, Switzerland",
                            "Marcel Büchler, Swiss Federal Archives, Berne, Switzerland",
                            "Yvan Dutoit, Swiss Federal Archives, Berne, Switzerland"),
                    Arrays.asList("Hartwig Thomas, Enter AG, Rüti ZH, Switzerland",
                            "Marcel Büchler, Swiss Federal Archives, Berne, Switzerland",
                            "Alain Mast, Swiss Federal Archives, Berne, Switzerland",
                            "Krystyna Ohnesorge, Swiss Federal Archives, Berne, Switzerland"));
        }
    }
}
