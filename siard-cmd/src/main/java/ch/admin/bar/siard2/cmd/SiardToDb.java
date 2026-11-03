/*== SiardToDb.java ====================================================
SiardFromDb loads the data from a siard file to a database instance.
Version     : $Id: SiardToDb.java 1922 2016-06-07 09:07:12Z hartwig $
Application : Siard2
Description : Loads the data from a siard file to a database instance.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2008
Created    : 24.03.2008, Hartwig Thomas, Enter AG, Zurich
======================================================================*/

package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siard2.api.MetaSchema;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.EU;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/** Loads the data from a siard file to a database instance.
 @author Hartwig Thomas
 */
@Slf4j
public class SiardToDb {

    private final RestoreConfig config;

    public SiardToDb(RestoreConfig config) {
        this.config = config;
    }

    /**
     * Restores the configured SIARD file into the target database.
     *
     * @return 0 on success, 1 on execution error
     */
    public int run(PrintWriter out, PrintWriter err) {
        try {
            ProgramInfoInit.ensure();
            return doRun(out, err);
        } catch (Exception e) {
            LOG.error("Exception while uploading SIARD archive", e);
            err.println(EU.getExceptionMessage(e));
            return 1;
        } catch (Error e) {
            LOG.error("Error while uploading SIARD archive", e);
            err.println(EU.getErrorMessage(e));
            return 1;
        }
    }

    private int doRun(PrintWriter out, PrintWriter err) throws IOException, SQLException {
        File fileSiard = config.siardFile().toFile();
        if (!fileSiard.isFile()) {
            err.println("SIARD file " + fileSiard.getAbsolutePath() + " does not exist!");
            return 1;
        }
        logParameters(fileSiard, out);

        Archive archive = ArchiveImpl.newInstance();
        archive.open(fileSiard);
        try {
            DriverManager.setLoginTimeout(config.loginTimeoutSeconds());
            Connection conn = SiardConnection.getSiardConnection()
                    .createValidConnection(config.jdbcUrl(), config.databaseUser(), config.databasePassword());
            if (conn == null || conn.isClosed()) {
                err.println("Connection to " + SiardConnection.maskCredentials(config.jdbcUrl()) + " failed!");
                return 1;
            }
            try {
                out.println("Connected to " + SiardConnection.maskCredentials(conn.getMetaData().getURL()));
                conn.setAutoCommit(false);

                MetaData md = archive.getMetaData();
                MetaDataToDb mdtd = MetaDataToDb.newInstance(conn.getMetaData(), md, config.schemaMapping());
                mdtd.setQueryTimeout(config.queryTimeoutSeconds());
                if (!config.overwrite()
                        && (mdtd.tablesDroppedByUpload() != 0 || mdtd.typesDroppedByUpload() != 0)) {
                    err.println("Database objects exist which would be overwritten on upload!");
                    err.println("Backup and delete them first or use --overwrite for overwriting them.");
                    return 1;
                }
                if (!mdtd.supportsUdts()) {
                    int typesInSiard = 0;
                    for (int i = 0; i < md.getMetaSchemas(); i++) {
                        MetaSchema ms = md.getMetaSchema(i);
                        typesInSiard += ms.getMetaTypes();
                    }
                    if (typesInSiard > 0) {
                        String message = "Target database does not support UDTs. UDTs will be \"flattened\".";
                        LOG.info(message);
                        out.println(message);
                    }
                }
                mdtd.upload(null);
                PrimaryDataToDb pdtd = PrimaryDataToDb.newInstance(conn, archive,
                        mdtd.getArchiveMapping(), mdtd.supportsArrays(), mdtd.supportsDistincts(), mdtd.supportsUdts());
                pdtd.setQueryTimeout(config.queryTimeoutSeconds());
                pdtd.upload(null);
                return 0;
            } finally {
                conn.close();
            }
        } finally {
            archive.close();
        }
    }

    private void logParameters(File fileSiard, PrintWriter out) {
        StringBuilder sb = new StringBuilder()
                .append("\nParameters\n")
                .append("  SIARD file             : ").append(fileSiard.getAbsolutePath()).append("\n")
                .append("  JDBC URL               : ").append(SiardConnection.maskCredentials(config.jdbcUrl())).append("\n")
                .append("  Database user          : ").append(config.databaseUser()).append("\n")
                .append("  Database password      : ***\n")
                .append("  Login timeout          : ").append(config.loginTimeoutSeconds()).append(" seconds\n")
                .append("  Query timeout          : ").append(config.queryTimeoutSeconds()).append(" seconds\n");
        if (config.overwrite())
            sb.append("  Overwrite              : Database objects may be overwritten on upload\n");
        for (Map.Entry<String, String> mapping : config.schemaMapping().entrySet()) {
            sb.append("  Mapped Schema          : \"")
                    .append(mapping.getKey())
                    .append("\" => \"")
                    .append(mapping.getValue())
                    .append("\"\n");
        }
        sb.append("\n");

        String message = sb.toString();
        LOG.info(message);
        out.println(message);
    }
}
