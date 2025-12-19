package ch.admin.bar.siard2.cmd.utils.siard.model.header;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.NonNull;
import lombok.val;

import java.io.File;
import java.io.IOException;

public class MetadataReader {

    @NonNull
    private final File pathToExtractedArchive;

    private final XmlMapper xmlMapper = new XmlMapper();

    public MetadataReader(@NonNull File pathToExtractedArchive) {
        this.pathToExtractedArchive = pathToExtractedArchive;

        xmlMapper
                .registerModule(new Jdk8Module())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    }

    public Metadata read() {
        val file = new File(pathToExtractedArchive + "/header/metadata.xml");
        try {
            return xmlMapper.readValue(file, Metadata.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize metadata at " + file);
        }
    }
}
