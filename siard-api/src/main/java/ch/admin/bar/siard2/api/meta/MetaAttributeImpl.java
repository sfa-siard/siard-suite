/*== MetaAttributeImpl.java ============================================
MetaAttributeImpl implements the interface MetaAttribute.
Application : SIARD 2.0
Description : MetaAttributeImpl implements the interface MetaAttribute.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 27.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.meta;

import ch.admin.bar.siard2.api.MetaAttribute;
import ch.admin.bar.siard2.api.MetaType;
import ch.admin.bar.siard2.api.Schema;
import ch.admin.bar.siard2.api.generated.AttributeType;
import ch.admin.bar.siard2.api.generated.CategoryType;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.sqlparser.BaseSqlFactory;
import ch.enterag.sqlparser.SqlFactory;
import ch.enterag.sqlparser.datatype.PredefinedType;
import ch.enterag.sqlparser.datatype.enums.PreType;
import ch.enterag.sqlparser.ddl.enums.Multiplier;
import ch.enterag.utils.DU;
import ch.enterag.utils.SU;
import ch.enterag.utils.xml.XU;

import java.io.IOException;
import java.math.BigInteger;

/**
 * MetaAttributeImpl implements the interface MetaAttribute.
 *
 */
public class MetaAttributeImpl
        extends MetaSearchImpl
        implements MetaAttribute {
    private static final long lKILO = 1024;
    private static final long lMEGA = lKILO * lKILO;
    private static final long lGIGA = lKILO * lMEGA;

    private MetaType _mtParent = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaType getParentMetaType() {
        return _mtParent;
    }

    private int _iPosition = -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPosition() {
        return _iPosition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        return (getType() != null) || (getTypeName() != null);
    }

    /**
     * get archive
     *
     * @return archive.
     */
    private ArchiveImpl getArchiveImpl() {
        MetaAttribute ma = this;
        MetaType mt = ma.getParentMetaType();
        return (ArchiveImpl) mt.getParentMetaSchema()
                               .getSchema()
                               .getParentArchive();
    } 

    private AttributeType _at = null;

    public AttributeType getAttributeType()
            throws IOException {
        return _at;
    } 

    private AttributeType _atTemplate = null;

    /**
     * set template meta data.
     *
     * @param atTemplate template data.
     */
    public void setTemplate(AttributeType atTemplate)
            throws IOException {
        _atTemplate = atTemplate;
        if (!SU.isNotEmpty(getDescription()))
            setDescription(XU.fromXml(_atTemplate.getDescription()));
    } 

    /**
     * constructor
     *
     * @param mtParent  parent type meta data object of SIARD archive.
     * @param at        AttributeType instance (JAXB).
     * @param iPosition position (1-based) of attribute in parent type.
     * @throws IOException if an I/O error occurred.
     */
    private MetaAttributeImpl(MetaType mtParent, AttributeType at, int iPosition)
            throws IOException {
        _mtParent = mtParent;
        _at = at;
        _iPosition = iPosition;
    } 

    /**
     * factory
     *
     * @param mtParent  parent type meta data object of SIARD archive.
     * @param at        AttributeType instance (JAXB).
     * @param iPosition position (1-based) of attribute in parent type.
     * @return new MetaAttribute instance.
     * @throws IOException if an I/O error occurred.
     */
    public static MetaAttribute newInstance(MetaType mtParent, AttributeType at, int iPosition)
            throws IOException {
        return new MetaAttributeImpl(mtParent, at, iPosition);
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return XU.fromXml(_at.getName());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setType(String sType)
            throws IOException {
        ArchiveImpl ai = getArchiveImpl();
        if (ai.canModifyPrimaryData()) {
            if (ai.isMetaDataDifferent(getTypeSchema(), null))
                _at.setTypeSchema(null);
            if (ai.isMetaDataDifferent(getTypeName(), null))
                _at.setTypeName(null);
            SqlFactory sf = new BaseSqlFactory();
            PredefinedType pt = new PredefinedType(sf);
            pt.parse(sType);
            sType = pt.format();
            if (ai.isMetaDataDifferent(getType(), sType))
                _at.setType(XU.toXml(sType));
        } else
            throw new IOException("Type cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPreType(int iDataType, long lPrecision, int iScale)
            throws IOException {
        SqlFactory sf = new BaseSqlFactory();
        PredefinedType prt = sf.newPredefinedType();
        prt.initialize(iDataType, lPrecision, iScale);
        String sType = prt.format();
        setType(sType);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return XU.fromXml(_at.getType());
    }

    /**
     * {@inheritDoc}
     */
    public int getPreType() {
        int iDataType = java.sql.Types.NULL;
        MetaType mt = getMetaType();
        CategoryType cat = null;
        if (mt != null)
            cat = mt.getCategoryType();
        String sType = getType();
        if (sType != null) {
            SqlFactory sf = new BaseSqlFactory();
            PredefinedType prt = sf.newPredefinedType();
            prt.parse(sType);
            PreType pt = prt.getType();
            iDataType = pt.getSqlType();
        } else if (cat == CategoryType.DISTINCT)
            iDataType = mt.getBasePreType();
        return iDataType;
    } 

    /**
     * {@inheritDoc}
     */
    public long getLength() {
        long lLength = -1;
        MetaType mt = getMetaType();
        CategoryType cat = null;
        if (mt != null)
            cat = mt.getCategoryType();
        String sType = getType();
        if (sType != null) {
            SqlFactory sf = new BaseSqlFactory();
            PredefinedType prt = sf.newPredefinedType();
            prt.parse(sType);
            lLength = prt.getLength();
            if (lLength != PredefinedType.iUNDEFINED) {
                Multiplier mult = prt.getMultiplier();
                if (mult != null) {
                    switch (prt.getMultiplier()) {
                        case K:
                            lLength = lLength * lKILO;
                            break;
                        case M:
                            lLength = lLength * lMEGA;
                            break;
                        case G:
                            lLength = lLength * lGIGA;
                            break;
                    }
                }
            } else
                lLength = prt.getPrecision();
        } else if (cat == CategoryType.DISTINCT)
            lLength = mt.getBaseLength();
        return lLength;
    } 

    /**
     * {@inheritDoc}
     */
    public int getScale() {
        int iScale = -1;
        MetaType mt = getMetaType();
        CategoryType cat = null;
        if (mt != null)
            cat = mt.getCategoryType();
        String sType = getType();
        if (sType != null) {
            SqlFactory sf = new BaseSqlFactory();
            PredefinedType prt = sf.newPredefinedType();
            prt.parse(sType);
            iScale = prt.getScale();
        } else if (cat == CategoryType.DISTINCT)
            iScale = mt.getBaseScale();
        return iScale;
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTypeOriginal(String sTypeOriginal)
            throws IOException {
        ArchiveImpl ai = getArchiveImpl();
        if (ai.canModifyPrimaryData()) {
            if (ai.isMetaDataDifferent(getTypeOriginal(), sTypeOriginal))
                _at.setTypeOriginal(XU.toXml(sTypeOriginal));
        } else
            throw new IOException("Original type cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeOriginal() {
        return XU.fromXml(_at.getTypeOriginal());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTypeSchema(String sTypeSchema)
            throws IOException {
        ArchiveImpl ai = getArchiveImpl();
        if (ai.canModifyPrimaryData()) {
            if (ai.isMetaDataDifferent(getType(), null))
                _at.setType(null);
            if (ai.isMetaDataDifferent(getTypeSchema(), sTypeSchema))
                _at.setTypeSchema(XU.toXml(sTypeSchema));
        } else
            throw new IOException("Type schema cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeSchema() {
        return XU.fromXml(_at.getTypeSchema());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTypeName(String sTypeName)
            throws IOException {
        ArchiveImpl ai = getArchiveImpl();
        if (ai.canModifyPrimaryData()) {
            if (ai.isMetaDataDifferent(getType(), null))
                _at.setType(null);
            if (ai.isMetaDataDifferent(getTypeName(), sTypeName)) {
                _at.setTypeName(XU.toXml(sTypeName));
                if (getTypeSchema() == null)
                    setTypeSchema(getParentMetaType().getParentMetaSchema()
                                                     .getName());
            }
        } else
            throw new IOException("Type name cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeName() {
        return XU.fromXml(_at.getTypeName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaType getMetaType() {
        MetaType mt = null;
        if (getTypeName() != null) {
            Schema schema = getArchiveImpl().getSchema(getTypeSchema());
            if (schema == null)
                System.err.println("Schema null found in MetaAttribute!");
            mt = schema.getMetaSchema()
                       .getMetaType(getTypeName());
        }
        return mt;
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNullable(boolean bNullable)
            throws IOException {
        ArchiveImpl ai = getArchiveImpl();
        if (ai.canModifyPrimaryData()) {
            if (ai.isMetaDataDifferent(Boolean.valueOf(isNullable()), Boolean.valueOf(bNullable)))
                _at.setNullable(bNullable);
        } else
            throw new IOException("Nullability cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNullable() {
        boolean bNullable = true; // default
        if (_at.isNullable() != null)
            bNullable = _at.isNullable()
                           .booleanValue();
        return bNullable;
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefaultValue(String sDefaultValue)
            throws IOException {
        ArchiveImpl ai = getArchiveImpl();
        if (ai.canModifyPrimaryData() && (getMetaType() == null)) {
            if (ai.isMetaDataDifferent(getDefaultValue(), sDefaultValue))
                _at.setDefaultValue(XU.toXml(sDefaultValue));
        } else
            throw new IOException("Default value cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDefaultValue() {
        return XU.fromXml(_at.getDefaultValue());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCardinality(int iCardinality)
            throws IOException {
        ArchiveImpl ai = getArchiveImpl();
        if (ai.canModifyPrimaryData()) {
            if (ai.isMetaDataDifferent(getCardinality(), iCardinality))
                _at.setCardinality(BigInteger.valueOf(iCardinality));
        } else
            throw new IOException("Cardinality cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCardinality() {
        int iCardinality = -1;
        BigInteger bi = _at.getCardinality();
        if (bi != null)
            iCardinality = bi.intValue();
        return iCardinality;
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String sDescription) {
        ArchiveImpl ai = getArchiveImpl();
        if (ai.isMetaDataDifferent(getDescription(), sDescription))
            _at.setDescription(XU.toXml(sDescription));
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return XU.fromXml(_at.getDescription());
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
                        String.valueOf(getPosition()),
                        getType(),
                        getTypeSchema(),
                        getTypeName(),
                        getTypeOriginal(),
                        String.valueOf(isNullable()),
                        getDefaultValue(),
                        (getCardinality() <= 0) ? "" : String.valueOf(getCardinality()),
                        getDescription()
                };
    } 

    /**
     * {@inheritDoc}
     * toString() returns the name of the attribute which is to be displayed
     * as the label of the attribute node of the tree displaying the archive.
     */
    @Override
    public String toString() {
        return getName();
    }
} 
