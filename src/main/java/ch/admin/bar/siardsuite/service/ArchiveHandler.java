package ch.admin.bar.siardsuite.service;

import ch.admin.bar.siard2.api.Archive;
import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siardsuite.model.UserDefinedMetadata;
import ch.admin.bar.siardsuite.model.facades.PreTypeFacade;
import ch.admin.bar.siardsuite.ui.presenter.archive.browser.forms.utils.ListAssembler;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility class to handle archive operations.
 */
@Slf4j
public class ArchiveHandler {

    /**
     * Initializes a new archive.
     * If the destination file already exists, it will be overwritten after deletion.
     *
     * @param destination The destination file for the archive.
     * @return The initialized archive.
     */
    @SneakyThrows
    public Archive init(final File destination) {
        if (destination.exists()) {
            log.warn("Archive at location {} will be overwritten", destination.getAbsolutePath());
            destination.delete();
        }

        final Archive archive = ArchiveImpl.newInstance();
        archive.create(destination);

        log.info("Archive initialized at {}", destination.getAbsolutePath());
        return archive;
    }

    /**
     * Initializes a new temporary archive.
     *
     * @return The initialized archive.
     */
    @SneakyThrows
    public Archive init() {
        val tempFile = createTempFile();
        tempFile.delete(); // needs to be deleted, otherwise ArchiveImpl.java throws an error

        return init(tempFile);
    }

    /**
     * Saves the archive to the specified destination file.
     *
     * @param archive     The archive to be saved.
     * @param destination The destination file.
     */
    @SneakyThrows
    public void save(@NonNull final Archive archive, @NonNull final File destination) {
        if (archive.getFile() == null) {
            // archive is not open
            archive.open(destination);
        } else if (!archive.getFile().equals(destination)) {
            throw new IllegalArgumentException("Tried to store open archive to different location");
        }

        archive.saveMetaData();
        archive.close();

        log.info("Archive saved to {}", destination.getAbsolutePath());
    }

    /**
     * Copies the archive to the specified destination file.
     *
     * @param archive     The archive to be copied.
     * @param destination The destination file.
     * @return The copied archive.
     */
    @SneakyThrows
    public Archive copy(final Archive archive, final File destination) {
        if (archive.getFile() == null) {
            // archive is not open, no file can be accessed
            archive.open(createTempFile());
        }

        val archiveFile = archive.getFile();

        if (archive.getFile() != null) {
            // archive is open, zip file is probably not valid
            archive.saveMetaData();
            archive.close();
        }

        copyFileUsingStream(archiveFile, destination);
        val archiveCopy = ArchiveImpl.newInstance();
        archiveCopy.open(destination);

        log.info("Archive copied from {} to {}",
                archiveFile.getAbsolutePath(),
                destination.getAbsolutePath());

        return archiveCopy;
    }

    @SneakyThrows
    public ArchiveHandler write(final Archive archive, final UserDefinedMetadata userDefinedMetadata) {
        val metaData = archive.getMetaData();

        metaData.setDbName(userDefinedMetadata.getDbName());
        metaData.setDescription(userDefinedMetadata.getDescription());
        metaData.setDataOwner(userDefinedMetadata.getOwner());
        metaData.setDataOriginTimespan(userDefinedMetadata.getDataOriginTimespan());
        metaData.setArchiver(userDefinedMetadata.getArchiverName());
        metaData.setArchiverContact(userDefinedMetadata.getArchiverContact());

        Optional.ofNullable(userDefinedMetadata.getLobFolder())
                .ifPresent(uri -> setExternalLobFolder(archive, uri));

        return this;
    }

    /**
     * Sets the (external) location for LOB's. Caution: Can only be called, if the
     * archive contains just metadata and no content yet!
     */
    @SneakyThrows
    public void setExternalLobFolder(final Archive archive, final URI lobsDest) {
        val lobDestAsString = lobsDest.toString().toLowerCase();
        val root = lobDestAsString.endsWith("/") ? lobDestAsString : lobDestAsString + "/";
        archive.getMetaData().setLobFolder(new URI(root));

        val blobColumns = findBlobColumns(archive);

        for (val blobColumn : blobColumns) {
            val schemaName = blobColumn.getParentMetaTable().getParentMetaSchema().getName().toLowerCase();
            val tableName = blobColumn.getParentMetaTable().getName().toLowerCase();
            val columnName = blobColumn.getName().toLowerCase();

            val columnBlobUri = new URI(schemaName + "/" + tableName + "/" + columnName + "/"); // relative to the lob-folder of the siard-archive
            blobColumn.setLobFolder(columnBlobUri);
        }
    }

    private List<MetaColumn> findBlobColumns(final Archive archive) {
        return ListAssembler.assemble(archive.getSchemas(), archive::getSchema).stream()
                .flatMap(schema -> ListAssembler.assemble(schema.getTables(), schema::getTable).stream()
                        .map(Table::getMetaTable)
                        .flatMap(table -> ListAssembler.assemble(table.getMetaColumns(), table::getMetaColumn).stream()
                                .filter(this::isBlobColumn)))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private boolean isBlobColumn(final MetaColumn metaColumn) {
        return new PreTypeFacade(metaColumn.getPreType()).isBlob();
    }

    private static void copyFileUsingStream(File source, File dest) throws IOException {
        try (InputStream is = Files.newInputStream(source.toPath()); OutputStream os = Files.newOutputStream(dest.toPath())) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    private static File createTempFile() throws IOException {
        val tempFile = File.createTempFile("tmp", ".siard");
        tempFile.deleteOnExit();

        return tempFile;
    }
}
