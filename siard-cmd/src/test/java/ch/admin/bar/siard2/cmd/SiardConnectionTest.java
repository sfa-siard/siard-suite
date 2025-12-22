package ch.admin.bar.siard2.cmd;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class SiardConnectionTest {

    @Test
    public void test() {
        // given

        // when
        val subSchema = SiardConnection.extractSubSchema("jdbc:mysql://localhost:59483/test");

        // then
        Assertions.assertThat(subSchema).isEqualTo("mysql");
    }
}