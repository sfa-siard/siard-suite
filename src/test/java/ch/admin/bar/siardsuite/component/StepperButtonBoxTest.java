package ch.admin.bar.siardsuite.component;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static ch.admin.bar.siardsuite.component.StepperButtonBox.Type.*;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(ApplicationExtension.class)
public class StepperButtonBoxTest {

    @Test
    void shouldCreateDefaultButtonBox() {
        // given

        // when
        StepperButtonBox buttonBox = new StepperButtonBox().make(DEFAULT);

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
        StepperButtonBox buttonBox = new StepperButtonBox().make(CANCEL);

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
        StepperButtonBox buttonBox = new StepperButtonBox().make(DOWNLOAD_FINISHED);

        // then
        assertNotNull(buttonBox);
        assertThat(buttonBox.getChildren().size(), is(2));
        assertThat(buttonBox.getChildren(), hasItems(buttonBox.cancelButton, buttonBox.nextButton));
        assertThat(buttonBox.cancelButton.getText(), is("Home"));
        assertThat(buttonBox.nextButton.getText(), is("View archive"));
    }
}