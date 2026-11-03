package ch.admin.bar.siard2.cmd;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

/**
 * Validated parameters for {@link SiardToDb}. Pure data — no I/O channels, usable by non-CLI callers.
 */
public record RestoreConfig(String jdbcUrl, String databaseUser, String databasePassword, Path siardFile,
                            boolean overwrite, int loginTimeoutSeconds, int queryTimeoutSeconds,
                            Map<String, String> schemaMapping) {

    public RestoreConfig {
        Objects.requireNonNull(jdbcUrl, "jdbcUrl");
        Objects.requireNonNull(databaseUser, "databaseUser");
        Objects.requireNonNull(databasePassword, "databasePassword");
        Objects.requireNonNull(siardFile, "siardFile");
        schemaMapping = schemaMapping == null ? Map.of() : Map.copyOf(schemaMapping);
    }

    /** Masked so the credential can never leak through logging or assertion output. */
    @Override
    public String toString() {
        return "RestoreConfig[jdbcUrl=" + jdbcUrl + ", databaseUser=" + databaseUser
                + ", databasePassword=***, siardFile=" + siardFile
                + ", overwrite=" + overwrite + ", loginTimeoutSeconds=" + loginTimeoutSeconds
                + ", queryTimeoutSeconds=" + queryTimeoutSeconds
                + ", schemaMapping=" + schemaMapping + "]";
    }
}
