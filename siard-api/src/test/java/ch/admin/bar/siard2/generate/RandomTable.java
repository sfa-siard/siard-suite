package ch.admin.bar.siard2.generate;

import ch.admin.bar.siard2.api.Table;
import ch.admin.bar.siard2.api.TableRecord;
import ch.admin.bar.siard2.api.TableRecordRetainer;

import java.io.IOException;

public class RandomTable {
    private Table _table = null;

    RandomTable(Table table) {
        _table = table;
    } 

    public int createTable() {
        int iReturn = RandomArchive.iRETURN_OK;
        try {
            TableRecordRetainer rr = _table.createTableRecords();
            for (long lRecord = 0; (iReturn == RandomArchive.iRETURN_OK) && (lRecord < _table.getMetaTable()
                                                                                             .getRows()); lRecord++) {
                TableRecord tableRecord = rr.create();
                RandomTableRecord rrec = new RandomTableRecord(tableRecord);
                iReturn = rrec.createTableRecord();
                rr.put(tableRecord);
                if ((lRecord % 1000) == 999) {
                    System.out.print('.');
                    if ((lRecord % 80000) == 79999)
                        System.out.println();
                }
            }
            rr.close();
            if ((_table.getMetaTable()
                       .getRows() & 80000) != 0)
                System.out.println();
        } catch (IOException ie) {
            System.err.println(RandomArchive.getExceptionMessage(ie));
        }
        return iReturn;
    }

}
