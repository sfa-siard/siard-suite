package ch.admin.bar.siardsuite.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileHelper {

    public static Path createTempFile(final String filename, final byte[] data) throws IOException {
        val suffix = extractFileExtension(filename);
        val tempFilePath = Files.createTempFile(filename, "." + suffix);
        Files.write(tempFilePath, data);

        return tempFilePath;
    }

    private static String extractFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                .orElse("bin");
    }
}
