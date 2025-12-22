package ch.admin.bar.siard2.sample;

import ch.admin.bar.siard2.api.*;

import java.io.IOException;

public class SampleTable extends SampleColumn {
    public static final String sTABLE_SIMPLE = "TSIMPLE";
    public static final String sTABLE_COMPLEX = "TCOMPLEX";
    private Table _table = null;

    SampleTable(Table table) {
        _table = table;
    } 

    public int readTrigger(MetaTrigger mt, String sTrigger) {
        int iReturn = SampleArchive.iRETURN_OK;
        printValue(sTrigger + " name", mt.getName());
        printValue(sTrigger + " action time", mt.getActionTime());
        printValue(sTrigger + " trigger event", mt.getTriggerEvent());
        printValue(sTrigger + " alias list", mt.getAliasList());
        printValue(sTrigger + " triggered action", mt.getTriggeredAction());
        printValue(sTrigger + " description", mt.getDescription());
        return iReturn;
    } 

    public int readCheckConstraint(MetaCheckConstraint mcc, String sCheckConstraint) {
        int iReturn = SampleArchive.iRETURN_OK;
        printValue(sCheckConstraint + " name", mcc.getName());
        printValue(sCheckConstraint + " description", mcc.getDescription());
        printValue(sCheckConstraint + " condition", mcc.getCondition());
        return iReturn;
    } 

    public int readForeignKey(MetaForeignKey mfk, String sForeignKey) {
        int iReturn = SampleArchive.iRETURN_OK;
        printValue(sForeignKey + " name", mfk.getName());
        printValue(sForeignKey + " description", mfk.getDescription());
        printValue(sForeignKey + " match type", mfk.getMatchType());
        printValue(sForeignKey + " delete action", mfk.getDeleteAction());
        printValue(sForeignKey + " update action", mfk.getUpdateAction());
        printValue(sForeignKey + " referenced schema", mfk.getReferencedSchema());
        printValue(sForeignKey + " referenced table", mfk.getReferencedTable());
        for (int iReference = 0; iReference < mfk.getReferences(); iReference++) {
            printValue(sForeignKey + " column[" + iReference + "]", mfk.getColumn(iReference));
            printValue(sForeignKey + " referenced column[" + iReference + "]", mfk.getReferenced(iReference));
        }
        return iReturn;
    } 

    public int readUniqueKey(MetaUniqueKey muk, String sUniqueKey) {
        int iReturn = SampleArchive.iRETURN_OK;
        printValue(sUniqueKey + " name", muk.getName());
        printValue(sUniqueKey + " description", muk.getDescription());
        for (int iColumn = 0; iColumn < muk.getColumns(); iColumn++)
            printValue(sUniqueKey + " column[" + iColumn + "]", muk.getColumn(iColumn));
        return iReturn;
    } 

    public int readMetaTable(MetaTable mt) {
        int iReturn = SampleArchive.iRETURN_OK;
        printValue("Table name", mt.getName());
        printValue("Table folder", mt.getFolder());
        printValue("Table description", mt.getDescription());
        printValue("Table number of columns", String.valueOf(mt.getMetaColumns()));
        for (int iColumn = 0; (iReturn == SampleArchive.iRETURN_OK) && (iColumn < mt.getMetaColumns()); iColumn++) {
            String sColumn = "Column[" + iColumn;
            MetaColumn mc = mt.getMetaColumn(iColumn);
            iReturn = readColumn(mc, sColumn);
        }
        if (iReturn == SampleArchive.iRETURN_OK) {
            MetaUniqueKey muk = mt.getMetaPrimaryKey();
            if (muk != null)
                iReturn = readUniqueKey(muk, "PKey");
        }
        for (int iCandidateKey = 0; (iReturn == SampleArchive.iRETURN_OK) && (iCandidateKey < mt.getMetaCandidateKeys()); iCandidateKey++) {
            String sCandidateKey = "CKey[" + iCandidateKey + "]";
            readUniqueKey(mt.getMetaCandidateKey(iCandidateKey), sCandidateKey);
        }
        for (int iForeignKey = 0; (iReturn == SampleArchive.iRETURN_OK) && (iForeignKey < mt.getMetaForeignKeys()); iForeignKey++) {
            String sForeignKey = "FKey[" + iForeignKey + "]";
            readForeignKey(mt.getMetaForeignKey(iForeignKey), sForeignKey);
        }
        for (int iCheckConstraint = 0; (iReturn == SampleArchive.iRETURN_OK) && (iCheckConstraint < mt.getMetaCheckConstraints()); iCheckConstraint++) {
            String sCheckConstraint = "Constraint[" + iCheckConstraint + "]";
            readCheckConstraint(mt.getMetaCheckConstraint(iCheckConstraint), sCheckConstraint);
        }
        for (int iTrigger = 0; (iReturn == SampleArchive.iRETURN_OK) && (iTrigger < mt.getMetaTriggers()); iTrigger++) {
            String sTrigger = "Trigger[" + iTrigger + "]";
            readTrigger(mt.getMetaTrigger(iTrigger), sTrigger);
        }
        return iReturn;
    } 

