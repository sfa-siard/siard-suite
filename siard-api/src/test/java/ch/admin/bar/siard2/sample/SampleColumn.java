package ch.admin.bar.siard2.sample;

import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.MetaField;

import java.io.IOException;

public class SampleColumn {
    public static void printValue(String sLabel, String sValue) {
        SampleArchive.printValue("  " + sLabel, sValue);
    } 

    private int readField(MetaField mf, String sField) {
        int iReturn = SampleArchive.iRETURN_ERROR;
        try {
            int iResult = SampleArchive.iRETURN_OK;
            printValue(sField + " name", mf.getName());
            printValue(sField + " position", String.valueOf(mf.getPosition()));
            printValue(sField + " MIME type", mf.getMimeType());
            printValue(sField + " description", mf.getDescription());
            printValue(sField + " number of fields", String.valueOf(mf.getMetaFields()));
            for (int iField = 0; (iResult == SampleArchive.iRETURN_OK) && (iField < mf.getMetaFields()); iField++) {
                String sFieldChild = sField + ".Field[" + iField + "]";
                MetaField mfChild = mf.getMetaField(iField);
                iResult = readField(mfChild, sFieldChild);
            }
            iReturn = iResult;
        } catch (IOException ie) {
            System.err.println(SampleArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    protected int readColumn(MetaColumn mc, String sColumn) {
        int iReturn = SampleArchive.iRETURN_ERROR;
        try {
            int iResult = SampleArchive.iRETURN_OK;
            printValue(sColumn + " name", mc.getName());
            printValue(sColumn + " position", String.valueOf(mc.getPosition()));
            printValue(sColumn + " type", mc.getType());
            printValue(sColumn + " original type", mc.getTypeOriginal());
            printValue(sColumn + " MIME type", mc.getMimeType());
            printValue(sColumn + " type schema", mc.getTypeSchema());
            printValue(sColumn + " type name", mc.getTypeName());
            printValue(sColumn + " nullability", String.valueOf(mc.isNullable()));
            printValue(sColumn + " default value", String.valueOf(mc.getDefaultValue()));
            printValue(sColumn + " descripion", mc.getDescription());
            printValue(sColumn + " number of fields", String.valueOf(mc.getMetaFields()));
            for (int iField = 0; (iResult == SampleArchive.iRETURN_OK) && (iField < mc.getMetaFields()); iField++) {
                String sField = "Field[" + iField + "]";
                MetaField mf = mc.getMetaField(iField);
                iResult = readField(mf, sField);
            }
            iReturn = iResult;
        } catch (IOException ie) {
            System.err.println(SampleArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

} 
