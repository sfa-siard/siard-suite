package ch.admin.bar.siard2.cmd;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class SiardConnectionTest {

    private static final String DRIVERS_PROPERTY = "ch.admin.bar.siard2.cmd.drivers";

    @Test
    public void test() {
        // given

        // when
        val subSchema = SiardConnection.extractSubSchema("jdbc:mysql://localhost:59483/test");

        // then
        Assertions.assertThat(subSchema)
                  .isEqualTo("mysql");
    }

    @Test
    void bundledDefaults_provideMySqlTitleAndAllSchemes() {
        // when
        SiardConnection connection = SiardConnection.getSiardConnection();

        // then
        assertThat(connection.getTitle("mysql")).isEqualTo("MySQL");
        assertThat(connection.getSchemes()).contains(
                "postgresql", "oracle", "sqlserver", "mysql", "db2", "access");
    }

    @Test
    void driversOverride_loadsExternalPropertiesFile(@TempDir Path tempDir) throws Exception {
        // given
        Path props = tempDir.resolve("jdbcdrivers.properties");
        Files.writeString(props, "postgresql=org.example.FakeDriver\npostgresql_title=CustomPG\n");

        Field singleton = SiardConnection.class.getDeclaredField("_sc");
        singleton.setAccessible(true);
        Object previous = singleton.get(null);
        System.setProperty(DRIVERS_PROPERTY, props.toString());
        try {
            // when
            singleton.set(null, null);
            SiardConnection connection = SiardConnection.getSiardConnection();

            // then
            assertThat(connection.getTitle("postgresql")).isEqualTo("CustomPG");
        } finally {
            singleton.set(null, previous);
            System.clearProperty(DRIVERS_PROPERTY);
        }
    }
}
