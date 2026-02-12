package ch.admin.bar.siard2.sample;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.CategoryType;

import java.io.IOException;

public class SampleSchema extends SampleColumn {
    public static final String sSCHEMA_NAME = "SampleSchema";
    private Schema _schema = null;

    SampleSchema(Schema schema) {
        _schema = schema;
    } 

    private int readParameter(MetaParameter mp, String sParameter) {
        int iReturn = SampleArchive.iRETURN_OK;
        printValue(sParameter + " name", mp.getName());
        printValue(sParameter + " mode", mp.getMode());
        printValue(sParameter + " type", mp.getType());
        printValue(sParameter + " original type", mp.getTypeOriginal());
        printValue(sParameter + " type schema", mp.getTypeSchema());
        printValue(sParameter + " type name", mp.getTypeName());
        printValue(sParameter + " description", mp.getDescription());
        return iReturn;
    } 

    private int readRoutine(MetaRoutine mr, String sRoutine) {
        int iReturn = SampleArchive.iRETURN_OK;
        printValue(sRoutine + " name", mr.getName());
        printValue(sRoutine + " body", mr.getBody());
        printValue(sRoutine + " source", mr.getSource());
        printValue(sRoutine + " description", mr.getDescription());
        printValue(sRoutine + " characteristic", mr.getCharacteristic());
        printValue(sRoutine + " return type", mr.getReturnType());
        printValue(sRoutine + " number of parameters", String.valueOf(mr.getMetaParameters()));
        for (int iParameter = 0; (iReturn == SampleArchive.iRETURN_OK) && (iParameter < mr.getMetaParameters()); iParameter++) {
            String sParameter = "Parameter[" + iParameter + "]";
            MetaParameter mp = mr.getMetaParameter(iParameter);
            iReturn = readParameter(mp, sParameter);
        }
        return iReturn;
    } 

    private int readRoutines() {
        int iReturn = SampleArchive.iRETURN_OK;
        MetaSchema ms = _schema.getMetaSchema();
        printValue("Number of routines in schema", String.valueOf(ms.getMetaRoutines()));
        for (int iRoutine = 0; (iReturn == SampleArchive.iRETURN_OK) && (iRoutine < ms.getMetaViews()); iRoutine++) {
            String sRoutine = "Routine[" + iRoutine + "]";
            MetaRoutine mr = ms.getMetaRoutine(iRoutine);
            iReturn = readRoutine(mr, sRoutine);
        }
        return iReturn;
    } 

    private int readView(MetaView mv, String sView) {
        int iReturn = SampleArchive.iRETURN_OK;
        printValue(sView + " name", mv.getName());
        printValue(sView + " original query", mv.getQueryOriginal());
        printValue(sView + " query", mv.getQuery());
        printValue(sView + " description", mv.getDescription());
        printValue(sView + " rows", String.valueOf(mv.getRows()));
        printValue(sView + " number of columns", String.valueOf(mv.getMetaColumns()));
        for (int iColumn = 0; (iReturn == SampleArchive.iRETURN_OK) && (iColumn < mv.getMetaColumns()); iColumn++) {
            String sColumn = "Column[" + iColumn;
            MetaColumn mc = mv.getMetaColumn(iColumn);
            iReturn = readColumn(mc, sColumn);
        }
        return iReturn;
    } 

    private int readViews() {
        MetaSchema ms = _schema.getMetaSchema();
        printValue("Number of views in schema", String.valueOf(ms.getMetaViews()));
        int iReturn = SampleArchive.iRETURN_OK;
        for (int iView = 0; (iReturn == SampleArchive.iRETURN_OK) && (iView < ms.getMetaViews()); iView++) {
            String sView = "View[" + iView + "]";
            MetaView mv = ms.getMetaView(iView);
            iReturn = readView(mv, sView);
        }
        return iReturn;
    } 

    private int readAttribute(MetaAttribute ma, String sAttribute) {
        int iReturn = SampleArchive.iRETURN_OK;
        printValue(sAttribute + " name", ma.getName());
        printValue(sAttribute + " type", ma.getType());
        printValue(sAttribute + " original type", ma.getTypeOriginal());
        printValue(sAttribute + " type schema", ma.getTypeSchema());
        printValue(sAttribute + " type name", ma.getTypeName());
        printValue(sAttribute + " default value", String.valueOf(ma.getDefaultValue()));
        return iReturn;
    } 

    private int readType(MetaType mt, String sType) {
        int iReturn = SampleArchive.iRETURN_OK;
        printValue(sType + " name", mt.getName());
        CategoryType cat = mt.getCategoryType();
        printValue(sType + " category", cat.value());
        printValue(sType + " description", mt.getDescription());
        printValue(sType + " super schema", mt.getUnderSchema());
        printValue(sType + " super type", mt.getUnderType());
        printValue(sType + " instantiable", String.valueOf(mt.isInstantiable()));
        printValue(sType + " final", String.valueOf(mt.isFinal()));
        switch (cat) {
            case DISTINCT:
                printValue(sType + " base type", mt.getBase());
                break;
            case UDT:
                printValue(sType + " number of attributes", String.valueOf(mt.getMetaAttributes()));
                for (int iAttribute = 0; (iReturn == SampleArchive.iRETURN_OK) && (iAttribute < mt.getMetaAttributes()); iAttribute++) {
                    String sAttribute = "Attribute[" + iAttribute + "]";
                    MetaAttribute ma = mt.getMetaAttribute(iAttribute);
                    iReturn = readAttribute(ma, sAttribute);
                }
                break;
        }
        return iReturn;
    } 

