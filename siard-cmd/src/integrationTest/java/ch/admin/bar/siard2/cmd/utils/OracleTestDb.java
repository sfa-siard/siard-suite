package ch.admin.bar.siard2.cmd.utils;

import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

/** Shared Oracle test container configuration. */
public final class OracleTestDb {

    public static final DockerImageName IMAGE = DockerImageName.parse("gvenzl/oracle-xe:21-slim-faststart");

    private OracleTestDb() {
    }

    /**
     * Creates an Oracle container with a generous startup timeout. Oracle XE cold-starts on a
     * shared CI runner can take several minutes; exceeding the testcontainers default startup
     * timeout is the common cause of flaky "container won't start" failures.
     */
    public static OracleContainer newContainer() {
        return new OracleContainer(IMAGE).withStartupTimeout(Duration.ofMinutes(5));
    }
}
