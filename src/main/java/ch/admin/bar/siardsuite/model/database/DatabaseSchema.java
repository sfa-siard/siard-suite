package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siardsuite.component.rendered.utils.ListAssembler;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class DatabaseSchema {

    private final Schema schema;

    private final List<DatabaseTable> tables;
    private final List<DatabaseView> views;
    private final List<DatabaseType> types;
    private final List<Routine> routines;

    @Setter
    @Getter
    private String description;

    protected DatabaseSchema(Schema schema) {
        this.schema = schema;
        val metaSchema = schema.getMetaSchema();

        this.description = metaSchema.getDescription();

        this.tables = ListAssembler.assemble(schema.getTables(), schema::getTable)
                .stream()
                .map(DatabaseTable::new)
                .collect(Collectors.toList());

        this.views = ListAssembler.assemble(metaSchema.getMetaViews(), metaSchema::getMetaView)
                .stream()
                .map(DatabaseView::new)
                .collect(Collectors.toList());

        this.routines = ListAssembler.assemble(metaSchema.getMetaRoutines(), metaSchema::getMetaRoutine)
                .stream()
                .map(Routine::new)
                .collect(Collectors.toList());

        this.types = ListAssembler.assemble(metaSchema.getMetaTypes(), metaSchema::getMetaType)
                .stream()
                .map(DatabaseType::new)
                .collect(Collectors.toList());
    }

    public void write() {
        schema.getMetaSchema().setDescription(description);
    }

    public String getName() {
        return schema.getMetaSchema().getName();
    }
}
