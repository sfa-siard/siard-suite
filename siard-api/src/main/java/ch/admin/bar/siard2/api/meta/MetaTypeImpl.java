package ch.admin.bar.siard2.api.meta;

import ch.admin.bar.siard2.api.MetaAttribute;
import ch.admin.bar.siard2.api.MetaSchema;
import ch.admin.bar.siard2.api.MetaSearch;
import ch.admin.bar.siard2.api.MetaType;
import ch.admin.bar.siard2.api.generated.*;
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
import java.util.HashMap;
import java.util.Map;

public class MetaTypeImpl
        extends MetaSearchImpl
        implements MetaType {
    private static final long lKILO = 1024;
    private static final long lMEGA = lKILO * lKILO;
    private static final long lGIGA = lKILO * lMEGA;
    private static final ObjectFactory _of = new ObjectFactory();
    private final Map<String, MetaAttribute> _mapMetaAttributes = new HashMap<String, MetaAttribute>();

    private MetaSchema _msParent = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaSchema getParentMetaSchema() {
        return _msParent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        boolean bValid = true;
        CategoryType cat = getCategoryType();
        if (cat != CategoryType.DISTINCT) {
            if (bValid && (getMetaAttributes() < 1))
                bValid = false;
        }
        for (int iAttribute = 0; bValid && (iAttribute < getMetaAttributes()); iAttribute++) {
            if (!getMetaAttribute(iAttribute).isValid())
                bValid = false;
        }
        return bValid;
    } 

    private TypeType _tt = null;

    public TypeType getTypeType()
            throws IOException {
        for (int iAttribute = 0; iAttribute < getMetaAttributes(); iAttribute++) {
            MetaAttribute ma = getMetaAttribute(iAttribute);
            ((MetaAttributeImpl) ma).getAttributeType();
        }
        return _tt;
    } 

    /**
     * get archive
     *
     * @return archive.
     */
    private ArchiveImpl getArchiveImpl() {
        return (ArchiveImpl) getParentMetaSchema().getSchema()
                                                  .getParentArchive();
    } 

    private TypeType _ttTemplate = null;

    /**
     * set template meta data.
     *
     * @param ttTemplate template meta data.
     */
    public void setTemplate(TypeType ttTemplate)
            throws IOException {
        _ttTemplate = ttTemplate;
        if (!SU.isNotEmpty(getDescription()))
            setDescription(XU.fromXml(_ttTemplate.getDescription()));
        AttributesType ats = _ttTemplate.getAttributes();
        if (ats != null) {
            for (int iAttribute = 0; iAttribute < ats.getAttribute()
                                                     .size(); iAttribute++) {
                AttributeType atTemplate = ats.getAttribute()
                                              .get(iAttribute);
                String sName = XU.fromXml(atTemplate.getName());
                MetaAttribute ma = getMetaAttribute(sName);
                if (ma != null) {
                    MetaAttributeImpl mai = (MetaAttributeImpl) ma;
                    mai.setTemplate(atTemplate);
                }
            }
        }
    } 

    /**
     * constructor
     *
     * @param msParent schema meta data object of SIARD archive.
     * @param tt       TypeType instance (JAXB).
     * @throws IOException if an I/O error occurred.
     */
    private MetaTypeImpl(MetaSchema msParent, TypeType tt)
            throws IOException {
        _msParent = msParent;
        _tt = tt;
        /* open all attribute meta data */
        AttributesType ats = _tt.getAttributes();
        if (ats != null) {
            for (int iAttribute = 0; iAttribute < ats.getAttribute()
                                                     .size(); iAttribute++) {
                AttributeType at = ats.getAttribute()
                                      .get(iAttribute);
                MetaAttribute ma = MetaAttributeImpl.newInstance(this, at, iAttribute + 1);
                _mapMetaAttributes.put(XU.fromXml(at.getName()), ma);
            }
        }
    } 

    /**
     * factory
     *
     * @param msParent schema meta data object of SIARD archive.
     * @param tt       TypeType instance (JAXB).
     * @return new MetaType instance.
     * @throws IOException if an I/O error occurred.
     */
    public static MetaType newInstance(MetaSchema msParent, TypeType tt)
            throws IOException {
        return new MetaTypeImpl(msParent, tt);
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return XU.fromXml(_tt.getName());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCategory(String sCategory)
            throws IOException {
        if (getArchiveImpl().canModifyPrimaryData()) {
            _tt.setBase(null);
            _mapMetaAttributes.clear();
            try {
                CategoryType ct = CategoryType.fromValue(sCategory.toLowerCase()
                                                                  .trim());
                if (getArchiveImpl().isMetaDataDifferent(_tt.getCategory(), ct))
                    _tt.setCategory(ct);
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException("Category must be \"distinct\" or \"udt\"!");
            }
        } else
            throw new IOException("Category cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCategory() {
        return _tt.getCategory()
                  .value();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CategoryType getCategoryType() {
        return CategoryType.fromValue(_tt.getCategory()
                                         .value());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUnderSchema(String sUnderSchema)
            throws IOException {
        if (getArchiveImpl().canModifyPrimaryData() && getArchiveImpl().isEmpty()) {
            if (getArchiveImpl().isMetaDataDifferent(getUnderSchema(), sUnderSchema))
                _tt.setUnderSchema(XU.toXml(sUnderSchema));
        } else
            throw new IOException("Schema of supertype cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUnderSchema() {
        return XU.fromXml(_tt.getUnderSchema());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUnderType(String sUnderType)
            throws IOException {
        if (getArchiveImpl().canModifyPrimaryData() && getArchiveImpl().isEmpty()) {
            if (getArchiveImpl().isMetaDataDifferent(getUnderType(), sUnderType)) {
                _tt.setUnderType(XU.toXml(sUnderType));
                if (getUnderSchema() == null)
                    setUnderSchema(getParentMetaSchema().getName());
            }
        } else
            throw new IOException("Supertype cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUnderType() {
        return XU.fromXml(_tt.getUnderType());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInstantiable(boolean bInstantiable)
            throws IOException {
        if (getArchiveImpl().canModifyPrimaryData() && getArchiveImpl().isEmpty()) {
            if (getArchiveImpl().isMetaDataDifferent(Boolean.valueOf(isInstantiable()), Boolean.valueOf(bInstantiable)))
                _tt.setInstantiable(bInstantiable);
        } else
            throw new IOException("Instantiability cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInstantiable() {
        return _tt.isInstantiable();
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFinal(boolean bFinal)
            throws IOException {
        if (getArchiveImpl().canModifyPrimaryData() && getArchiveImpl().isEmpty()) {
            if (getArchiveImpl().isMetaDataDifferent(Boolean.valueOf(isFinal()), Boolean.valueOf(bFinal)))
                _tt.setFinal(bFinal);
        } else
            throw new IOException("Finality cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFinal() {
        return _tt.isFinal();
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBase(String sBase)
            throws IOException {
        if (getArchiveImpl().canModifyPrimaryData()) {
            if (sBase != null) {
                CategoryType cat = getCategoryType();
                if ((cat == CategoryType.DISTINCT)) {
                    SqlFactory sf = new BaseSqlFactory();
                    PredefinedType pt = new PredefinedType(sf);
                    pt.parse(sBase);
                    sBase = pt.format();
                } else
                    throw new IOException("Base type can only be set for \"distinct\" or \"array\" types!");
                if (getArchiveImpl().isMetaDataDifferent(getBase(), sBase))
                    _tt.setBase(XU.toXml(sBase));
            }
        } else
            throw new IOException("Base type cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    public void setBasePreType(int iBaseType, long lPrecision, int iScale)
            throws IOException {
        SqlFactory sf = new BaseSqlFactory();
        PredefinedType prt = sf.newPredefinedType();
        prt.initialize(iBaseType, lPrecision, iScale);
        String sBase = prt.format();
        setBase(sBase);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBase() {
        return XU.fromXml(_tt.getBase());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBasePreType() {
        int iBaseType = java.sql.Types.NULL;
        String sType = getBase();
        if (sType != null) {
            SqlFactory sf = new BaseSqlFactory();
            PredefinedType pt = sf.newPredefinedType();
            pt.parse(sType);
            PreType ptBase = pt.getType();
            iBaseType = ptBase.getSqlType();
        }
        return iBaseType;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public long getBaseLength() {
        long lLength = -1;
        String sType = getBase();
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
        }
        return lLength;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBaseScale() {
        int iScale = -1;
        String sType = getBase();
        if (sType != null) {
            SqlFactory sf = new BaseSqlFactory();
            PredefinedType prt = sf.newPredefinedType();
            prt.parse(sType);
            iScale = prt.getScale();
        }
        return iScale;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMetaAttributes() {
        return _mapMetaAttributes.size();
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaAttribute getMetaAttribute(int iAttribute) {
        MetaAttribute ma = null;
        AttributesType ats = _tt.getAttributes();
        if (ats != null) {
            AttributeType at = ats.getAttribute()
                                  .get(iAttribute);
            String sName = XU.fromXml(at.getName());
            ma = getMetaAttribute(sName);
        }
        return ma;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaAttribute getMetaAttribute(String sName) {
        return _mapMetaAttributes.get(sName);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaAttribute createMetaAttribute(String sName)
            throws IOException {
        MetaAttribute ma = null;
        CategoryType cat = getCategoryType();
        if (getArchiveImpl().canModifyPrimaryData() &&
                (cat != CategoryType.DISTINCT)) {
            if (getMetaAttribute(sName) == null) {
                AttributesType ats = _tt.getAttributes();
                if (ats == null) {
                    ats = _of.createAttributesType();
                    _tt.setAttributes(ats);
                }
                AttributeType at = _of.createAttributeType();
                at.setName(XU.toXml(sName));
                ats.getAttribute()
                   .add(at);
                ma = MetaAttributeImpl.newInstance(this, at, _mapMetaAttributes.size() + 1);
                _mapMetaAttributes.put(sName, ma);
                getArchiveImpl().isMetaDataDifferent(null, ma);
                if (_ttTemplate != null) {
                    AttributesType atsTemplate = _ttTemplate.getAttributes();
                    if (atsTemplate != null) {
                        AttributeType atTemplate = null;
                        for (int iAttribute = 0; (atTemplate == null) && (iAttribute < atsTemplate.getAttribute()
                                                                                                  .size()); iAttribute++) {
                            AttributeType atTry = atsTemplate.getAttribute()
                                                             .get(iAttribute);
                            if (sName.equals(XU.fromXml(atTry.getName())))
                                atTemplate = atTry;
                        }
                        if ((atTemplate != null) && (ma instanceof MetaAttributeImpl)) {
                            MetaAttributeImpl mai = (MetaAttributeImpl) ma;
                            mai.setTemplate(atTemplate);
                        }
                    }
                }
            } else
                throw new IOException("Only one attribute with the same name allowed!");
        } else
            throw new IOException("Attribute cannot be created!");
        return ma;
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String sDescription) {
        if (getArchiveImpl().isMetaDataDifferent(getDescription(), sDescription))
            _tt.setDescription(XU.toXml(sDescription));
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return XU.fromXml(_tt.getDescription());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MetaSearch[] getSubMetaSearches()
            throws IOException {
        MetaSearch[] ams = new MetaSearch[getMetaAttributes()];
        for (int iAttribute = 0; iAttribute < getMetaAttributes(); iAttribute++)
            ams[iAttribute] = getMetaAttribute(iAttribute);
        return ams;
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
                        getCategory(),
                        /**
                        getUnderSchema(),
                        getUnderType(),
                         **/
                        String.valueOf(isInstantiable()),
                        String.valueOf(isFinal()),
                        getBase(),
                        getDescription()
                };
    } 

    /**
     * {@inheritDoc}
     * toString() returns the name of the type which is to be displayed
     * as the label of the type node of the tree displaying the archive.
     */
    @Override
    public String toString() {
        return getName();
    }
} 
