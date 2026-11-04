package ch.admin.bar.siard2.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Spec;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(name = "archive",
        mixinStandardHelpOptions = true,
        description = "Archive a database into a SIARD file")
public class ArchiveCommand implements Callable<Integer> {

    @Spec
    CommandSpec spec;
    @Option(names = {"-j", "--jdbc-url"}, description = "JDBC URL of the database to archive (required)")
    String jdbcUrl;
    @Option(names = {"-u", "--user"}, description = "Database user (required)")
    String user;
    @Option(names = {"-p", "--password"}, arity = "0..1",
            description = "Database password; without a value, prompts on the console (or set SIARD_PASSWORD)")
    String password;
    @Option(names = {"-s", "--siard-file"}, description = "SIARD file to write (at least one of " +
            "--siard-file/--export-xml required)")
    Path siardFile;
    @Option(names = {"-e", "--export-xml"}, description = "Export database metadata to this XML file")
    Path exportXml;
    @Option(names = {"-o", "--overwrite"}, description = "Overwrite existing SIARD/export files")
    boolean overwrite;
    @Option(names = {"-v", "--views-as-tables"}, description = "Archive views as tables")
    boolean viewsAsTables;
    @Option(names = {"-l", "--login-timeout"}, description =
            "Login timeout in seconds (default: " + SiardConnection.iDEFAULT_LOGIN_TIMEOUT_SECONDS + ")")
    Integer loginTimeout;
    @Option(names = {"-q", "--query-timeout"}, description =
            "Query timeout in seconds (default: " + SiardConnection.iDEFAULT_QUERY_TIMEOUT_SECONDS + ")")
    Integer queryTimeout;
    @Option(names = {"-i", "--import-xml"}, description = "Metadata XML template applied before metadata download")
    Path importXml;
    @Option(names = {"-x", "--lob-folder"}, description = "Folder for storing the largest LOB column externally")
    Path lobFolder;
    @Option(names = {"-m", "--mime-type"}, description = "MIME type of data in the largest LOB column")
    String mimeType;
    @Option(names = "--schema", description = "Name of the schema to archive")
    String schema;
    @ParentCommand
    private SiardCli parent;

    @Override
    public Integer call() {
        PrintWriter out = spec.commandLine()
                              .getOut();
        PrintWriter err = spec.commandLine()
                              .getErr();

        if (jdbcUrl == null) return usageError("Missing required option: '--jdbc-url=<url>'");
        if (user == null) return usageError("Missing required option: '--user=<user>'");
        if (siardFile == null && exportXml == null) {
            return usageError("At least one of '--siard-file' or '--export-xml' must be given");
        }
        String badScheme = SiardConnection.getSiardConnection()
                                          .unsupportedScheme(jdbcUrl);
        if (badScheme != null) {
            return usageError("JDBC URL " + SiardConnection.maskCredentials(jdbcUrl) +
                                      " has an unsupported scheme: " + badScheme);
        }
        if (importXml != null && !Files.isRegularFile(importXml)) {
            return usageError("Import XML file " + importXml + " does not exist!");
        }
        if (lobFolder != null && !Files.isDirectory(lobFolder)) {
            return usageError("External LOB folder " + lobFolder + " does not exist!");
        }
        if (lobFolder != null && (siardFile == null || siardFile.toFile()
                                                                .getParentFile() == null)) {
            return usageError("To use external LOB folder, specify --siard-file with a parent directory!");
        }

        boolean passwordRequested = spec.commandLine()
                                        .getParseResult()
                                        .hasMatchedOption("--password");
        String resolvedPassword = PasswordResolver.resolve(password, passwordRequested,
                                                           parent.passwordPrompter(), parent.env(), err);
        if (resolvedPassword == null) return 2;

        ArchiveConfig config = new ArchiveConfig(jdbcUrl, user, resolvedPassword,
                                                 siardFile, exportXml,
                                                 overwrite, viewsAsTables,
                                                 loginTimeout != null ? loginTimeout :
                                                         SiardConnection.iDEFAULT_LOGIN_TIMEOUT_SECONDS,
                                                 queryTimeout != null ? queryTimeout :
                                                         SiardConnection.iDEFAULT_QUERY_TIMEOUT_SECONDS,
                                                 importXml, lobFolder, mimeType, schema);
        return new SiardFromDb(config).run(out, err);
    }

    private int usageError(String message) {
        return SiardCli.usageError(spec, message);
    }
}
