package ch.admin.bar.siardsuite.ui.component;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static ch.admin.bar.siardsuite.ui.component.ButtonBox.Type.CANCEL;
import static ch.admin.bar.siardsuite.ui.component.ButtonBox.Type.DEFAULT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
public class ButtonBoxTest {

    @BeforeAll
    public static void setUpHeadlessMode() {
        System.setProperty("java.awt.headless", "true");
        System.setProperty("testfx.headless", "true");
    }

    @Test
    void shouldCreateDefaultButtonBox() {
        // given

        // when
        ButtonBox buttonBox = new ButtonBox().make(DEFAULT);

        // then
        assertNotNull(buttonBox);
        assertEquals(3, buttonBox.getChildren()
                                 .size());
        assertTrue(buttonBox.getChildren()
                            .contains(buttonBox.cancelButton));
        assertTrue(buttonBox.getChildren()
                            .contains(buttonBox.nextButton));
        assertTrue(buttonBox.getChildren()
                            .contains(buttonBox.previousButton));
        assertEquals("Cancel", buttonBox.cancelButton.getText());
        assertEquals("Next", buttonBox.nextButton.getText());
        assertEquals("Back", buttonBox.previousButton.getText());
    }

    @Test
    void shouldCreateCancelButtonBox() {
        // given

        // when
        ButtonBox buttonBox = new ButtonBox().make(CANCEL);

        // then
        assertNotNull(buttonBox);
        assertEquals(1, buttonBox.getChildren()
                                 .size());
        assertEquals(buttonBox.cancelButton, buttonBox.getChildren()
                                                   .get(0));
        assertEquals("Cancel", buttonBox.cancelButton.getText());
    }
}
