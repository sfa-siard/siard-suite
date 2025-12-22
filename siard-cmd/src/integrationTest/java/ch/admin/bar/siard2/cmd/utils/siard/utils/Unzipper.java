package ch.admin.bar.siard2.cmd.utils.siard.utils;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Utility class for extracting ZIP archives.
 * <p>
 * Usage example:
 * <pre>{@code
 * File zipArchive = new File("path/to/archive.siard");
 * File extractionDirectory = new File("path/to/extract");
 * Unzipper unzipper = new Unzipper(zipArchive, extractionDirectory);
 * File extractedDir = unzipper.unzip();
 * }</pre>
 */
@RequiredArgsConstructor
public class Unzipper {

    private final File pathToArchive;
    private final File extractTo;

    /**
     * Unzips the contents of the ZIP archive to the specified directory.
     */
    public File unzip() throws IOException {
        try (ZipFile zipFile = new ZipFile(pathToArchive)) {
            zipFile.stream().forEach(entry -> {
                try {
                    File newFile = newFile(extractTo, entry);
                    
                    if (entry.isDirectory()) {
                        if (!newFile.isDirectory() && !newFile.mkdirs()) {
                            throw new IOException("Failed to create directory " + newFile);
                        }
                    } else {
                        // fix for Windows-created archives
                        final File parent = newFile.getParentFile();
                        if (!parent.isDirectory() && !parent.mkdirs()) {
                            throw new IOException("Failed to create directory " + parent);
                        }

                        // write file content
                        try (InputStream is = zipFile.getInputStream(entry);
                             FileOutputStream fos = new FileOutputStream(newFile)) {
                            
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = is.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        
        return extractTo;
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}
