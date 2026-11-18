package ch.admin.bar.siardsuite.webcore.archive;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.fixture;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.get;
import static ch.admin.bar.siardsuite.webcore.archive.TestArchives.newScope;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spec requirement "Archives are referenced by opaque ids" (the id generator is tested in
 * {@link ArchiveIdTest}).
 */
@QuarkusTest
class OpaqueIdsTest {

    @Inject
    ArchiveRegistry registry;

    @TempDir
    Path tmp;

    @AfterEach
    void closeAll() {
        registry.closeAll();
    }

    @Test
    void registeringAValidArchive() throws IOException {
        ArchiveScope scope = newScope();
        String id = registry.register(scope, fixture("sample.siard"), () -> { }).value();

        assertTrue(id.length() >= 22, id);
        assertTrue(id.matches("[A-Za-z0-9_-]+"), id);
        assertFalse(id.toLowerCase().contains("sample"), id);
        get(scope, "/api/archives/" + id).then().statusCode(200);
    }

    @Test
    void registeringAFileThatIsNotAValidArchive() throws IOException {
        Path withoutMetadata = tmp.resolve("no-metadata.siard");
        try (OutputStream out = Files.newOutputStream(withoutMetadata);
             ZipOutputStream zip = new ZipOutputStream(out)) {
            zip.putNextEntry(new ZipEntry("content/readme.txt"));
            zip.write("no header".getBytes());
            zip.closeEntry();
        }
        Path notAZip = Files.writeString(tmp.resolve("not-a-zip.siard"), "plain text");
        Path missing = tmp.resolve("missing.siard");

        for (Path file : List.of(withoutMetadata, notAZip, missing)) {
            AtomicInteger releases = new AtomicInteger();
            int before = registry.size();
            assertThrows(IOException.class,
                    () -> registry.register(newScope(), file, releases::incrementAndGet), file.toString());
            assertEquals(before, registry.size(), file.toString());
            assertEquals(1, releases.get(), file.toString());
        }
    }
}