    public int readTable() {
        int iReturn = readMetaTable(_table.getMetaTable());
        try {
            int iResult = SampleArchive.iRETURN_OK;
            TableRecordDispenser rd = _table.openTableRecords();
            for (int iRecord = 0; (iResult == SampleArchive.iRETURN_OK) && iRecord < _table.getMetaTable()
                                                                                           .getRows(); iRecord++) {
                TableRecord tableRecord = rd.get();
                SampleTableRecord sr = new SampleTableRecord(tableRecord, iRecord);
                sr.readTableRecord();
            }
            rd.close();
            iReturn = iResult;
        } catch (IOException ie) {
            System.err.println(SampleArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private static final String sDESCRIPTION_SIMPLE = "Sample table with all predefined SQL data types";
    private static final String sPK_SIMPLE = "PKSIMPLE";
    private static final String sCK_SIMPLE = "CKSIMPLE";
    public static final String sCOLUMN1_NAME = "CCHAR";
    private static final String sTYPE1_NAME = "CHAR";
    public static final String sCOLUMN2_NAME = "CVARCHAR";
    private static final String sTYPE2_NAME = "VARCHAR(256)";
    public static final String sCOLUMN3_NAME = "CCLOB";
    private static final String sTYPE3_NAME = "CLOB(4M)";
    public static final String sCOLUMN4_NAME = "CNCHAR";
    private static final String sTYPE4_NAME = "NCHAR";
    public static final String sCOLUMN5_NAME = "CNCHAR_VARYING";
    private static final String sTYPE5_NAME = "NCHAR VARYING(256)";
    public static final String sCOLUMN6_NAME = "CNCLOB";
    private static final String sTYPE6_NAME = "NCLOB(4G)";
    public static final String sCOLUMN7_NAME = "CXML";
    private static final String sTYPE7_NAME = "XML";
    public static final String sCOLUMN8_NAME = "CBINARY";
    private static final String sTYPE8_NAME = "BINARY";
    public static final String sCOLUMN9_NAME = "CVARBINARY";
    private static final String sTYPE9_NAME = "VARBINARY(256)";
    public static final String sCOLUMN10_NAME = "CBLOB";
    private static final String sTYPE10_NAME = "BLOB";
    public static final String sCOLUMN11_NAME = "CNUMERIC";
    private static final String sTYPE11_NAME = "NUMERIC(10,3)";
    public static final String sCOLUMN12_NAME = "CDECIMAL";
    private static final String sTYPE12_NAME = "DECIMAL";
    public static final String sCOLUMN13_NAME = "CSMALLINT";
    private static final String sTYPE13_NAME = "SMALLINT";
    public static final String sCOLUMN14_NAME = "CINTEGER";
    private static final String sTYPE14_NAME = "INTEGER";
    public static final String sCOLUMN15_NAME = "CBIGINT";
    private static final String sTYPE15_NAME = "BIGINT";
    public static final String sCOLUMN16_NAME = "CFLOAT";
    private static final String sTYPE16_NAME = "FLOAT(7)";
    public static final String sCOLUMN17_NAME = "CREAL";
    private static final String sTYPE17_NAME = "REAL";
    public static final String sCOLUMN18_NAME = "CDOUBLE";
    private static final String sTYPE18_NAME = "DOUBLE PRECISION";
    public static final String sCOLUMN19_NAME = "CBOOLEAN";
    private static final String sTYPE19_NAME = "BOOLEAN";
    public static final String sCOLUMN20_NAME = "CDATE";
    private static final String sTYPE20_NAME = "DATE";
    public static final String sCOLUMN21_NAME = "CTIME";
    private static final String sTYPE21_NAME = "TIME(3)";
    public static final String sCOLUMN22_NAME = "CTIMESTAMP";
    private static final String sTYPE22_NAME = "TIMESTAMP(9)";
    public static final String sCOLUMN23_NAME = "CINTERVALYEAR";
    private static final String sTYPE23_NAME = "INTERVAL YEAR(2) TO MONTH";
    public static final String sCOLUMN24_NAME = "CINTERVALDAY";
    private static final String sTYPE24_NAME = "INTERVAL DAY TO MINUTE";
    public static final String sCOLUMN25_NAME = "CINTERVALSECOND";
    private static final String sTYPE25_NAME = "INTERVAL SECOND(2,5)";
    public static final String sCOLUMN26_NAME = "COLUMN_DATALINK";
    private static final String sTYPE26_NAME = "DATALINK";

    private int createSimpleMetaTable(MetaTable mt) {
        int iReturn = SampleArchive.iRETURN_ERROR;
        try {
            mt.setDescription(sDESCRIPTION_SIMPLE);
            MetaColumn mc1 = mt.createMetaColumn(sCOLUMN1_NAME);
            mc1.setType(sTYPE1_NAME);
            mc1.setNullable(false);

            MetaColumn mc2 = mt.createMetaColumn(sCOLUMN2_NAME);
            mc2.setType(sTYPE2_NAME);

            MetaColumn mc3 = mt.createMetaColumn(sCOLUMN3_NAME);
            mc3.setType(sTYPE3_NAME);

            MetaColumn mc4 = mt.createMetaColumn(sCOLUMN4_NAME);
            mc4.setType(sTYPE4_NAME);

            MetaColumn mc5 = mt.createMetaColumn(sCOLUMN5_NAME);
            mc5.setType(sTYPE5_NAME);

            MetaColumn mc6 = mt.createMetaColumn(sCOLUMN6_NAME);
            mc6.setType(sTYPE6_NAME);

            MetaColumn mc7 = mt.createMetaColumn(sCOLUMN7_NAME);
            mc7.setType(sTYPE7_NAME);

            MetaColumn mc8 = mt.createMetaColumn(sCOLUMN8_NAME);
            mc8.setType(sTYPE8_NAME);

            MetaColumn mc9 = mt.createMetaColumn(sCOLUMN9_NAME);
            mc9.setType(sTYPE9_NAME);

            MetaColumn mc10 = mt.createMetaColumn(sCOLUMN10_NAME);
            mc10.setType(sTYPE10_NAME);

            MetaColumn mc11 = mt.createMetaColumn(sCOLUMN11_NAME);
            mc11.setType(sTYPE11_NAME);

            MetaColumn mc12 = mt.createMetaColumn(sCOLUMN12_NAME);
            mc12.setType(sTYPE12_NAME);

            MetaColumn mc13 = mt.createMetaColumn(sCOLUMN13_NAME);
            mc13.setType(sTYPE13_NAME);

            MetaColumn mc14 = mt.createMetaColumn(sCOLUMN14_NAME);
            mc14.setType(sTYPE14_NAME);
            mc14.setNullable(false);

            MetaColumn mc15 = mt.createMetaColumn(sCOLUMN15_NAME);
            mc15.setType(sTYPE15_NAME);

            MetaColumn mc16 = mt.createMetaColumn(sCOLUMN16_NAME);
            mc16.setType(sTYPE16_NAME);

            MetaColumn mc17 = mt.createMetaColumn(sCOLUMN17_NAME);
            mc17.setType(sTYPE17_NAME);

            MetaColumn mc18 = mt.createMetaColumn(sCOLUMN18_NAME);
            mc18.setType(sTYPE18_NAME);

            MetaColumn mc19 = mt.createMetaColumn(sCOLUMN19_NAME);
            mc19.setType(sTYPE19_NAME);

            MetaColumn mc20 = mt.createMetaColumn(sCOLUMN20_NAME);
            mc20.setType(sTYPE20_NAME);

            MetaColumn mc21 = mt.createMetaColumn(sCOLUMN21_NAME);
            mc21.setType(sTYPE21_NAME);

            MetaColumn mc22 = mt.createMetaColumn(sCOLUMN22_NAME);
            mc22.setType(sTYPE22_NAME);

            MetaColumn mc23 = mt.createMetaColumn(sCOLUMN23_NAME);
            mc23.setType(sTYPE23_NAME);

            MetaColumn mc24 = mt.createMetaColumn(sCOLUMN24_NAME);
            mc24.setType(sTYPE24_NAME);

            MetaColumn mc25 = mt.createMetaColumn(sCOLUMN25_NAME);
            mc25.setType(sTYPE25_NAME);

            MetaColumn mc26 = mt.createMetaColumn(sCOLUMN26_NAME);
            mc26.setType(sTYPE26_NAME);

            MetaUniqueKey muk = mt.createMetaCandidateKey(sCK_SIMPLE);
            muk.addColumn(sCOLUMN1_NAME);
            muk = mt.createMetaPrimaryKey(sPK_SIMPLE);
            muk.addColumn(sCOLUMN14_NAME);

            iReturn = SampleArchive.iRETURN_OK;
        } catch (IOException ie) {
            System.err.println(SampleArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int createSimpleTable() {
        int iReturn = createSimpleMetaTable(_table.getMetaTable());
        try {
            int iResult = SampleArchive.iRETURN_OK;
            TableRecordRetainer rr = _table.createTableRecords();
            for (int iRecord = 0; (iResult == SampleArchive.iRETURN_OK) && (iRecord < SampleTableRecord.iSIMPLE_RECORDS); iRecord++) {
                TableRecord tableRecord = rr.create();
                SampleTableRecord sr = new SampleTableRecord(tableRecord, iRecord);
                iResult = sr.createRecord();
                rr.put(tableRecord);
            }
            rr.close();
            iReturn = iResult;
        } catch (IOException ie) {
            System.err.println(SampleArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private static final String sDESCRIPTION_COMPLEX = "Sample table with DISTINCT, ROW, ARRAY and UDT types and recursion";
    private static final String sPK_COMPLEX = "PKCOMPLEX";
    private static final String sFK_COMPLEX = "FKCOMPLEX";
    public static final String sCOLUMN_ID = "CID";
    private static final String sTYPE_ID = "INTEGER";
    public static final String sCOLUMN_DISTINCT = "CDISTINCT";
    public static final String sCOLUMN_UDTS = "CUDTS";
    public static final String sCOLUMN_ARRAY = "CARRAY";
    private static final String sARRAY_BASE_TYPE = "VARCHAR(256)";
    private static final int iARRAY_CARDINALITY = 4;
    public static final String sCOLUMN_UDTC = "CUDTC";

    private int createComplexMetaTable(MetaTable mt) {
        int iReturn = SampleArchive.iRETURN_ERROR;
        try {
            mt.setDescription(sDESCRIPTION_COMPLEX);

            MetaColumn mc1 = mt.createMetaColumn(sCOLUMN_ID);
            mc1.setType(sTYPE_ID);
            mc1.setNullable(false);

            MetaColumn mc2 = mt.createMetaColumn(sCOLUMN_DISTINCT);
            mc2.setTypeName(SampleSchema.sTYPE_DISTINCT);

            MetaColumn mc3 = mt.createMetaColumn(sCOLUMN_UDTS);
            mc3.setTypeName(SampleSchema.sTYPE_UDT_SIMPLE);

            MetaColumn mc4 = mt.createMetaColumn(sCOLUMN_ARRAY);
            mc4.setType(sARRAY_BASE_TYPE);
            mc4.setCardinality(iARRAY_CARDINALITY);

            MetaColumn mc5 = mt.createMetaColumn(sCOLUMN_UDTC);
            mc5.setTypeName(SampleSchema.sTYPE_UDT_COMPLEX);

            MetaUniqueKey muk = mt.createMetaPrimaryKey(sPK_COMPLEX);
            muk.addColumn(sCOLUMN_ID);

            MetaForeignKey mfk = mt.createMetaForeignKey(sFK_COMPLEX);
            mfk.setReferencedTable(sTABLE_SIMPLE);
            mfk.addReference(sCOLUMN_ID, sCOLUMN14_NAME);

            iReturn = SampleArchive.iRETURN_OK;
        } catch (IOException ie) {
            System.err.println(SampleArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int createComplexTable() {
        int iReturn = createComplexMetaTable(_table.getMetaTable());
        try {
            int iResult = SampleArchive.iRETURN_OK;
            TableRecordRetainer rr = _table.createTableRecords();
            for (int iRecord = 0; (iResult == SampleArchive.iRETURN_OK) && (iRecord < SampleTableRecord.iCOMPLEX_RECORDS); iRecord++) {
                TableRecord tableRecord = rr.create();
                SampleTableRecord sr = new SampleTableRecord(tableRecord, iRecord);
                iResult = sr.createRecord();
                rr.put(tableRecord);
            }
            rr.close();
            iReturn = iResult;
        } catch (IOException ie) {
            System.err.println(SampleArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    public int createTable() {
        int iReturn = SampleArchive.iRETURN_ERROR;
        if (sTABLE_SIMPLE.equals(_table.getMetaTable()
                                       .getName()))
            iReturn = createSimpleTable();
        else if (sTABLE_COMPLEX.equals(_table.getMetaTable()
                                             .getName()))
            iReturn = createComplexTable();
        return iReturn;
    } 

} 
