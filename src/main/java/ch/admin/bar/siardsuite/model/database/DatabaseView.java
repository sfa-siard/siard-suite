package ch.admin.bar.siardsuite.model.database;

import ch.admin.bar.siard2.api.MetaView;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class DatabaseView extends DatabaseObject {

    @Getter
    protected final MetaView metaView;
    protected final List<DatabaseColumn> columns = new ArrayList<>();
    private DatabaseSchema schema;

    public DatabaseView(SiardArchive archive, DatabaseSchema schema, MetaView metaView) {
        this.metaView = metaView;
        this.schema = schema;

        for (int i = 0; i < metaView.getMetaColumns(); i++) {
            columns.add(new DatabaseColumn(archive, schema, metaView.getMetaColumn(i)));
        }
    }

    @Override
    public String name() {
        return metaView.getName();
    }

    public String getNumberOfColumns() {
        return String.valueOf(columns.size());
    }

    public String getNumberOfRows() {
        return String.valueOf(metaView.getRows());
    }

    public List<DatabaseColumn> columns() {
        return this.columns;
    }
}
