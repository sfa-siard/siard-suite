package ch.admin.bar.siardsuite.localwebapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Spec scenario "Information of the packaged agent".
 */
class SystemInfoIT {

    /*
     * The drivers registered right after startup, in alphabetical order. The
     * wrappers come from the META-INF/services/java.sql.Driver entries of jdbc-access, -db2,
     * -mssql, -mysql and -oracle; loading a wrapper deregisters the vendor driver it wraps
     * (BaseDriver.register), so ojdbc7, db2jcc4, mssql-jdbc and mysql-connector-j do not show up.
     * jdbc-postgres has no services entry, so the vendor PostgreSQL driver stays registered until
     * something loads PostgresDriver. MySqlDriver also serves MariaDB.
     */
    private static final List<String> EXPECTED_DRIVERS = List.of(
            "ch.admin.bar.siard2.jdbc.AccessDriver",   // MS Access
            "ch.admin.bar.siard2.jdbc.Db2Driver",      // DB2
            "ch.admin.bar.siard2.jdbc.MsSqlDriver",    // MS SQL Server
            "ch.admin.bar.siard2.jdbc.MySqlDriver",    // MySQL/MariaDB
            "ch.admin.bar.siard2.jdbc.OracleDriver",   // Oracle
            "org.postgresql.Driver");                  // PostgreSQL

    @Test
    void informationOfThePackagedAgent() throws Exception {
        // set by the build to the project version the jar was built with
        String expectedVersion = System.getProperty("siard.expected-app-version");
        assertNotNull(expectedVersion, "siard.expected-app-version not set by the build");

        try (PackagedAgent agent = PackagedAgent.start()) {
            HttpResponse<String> response = agent.get("/api/system/info", "Cookie", agent.sessionCookie());
            assertEquals(200, response.statusCode());

            JsonNode info = new ObjectMapper().readTree(response.body());
            assertEquals(expectedVersion, info.path("appVersion").asText());
            assertFalse(info.path("javaVersion").asText().isEmpty());

            List<String> drivers = new ArrayList<>();
            info.path("jdbcDrivers").forEach(driver -> drivers.add(driver.asText()));
            // compared sorted: the registration order is not guaranteed across machines
            assertEquals(EXPECTED_DRIVERS.stream().sorted().toList(), drivers.stream().sorted().toList());
        }
    }
}
