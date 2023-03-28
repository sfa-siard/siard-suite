package ch.admin.bar.siardsuite;

import ch.admin.bar.siardsuite.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ControllerTest {

    @Test
    void shouldCreateController() {
        // given
        Model model = new Model();

        // when
        Controller controller = new Controller(model);

        // then
        Assertions.assertNotNull(controller);
    }
}
