package ch.admin.bar.siard2.cmd.postgres;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

public class SecurePostgresContainer extends PostgreSQLContainer<SecurePostgresContainer> {
    public SecurePostgresContainer() {
        super("postgres:16-alpine");

        final MountableFile rootCrt = MountableFile.forClasspathResource("postgres/ssl/root.crt");

        // 1. Copy the certificate/key files to a temporary 'staging' area in the container.
        // The data directory is not yet populated at this point, so we can not copy directly.
        this.withCopyFileToContainer(
                    MountableFile.forClasspathResource("postgres/ssl/server.key"),
                    "/tmp/server.key")
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("postgres/ssl/server.crt"),
                    "/tmp/server.crt")
            .withCopyFileToContainer(
                    rootCrt,
                    "/tmp/root.crt");

        // 2. Wrap the startup: copy from /tmp to the data dir, fix ownership/permissions
        // and hand over to the standard docker-entrypoint so the normal initialization
        // (user/db creation, etc.) still happens.
        this.withCommand("sh", "-c",
                         "cp /tmp/server.key /var/lib/postgresql/server.key && " +
                                 "cp /tmp/server.crt /var/lib/postgresql/server.crt && " +
                                 "cp /tmp/root.crt /var/lib/postgresql/root.crt && " +
                                 "chown postgres:postgres " +
                                 "/var/lib/postgresql/server.key " +
                                 "/var/lib/postgresql/server.crt " +
                                 "/var/lib/postgresql/root.crt && " +
                                 "chmod 600 /var/lib/postgresql/server.key && " +
                                 "exec docker-entrypoint.sh postgres " +
                                 "-c ssl=on " +
                                 "-c ssl_cert_file=/var/lib/postgresql/server.crt " +
                                 "-c ssl_key_file=/var/lib/postgresql/server.key " +
                                 "-c ssl_ca_file=/var/lib/postgresql/root.crt");

        // 3. Expose the CA certificate location on the test runner's filesystem to the
        // JDBC client so `sslmode=verify-ca` / `verify-full` can validate the server cert
        // without relying on the default ~/.postgresql/root.crt location.
        this.withUrlParam("sslrootcert", rootCrt.getResolvedPath());
        this.withUrlParam("sslmode", "verify-ca");
    }
}
