package ch.enterag.utils.mime;

import org.apache.tika.mime.MimeTypeException;

import static org.apache.tika.mime.MimeTypes.getDefaultMimeTypes;

public class MimeTypes {
    private static final String DEFAULT_EXTENSION = "bin";

    public static String getExtension(String mimeType) {
        try {
            String extension = getDefaultMimeTypes().forName(mimeType).getExtension();
            if (extension.startsWith(".")) {
                return extension.substring(1);
            } else {
                return DEFAULT_EXTENSION;
            }
        } catch (MimeTypeException e) {
            return DEFAULT_EXTENSION;
        }
    }
}


