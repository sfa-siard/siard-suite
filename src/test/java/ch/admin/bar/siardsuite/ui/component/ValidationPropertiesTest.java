package ch.admin.bar.siardsuite.ui.component;

import ch.admin.bar.siardsuite.ui.component.ValidationProperties;
import ch.admin.bar.siardsuite.ui.component.ValidationProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class ValidationPropertiesTest {

    private TextField field1;
    private Label msg1;
    private TextField field2;
    private Label msg2;
    private ValidationProperties validationProperties;

    @Before
    public void setUpHeadlessMode() {
        System.setProperty("java.awt.headless", "true");
        System.setProperty("testfx.headless", "true");
    }

    @BeforeEach
    void setUp() {
        field1 = new TextField("");
        msg1 = new Label("");
        field2 = new TextField("");
        msg2 = new Label("");

        validationProperties = new ValidationProperties(Arrays.asList(new ValidationProperty(field1,
                                                                                             msg1,
                                                                                             "test.validationmsg1"),
                                                                      new ValidationProperty(field2,
                                                                                             msg2,
                                                                                             "test.validationmsg2")));
    }

    @Test
    void shouldValidateAllProperties() {
        boolean valid = validationProperties.validate();

        assertFalse(valid);
        assertEquals(msg1.textProperty().get(), "test.validationmsg1");
        assertEquals(msg2.textProperty().get(), "test.validationmsg2");
    }

    @Test
    void shouldReturnTrueWhenAllPropertiesAreSet() {
        field1.setText("text1");
        field2.setText("text2");

        boolean valid = validationProperties.validate();

        assertTrue(valid);
        assertEquals(msg1.textProperty().get(), "");
        assertEquals(msg2.textProperty().get(), "");
    }

    @Test
    void shouldReturnFalseWhenOnePropertyIsInvalid() {
        field1.setText("text1");

        boolean valid = validationProperties.validate();

        assertFalse(valid);
        assertEquals(msg1.textProperty().get(), "");
        assertEquals(msg2.textProperty().get(), "test.validationmsg2");
    }
}