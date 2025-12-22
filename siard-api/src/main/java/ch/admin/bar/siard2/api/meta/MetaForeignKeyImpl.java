/*== MetaForeignKeyImpl.java ===========================================
MetaForeignKeyImpl implements the interface MetaForeignKey.
Application : SIARD 2.0
Description : MetaForeignKeyImpl implements the interface MetaForeignKey.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 27.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.meta;

import ch.admin.bar.siard2.api.MetaForeignKey;
import ch.admin.bar.siard2.api.MetaTable;
import ch.admin.bar.siard2.api.generated.*;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.DU;
import ch.enterag.utils.SU;
import ch.enterag.utils.xml.XU;

import java.io.IOException;


/**
 * MetaForeignKeyImpl implements the interface MetaForeignKey.
 *
 */
public class MetaForeignKeyImpl
        extends MetaSearchImpl
        implements MetaForeignKey {
    private static final ObjectFactory _of = new ObjectFactory();

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
        return getReferences() > 0;
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

    private final ForeignKeyType _fkt;

    /**
     * set template meta data.
     *
     * @param fktTemplate template data.
     */
    public void setTemplate(ForeignKeyType fktTemplate) {
        if (!SU.isNotEmpty(getDescription()))
            setDescription(XU.fromXml(fktTemplate.getDescription()));
    } 

    /**
     * constructor
     *
     * @param mtParent table meta data object of SIARD archive.
     * @param fkt      ForeignKeyType instance (JAXB).
     */
    private MetaForeignKeyImpl(MetaTable mtParent, ForeignKeyType fkt) {
        _mtParent = mtParent;
        _fkt = fkt;
    } 

    /**
     * factory
     *
     * @param mtParent table meta data object of SIARD archive.
     * @param fkt      ForeignKeyType instance (JAXB).
     * @return new MetaForeignKey instance.
     */
    public static MetaForeignKey newInstance(MetaTable mtParent, ForeignKeyType fkt) {
        return new MetaForeignKeyImpl(mtParent, fkt);
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return XU.fromXml(_fkt.getName());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReferencedSchema(String sReferencedSchema)
            throws IOException {
        if (getArchive().canModifyPrimaryData()) {
            if (getArchive().isMetaDataDifferent(getReferencedSchema(), sReferencedSchema))
                _fkt.setReferencedSchema(XU.toXml(sReferencedSchema));
        } else
            throw new IOException("Referenced schema cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReferencedSchema() {
        return XU.fromXml(_fkt.getReferencedSchema());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReferencedTable(String sReferencedTable)
            throws IOException {
        if (getArchive().canModifyPrimaryData()) {
            if (getArchive().isMetaDataDifferent(getReferencedTable(), sReferencedTable)) {
                _fkt.setReferencedTable(XU.toXml(sReferencedTable));
                if (getReferencedSchema() == null)
                    setReferencedSchema(getParentMetaTable().getParentMetaSchema()
                                                            .getName());
            }
        } else
            throw new IOException("Referenced table cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReferencedTable() {
        return XU.fromXml(_fkt.getReferencedTable());
    }

    /* list property reference */

    /**
     * {@inheritDoc}
     */
    @Override
    public int getReferences() {
        return _fkt.getReference()
                   .size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumn(int iColumn) {
        return XU.fromXml(_fkt.getReference()
                              .get(iColumn)
                              .getColumn());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReferenced(int iColumn) {
        return XU.fromXml(_fkt.getReference()
                              .get(iColumn)
                              .getReferenced());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addReference(String sColumn, String sReferenced)
            throws IOException {
        if (getArchive().canModifyPrimaryData()) {
            ReferenceType rt = _of.createReferenceType();
            rt.setColumn(XU.toXml(sColumn));
            rt.setReferenced(XU.toXml(sReferenced));
            _fkt.getReference()
                .add(rt);
            getArchive().isMetaDataDifferent(null, rt);
        } else
            throw new IOException("Reference cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnsString() {
        StringBuffer sbColumns = new StringBuffer();
        for (int iReference = 0; iReference < getReferences(); iReference++) {
            if (iReference > 0)
                sbColumns.append(", ");
            sbColumns.append(getColumn(iReference));
        }
        return sbColumns.toString();
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReferencesString() {
        StringBuffer sbReferenced = new StringBuffer();
        for (int iReference = 0; iReference < getReferences(); iReference++) {
            if (iReference > 0)
                sbReferenced.append(", ");
            sbReferenced.append(getReferenced(iReference));
        }
        return sbReferenced.toString();
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMatchType(String sMatchType)
            throws IOException {
        if (getArchive().canModifyPrimaryData()) {
            try {
                MatchTypeType mtt = MatchTypeType.fromValue(sMatchType.toUpperCase()
                                                                      .trim());
                if (getArchive().isMetaDataDifferent(_fkt.getMatchType(), mtt))
                    _fkt.setMatchType(mtt);
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException("Invalid match type! " +
                                                           "(Match type must be \"FULL\", \"PARTIAL\" or \"SIMPLE\"!");
            }
        } else
            throw new IOException("Match type cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMatchType() {
        String sMatchType = null;
        if (_fkt.getMatchType() != null)
            sMatchType = _fkt.getMatchType()
                             .value();
        return sMatchType;
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDeleteAction(String sDeleteAction)
            throws IOException {
        if (getArchive().canModifyPrimaryData()) {
            try {
                ReferentialActionType rat = ReferentialActionType.fromValue(sDeleteAction.toUpperCase()
                                                                                         .trim());
                if (getArchive().isMetaDataDifferent(_fkt.getDeleteAction(), rat))
                    _fkt.setDeleteAction(rat);
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException("Invalid referential action! " +
                                                           "(Referential action must be \"CASCADE\", \"SET NULL\", \"SET DEFAULT\", \"RESTRICT\" or \"NO ACTION\"!");
            }
        } else
            throw new IOException("Referential action cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeleteAction() {
        String sDeleteAction = null;
        if (_fkt.getDeleteAction() != null)
            sDeleteAction = _fkt.getDeleteAction()
                                .value();
        return sDeleteAction;
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUpdateAction(String sUpdateAction)
            throws IOException {
        if (getArchive().canModifyPrimaryData()) {
            try {
                ReferentialActionType rat = ReferentialActionType.fromValue(sUpdateAction.toUpperCase()
                                                                                         .trim());
                if (getArchive().isMetaDataDifferent(_fkt.getUpdateAction(), rat))
                    _fkt.setUpdateAction(rat);
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException("Invalid referential action! " +
                                                           "(Referential action must be \"CASCADE\", \"SET NULL\", \"SET DEFAULT\", \"RESTRICT\" or \"NO ACTION\"!");
            }
        } else
            throw new IOException("Referential action cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUpdateAction() {
        String sUpdateAction = null;
        if (_fkt.getUpdateAction() != null)
            sUpdateAction = _fkt.getUpdateAction()
                                .value();
        return sUpdateAction;
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String sDescription) {
        if (getArchive().isMetaDataDifferent(getDescription(), sDescription))
            _fkt.setDescription(XU.toXml(sDescription));
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return XU.fromXml(_fkt.getDescription());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getSearchElements(DU du)
            throws IOException {
        return new String[]
                {
                        getName(),
                        getColumnsString(),
                        getReferencedSchema(),
                        getReferencedTable(),
                        getReferencesString(),
                        getMatchType(),
                        getDeleteAction(),
                        getUpdateAction(),
                        getDescription()
                };
    } 

    /**
     * {@inheritDoc}
     * toString() returns the name of the foreign key which is to be
     * displayed as the label of the foreign key node of the tree
     * displaying the archive.
     */
    @Override
    public String toString() {
        return getName();
    }
} 
