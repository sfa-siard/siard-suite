package ch.admin.bar.siardsuite.service;

import ch.admin.bar.siardsuite.model.database.DatabaseSchema;
import ch.admin.bar.siardsuite.model.database.DatabaseTable;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Builder
public class TableExporterService {

    @NonNull
    private final List<DatabaseSchema> schemas;

    @NonNull
    @Builder.Default
    private final Predicate<DatabaseTable> shouldBeExportedFilter = databaseTable -> true;

    @NonNull
    private final File exportDir;

    @NonNull
    @Builder.Default
    private final String lobsDirName = "lobs";

    public void export() throws IOException {
        for (val schema : schemas) {
            export(schema);
        }
    }

    private void export(DatabaseSchema schema) throws IOException {
        val filtered = schema.getTables().stream()
                .filter(shouldBeExportedFilter)
                .collect(Collectors.toList());

        for (val table : filtered) {
            export(table);
        }
    }

    private void export(DatabaseTable table) throws IOException {
        File destination = new File(exportDir.getAbsolutePath(), table.getName() + ".html");
        File lobFolder = new File(exportDir, lobsDirName);

        try (OutputStream outPutStream = Files.newOutputStream(destination.toPath())) {
            table.getTable().exportAsHtml(outPutStream, lobFolder);
        }
    }
}
