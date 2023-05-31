package ch.admin.bar.siardsuite.model.facades;

import ch.admin.bar.siard2.api.MetaAttribute;
import ch.admin.bar.siard2.api.MetaType;
import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siardsuite.model.database.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MetaSchemaFacade {

    private final Schema schema;

    public MetaSchemaFacade(Schema schema) {
        this.schema = schema;
    }

    public String name() {
        return schema.getMetaSchema().getName();
    }

    public String description() {
        return schema.getMetaSchema().getDescription();
    }

    public List<DatabaseTable> tables(SiardArchive archive, DatabaseSchema databaseSchema, boolean onlyMetaData) {
        return IntStream.range(0, schema.getTables())
                        .mapToObj(schema::getTable)
                        .map(table -> new DatabaseTable(archive, databaseSchema, table, onlyMetaData))
                        .collect(Collectors.toList());

    }

    public List<DatabaseView> views(SiardArchive archive, DatabaseSchema databaseSchema) {
        return IntStream.range(0, schema.getMetaSchema().getMetaViews())
                        .mapToObj(schema.getMetaSchema()::getMetaView)
                        .map(view -> new DatabaseView(archive, databaseSchema, view))
                        .collect(Collectors.toList());
    }

    public List<Routine> routines(SiardArchive archive, DatabaseSchema databaseSchema) {
        return IntStream.range(0, schema.getMetaSchema().getMetaRoutines())
                        .mapToObj(schema.getMetaSchema()::getMetaRoutine)
                        .map(metaRoutine -> new Routine(metaRoutine))
                        .collect(Collectors.toList());
    }

    public List<DatabaseType> types() {
        return IntStream.range(0, schema.getMetaSchema().getMetaTypes())
                        .mapToObj(schema.getMetaSchema()::getMetaType)
                        .map(type -> new DatabaseType(type.getName(),
                                                      type.getCategory(),
                                                      type.isInstantiable(),
                                                      type.isFinal(),
                                                      type.getBase(),
                                                      type.getDescription(),
                                                      getMetaAttributes(type)))
                        .collect(Collectors.toList());
    }

    private List<MetaAttribute> getMetaAttributes(MetaType type) {
        return IntStream.range(0, type.getMetaAttributes())
                        .mapToObj(type::getMetaAttribute)
                        .collect(Collectors.toList());
    }


}
