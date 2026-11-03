package ch.admin.bar.siard2.cmd;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.io.PrintWriter;
import java.util.concurrent.Callable;
import java.util.function.Function;

@Command(name = "siard",
        mixinStandardHelpOptions = true,
        versionProvider = SiardVersionProvider.class,
        subcommands = {ArchiveCommand.class, RestoreCommand.class})
public class SiardCli implements Callable<Integer> {

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

    /** Shared usage-error helper: diagnostic + --help hint on stderr, exit code 2. */
    static int usageError(CommandSpec spec, String message) {
        PrintWriter err = spec.commandLine().getErr();
        err.println("Error: " + message);
        err.println("Try 'siard " + spec.commandLine().getCommandName() + " --help' for more information.");
        return 2;
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
