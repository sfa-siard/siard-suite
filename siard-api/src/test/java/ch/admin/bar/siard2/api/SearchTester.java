package ch.admin.bar.siard2.api;

import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.admin.bar.siard2.api.primary.SearchImpl;
import ch.enterag.utils.DU;
import ch.enterag.utils.EU;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class SearchTester {
    private static final File _fileSAKILA = new File("src/test/resources/testfiles/sfdbsakila.siard");
    private static final File _fileAW = new File("src/test/resources/testfiles/sfdbaw.siard");

    @Test
    public void testSakila() {
        try {
            Archive archive = ArchiveImpl.newInstance();
            archive.open(_fileSAKILA);
            Schema schema = archive.getSchema(0);
            Table table = schema.getTable(0);
            TableRecordDispenser rd = table.openTableRecords();
            for (TableRecord tableRecord = rd.get(); tableRecord != null; tableRecord = rd.get()) {
                System.out.print((rd.getPosition() - 1) + " (" + tableRecord.getRecord() + ")");
                for (int iCell = 0; iCell < tableRecord.getCells(); iCell++) {
                    Cell cell = tableRecord.getCell(iCell);
                    System.out.print("\t" + cell.getObject());
                }
                System.out.println();
            }
            rd.close();

            MetaTable mt = table.getMetaTable();
            List<MetaColumn> listColumns = new ArrayList<MetaColumn>();
            for (int iColumn = 0; iColumn < mt.getMetaColumns(); iColumn++)
                listColumns.add(mt.getMetaColumn(iColumn));
            String sFindString = "William"; // "ZELLWEGER" //
            boolean bMatchCase = false; // true
            Search search = new SearchImpl();
            search.find(listColumns, sFindString, bMatchCase);
            DU du = DU.getInstance("de", "dd.MM.yyyy");
            for (Cell cell = search.findNext(du);
                 cell != null;
                 cell = search.findNext(du)) {
                System.out.println(search.getFindString() +
                                           " found in row " + search.getFoundRow() +
                                           " and column position " + search.getFoundPosition() +
                                           " with value " + search.getFoundString(du) +
                                           " at offset " + search.getFoundOffset());
            }
            System.out.println("Nothing found!");
            archive.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    } 

    @Test
    public void testAw() {
        try {
            Archive archive = ArchiveImpl.newInstance();
            archive.open(_fileAW);
            Schema schema = archive.getSchema(2);
            System.out.println("Schema: " + schema.getMetaSchema()
                                                  .getName());
            Table table = schema.getTable(1);
            MetaTable mt = table.getMetaTable();
            System.out.println("Table: " + mt.getName());
            TableRecordDispenser rd = table.openTableRecords();
            for (TableRecord tableRecord = rd.get(); tableRecord != null; tableRecord = rd.get()) {
                System.out.print((rd.getPosition() - 1) + " (" + tableRecord.getRecord() + ")");
                for (int iCell = 0; iCell < tableRecord.getCells(); iCell++) {
                    Cell cell = tableRecord.getCell(iCell);
                    System.out.print("\t" + cell.getObject());
                }
                System.out.println();
            }
            rd.close();

            List<MetaColumn> listColumns = new ArrayList<MetaColumn>();
            for (int iColumn = 0; iColumn < mt.getMetaColumns(); iColumn++)
                listColumns.add(mt.getMetaColumn(iColumn));
            String sFindString = "Michael";
            boolean bMatchCase = false; // true
            Search search = new SearchImpl();
            search.find(listColumns, sFindString, bMatchCase);
            DU du = DU.getInstance("de", "dd.MM.yyyy");
            for (Cell cell = search.findNext(du);
                 cell != null;
                 cell = search.findNext(du)) {
                System.out.println(search.getFindString() +
                                           " found in row " + search.getFoundRow() +
                                           " and column position " + search.getFoundPosition() +
                                           " with value " + search.getFoundString(du) +
                                           " at offset " + search.getFoundOffset());
            }
            System.out.println("Nothing found!");
            archive.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    } 

} 
