package ch.admin.bar.siard2.api.facade;

import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.MetaTable;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

// provides a simplified API to MetaTable
public class MetaTableFacade {

    private final MetaTable metaTable;

    public MetaTableFacade(MetaTable metaTable) {
        this.metaTable = metaTable;
    }

    public List<MetaColumn> getMetaColums() {
        int numberOfColumns = metaTable.getMetaColumns();
        return IntStream.range(0, numberOfColumns).mapToObj(metaTable::getMetaColumn).toList();
    }
}
