package ch.admin.bar.siardsuite.util;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.testfx.assertions.api.Assertions;

import java.io.File;

class FileHelperTest {

    @Test
    public void extractFilenameWithoutExtension_withFileWithExtension_expectFilenameWithoutExtension() {
        // given
        val file = new File("C:/i/am/a/file_with.extension");

        // when
        val filename = FileHelper.extractFilenameWithoutExtension(file);

        // then
        Assertions.assertThat(filename).isEqualTo("file_with");
    }

    @Test
    public void extractFilenameWithoutExtension_withFileWithoutExtension_expectFilenameWithoutExtension() {
        // given
        val file = new File("C:/i/am/a/file_without_extension");

        // when
        val filename = FileHelper.extractFilenameWithoutExtension(file);

        // then
        Assertions.assertThat(filename).isEqualTo("file_without_extension");
    }

}