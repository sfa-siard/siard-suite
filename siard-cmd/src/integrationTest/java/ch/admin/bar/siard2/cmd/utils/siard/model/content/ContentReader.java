package ch.admin.bar.siard2.cmd.utils.siard.model.content;

import ch.admin.bar.siard2.cmd.utils.siard.model.utils.FolderId;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ContentReader {

    @NonNull
    private final File pathToExtractedArchive;

    private final XmlMapper xmlMapper = new XmlMapper();

    public ContentReader(@NonNull File pathToExtractedArchive) {
        this.pathToExtractedArchive = pathToExtractedArchive;

        xmlMapper
                .registerModule(new Jdk8Module())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    }

    public Content read() {
        val tableXmlFiles = findTableXmlFiles();

        val tables = tableXmlFiles.stream()
                .map(file -> {
                    try {
                        val tableContent = xmlMapper.readValue(file.getFile(), Content.TableContent.class);
                        return Content.Table.builder()
                                .schemaFolder(file.getSchemaFolder())
                                .tableFolder(file.getTableFolder())
                                .tableContent(tableContent)
                                .build();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to deserialize " + file);
                    }
                })
                .collect(Collectors.toList());

        return new Content(tables);
    }

    private List<TableXmlFile> findTableXmlFiles() {
        val files = Arrays.stream(new File(pathToExtractedArchive + "/content").listFiles())
                .filter(File::isDirectory)
                .flatMap(schemaDir -> Arrays.stream(schemaDir.listFiles()))
                .filter(File::isDirectory)
                .flatMap(tableDir -> Arrays.stream(tableDir.listFiles()))
                .filter(file -> file.getName().endsWith(".xml"))
                .collect(Collectors.toList());

        return files.stream()
                .map(file -> {
                    val tableDir = file.getParentFile();
                    val schemaDir = tableDir.getParentFile();

                    return TableXmlFile.builder()
                            .schemaFolder(FolderId.of(schemaDir.getName()))
                            .tableFolder(FolderId.of(tableDir.getName()))
                            .file(file)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public static class Deserializer extends StdDeserializer<Content.TableRow> {

        public Deserializer() {
            super(Content.TableRow.class);
        }

        @Override
        public Content.TableRow deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            final JsonNode node = jp.getCodec().readTree(jp);

            val cells = stream(node.fieldNames())
                    .map(fieldName -> {
                        val number = Integer.parseInt(fieldName.substring(1));
                        val value = node.get(fieldName).asText();

                        return new Content.TableCell(number, value);
                    })
                    .collect(Collectors.toList());

            return new Content.TableRow(cells);
        }

        private static <T> Stream<T> stream(Iterator<T> iterator) {
            return StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                    false);
        }
    }

    @Value
    @Builder
    private static class TableXmlFile {
        @NonNull FolderId schemaFolder;
        @NonNull FolderId tableFolder;
        @NonNull File file;
    }
}
