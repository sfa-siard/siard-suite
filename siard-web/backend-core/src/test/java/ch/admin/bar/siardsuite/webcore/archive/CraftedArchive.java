package ch.admin.bar.siardsuite.webcore.archive;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Builds a variant of a fixture for one scenario: a copy of the archive with single entries
 * replaced, renamed or added (design D13). Entries are written uncompressed, so the copy has no
 * data descriptors.
 */
final class CraftedArchive {

    static final String METADATA = "header/metadata.xml";
    static final String TSIMPLE = "content/schema0/table0/table0.xml";

    private final Path source;
    private final Map<String, UnaryOperator<byte[]>> changes = new LinkedHashMap<>();
    private final Map<String, String> renames = new LinkedHashMap<>();

    private CraftedArchive(Path source) {
        this.source = source;
    }

    static CraftedArchive of(String fixture) {
        return new CraftedArchive(TestArchives.fixture(fixture));
    }

    CraftedArchive replaceText(String entry, String from, String to) {
        return change(entry, bytes -> {
            String text = new String(bytes, StandardCharsets.UTF_8);
            if (!text.contains(from)) {
                throw new IllegalArgumentException(from + " not in " + entry);
            }
            return text.replace(from, to).getBytes(StandardCharsets.UTF_8);
        });
    }

    /**
     * Gives the LOB stored in {@code file} of table {@code TSIMPLE} new content under a new entry
     * name, and points its cell there with the new length and no digest.
     */
    CraftedArchive replaceLob(String file, String newFile, byte[] content) {
        renames.put(file, newFile);
        change(file, bytes -> content);
        return change(TSIMPLE, bytes -> new String(bytes, StandardCharsets.UTF_8)
                .replaceFirst("(<c\\d+) [^>]*file=\"" + file + "\"[^>]*/>",
                        "$1 file=\"" + newFile + "\" length=\"" + content.length + "\"/>")
                .getBytes(StandardCharsets.UTF_8));
    }

    private CraftedArchive change(String entry, UnaryOperator<byte[]> change) {
        changes.merge(entry, change, (a, b) -> bytes -> b.apply(a.apply(bytes)));
        return this;
    }

    Path writeTo(Path directory) throws IOException {
        Path target = directory.resolve("crafted-" + source.getFileName());
        try (ZipFile zip = new ZipFile(source.toFile());
             OutputStream out = Files.newOutputStream(target);
             ZipOutputStream zipOut = new ZipOutputStream(out)) {
            for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = entries.nextElement();
                byte[] bytes;
                try (InputStream in = zip.getInputStream(entry)) {
                    bytes = in.readAllBytes();
                }
                bytes = changes.getOrDefault(entry.getName(), b -> b).apply(bytes);
                ZipEntry copy = new ZipEntry(renames.getOrDefault(entry.getName(), entry.getName()));
                CRC32 crc = new CRC32();
                crc.update(bytes);
                copy.setMethod(ZipEntry.STORED);
                copy.setSize(bytes.length);
                copy.setCompressedSize(bytes.length);
                copy.setCrc(crc.getValue());
                zipOut.putNextEntry(copy);
                zipOut.write(bytes);
                zipOut.closeEntry();
            }
        }
        return target;
    }

    /** Bytes of an entry of a fixture, as stored in the archive. */
    static byte[] entry(String fixture, String entry) throws IOException {
        try (ZipFile zip = new ZipFile(TestArchives.fixture(fixture).toFile());
             InputStream in = zip.getInputStream(zip.getEntry(entry))) {
            return in.readAllBytes();
        }
    }
}
