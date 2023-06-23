package ch.admin.bar.siardsuite.model.facades;

import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siard2.api.MetaSchema;
import ch.admin.bar.siard2.api.MetaTable;
import ch.admin.bar.siardsuite.model.database.Privilige;
import ch.admin.bar.siardsuite.model.database.User;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MetaDataFacade {

    private final MetaData metaData;

    public MetaDataFacade(MetaData metaData) {
        this.metaData = metaData;
    }

    public List<User> users() {
        return IntStream.range(0, this.metaData.getMetaUsers())
                        .mapToObj(this.metaData::getMetaUser)
                        .map(user -> new User(user.getName(), user.getDescription()))
                        .collect(Collectors.toList());
    }

    public List<Privilige> priviliges() {
        return IntStream.range(0, this.metaData.getMetaPrivileges())
                        .mapToObj(this.metaData::getMetaPrivilege)
                        .map(privilige -> new Privilige(privilige.getType(),
                                                        privilige.getObject(),
                                                        privilige.getGrantor(),
                                                        privilige.getGrantee(),
                                                        privilige.getOption(),
                                                        privilige.getDescription()))
                        .collect(Collectors.toList());
    }

    public void setLobFolder(URI lobFolder) throws IOException {
        if (lobFolder.toString().isEmpty()) return;
        this.metaData.setLobFolder(lobFolder);
        withSchemas(this.metaData).forEach(metaSchema -> withTables(metaSchema)
                .forEach(metaTable -> withColumns(metaTable).forEach(metaColumn -> {
                    String root = lobFolder.toString().toLowerCase();
                    String schemaName = metaColumn.getParentMetaTable().getParentMetaSchema().getName().toLowerCase();
                    String tableName = metaColumn.getParentMetaTable().getName().toLowerCase();
                    String columnName = metaColumn.getName().toLowerCase();
                    try {
                        if (!new PreTypeFacade(metaColumn.getPreType()).isBlob()) return;
                        metaColumn.setLobFolder(new URI(root + "/" + schemaName + "/" + tableName + "/" + columnName + "/")); // do not use File.separator - does not work on windows!
                    } catch (IOException | URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                })));
    }

    private List<MetaColumn> withColumns(MetaTable metaTable) {
        return IntStream.range(0, metaTable.getMetaColumns())
                        .mapToObj(metaTable::getMetaColumn)
                        .collect(Collectors.toList());
    }

    private List<MetaTable> withTables(MetaSchema metaSchema) {
        return IntStream.range(0, metaSchema.getMetaTables())
                        .mapToObj(metaSchema::getMetaTable)
                        .collect(Collectors.toList());
    }

    private List<MetaSchema> withSchemas(MetaData metaData) {
        return IntStream.range(0, this.metaData.getMetaSchemas())
                        .mapToObj(this.metaData::getMetaSchema).collect(Collectors.toList());
    }
}
