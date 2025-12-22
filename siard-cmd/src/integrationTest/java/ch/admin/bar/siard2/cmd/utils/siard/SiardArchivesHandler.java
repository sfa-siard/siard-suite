package ch.admin.bar.siard2.cmd.utils.siard;

import ch.admin.bar.siard2.cmd.utils.siard.model.SiardArchive;
import ch.admin.bar.siard2.cmd.utils.siard.model.content.ContentReader;
import ch.admin.bar.siard2.cmd.utils.siard.model.header.MetadataReader;
import ch.admin.bar.siard2.cmd.utils.siard.utils.ContentExplorer;
import ch.admin.bar.siard2.cmd.utils.siard.utils.MetadataExplorer;
import ch.admin.bar.siard2.cmd.utils.siard.utils.Unzipper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static ch.admin.bar.siard2.cmd.utils.TestResourcesResolver.resolve;

/**
 * This class is used for handling SIARD archives and their file paths. It supports two use cases:
 * - Exploring an existing SIARD archive. In this case, use {@link #prepareResource(String)}.
 * - Creating a temporary directory for downloading a new SIARD archive and exploring it.
 * In this case, use {@link #prepareEmpty()}.
 * <p>
 * Note: Annotate a {@link SiardArchivesHandler} instance always as {@link org.junit.Rule}
 */
public class SiardArchivesHandler extends ExternalResource {

    private final TemporaryFolder temporaryFolder = new TemporaryFolder();
    private final XmlMapper xmlMapper = new XmlMapper();

    private String testClassName;
    private String testName;

    @SneakyThrows
    public SiardArchivesHandler() {
        xmlMapper
                .registerModule(new Jdk8Module())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    }

    /**
     * Prepares a SIARD archive explorer for the provided SIARD archive resource. The returned
     * {@link SiardArchiveExplorer} can be used immediately after this method call (data is available).
     */
    @SneakyThrows
    public SiardArchiveExplorer prepareResource(String resource) {
        final File pathToArchive = resolve(resource);
        final File pathToExtracted = temporaryFolder.newFolder();

        return createExplorer(pathToArchive, pathToExtracted);
    }

    /**
     * Prepares a SIARD archive explorer for which the data is not yet available. The returned
     * {@link SiardArchiveExplorer} can NOT be used immediately after this method call. The SIARD archive
     * needs to be downloaded/copied first to the {@link SiardArchiveExplorer#getPathToArchiveFile()} location.
     */
    @SneakyThrows
    public SiardArchiveExplorer prepareEmpty() {
        final File pathToArchive = File.createTempFile("temp", ".siard", temporaryFolder.getRoot());
        final File pathToExtracted = temporaryFolder.newFolder();

        return createExplorer(pathToArchive, pathToExtracted);
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        temporaryFolder.create();
    }

    @NotNull
    @Override
    public Statement apply(@NotNull Statement base, @NotNull Description description) {
        testClassName = description.getClassName();
        testName = description.getMethodName();

        return super.apply(base, description);
    }

    @Override
    protected void after() {
        super.after();
        temporaryFolder.delete();
    }

    private SiardArchiveExplorer createExplorer(final File pathToArchive, final File pathToExtracted) {
        return SiardArchiveExplorer.builder()
                .xmlMapper(xmlMapper)
                .pathToArchiveFile(pathToArchive)
                .pathToExtractingDirectory(pathToExtracted)
                .testClassName(testClassName)
                .testName(testName)
                .build();
    }

    /**
     * A builder class for simplifying the exploration of SIARD archives. This includes extraction, deserialization,
     * and browsing of the data.
     * <p/>
     * Please use the {@link SiardArchivesHandler#prepareResource(String)} or the
     * {@link SiardArchivesHandler#prepareEmpty()} method to create a new {@link SiardArchiveExplorer} instance.
     */
    @Slf4j
    @Builder
    @RequiredArgsConstructor
    public static class SiardArchiveExplorer {

        @NonNull
        private final XmlMapper xmlMapper;
        @NonNull
        @Getter
        private final File pathToArchiveFile;
        @NonNull
        private final File pathToExtractingDirectory;

        @NonNull
        private final String testClassName;
        @NonNull
        private final String testName;

        /**
         * Extracts the SIARD archive (if not done before) and deserializes the data into the returned {@link SiardArchive} instance.
         */
        public SiardArchive readArchive() {
            if (!isArchiveAvailable()) {
                throw new IllegalStateException("No SIARD archive is available");
            }
            if (!isExtractedAvailable()) {
                extractArchive();
            }

            val metadata = new MetadataReader(pathToExtractingDirectory).read();
            val content = new ContentReader(pathToExtractingDirectory).read();

            return new SiardArchive(metadata, content);
        }

        /**
         * Extracts the SIARD archive (if not done before) and deserializes the data. Wraps the data into a
         * {@link MetadataExplorer} instance which provides methods to search the metadata of that data.
         */
        public MetadataExplorer exploreMetadata() {
            return new MetadataExplorer(readArchive());
        }

        /**
         * Extracts the SIARD archive (if not done before) and deserializes the data. Wraps the data into a
         * {@link ContentExplorer} instance which provides methods to search the content of that data.
         */
        public ContentExplorer exploreContent() {
            return new ContentExplorer(readArchive());
        }

        /**
         * Preserves the SIARD archive by copying it to the test-outputs-directory. This is useful if a test does
         * download a SIARD archive and the downloaded data should be preserved for  analytics reasons.
         */
        @SneakyThrows
        public SiardArchiveExplorer preserveArchive() {
            val filename = pathToArchiveFile.getName();

            val outputFile = new File(String.format("build/test-outputs/%s/%s/%s",
                    testClassName,
                    testName,
                    filename));

            Files.createDirectories(outputFile.getParentFile().toPath());

            if (outputFile.exists()) {
                outputFile.delete();
            }

            Files.copy(
                    pathToArchiveFile.toPath(),
                    outputFile.toPath());

            log.info("Archive {} preserved at {}", filename, outputFile.getAbsolutePath());

            return this;
        }

        private boolean isArchiveAvailable() {
            return pathToArchiveFile.exists();
        }

        private boolean isExtractedAvailable() {
            val children = pathToExtractingDirectory.list();
            if (children == null) {
                throw new IllegalStateException("The 'Path to extracting location' does not refer to a valid directory.");
            }

            return children.length > 0;
        }

        private void extractArchive() {
            try {
                new Unzipper(pathToArchiveFile, pathToExtractingDirectory)
                        .unzip();
            } catch (IOException e) {
                throw new RuntimeException("Failed to unzip " + pathToArchiveFile, e);
            }
        }
    }
}
