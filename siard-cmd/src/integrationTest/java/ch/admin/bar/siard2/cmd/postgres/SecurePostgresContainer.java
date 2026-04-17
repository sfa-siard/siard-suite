package ch.admin.bar.siard2.cmd.postgres;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

public class SecurePostgresContainer extends PostgreSQLContainer<SecurePostgresContainer> {
    public SecurePostgresContainer() {
        super("postgres:16-alpine");

        // 1. Copy files to a temporary 'staging' area in the container
        this.withCopyFileToContainer(
                    MountableFile.forClasspathResource("postgres/ssl/server.key"),
                    "/tmp/server.key")
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("postgres/ssl/server.crt"),
                    "/tmp/server.crt");
        /*    .withCopyFileToContainer(
                    MountableFile.forClasspathResource("ssl/root.crt"),
                    "/tmp/root.crt");

*/
        // 2. Wrap the startup: Copy from /tmp to the data dir and fix ownership
        // We use 'docker-entrypoint.sh' to ensure standard initialization still happens
        this.withCommand("sh", "-c",
                         "cp /tmp/server.key /var/lib/postgresql/server.key && " +
                                 "cp /tmp/server.crt /var/lib/postgresql/server.crt && " +
                                 //"cp /tmp/root.crt /var/lib/postgresql/root.crt && " +
                                 "chown postgres:postgres /var/lib/postgresql/server.key && " +
                                 "chmod 600 /var/lib/postgresql/server.key && " +
                                 "exec docker-entrypoint.sh postgres " +
                                 "-c ssl=on " +
                                 "-c ssl_cert_file=/var/lib/postgresql/server.crt " +
                                 "-c ssl_key_file=/var/lib/postgresql/server.key "
                                 //"-c ssl_ca_file=/var/lib/postgresql/root.crt"
        );


    }
}
