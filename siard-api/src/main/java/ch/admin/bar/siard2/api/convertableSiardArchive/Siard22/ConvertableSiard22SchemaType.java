package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.*;

import java.util.Collection;

public class ConvertableSiard22SchemaType extends SchemaType {
    public ConvertableSiard22SchemaType(String name, String description, String folder,
                                        Collection<TypeType> types,
                                        Collection<RoutineType> routines,
                                        Collection<TableType> tables,
                                        Collection<ViewType> views) {
        super();
        this.name = name;
        this.description = description;
        this.folder = folder;
        if (types.size() > 0) {
            this.types = new TypesType();
            this.types.getType()
                      .addAll(types);
        }

        if (routines.size() > 0) {
            this.routines = new RoutinesType();
            this.routines.getRoutine()
                         .addAll(routines);
        }

        if (tables.size() > 0) {
            this.tables = new TablesType();
            this.tables.getTable()
                       .addAll(tables);
        }

        if (views.size() > 0) {
            this.views = new ViewsType();
            this.views.getView()
                      .addAll(views);
        }

    }
}
