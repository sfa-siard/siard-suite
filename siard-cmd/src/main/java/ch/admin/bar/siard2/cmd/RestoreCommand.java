package ch.admin.bar.siard2.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Spec;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Command(name = "restore",
        mixinStandardHelpOptions = true,
        description = "Restore a SIARD file into a database")
public class RestoreCommand implements Callable<Integer> {

    @Spec
    CommandSpec spec;

    @Option(names = {"-j", "--jdbc-url"}, description = "JDBC URL of the target database (required)")
    String jdbcUrl;

    @Option(names = {"-u", "--user"}, description = "Database user (required)")
    String user;

    @Option(names = {"-p", "--password"}, arity = "0..1",
            description = "Database password; without a value, prompts on the console (or set SIARD_PASSWORD)")
    String password;

    @Option(names = {"-s", "--siard-file"}, description = "SIARD file to restore (required)")
    Path siardFile;

    @Option(names = {"-o", "--overwrite"}, description = "Overwrite existing database objects")
    boolean overwrite;

    @Option(names = {"-l", "--login-timeout"}, description =
            "Login timeout in seconds (default: " + SiardConnection.iDEFAULT_LOGIN_TIMEOUT_SECONDS + ")")
    Integer loginTimeout;

    @Option(names = {"-q", "--query-timeout"}, description =
            "Query timeout in seconds (default: " + SiardConnection.iDEFAULT_QUERY_TIMEOUT_SECONDS + ")")
    Integer queryTimeout;

    @Option(names = "--map-schema", description = "Map an archive schema to a database schema: " +
            "archive-schema=db-schema (repeatable)")
    List<String> schemaMappings;

    @ParentCommand
    private SiardCli parent;

    @Override
    public Integer call() {
        PrintWriter out = spec.commandLine().getOut();
        PrintWriter err = spec.commandLine().getErr();

        if (jdbcUrl == null) return usageError("Missing required option: '--jdbc-url=<url>'");
        if (user == null) return usageError("Missing required option: '--user=<user>'");
        if (siardFile == null) return usageError("Missing required option: '--siard-file=<siardFile>'");

        Map<String, String> schemaMapping = new LinkedHashMap<>();
        if (schemaMappings != null) {
            for (String mapping : schemaMappings) {
                int separator = mapping.indexOf('=');
                if (separator <= 0 || separator == mapping.length() - 1) {
                    return usageError("Invalid schema mapping '" + mapping + "': expected archive-schema=db-schema");
                }
                schemaMapping.put(mapping.substring(0, separator), mapping.substring(separator + 1));
            }
        }

        String badScheme = SiardConnection.getSiardConnection().unsupportedScheme(jdbcUrl);
        if (badScheme != null) {
            return usageError("JDBC URL " + SiardConnection.maskCredentials(jdbcUrl) +
                                      " has an unsupported scheme: " + badScheme);
        }

        boolean passwordRequested = PasswordResolver.passwordRequested(spec.commandLine()
                                                                         .getParseResult());
        String resolvedPassword = PasswordResolver.resolve(password, passwordRequested,
                                                           parent.passwordPrompter(), parent.env(), err);
        if (resolvedPassword == null) return 2;

        RestoreConfig config = new RestoreConfig(jdbcUrl, user, resolvedPassword, siardFile,
                                                 overwrite,
                                                 loginTimeout != null ? loginTimeout :
                                                         SiardConnection.iDEFAULT_LOGIN_TIMEOUT_SECONDS,
                                                 queryTimeout != null ? queryTimeout :
                                                         SiardConnection.iDEFAULT_QUERY_TIMEOUT_SECONDS,
                                                 schemaMapping);
        return new SiardToDb(config).run(out, err);
    }

    private int usageError(String message) {
        return SiardCli.usageError(spec, message);
    }
}
