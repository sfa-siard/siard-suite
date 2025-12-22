/*== MetaValueImpl.java ================================================
MetaValueImpl implements the common methods in the interface MetaValue.
Application : SIARD 2.0
Description : MetaValueImpl implements the methods of the interface 
              MetaValue that are common to MetaColumn and MetaField.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 01.10.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.meta;

import ch.admin.bar.siard2.api.MetaField;
import ch.admin.bar.siard2.api.MetaSearch;
import ch.admin.bar.siard2.api.MetaType;
import ch.admin.bar.siard2.api.MetaValue;
import ch.admin.bar.siard2.api.generated.CategoryType;
import ch.enterag.sqlparser.identifier.QualifiedId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * MetaValueImpl implements the interface MetaValue.
 *
 */
public abstract class MetaValueImpl
        extends MetaSearchImpl
        implements MetaValue {
    protected static final Pattern _patARRAY_INDEX = Pattern.compile("^.*?\\[\\s*(\\d+)\\s*\\]$");
    private int _iPosition = -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPosition() {
        return _iPosition;
    }

    public MetaValueImpl(int iPosition) {
        _iPosition = iPosition;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public List<List<String>> getNames(
            boolean bSupportsArrays, boolean bSupportsUdts)
            throws IOException {
        List<String> listNames = new ArrayList<String>();
        List<List<String>> llNames = new ArrayList<List<String>>();
        CategoryType cat = null;
        MetaType mt = getMetaType();
        if (mt != null)
            cat = mt.getCategoryType();
        if (((getCardinality() < 0) && (getMetaFields() == 0)) ||
                ((getCardinality() >= 0) && bSupportsArrays) ||
                ((cat == CategoryType.UDT) && bSupportsUdts)) {
            listNames.add(getName());
            llNames.add(listNames);
        } else {
            for (int iField = 0; iField < getMetaFields(); iField++) {
                MetaField mf = getMetaField(iField);
                List<List<String>> llField = mf.getNames(bSupportsArrays, bSupportsUdts);
                // prepend this column's name to each list
                for (int i = 0; i < llField.size(); i++) {
                    List<String> listField = llField.get(i);
                    listField.add(0, getName());
                    llNames.add(listField);
                }
            }
        }
        return llNames;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType(List<String> listNames)
            throws IOException {
        String sType = null;
        CategoryType cat = null;
        MetaType mt = getMetaType();
        if (mt != null)
            cat = mt.getCategoryType();
        if (listNames.size() == 1) {
            sType = getType();
            if (sType == null) {
                if (cat == CategoryType.DISTINCT)
                    sType = mt.getBase();
                else {
                    QualifiedId qiType = new QualifiedId(
                            null, mt.getParentMetaSchema()
                                    .getName(), mt.getName());
                    sType = qiType.format();
                }
            }
            if (getCardinality() >= 0)
                sType = sType + " ARRAY[" + getCardinality() + "]";
        } else {
            MetaField mf = getMetaField(listNames.get(1));
            sType = mf.getType(listNames.subList(1, listNames.size()));
        }
        return sType;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public long getMaxLength() throws IOException {
        long l = getLength();
        if (l == -1) {
            int iDataType = getPreType();
            if ((iDataType == java.sql.Types.CHAR) ||
                    (iDataType == java.sql.Types.NCHAR) ||
                    (iDataType == java.sql.Types.BINARY))
                l = 1;
            else
                l = Long.MAX_VALUE;
        }
        return l;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    protected MetaSearch[] getSubMetaSearches()
            throws IOException {
        MetaSearch[] ams = new MetaSearch[getMetaFields()];
        for (int iField = 0; iField < getMetaFields(); iField++)
            ams[iField] = getMetaField(iField);
        return ams;
    } 

} 
