/*== MetaCheckConstraintImpl.java ======================================
MetaCheckConstraintImpl implements the interface MetaCheckConstraint.
Application : SIARD 2.0
Description : MetaCheckConstraintImpl implements the interface MetaCheckConstraint.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 27.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.meta;

import ch.admin.bar.siard2.api.MetaCheckConstraint;
import ch.admin.bar.siard2.api.MetaTable;
import ch.admin.bar.siard2.api.generated.CheckConstraintType;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.DU;
import ch.enterag.utils.SU;
import ch.enterag.utils.xml.XU;

import java.io.IOException;


/**
 * MetaCheckConstraintImpl implements the interface MetaCheckConstraint.
 *
 */
public class MetaCheckConstraintImpl
        extends MetaSearchImpl
        implements MetaCheckConstraint {
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
        return getCondition() != null;
    }

    /**
     * get archive
     *
     * @return archive.
     */
    private ArchiveImpl getArchiveImpl() {
        return (ArchiveImpl) getParentMetaTable().getTable()
                                                 .getParentSchema()
                                                 .getParentArchive();
    } 

    private final CheckConstraintType _cct;

    /**
     * set template meta data.
     *
     * @param cctTemplate template data.
     */
    public void setTemplate(CheckConstraintType cctTemplate) {
        if (!SU.isNotEmpty(getDescription()))
            setDescription(XU.fromXml(cctTemplate.getDescription()));
    } 

    /**
     * constructor
     *
     * @param mtParent table meta data object of SIARD archive.
     * @param cct      CheckConstraintType instance (JAXB).
     */
    private MetaCheckConstraintImpl(MetaTable mtParent, CheckConstraintType cct) {
        _mtParent = mtParent;
        _cct = cct;
    } 

    /**
     * factory
     *
     * @param mtParent table meta data object of SIARD archive.
     * @param cct      CheckConstraintType instance (JAXB).
     * @return new MetaCheckConstraint instance.
     */
    public static MetaCheckConstraint newInstance(MetaTable mtParent, CheckConstraintType cct) {
        return new MetaCheckConstraintImpl(mtParent, cct);
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return XU.fromXml(_cct.getName());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCondition(String sCondition)
            throws IOException {
        if (getArchiveImpl().canModifyPrimaryData()) {
            if (getArchiveImpl().isMetaDataDifferent(getCondition(), sCondition))
                _cct.setCondition(XU.toXml(sCondition));
        } else
            throw new IOException("Condition cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCondition() {
        return XU.fromXml(_cct.getCondition());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String sDescription) {
        if (getArchiveImpl().isMetaDataDifferent(getDescription(), sDescription))
            _cct.setDescription(XU.toXml(sDescription));
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return XU.fromXml(_cct.getDescription());
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
                        getCondition(),
                        getDescription()
                };
    } 

    /**
     * {@inheritDoc}
     * toString() returns the name of the check constraint which is to be
     * displayed as the label of the check constraint node of the tree
     * displaying the archive.
     */
    @Override
    public String toString() {
        return getName();
    }
} 
