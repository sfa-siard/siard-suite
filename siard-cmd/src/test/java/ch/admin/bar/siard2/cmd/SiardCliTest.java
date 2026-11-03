package ch.admin.bar.siard2.cmd;

import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Function;

public final class SiardCliTest {

    private SiardCliTest() {
    }

    public static Result execute(String... args) {
        return execute(new SiardCli(), args);
    }

    public static Result execute(PasswordPrompter passwordPrompter, Function<String, String> env, String... args) {
        return execute(new SiardCli(passwordPrompter, env), args);
    }

    private static Result execute(SiardCli cli, String... args) {
        CommandLine commandLine = new CommandLine(cli);
        StringWriter out = new StringWriter();
        StringWriter err = new StringWriter();
        commandLine.setOut(new PrintWriter(out));
        commandLine.setErr(new PrintWriter(err));
        int exitCode = commandLine.execute(args);
        commandLine.getOut().flush();
        commandLine.getErr().flush();
        return new Result(exitCode, out.toString(), err.toString());
    }

    public record Result(int exitCode, String out, String err) {
    }
}
