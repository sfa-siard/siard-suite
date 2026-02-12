package ch.admin.bar.siard2.api.export;

import ch.admin.bar.siard2.api.MetaValue;
import ch.admin.bar.siard2.api.Value;

import java.io.*;
import java.net.URI;
import java.sql.Types;

/**
 * Service class for handling LOB (Large Object) file operations during HTML export.
 * Manages copying internal LOBs to external folders and generating appropriate file links.
 */
class LobFileHandler {
    
    private static final int BUFFER_SIZE = 8192;
    
    private final File lobFolder;
    
    /**
     * Create a new LOB file handler.
     *
     * @param lobFolder the folder where LOB files should be copied (can be null for external LOBs only)
     */
    LobFileHandler(File lobFolder) {
        this.lobFolder = lobFolder;
    }
    
    /**
     * Process a LOB value and return the appropriate file name/path for HTML links.
     * Handles both external LOBs (already have absolute paths) and internal LOBs (need to be copied).
     *
     * @param value the LOB value to process
     * @param fileName the original file name from the LOB
     * @return the processed file name/path for HTML links
     * @throws IOException if an I/O error occurs during file operations
     */
    String processLobFile(Value value, String fileName) throws IOException {
        MetaValue metaValue = value.getMetaValue();
        URI absoluteLobFolder = metaValue.getAbsoluteLobFolder();
        
        if (absoluteLobFolder != null) {
            // External LOB - return absolute URL
            return absoluteLobFolder.resolve(fileName).toURL().toString();
        } else if (lobFolder != null) {
            // Internal LOB - copy to output folder
            copyInternalLobToFolder(value, fileName);
            return fileName; // Return relative path for HTML link
        } else {
            // No LOB folder specified - return original filename
            return fileName;
        }
    }
    
    /**
     * Copy an internal LOB file to the configured LOB folder.
     *
     * @param value the LOB value containing the data
     * @param fileName the target file name
     * @throws IOException if an I/O error occurs during copying
     */
    private void copyInternalLobToFolder(Value value, String fileName) throws IOException {
        File targetFile = new File(lobFolder, fileName);
        
        // Ensure parent directories exist
        File parentDir = targetFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        MetaValue metaValue = value.getMetaValue();
        int predefinedType = metaValue.getPreType();
        
        if (isBinaryType(predefinedType)) {
            copyBinaryLob(value, targetFile);
        } else {
            copyTextLob(value, targetFile);
        }
    }
    
    /**
     * Check if the given SQL type represents a binary LOB type.
     *
     * @param sqlType the SQL type constant
     * @return true if the type is binary
     */
    private boolean isBinaryType(int sqlType) {
        return sqlType == Types.BINARY || 
               sqlType == Types.VARBINARY || 
               sqlType == Types.BLOB || 
               sqlType == Types.DATALINK;
    }
    
    /**
     * Copy a binary LOB to the target file.
     *
     * @param value the LOB value
     * @param targetFile the target file
     * @throws IOException if an I/O error occurs
     */
    private void copyBinaryLob(Value value, File targetFile) throws IOException {
        try (InputStream inputStream = value.getInputStream()) {
            if (inputStream != null) {
                try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }
        }
    }
    
    /**
     * Copy a text LOB to the target file.
     *
     * @param value the LOB value
     * @param targetFile the target file
     * @throws IOException if an I/O error occurs
     */
    private void copyTextLob(Value value, File targetFile) throws IOException {
        try (Reader reader = value.getReader()) {
            if (reader != null) {
                try (Writer writer = new FileWriter(targetFile)) {
                    char[] buffer = new char[BUFFER_SIZE];
                    int charsRead;
                    while ((charsRead = reader.read(buffer)) != -1) {
                        writer.write(buffer, 0, charsRead);
                    }
                }
            }
        }
    }
    
    /**
     * Get the configured LOB folder.
     *
     * @return the LOB folder, or null if not configured
     */
    File getLobFolder() {
        return lobFolder;
    }
}
