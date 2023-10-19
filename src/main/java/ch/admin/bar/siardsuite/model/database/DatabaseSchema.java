package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siardsuite.model.facades.MetaSchemaFacade;
import javafx.scene.control.CheckBoxTreeItem;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseSchema {

    private final Schema schema;

    private final String name;

    @Setter
    @Getter
    private String description;

    @Getter
    private final List<DatabaseTable> tables;

    @Getter
    private final List<DatabaseView> views;

    @Getter
    private final List<DatabaseType> types;

    @Getter
    private final List<Routine> routines;

    protected DatabaseSchema(SiardArchive siardArchive, Schema schema) {
        this.schema = schema;

        MetaSchemaFacade metaSchemaFacade = new MetaSchemaFacade(schema);
        name = metaSchemaFacade.name();
        description = metaSchemaFacade.description();

        this.tables = metaSchemaFacade.tables(siardArchive, this);
        this.views = metaSchemaFacade.views(siardArchive, this);
        this.routines = metaSchemaFacade.routines(siardArchive, this);

        this.types = metaSchemaFacade.types();
    }

    public void write() {
        schema.getMetaSchema().setDescription(description);
    }

    public void export(List<String> tablesToExport, File directory) {
        this.tables.stream()
                .filter(databaseTable -> tablesToExport.contains(databaseTable.getName()))
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
                .map(table -> new CheckBoxTreeItem<>(table.getName()))
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
