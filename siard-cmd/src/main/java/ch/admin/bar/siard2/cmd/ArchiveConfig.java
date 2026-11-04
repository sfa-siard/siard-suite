package ch.admin.bar.siard2.cmd;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Validated parameters for {@link SiardFromDb}. Pure data — no I/O channels, usable by non-CLI callers.
 */
public record ArchiveConfig(
        String jdbcUrl,
        String databaseUser,
        String databasePassword,
        Path siardFile,
        Path exportXml,
        boolean overwrite,
        boolean viewsAsTables,
        int loginTimeoutSeconds,
        int queryTimeoutSeconds,
        Path importXml,
        Path externalLobFolder,
        String mimeType,
        String schema) {

    public ArchiveConfig {
        Objects.requireNonNull(jdbcUrl, "jdbcUrl");
        Objects.requireNonNull(databaseUser, "databaseUser");
        Objects.requireNonNull(databasePassword, "databasePassword");

        if (siardFile == null && exportXml == null) {
            throw new IllegalArgumentException("siardFile and/or exportXml must be given");
        }
        if (externalLobFolder != null && siardFile == null) {
            throw new IllegalArgumentException("externalLobFolder requires siardFile to be given");
        }
    }

    // masked the password so that it never leaks through logging or assertion output
    @Override
    public String toString() {
        return "ArchiveConfig[jdbcUrl=" + SiardConnection.maskCredentials(jdbcUrl)
                + ", databaseUser=" + databaseUser
                + ", databasePassword=***, siardFile=" + siardFile + ", exportXml=" + exportXml
                + ", overwrite=" + overwrite + ", viewsAsTables=" + viewsAsTables
                + ", loginTimeoutSeconds=" + loginTimeoutSeconds
                + ", queryTimeoutSeconds=" + queryTimeoutSeconds
                + ", importXml=" + importXml + ", externalLobFolder=" + externalLobFolder
                + ", mimeType=" + mimeType + ", schema=" + schema + "]";
    }

    /** The external LOB folder URI, or {@code null} when no LOB folder is configured. */
    public URI externalLobFolderUri() {
        if (externalLobFolder == null) return null;
        return externalLobFolderUri(siardFile, externalLobFolder);
    }

    /**
     * The external LOB folder expressed relative to the SIARD file's parent,
     * prefixed with {@code ../} and suffixed with {@code /} as required by the archive format.
     *
     * @throws IllegalArgumentException if the folder cannot be expressed as a URI
     */
    public static URI externalLobFolderUri(Path siardFile, Path externalLobFolder) {
        File relative = siardFile.toFile()
                                 .getParentFile()
                                 .getAbsoluteFile()
                                 .toPath()
                                 .relativize(externalLobFolder.toFile()
                                                              .getAbsoluteFile()
                                                              .toPath())
                                 .toFile();
        try {
            return new URI("../" + relative + "/");
        } catch (URISyntaxException use) {
            throw new IllegalArgumentException(
                    "External LOB folder " + externalLobFolder.toFile()
                                                              .getAbsolutePath() + " could not be relativized!",
                    use);
        }
    }
}
