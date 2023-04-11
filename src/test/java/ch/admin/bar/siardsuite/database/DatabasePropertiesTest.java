package ch.admin.bar.siardsuite.database;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class DatabasePropertiesTest {


    @Test
    void shouldGetJdbcUrlForPostgres() {
        final DatabaseProperties props = DatabaseConnectionProperties.dbTypes.get("PostgreSQL");

        assertEquals("jdbc:postgresql://serverurl:4444/dbname", props.jdbcUrl("serverurl", "4444", "dbname"));
        assertEquals("jdbc:postgresql://serverurl:5432/dbname", props.jdbcUrl("serverurl", "", "dbname"));
        assertEquals("jdbc:postgresql://serverurl:5432/dbname", props.jdbcUrl("serverurl", null, "dbname"));
        assertEquals("jdbc:postgresql://localhost:5432/dbname", props.jdbcUrl("", null, "dbname"));
        assertEquals("jdbc:postgresql://localhost:5432/dbname", props.jdbcUrl(null, null, "dbname"));
        assertEquals("jdbc:postgresql://localhost:5432/test-db", props.jdbcUrl(null, null, ""));
        assertEquals("jdbc:postgresql://localhost:5432/test-db", props.jdbcUrl(null, null, null));

        assertEquals(props.jdbcUrl(), props.jdbcUrl(null, null, null));
        assertEquals(props.jdbcUrl(), props.jdbcUrl("", "", ""));
    }

    @Test
    void shouldGetJdbcUrlForMySql() {
        final DatabaseProperties props = DatabaseConnectionProperties.dbTypes.get("MySQL");

        assertEquals("jdbc:mysql://serverurl:4444/dbname", props.jdbcUrl("serverurl", "4444", "dbname"));
        assertEquals("jdbc:mysql://serverurl:3306/dbname", props.jdbcUrl("serverurl", "", "dbname"));
        assertEquals("jdbc:mysql://serverurl:3306/dbname", props.jdbcUrl("serverurl", null, "dbname"));
        assertEquals("jdbc:mysql://localhost:3306/dbname", props.jdbcUrl("", null, "dbname"));
        assertEquals("jdbc:mysql://localhost:3306/dbname", props.jdbcUrl(null, null, "dbname"));
        assertEquals("jdbc:mysql://localhost:3306/test-db", props.jdbcUrl(null, null, ""));
        assertEquals("jdbc:mysql://localhost:3306/test-db", props.jdbcUrl(null, null, null));

        assertEquals(props.jdbcUrl(), props.jdbcUrl(null, null, null));
        assertEquals(props.jdbcUrl(), props.jdbcUrl("", "", ""));
    }

    @Test
    void shouldGetJdbcUrlForMSSql() {
        final DatabaseProperties props = DatabaseConnectionProperties.dbTypes.get("Microsoft SQL Server");

        assertEquals("jdbc:sqlserver://serverurl:4444;databaseName=dbname",
                     props.jdbcUrl("serverurl", "4444", "dbname"));
        assertEquals("jdbc:sqlserver://serverurl:1433;databaseName=dbname", props.jdbcUrl("serverurl", "", "dbname"));
        assertEquals("jdbc:sqlserver://serverurl:1433;databaseName=dbname",
                     props.jdbcUrl("serverurl", null, "dbname"));
        assertEquals("jdbc:sqlserver://localhost:1433;databaseName=dbname", props.jdbcUrl("", null, "dbname"));
        assertEquals("jdbc:sqlserver://localhost:1433;databaseName=dbname", props.jdbcUrl(null, null, "dbname"));
        assertEquals("jdbc:sqlserver://localhost:1433;databaseName=test-db", props.jdbcUrl(null, null, ""));
        assertEquals("jdbc:sqlserver://localhost:1433;databaseName=test-db", props.jdbcUrl(null, null, null));

        assertEquals(props.jdbcUrl(), props.jdbcUrl(null, null, null));
        assertEquals(props.jdbcUrl(), props.jdbcUrl("", "", ""));
    }

    @Test
    void shouldGetJdbcUrlForDB2() {
        final DatabaseProperties props = DatabaseConnectionProperties.dbTypes.get("DB/2");

        assertEquals("jdbc:db2:serverurl:4444/dbname", props.jdbcUrl("serverurl", "4444", "dbname"));
        assertEquals("jdbc:db2:serverurl:50000/dbname", props.jdbcUrl("serverurl", "", "dbname"));
        assertEquals("jdbc:db2:serverurl:50000/dbname", props.jdbcUrl("serverurl", null, "dbname"));
        assertEquals("jdbc:db2:localhost:50000/dbname", props.jdbcUrl("", null, "dbname"));
        assertEquals("jdbc:db2:localhost:50000/dbname", props.jdbcUrl(null, null, "dbname"));
        assertEquals("jdbc:db2:localhost:50000/test-db", props.jdbcUrl(null, null, ""));
        assertEquals("jdbc:db2:localhost:50000/test-db", props.jdbcUrl(null, null, null));

        assertEquals(props.jdbcUrl(), props.jdbcUrl(null, null, null));
        assertEquals(props.jdbcUrl(), props.jdbcUrl("", "", ""));
    }

    @Test
    void shouldGetJdbcUrlForOracle() {
        final DatabaseProperties props = DatabaseConnectionProperties.dbTypes.get("Oracle");

        assertEquals("jdbc:oracle:thin:@serverurl:4444:dbname", props.jdbcUrl("serverurl", "4444", "dbname"));
        assertEquals("jdbc:oracle:thin:@serverurl:1521:dbname", props.jdbcUrl("serverurl", "", "dbname"));
        assertEquals("jdbc:oracle:thin:@serverurl:1521:dbname", props.jdbcUrl("serverurl", null, "dbname"));
        assertEquals("jdbc:oracle:thin:@localhost:1521:dbname", props.jdbcUrl("", null, "dbname"));
        assertEquals("jdbc:oracle:thin:@localhost:1521:dbname", props.jdbcUrl(null, null, "dbname"));
        assertEquals("jdbc:oracle:thin:@localhost:1521:test-db", props.jdbcUrl(null, null, ""));
        assertEquals("jdbc:oracle:thin:@localhost:1521:test-db", props.jdbcUrl(null, null, null));

        assertEquals(props.jdbcUrl(), props.jdbcUrl(null, null, null));
        assertEquals(props.jdbcUrl(), props.jdbcUrl("", "", ""));
    }


}