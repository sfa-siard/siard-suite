package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.cmd.utils.VersionsExplorer;
import ch.enterag.utils.ProgramInfo;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.function.Function;

@Command(name = "siard",
        mixinStandardHelpOptions = true,
        versionProvider = SiardVersionProvider.class,
        subcommands = {ArchiveCommand.class, RestoreCommand.class})
public class SiardCli implements Callable<Integer> {

    static {
        ensureProgramInfo();
    }

    private final PasswordPrompter passwordPrompter;
    private final Function<String, String> env;
    @Spec
    CommandSpec spec;

    public SiardCli() {
        this(new ConsolePasswordPrompter(), System::getenv);
    }

    public SiardCli(PasswordPrompter passwordPrompter, Function<String, String> env) {
        this.passwordPrompter = passwordPrompter;
        this.env = env;
    }

    /* MetaDataFromDb requires an initialized ProgramInfo for the producer
       information written into archives. Creating it does not print the banner. */
    private static void ensureProgramInfo() {
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

    public static void main(String[] args) {
        System.exit(new CommandLine(new SiardCli()).execute(args));
    }

    PasswordPrompter passwordPrompter() {
        return passwordPrompter;
    }

    Function<String, String> env() {
        return env;
    }

    @Override
    public Integer call() {
        spec.commandLine().usage(spec.commandLine().getOut());
        return 2;
    }
}
