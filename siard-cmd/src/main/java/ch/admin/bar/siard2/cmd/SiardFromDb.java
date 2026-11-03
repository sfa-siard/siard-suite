/*
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2008
Created    : 29.08.2016, Hartwig Thomas, Enter AG, Rüti ZH
*/

package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.MetaSchema;
import ch.admin.bar.siard2.api.MetaTable;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.EU;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores database content in a SIARD file.
 *
 * @author Hartwig Thomas
 */
@Slf4j
public class SiardFromDb {

    private final ArchiveConfig config;

    public SiardFromDb(ArchiveConfig config) {
        this.config = config;
    }

    /**
     * Archives the configured database to a SIARD file and/or exports its metadata XML.
     *
     * @return 0 on success, 1 on execution error
     */
    public int run(PrintWriter out, PrintWriter err) {
        try {
            return doRun(out, err);
        } catch (Exception e) {
            LOG.error("Exception while downloading SIARD archive", e);
            err.println(EU.getExceptionMessage(e));
            return 1;
        } catch (Error e) {
            LOG.error("Error while downloading SIARD archive", e);
            err.println(EU.getErrorMessage(e));
            return 1;
        }
    }

    private int doRun(PrintWriter out, PrintWriter err) throws SQLException, IOException {
        File fileSiard = config.siardFile() != null ? config.siardFile().toFile() : null;
        File fileExportXml = config.exportXml() != null ? config.exportXml().toFile() : null;

        if (config.overwrite()) {
            if (fileSiard != null) fileSiard.delete();
            if (fileExportXml != null) fileExportXml.delete();
        }
        List<String> existing = new ArrayList<>();
        if (fileSiard != null && fileSiard.exists()) existing.add(fileSiard.getAbsolutePath());
        if (fileExportXml != null && fileExportXml.exists()) existing.add(fileExportXml.getAbsolutePath());
        if (!existing.isEmpty()) {
            err.println("File " + String.join(" and ", existing) + " exists already!");
            err.println("Backup and delete it or use --overwrite for overwriting it.");
            return 1;
        }

        logParameters(fileSiard, fileExportXml, out);

        DriverManager.setLoginTimeout(config.loginTimeoutSeconds());
        Connection conn = SiardConnection.getSiardConnection()
                .createValidConnection(config.jdbcUrl(), config.databaseUser(), config.databasePassword());
        if (conn == null || conn.isClosed()) {
            err.println("Connection to " + config.jdbcUrl() + " failed!");
            return 1;
        }
        try {
            out.println("Connected to " + conn.getMetaData().getURL());
            conn.setAutoCommit(false);

            Archive archive = ArchiveImpl.newInstance();
            File fileArchive = fileSiard;
            if (fileArchive == null) {
                fileArchive = File.createTempFile("siard", ".siard");
                fileArchive.delete();
            }
            archive.create(fileArchive);
            try {
                if (config.importXml() != null) {
                    try (FileInputStream fis = new FileInputStream(config.importXml().toFile())) {
                        archive.importMetaDataTemplate(fis);
                    }
                }
                URI externalLobFolderUri = config.externalLobFolderUri();
                MetaDataFromDb mdfd = MetaDataFromDb.newInstance(conn.getMetaData(), archive.getMetaData());
                mdfd.setQueryTimeout(config.queryTimeoutSeconds());
                mdfd.download(config.viewsAsTables(), externalLobFolderUri != null,
                        config.schema() != null ? config.schema() : "%", null);
                if (externalLobFolderUri != null) {
                    externalizeMaxLob(mdfd, externalLobFolderUri, out);
                }
                if (fileExportXml != null) {
                    try (OutputStream osXml = new FileOutputStream(fileExportXml)) {
                        archive.exportMetaData(osXml);
                    }
                }
                if (fileSiard != null) {
                    PrimaryDataFromDb pdfd = PrimaryDataFromDb.newInstance(conn, archive);
                    pdfd.setQueryTimeout(config.queryTimeoutSeconds());
                    pdfd.download(null);
                } else {
                    fileArchive.deleteOnExit();
                }
                archive.close();
            } catch (Throwable t) {
                try {
                    archive.close();
                } catch (Throwable closeFailure) {
                    t.addSuppressed(closeFailure);
                }
                throw t;
            }
            conn.rollback();
            return 0;
        } finally {
            conn.close();
        }
    }

    private void externalizeMaxLob(MetaDataFromDb mdfd, URI externalLobFolderUri, PrintWriter out) throws IOException {
        MetaColumn mcMaxLob = mdfd.getMaxLobColumn();
        if (mcMaxLob == null) {
            out.println("No LOB column found to be externalized!");
            return;
        }
        MetaTable mtLob = mcMaxLob.getParentMetaTable();
        MetaSchema msLob = mtLob.getParentMetaSchema();
        String message = "LOBs in database column \"" + mcMaxLob.getName()
                + "\" in table \"" + mtLob.getName()
                + "\" in schema \"" + msLob.getName()
                + "\" will be stored externally in folder \""
                + config.externalLobFolder().toFile().getAbsolutePath() + "\"";
        mcMaxLob.setLobFolder(externalLobFolderUri);
        if (config.mimeType() != null) {
            mcMaxLob.setMimeType(config.mimeType());
            message = message + " with MIME type " + mcMaxLob.getMimeType();
        }
        out.println();
        out.println(message);
    }

    private void logParameters(File fileSiard, File fileExportXml, PrintWriter out) {
        StringBuilder sb = new StringBuilder()
                .append("\nParameters\n")
                .append("  JDBC URL               : ").append(config.jdbcUrl()).append("\n")
                .append("  Database user          : ").append(config.databaseUser()).append("\n")
                .append("  Database password      : ***\n")
                .append("  Login timeout          : ").append(config.loginTimeoutSeconds()).append(" seconds\n")
                .append("  Query timeout          : ").append(config.queryTimeoutSeconds()).append(" seconds\n");
        if (fileSiard != null)
            sb.append("  SIARD file             : ").append(fileSiard.getAbsolutePath()).append("\n");
        if (fileExportXml != null)
            sb.append("  Export meta data XML   : ").append(fileExportXml.getAbsolutePath()).append("\n");
        if (config.importXml() != null)
            sb.append("  Import meta data XML   : ").append(config.importXml().toFile().getAbsolutePath()).append("\n");
        if (config.externalLobFolder() != null)
            sb.append("  External LOB folder    : ").append(config.externalLobFolderUri()).append("\n");
        if (config.mimeType() != null)
            sb.append("  External LOB MIME type : ").append(config.mimeType()).append("\n");
        if (config.viewsAsTables())
            sb.append("  Archive views as tables: ").append(true).append("\n");
        if (config.schema() != null)
            sb.append("  Schema name            : ").append(config.schema()).append("\n");
        sb.append("\n");

        String message = sb.toString();
        LOG.info(message);
        out.println(message);
    }
}