    private int readTypes() {
        int iReturn = SampleArchive.iRETURN_OK;
        MetaSchema ms = _schema.getMetaSchema();
        printValue("Number of types", String.valueOf(ms.getMetaTypes()));
        for (int iType = 0; (iReturn == SampleArchive.iRETURN_OK) && (iType < ms.getMetaTypes()); iType++) {
            String sType = "Type[" + iType + "]";
            MetaType mt = ms.getMetaType(iType);
            iReturn = readType(mt, sType);
        }
        return iReturn;
    } 

    public int readSchema() {
        MetaSchema ms = _schema.getMetaSchema();
        printValue("Name", ms.getName());
        printValue("Folder", ms.getFolder());
        int iReturn = readTypes();
        if (iReturn == SampleArchive.iRETURN_OK)
            iReturn = readViews();
        if (iReturn == SampleArchive.iRETURN_OK)
            iReturn = readRoutines();
        if (iReturn == SampleArchive.iRETURN_OK) {
            for (int iTable = 0; (iReturn == SampleArchive.iRETURN_OK) && (iTable < _schema.getTables()); iTable++) {
                SampleTable st = new SampleTable(_schema.getTable(iTable));
                iReturn = st.readTable();
            }
        }
        return iReturn;
    } 

    private static final String sROUTINE1_NAME = "digest";
    private static final String sPARAMETER_NAME = "i";
    private static final String sPARAMETER_TYPE = "INTEGER";
    private static final String sROUTINE2_NAME = "compare";
    private static final String sSOURCE =
            "int iCompare = 0;\r\n" +
                    "if (s1 < s2)\r\n" +
                    "  iCompare = -1;\r\n" +
                    "else if (s1 > s2)\r\n" +
                    "  iCompare = 1;\r\n" +
                    "return iCompare;";
    private static final String sRETURN_TYPE = "INTEGER";
    private static final String sPARAMETER1_NAME = "s1";
    private static final String sPARAMETER1_TYPE = "VARCHAR(256)";
    private static final String sPARAMETER2_NAME = "s2";
    private static final String sPARAMETER2_TYPE = "VARCHAR(256)";

