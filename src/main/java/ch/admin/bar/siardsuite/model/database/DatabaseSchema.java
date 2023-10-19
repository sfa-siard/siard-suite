package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siardsuite.component.rendered.utils.ListAssembler;
import ch.admin.bar.siardsuite.model.facades.MetaSchemaFacade;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

public class DatabaseSchema {

    @Getter
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

        val metaSchema = schema.getMetaSchema();

        this.tables = ListAssembler.assemble(schema.getTables(), schema::getTable)
                .stream()
                .map(DatabaseTable::new)
                .collect(Collectors.toList());

        this.views = metaSchemaFacade.views(siardArchive, this);
        this.routines = metaSchemaFacade.routines(siardArchive, this);


        this.types = ListAssembler.assemble(metaSchema.getMetaTypes(), metaSchema::getMetaType)
                .stream()
                .map(DatabaseType::new)
                .collect(Collectors.toList());
    }

    public void write() {
        schema.getMetaSchema().setDescription(description);
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
