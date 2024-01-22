package ch.admin.bar.siardsuite.database;

import ch.admin.bar.siardsuite.database.model.DbmsId;
import ch.admin.bar.siardsuite.database.model.ServerBasedDbms;
import ch.admin.bar.siardsuite.database.model.ServerBasedDbmsConnectionProperties;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.testfx.assertions.api.Assertions;

import java.util.Optional;

public class DbmsRegistryTest {

    @Test
    public void test() throws Exception {
        // given
        final ServerBasedDbms dbms = (ServerBasedDbms) DbmsRegistry.findDbmsById(DbmsId.of("postgresql"));

        // when
        val properties = dbms.getJdbcConnectionStringDecoder().apply("jdbc:postgresql://db.host.org:1234/test?ssl=true");

        // then
        Assertions.assertThat(properties)
                        .isEqualTo(ServerBasedDbmsConnectionProperties.builder()
                                .host("db.host.org")
                                .port("1234")
                                .dbName("test")
                                .options(Optional.ofNullable("ssl=true"))
                                .user("")
                                .password("")
                                .build());
    }
}