package ch.admin.bar.siardsuite.component;

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static ch.admin.bar.siardsuite.component.ButtonBox.Type.*;
import static org.hamcrest.core.Is.is;

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

        // whencd .
        ButtonBox buttonBox = new ButtonBox().make(DEFAULT);

        // then
        Assertions.assertNotNull(buttonBox);
        Assert.assertThat(buttonBox.getChildren().size(), Is.is(3));
        Assert.assertThat(buttonBox.getChildren(),
                          CoreMatchers.hasItems(buttonBox.cancelButton,
                                                buttonBox.nextButton,
                                                buttonBox.previousButton));
        Assert.assertThat(buttonBox.cancelButton.getText(), Is.is("Cancel"));
        Assert.assertThat(buttonBox.nextButton.getText(), Is.is("Next"));
        Assert.assertThat(buttonBox.previousButton.getText(), Is.is("Back"));
    }

    @Test
    void shouldCreateCancelButtonBox() {
        // given

        // when
        ButtonBox buttonBox = new ButtonBox().make(CANCEL);

        // then
        Assertions.assertNotNull(buttonBox);
        Assert.assertThat(buttonBox.getChildren().size(), Is.is(1));
        Assert.assertThat(buttonBox.getChildren().get(0), is(buttonBox.cancelButton));
        Assert.assertThat(buttonBox.cancelButton.getText(), Is.is("Cancel"));
    }

    @Test
    void shouldCreateDownloadFinishedButtonBox() {
        // given

        // when
        ButtonBox buttonBox = new ButtonBox().make(DOWNLOAD_FINISHED);

        // then
        Assertions.assertNotNull(buttonBox);
        Assert.assertThat(buttonBox.getChildren().size(), Is.is(2));
        Assert.assertThat(buttonBox.getChildren(), CoreMatchers.hasItems(buttonBox.cancelButton, buttonBox.nextButton));
        Assert.assertThat(buttonBox.cancelButton.getText(), Is.is("View archive"));
        Assert.assertThat(buttonBox.nextButton.getText(), Is.is("Home"));
    }
}
