package ch.admin.bar.siardsuite;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    @Test
    void name() {
        // given


        // when
        Controller controller = new Controller();

        // then
        assertNotNull(controller);
    }
}