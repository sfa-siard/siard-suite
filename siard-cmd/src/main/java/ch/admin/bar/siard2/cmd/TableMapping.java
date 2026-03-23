package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.MetaTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableMapping extends Mapping {
    private String _sMappedTableName = null;

    public String getMappedTableName() {
        return _sMappedTableName;
    }

    public void setMappedTableName(String sMappedTableName) {
        _sMappedTableName = sMappedTableName;
    }

    private Map<String, String> _mapColumns = new HashMap<String, String>();

    public String getMappedColumnName(String sColumnName) {
        return _mapColumns.get(sColumnName);
    }

    private Map<String, String> _mapExtendedColumns = new HashMap<String, String>();

    String getMappedExtendedColumnName(String sExtendedColumnName) {
        return _mapExtendedColumns.get(sExtendedColumnName);
    }

    public void putMappedExtendedColumnName(String sExtendedColumnName, String sMappedColumnName) {
        _mapExtendedColumns.put(sExtendedColumnName, sMappedColumnName);
    }

    private TableMapping(boolean bSupportsArrays, boolean bSupportsUdts,
                         String sMappedTableName, MetaTable mt, int iMaxColumnNameLength)
            throws IOException {
        _sMappedTableName = sMappedTableName;
        List<String> listColumns = new ArrayList<String>();
        for (int iColumn = 0; iColumn < mt.getMetaColumns(); iColumn++) {
            MetaColumn mc = mt.getMetaColumn(iColumn);
            listColumns.add(mc.getName());
        }
        _mapColumns = getDisambiguated(listColumns, iMaxColumnNameLength);
        List<List<String>> llColumnNames = mt.getColumnNames(bSupportsArrays, bSupportsUdts);
        List<String> listExtendedColumnNames = new ArrayList<String>();
        for (int iColumn = 0; iColumn < llColumnNames.size(); iColumn++) {
            List<String> listColumn = llColumnNames.get(iColumn);
            StringBuilder sbColumn = new StringBuilder();
            for (int i = 0; i < listColumn.size(); i++) {
                if (i > 0)
                    sbColumn.append(".");
                sbColumn.append(listColumn.get(i));
            }
            listExtendedColumnNames.add(sbColumn.toString());
        }
        _mapExtendedColumns = getDisambiguated(listExtendedColumnNames, iMaxColumnNameLength);
    }

    public static TableMapping newInstance(boolean bSupportsArrays, boolean bSupportsUdts,
                                           String sMappedTableName, MetaTable mt, int iMaxColumnNameLength)
            throws IOException {
        return new TableMapping(bSupportsArrays, bSupportsUdts,
                                sMappedTableName, mt, iMaxColumnNameLength);
    }

}
