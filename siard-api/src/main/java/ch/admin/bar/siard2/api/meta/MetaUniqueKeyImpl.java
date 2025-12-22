/*== MetaUniqueKeyImpl.java =========================================
MetaUniqueKeyImpl implements the interface MetaUniqueKey.
Application : SIARD 2.0
Description : MetaUniqueKeyImpl implements the interface MetaUniqueKey.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 27.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.meta;

import ch.admin.bar.siard2.api.MetaTable;
import ch.admin.bar.siard2.api.MetaUniqueKey;
import ch.admin.bar.siard2.api.generated.UniqueKeyType;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.DU;
import ch.enterag.utils.SU;
import ch.enterag.utils.xml.XU;

import java.io.IOException;


/**
 * MetaUniqueKeyImpl implements the interface MetaUniqueKey.
 *
 */
public class MetaUniqueKeyImpl
        extends MetaSearchImpl
        implements MetaUniqueKey {
    private final MetaTable _mtParent;

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaTable getParentMetaTable() {
        return _mtParent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        return getColumns() > 0;
    }

    /**
     * get archive
     *
     * @return archive.
     */
    private ArchiveImpl getArchive() {
        return (ArchiveImpl) getParentMetaTable().getTable()
                                                 .getParentSchema()
                                                 .getParentArchive();
    } 

    private final UniqueKeyType _ukt;

    /**
     * set template meta data.
     *
     * @param uktTemplate template data.
     */
    public void setTemplate(UniqueKeyType uktTemplate) {
        if (!SU.isNotEmpty(getDescription()))
            setDescription(XU.fromXml(uktTemplate.getDescription()));
    } 

    /**
     * constructor
     *
     * @param mtParent table meta data object of SIARD archive.
     * @param ukt      UniqueKeyType instance (JAXB).
     */
    private MetaUniqueKeyImpl(MetaTable mtParent, UniqueKeyType ukt) {
        _mtParent = mtParent;
        _ukt = ukt;
    } 

    /**
     * factory
     *
     * @param mtParent table meta data object of SIARD archive.
     * @param ukt      UniqueKeyType instance (JAXB).
     * @return new MetaUniqueKey instance.
     */
    public static MetaUniqueKey newInstance(MetaTable mtParent, UniqueKeyType ukt) {
        return new MetaUniqueKeyImpl(mtParent, ukt);
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return XU.fromXml(_ukt.getName());
    }

    /* list property column */

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumns() {
        return _ukt.getColumn()
                   .size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumn(int iColumn) {
        return XU.fromXml(_ukt.getColumn()
                              .get(iColumn));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addColumn(String sColumn)
            throws IOException {
        if (getArchive().canModifyPrimaryData()) {
            _ukt.getColumn()
                .add(XU.toXml(sColumn));
            getArchive().isMetaDataDifferent(null, sColumn);
        } else
            throw new IOException("Column cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnsString() {
        StringBuilder sbColumns = new StringBuilder();
        for (int iColumn = 0; iColumn < getColumns(); iColumn++) {
            if (iColumn > 0)
                sbColumns.append(", ");
            sbColumns.append(getColumn(iColumn));
        }
        return sbColumns.toString();
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String sDescription) {
        if (getArchive().isMetaDataDifferent(getDescription(), sDescription))
            _ukt.setDescription(XU.toXml(sDescription));
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return XU.fromXml(_ukt.getDescription());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getSearchElements(DU du)
            throws IOException {
        return new String[]{
                getName(),
                getColumnsString(),
                getDescription()
        };
    } 

    /**
     * {@inheritDoc}
     * toString() returns the name of the unique key which is to be displayed
     * as the label of the unique key (primary or candidate) node of the
     * tree displaying the archive.
     */
    @Override
    public String toString() {
        String s = null;
        if (this == getParentMetaTable().getMetaPrimaryKey())
            s = "primary key";
        else
            s = getName();
        return s;
    }
} 
