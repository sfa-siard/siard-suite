package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siardsuite.model.facades.MetaSchemaFacade;
import javafx.scene.control.CheckBoxTreeItem;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseSchema extends DatabaseObject {

    protected final SiardArchive siardArchive;
    protected final Schema schema;
    protected final boolean onlyMetaData;
    protected final String name;

    @Setter
    @Getter
    protected String description;

    @Getter
    protected List<DatabaseTable> tables;

    @Getter
    protected List<DatabaseView> views;

    @Getter
    protected List<DatabaseType> types;

    @Getter
    protected List<Routine> routines;

    protected DatabaseSchema(SiardArchive siardArchive, Schema schema, boolean onlyMetaData) {
        this.siardArchive = siardArchive;
        this.schema = schema;
        this.onlyMetaData = onlyMetaData;

        MetaSchemaFacade metaSchemaFacade = new MetaSchemaFacade(schema);
        name = metaSchemaFacade.name();
        description = metaSchemaFacade.description();

        this.tables = metaSchemaFacade.tables(siardArchive, this, onlyMetaData);
        this.views = metaSchemaFacade.views(siardArchive, this);
        this.routines = metaSchemaFacade.routines(siardArchive, this);

        this.types = metaSchemaFacade.types();
    }

    public void write() {
        val schema = siardArchive.getArchive()
                .getSchema(name);
        schema.getMetaSchema().setDescription(description);
    }

    public void export(List<String> tablesToExport, File directory) {
        this.tables.stream()
                .filter(databaseTable -> tablesToExport.contains(databaseTable.name))
                .forEach(databaseTable -> {
                    try {
                        databaseTable.export(directory);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                });
    }

    public void populate(CheckBoxTreeItem<String> schemaItem) {
        List<CheckBoxTreeItem<String>> checkBoxTreeItems = this.tables.stream()
                .map(table -> new CheckBoxTreeItem<>(table.name))
                .collect(Collectors.toList());
        schemaItem.getChildren().setAll(checkBoxTreeItems);
    }

    public String name() {
        return name;
    }

    public List<DatabaseType> types() {
        return this.types;
    }

    public List<Routine> routines() {
        return this.routines;
    }

    public List<DatabaseView> views() {
        return this.views;
    }

    public List<DatabaseTable> tables() {
        return this.tables;
    }
}
