package ch.admin.bar.siardsuite.component;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.*;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(ApplicationExtension.class)
public class ButtonBoxTest {

    @Before
    public void setUpHeadlessMode() {
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
        assertThat(buttonBox.getChildren().size(), is(3));
        assertThat(buttonBox.getChildren(), hasItems(buttonBox.cancelButton, buttonBox.nextButton, buttonBox.previousButton));
        assertThat(buttonBox.cancelButton.getText(), is("Cancel"));
        assertThat(buttonBox.nextButton.getText(), is("Next"));
        assertThat(buttonBox.previousButton.getText(), is("Back"));
    }

    @Test
    void shouldCreateCancelButtonBox() {
        // given

        // when
        ButtonBox buttonBox = new ButtonBox().make(CANCEL);

        // then
        assertNotNull(buttonBox);
        assertThat(buttonBox.getChildren().size(), is(1));
        assertThat(buttonBox.getChildren().get(0), is(buttonBox.cancelButton));
        assertThat(buttonBox.cancelButton.getText(), is("Cancel"));
    }

    @Test
    void shouldCreateDownloadFinishedButtonBox() {
        // given

        // when
        ButtonBox buttonBox = new ButtonBox().make(DOWNLOAD_FINISHED);

        // then
        assertNotNull(buttonBox);
        assertThat(buttonBox.getChildren().size(), is(2));
        assertThat(buttonBox.getChildren(), hasItems(buttonBox.cancelButton, buttonBox.nextButton));
        assertThat(buttonBox.cancelButton.getText(), is("View archive"));
        assertThat(buttonBox.nextButton.getText(), is("Home"));
    }
}