    private int createRoutines() {
        int iReturn = SampleArchive.iRETURN_ERROR;
        try {
            MetaSchema ms = _schema.getMetaSchema();

            MetaRoutine mr = ms.createMetaRoutine(sROUTINE1_NAME);
            MetaParameter mp = mr.createMetaParameter(sPARAMETER_NAME);
            mp.setType(sPARAMETER_TYPE);

            mr = ms.createMetaRoutine(sROUTINE2_NAME);
            mr.setSource(sSOURCE);
            mr.setReturnType(sRETURN_TYPE);
            mp = mr.createMetaParameter(sPARAMETER1_NAME);
            mp.setType(sPARAMETER1_TYPE);
            mp = mr.createMetaParameter(sPARAMETER2_NAME);
            mp.setType(sPARAMETER2_TYPE);
            iReturn = SampleArchive.iRETURN_OK;
        } catch (IOException ie) {
            System.err.println(SampleArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private static final String sVIEW1_NAME = "VIEW1";
    private static final String sVIEW1_QUERY_ORIGINAL = "SELECT * FROM SOMETABLE WHERE SOMECONDITION";
    private static final String sVIEW_COLUMN1_NAME = "ID";
    private static final String sVIEW_COLUMN1_TYPE = "INTEGER";
    private static final String sVIEW_COLUMN2_NAME = "NAME";
    private static final String sVIEW_COLUMN2_TYPE = "VARCHAR(256)";
    private static final String sVIEW_COLUMN3_NAME = "CONTENT";
    private static final String sVIEW2_NAME = "VIEW2";
    private static final String sVIEW2_QUERY_ORIGINAL = "SELECT COL1, COL2 FROM SOMETABLE";
    private static final String sVIEW2_DESCRIPTION = "Projection of SOMETABLE";

    private int createViews() {
        int iReturn = SampleArchive.iRETURN_ERROR;
        try {
            MetaSchema ms = _schema.getMetaSchema();

            MetaView mv = ms.createMetaView(sVIEW1_NAME);
            mv.setQueryOriginal(sVIEW1_QUERY_ORIGINAL);

            MetaColumn mc = mv.createMetaColumn(sVIEW_COLUMN1_NAME);
            mc.setType(sVIEW_COLUMN1_TYPE);

            mc = mv.createMetaColumn(sVIEW_COLUMN2_NAME);
            mc.setType(sVIEW_COLUMN2_TYPE);

            mc = mv.createMetaColumn(sVIEW_COLUMN3_NAME);
            mc.setTypeName(sTYPE_UDT_SIMPLE);

            mv = ms.createMetaView(sVIEW2_NAME);
            mv.setQueryOriginal(sVIEW2_QUERY_ORIGINAL);
            mv.setDescription(sVIEW2_DESCRIPTION);

            mc = mv.createMetaColumn(sVIEW_COLUMN1_NAME);
            mc.setType(sVIEW_COLUMN1_TYPE);

            mc = mv.createMetaColumn(sVIEW_COLUMN2_NAME);
            mc.setType(sVIEW_COLUMN2_TYPE);
            iReturn = SampleArchive.iRETURN_OK;
        } catch (IOException ie) {
            System.err.println(SampleArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    public static final String sTYPE_DISTINCT = "TDISTINCT";
    public static final String sTYPE_UDT_SIMPLE = "TUDTS";
    public static final String sTYPE_UDT_COMPLEX = "TUDTC";

    private static final String sDISTINCT_BASE_TYPE = "INTEGER";
    private static final String sUDTS_ATTRIBUTE1_NAME = "TABLEID";
    private static final String sUDTS_ATTRIBUTE1_TYPE = "INTEGER";
    private static final String sUDTS_ATTRIBUTE2_NAME = "TRANSCRIPTION";
    private static final String sUDTS_ATTRIBUTE2_TYPE = "CLOB";
    private static final String sUDTS_ATTRIBUTE3_NAME = "SOUND";
    private static final String sUDTS_ATTRIBUTE3_TYPE = "BLOB";
    private static final String sUDTC_ATTRIBUTE1_NAME = "ID";
    private static final String sUDTC_ATTRIBUTE1_TYPE = "INTEGER";
    private static final String sUDTC_ATTRIBUTE2_NAME = "NESTEDROW";
    private static final String sUDTS_ATTRIBUTE4_NAME = "FILE";
    private static final String sUDTS_ATTRIBUTE4_TYPE = "DATALINK";

    private int createTypes() {
        int iReturn = SampleArchive.iRETURN_ERROR;
        try {
            MetaSchema ms = _schema.getMetaSchema();
            MetaType mtDistinct = ms.createMetaType(sTYPE_DISTINCT);
            mtDistinct.setCategory(CategoryType.DISTINCT.value());
            mtDistinct.setBase(sDISTINCT_BASE_TYPE);

            MetaType mtRow = ms.createMetaType(sTYPE_UDT_SIMPLE);
            mtRow.setCategory(CategoryType.UDT.value());
            MetaAttribute mr1 = mtRow.createMetaAttribute(sUDTS_ATTRIBUTE1_NAME);
            mr1.setType(sUDTS_ATTRIBUTE1_TYPE);
            MetaAttribute mr2 = mtRow.createMetaAttribute(sUDTS_ATTRIBUTE2_NAME);
            mr2.setType(sUDTS_ATTRIBUTE2_TYPE);
            MetaAttribute mr3 = mtRow.createMetaAttribute(sUDTS_ATTRIBUTE3_NAME);
            mr3.setType(sUDTS_ATTRIBUTE3_TYPE);
            MetaAttribute mr4 = mtRow.createMetaAttribute(sUDTS_ATTRIBUTE4_NAME);
            mr4.setType(sUDTS_ATTRIBUTE4_TYPE);

            MetaType mtUdt = ms.createMetaType(sTYPE_UDT_COMPLEX);
            mtUdt.setCategory(CategoryType.UDT.value());
            MetaAttribute mu1 = mtUdt.createMetaAttribute(sUDTC_ATTRIBUTE1_NAME);
            mu1.setType(sUDTC_ATTRIBUTE1_TYPE);
            MetaAttribute mu2 = mtUdt.createMetaAttribute(sUDTC_ATTRIBUTE2_NAME);
            mu2.setTypeName(sTYPE_UDT_SIMPLE);
            iReturn = SampleArchive.iRETURN_OK;
        } catch (IOException ie) {
            System.err.println(SampleArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    public int createSchema() {
        int iReturn = createTypes();
        if (iReturn == SampleArchive.iRETURN_OK)
            iReturn = createViews();
        if (iReturn == SampleArchive.iRETURN_OK)
            iReturn = createRoutines();
        if (iReturn == SampleArchive.iRETURN_OK) {
            try {
                SampleTable st = new SampleTable(_schema.createTable(SampleTable.sTABLE_SIMPLE));
                int iResult = st.createTable();
                if (iResult == SampleArchive.iRETURN_OK) {
                    st = new SampleTable(_schema.createTable(SampleTable.sTABLE_COMPLEX));
                    iResult = st.createTable();
                }
                iReturn = iResult;
            } catch (IOException ie) {
                System.err.println(SampleArchive.getExceptionMessage(ie));
            }
        }
        return iReturn;
    } 

} 
