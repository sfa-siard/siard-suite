package ch.admin.bar.siard2.cmd.mysql.issues.siardsuite112;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlFileTypesIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MySQLContainer<?> db = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5_6))
            .withUsername("public")
            .withPassword("public")
            .withDatabaseName("public")
            .withInitScript(SqlScripts.MySQL.SIARDSUITE_112)
            .withConfigurationOverride("mysql/config/with-blobs");

    @Test
    public void downloadArchive() throws SQLException, IOException, ClassNotFoundException {
        loadFilesIntoDatabase();

        val siardArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb dbToSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbToSiard.getReturn());

        siardArchive.preserveArchive();
        val metadataExplorer = siardArchive.exploreMetadata();

        val columnAllFiles = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("public"))
                .tableId(Id.of("all_files"))
                .columnId(Id.of("file_data"))
                .build());
        Assertions.assertThat(columnAllFiles.getMimeType()).contains(Id.of("mixed"));

        val columnJpgFiles = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("public"))
                .tableId(Id.of("jpg_files"))
                .columnId(Id.of("file_data"))
                .build());
        Assertions.assertThat(columnJpgFiles.getMimeType()).contains(Id.of("image/jpeg"));

        val columnPdfFiles = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("public"))
                .tableId(Id.of("pdf_files"))
                .columnId(Id.of("file_data"))
                .build());
        Assertions.assertThat(columnPdfFiles.getMimeType()).contains(Id.of("application/pdf"));
    }

    private void loadFilesIntoDatabase() throws SQLException, IOException {
        String[] fileTypes = {"gif", "jpeg", "jpg", "png", "pdf", "webp", "tiff", "heif"};

        try (Connection connection = DriverManager.getConnection(db.getJdbcUrl(), db.getUsername(), db.getPassword())) {
            connection.setAutoCommit(false);

            String insertSql = "INSERT INTO public.all_files (filename, file_type, file_data) VALUES (?, ?, ?)";
            String insertPdfSql = "INSERT INTO public.pdf_files (filename, file_data) VALUES (?, ?)";
            String insertJpgSql = "INSERT INTO public.jpg_files (filename, file_data) VALUES (?, ?)";

            try {
                for (String fileType : fileTypes) {
                    List<String> files = getFilesInDirectory(fileType);

                    for (String filename : files) {
                        byte[] fileData = loadFileData(fileType, filename);

                        try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
                            stmt.setString(1, filename);
                            stmt.setString(2, fileType.toUpperCase());
                            stmt.setBytes(3, fileData);
                            stmt.executeUpdate();
                        }

                        if ("pdf".equalsIgnoreCase(fileType)) {
                            try (PreparedStatement stmt = connection.prepareStatement(insertPdfSql)) {
                                stmt.setString(1, filename);
                                stmt.setBytes(2, fileData);
                                stmt.executeUpdate();
                            }
                        }

                        if ("jpg".equalsIgnoreCase(fileType) || "jpeg".equalsIgnoreCase(fileType)) {
                            try (PreparedStatement stmt = connection.prepareStatement(insertJpgSql)) {
                                stmt.setString(1, filename);
                                stmt.setBytes(2, fileData);
                                stmt.executeUpdate();
                            }
                        }

                        System.out.println("Loaded file: " + filename);
                    }
                }

                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                System.err.println("Error loading files, rolling back transaction: " + e.getMessage());
                throw e;
            }
        }
    }

    private List<String> getFilesInDirectory(String fileType) {
        List<String> files = new ArrayList<>();
        try {
            URL resourceUrl = getClass().getResource("/mysql/issues/siardsuite112/" + fileType);
            if (resourceUrl != null) {
                Path dirPath = Paths.get(resourceUrl.toURI());
                Files.list(dirPath)
                        .filter(Files::isRegularFile)
                        .forEach(path -> files.add(path.getFileName().toString()));
            } else {
                System.err.println("Resource directory not found");
            }
        } catch (Exception e) {
            System.err.println("Could not scan directory " + fileType + ": " + e.getMessage());
        }
        return files;
    }

    private byte[] loadFileData(String fileType, String filename) throws IOException {
        String resourcePath = "/mysql/issues/siardsuite112/" + fileType + "/" + filename;

        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("File not found: " + resourcePath);
            }

            return is.readAllBytes();
        }
    }
}
