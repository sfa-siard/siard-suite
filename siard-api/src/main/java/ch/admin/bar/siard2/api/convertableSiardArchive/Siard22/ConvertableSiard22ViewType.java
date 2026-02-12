package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.ColumnType;
import ch.admin.bar.siard2.api.generated.ColumnsType;
import ch.admin.bar.siard2.api.generated.ViewType;

import java.math.BigInteger;
import java.util.List;

public class ConvertableSiard22ViewType extends ViewType {

    public ConvertableSiard22ViewType(String name, String description, BigInteger rows, String query,
                                      String queryOriginal, List<ColumnType> columns) {
        super();
        this.name = name;
        this.description = description;
        this.rows = rows;
        this.query = query;
        this.queryOriginal = queryOriginal;
        if (columns.size() > 0) {
            this.columns = new ColumnsType();
            this.columns.getColumn()
                        .addAll(columns);
        }
    }
}
