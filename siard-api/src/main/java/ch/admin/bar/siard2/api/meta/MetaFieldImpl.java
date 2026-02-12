/*== MetaFieldImpl.java ================================================
MetaFieldImpl implements the interface MetaField.
Application : SIARD 2.0
Description : MetaFieldImpl implements the interface MetaField.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 28.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.meta;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.CategoryType;
import ch.admin.bar.siard2.api.generated.FieldType;
import ch.admin.bar.siard2.api.generated.FieldsType;
import ch.admin.bar.siard2.api.generated.ObjectFactory;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.DU;
import ch.enterag.utils.SU;
import ch.enterag.utils.xml.XU;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;


/**
 * MetaFieldImpl implements the interface MetaField.
 *
 */
public class MetaFieldImpl
        extends MetaValueImpl
        implements MetaField {
    public static final String _sFIELD_FOLDER_PREFIX = "field";
    private static final ObjectFactory _of = new ObjectFactory();
    private final Map<String, MetaField> _mapMetaFields = new HashMap<String, MetaField>();

    private Map<String, MetaField> getMetaFieldsMap()
            throws IOException {
        if (getArchiveImpl().canModifyPrimaryData()) {
            if (getCardinality() < 0) {
                MetaType mt = getMetaType();
                if (mt != null) {
                    CategoryType cat = mt.getCategoryType();
                    if (cat != CategoryType.DISTINCT) {
                        for (int i = _mapMetaFields.size(); i < mt.getMetaAttributes(); i++)
                            createMetaField();
                    }
                }
            }
        }
        return _mapMetaFields;
    } 

    private String _sFolder = null;


    /**
     * get relative internal lob folder for this field or null, if it
     * is the field of a view or an external LOB folder is set.
     *
     * @return relative internal lob folder.
     */
    public String getFolder() {
        String sFolder = null;
        if (getLobFolder() == null)
            sFolder = _sFolder;
        return sFolder;
    } 

    private MetaColumn _mcAncestor = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaColumn getAncestorMetaColumn() {
        return _mcAncestor;
    }

    private MetaColumn _mcParent = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaColumn getParentMetaColumn() {
        return _mcParent;
    }

    private MetaField _mfParent = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaField getParentMetaField() {
        return _mfParent;
    }

    /**
     * get table meta data.
     *
     * @return table meta data.
     */
    private MetaTable getMetaTable() {
        MetaField mf = this;
        for (; mf.getParentMetaField() != null; mf = getParentMetaField()) {
        }
        return mf.getParentMetaColumn()
                 .getParentMetaTable();
    } 

    /**
     * get view meta data.
     *
     * @return view meta data.
     */
    private MetaView getMetaView() {
        MetaField mf = this;
        for (; mf.getParentMetaField() != null; mf = getParentMetaField()) {
        }
        return mf.getParentMetaColumn()
                 .getParentMetaView();
    } 

    /**
     * get archive
     *
     * @return archive.
     */
    private ArchiveImpl getArchiveImpl() {
        Archive archive = null;
        if (getMetaTable() != null)
            archive = getMetaTable().getTable()
                                    .getParentSchema()
                                    .getParentArchive();
        else if (getMetaView() != null)
            archive = getMetaView().getParentMetaSchema()
                                   .getSchema()
                                   .getParentArchive();
        return (ArchiveImpl) archive;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaAttribute getMetaAttribute()
            throws IOException {
        MetaAttribute ma = null;
        MetaType mtParent = null;
        // if the field comes from a column, then the type comes from the column
        MetaColumn mcParent = getParentMetaColumn();
        MetaField mfParent = getParentMetaField();
        if (mcParent != null)
            mtParent = mcParent.getMetaType();
        else // else it comes from the parent attribute
            mtParent = mfParent.getMetaType();
        if (mtParent != null)
            ma = mtParent.getMetaAttribute(getPosition() - 1);
        return ma;
    } 

    FieldType _ft = null;

    FieldType getFieldType()
            throws IOException {
        for (int iField = 0; iField < getMetaFields(); iField++) {
            MetaField mf = getMetaField(iField);
            ((MetaFieldImpl) mf).getFieldType();
        }
        return _ft;
    } 

    private FieldType _ftTemplate = null;

    /**
     * set template meta data.
     *
     * @param ftTemplate template data.
     */
    public void setTemplate(FieldType ftTemplate)
            throws IOException {
        _ftTemplate = ftTemplate;
        if (!SU.isNotEmpty(getDescription()))
            setDescription(XU.fromXml(_ftTemplate.getDescription()));
        FieldsType fts = _ftTemplate.getFields();
        if (fts != null) {
            for (int iField = 0; iField < fts.getField()
                                             .size(); iField++) {
                FieldType ftSubTemplate = fts.getField()
                                             .get(iField);
                String sName = XU.fromXml(ftSubTemplate.getName());
                MetaField mf = getMetaField(sName);
                if (mf != null) {
                    MetaFieldImpl mfi = (MetaFieldImpl) mf;
                    mfi.setTemplate(ftSubTemplate);
                }
            }
        }
    } 

    /**
     * open all sub field meta data.
     *
     * @throws IOException if an I/O error occurred.
     */
    private void openMetaFields()
            throws IOException {
        /* open all sub field meta data */
        FieldsType fts = _ft.getFields();
        if (fts != null) {
            for (int iField = 0; iField < fts.getField()
                                             .size(); iField++) {
                FieldType ftSub = fts.getField()
                                     .get(iField);
                MetaField mfSub = MetaFieldImpl.newInstance(this, ftSub, _sFolder, iField + 1);
                _mapMetaFields.put(XU.fromXml(ftSub.getName()), mfSub);
            }
        }
    } 

    /**
     * constructor
     *
     * @param mcParent, MetaColumn mcAncestor parent column meta data object of SIARD archive.
     * @param ft        FieldType instance (JAXB).
     * @param sFolder   internal folder for LOB data
     * @param iPosition position (1-based) of field in column.
     * @throws IOException if the existing field meta data could not be
     *                     matched to attributes.
     */
    private MetaFieldImpl(MetaColumn mcParent, FieldType ft, String sFolder, int iPosition)
            throws IOException {
        super(iPosition);
        _mcParent = mcParent;
        _mcAncestor = mcParent;
        _ft = ft;
        if (sFolder != null)
            _sFolder = sFolder + _sFIELD_FOLDER_PREFIX + (iPosition - 1) + "/";
        openMetaFields();
    } 

    /**
     * constructor
     *
     * @param mfParent  parent field meta data object of SIARD archive.
     * @param ft        FieldType instance (JAXB).
     * @param sFolder   internal folder for LOB data
     * @param iPosition position (1-based) of field in parent field.
     * @throws IOException if the existing field meta data could not be
     *                     matched to attributes.
     */
    private MetaFieldImpl(MetaField mfParent, FieldType ft, String sFolder, int iPosition)
            throws IOException {
        super(iPosition);
        _mfParent = mfParent;
        _mcAncestor = mfParent.getAncestorMetaColumn();
        _ft = ft;
        if (sFolder != null)
            _sFolder = sFolder + _sFIELD_FOLDER_PREFIX + (iPosition - 1) + "/";
        openMetaFields();
    } 

    /**
     * factory
     *
     * @param mcParent parent column meta data object of SIARD archive.
     * @param ft       FieldType instance (JAXB).
     * @param iIndex   index of field in column.
     * @return new MetaField instance.
     * @throws IOException if the existing field meta data could not be
     *                     matched to attributes.
     */
    public static MetaField newInstance(MetaColumn mcParent, FieldType ft, String sFolder, int iIndex)
            throws IOException {
        return new MetaFieldImpl(mcParent, ft, sFolder, iIndex);
    } 

    /**
     * factory
     *
     * @param mfParent parent field meta data object of SIARD archive.
     * @param ft       FieldType instance (JAXB).
     * @param iIndex   index of field in parent field.
     * @return new MetaField instance.
     */
    public static MetaField newInstance(MetaField mfParent, FieldType ft, String sFolder, int iIndex)
            throws IOException {
        return new MetaFieldImpl(mfParent, ft, sFolder, iIndex);
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return XU.fromXml(_ft.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLobFolder(URI uriLobFolder)
            throws IOException {
        boolean bMayBeSet = false;
        if (getLobFolder() == null) {
            if ((getMetaTable() != null) && getMetaTable().getTable()
                                                          .isEmpty())
                bMayBeSet = true;
        } else
            bMayBeSet = true;
        if (bMayBeSet) {
            if (getArchiveImpl().isMetaDataDifferent(getLobFolder(), uriLobFolder)) {
                if (uriLobFolder != null) {
                    MetaDataImpl mdi = (MetaDataImpl) (getMetaTable().getTable()
                                                                     .getParentSchema()
                                                                     .getParentArchive()
                                                                     .getMetaData());
                    if (uriLobFolder.getPath()
                                    .endsWith("/")) {
                        if (uriLobFolder.isAbsolute()) {
                            if (uriLobFolder.getScheme() == null) {
                                try {
                                    uriLobFolder = new URI(MetaDataImpl._sURI_SCHEME_FILE, "", uriLobFolder.getPath(), null);
                                } catch (URISyntaxException use) {
                                }
                            }
                            if (!uriLobFolder.getScheme()
                                             .equals(MetaDataImpl._sURI_SCHEME_FILE))
                                throw new IllegalArgumentException("Only URIs with scheme \"" + MetaDataImpl._sURI_SCHEME_FILE + "\" allowed for LOB folder!");
                        } else if (mdi.getLobFolder() == null) {
                            if (!uriLobFolder.getPath()
                                             .startsWith("../"))
                                throw new IllegalArgumentException("Relative LOB folder URIs must start with \"..\"!");
                        }
                        _ft.setLobFolder(XU.toXml(uriLobFolder.toString()));
                    } else
                        throw new IllegalArgumentException("Path of LOB folder URI must denote a folder (end with \"/\")!");
                } else
                    throw new IllegalArgumentException("LOB folder URI must not be null!");
            }
        } else
            throw new IOException("LOB folder value cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getLobFolder() {
        URI uriLobFolder = null;
        if (_ft.getLobFolder() != null) {
            try {
                uriLobFolder = new URI(XU.fromXml(_ft.getLobFolder()));
            } catch (URISyntaxException use) {
            }
        }
        return uriLobFolder;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getAbsoluteLobFolder() {
        URI uriLocal = getLobFolder();
        MetaDataImpl mdi = (MetaDataImpl) (getArchiveImpl().getMetaData());
        if (uriLocal != null) {
            if (!uriLocal.isAbsolute()) {
                URI uriGlobal = mdi.getLobFolder();
                if (uriGlobal != null) {
                    uriGlobal = mdi.getAbsoluteUri(uriGlobal);
                    uriLocal = uriGlobal.resolve(uriLocal);
                } else
                    uriLocal = mdi.getAbsoluteUri(uriLocal);
            }
        }
        return uriLocal;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType()
            throws IOException {
        String sType = null;
        MetaAttribute ma = getMetaAttribute();
        if (ma != null)
            sType = ma.getType();
        else // array element
        {
            MetaColumn mcParent = getParentMetaColumn();
            if (mcParent != null)
                sType = mcParent.getType();
            else {
                MetaField mfParent = getParentMetaField();
                sType = mfParent.getType();
            }
        }
        return sType;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPreType()
            throws IOException {
        int iDataType = java.sql.Types.NULL;
        MetaAttribute ma = getMetaAttribute();
        if (ma != null)
            iDataType = ma.getPreType();
        else // array element
        {
            MetaColumn mcParent = getParentMetaColumn();
            if (mcParent != null)
                iDataType = mcParent.getPreType();
            else {
                MetaField mfParent = getParentMetaField();
                iDataType = mfParent.getPreType();
            }
        }
        return iDataType;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeOriginal()
            throws IOException {
        String sTypeOriginal = null;
        MetaAttribute ma = getMetaAttribute();
        if (ma != null)
            sTypeOriginal = ma.getTypeOriginal();
        else  // array element
        {
            MetaColumn mcParent = getParentMetaColumn();
            if (mcParent != null)
                sTypeOriginal = mcParent.getTypeOriginal();
            else {
                MetaField mfParent = getParentMetaField();
                sTypeOriginal = mfParent.getTypeOriginal();
            }
        }
        return sTypeOriginal;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeSchema()
            throws IOException {
        String sTypeSchema = null;
        MetaAttribute ma = getMetaAttribute();
        if (ma != null)
            sTypeSchema = ma.getTypeSchema();
        else  // array element
        {
            MetaColumn mcParent = getParentMetaColumn();
            if (mcParent != null)
                sTypeSchema = mcParent.getTypeSchema();
            else {
                MetaField mfParent = getParentMetaField();
                sTypeSchema = mfParent.getTypeSchema();
            }
        }
        return sTypeSchema;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeName()
            throws IOException {
        String sTypeName = null;
        MetaAttribute ma = getMetaAttribute();
        if (ma != null)
            sTypeName = ma.getTypeName();
        else  // array element
        {
            MetaColumn mcParent = getParentMetaColumn();
            if (mcParent != null)
                sTypeName = mcParent.getTypeName();
            else {
                MetaField mfParent = getParentMetaField();
                sTypeName = mfParent.getTypeName();
            }
        }
        return sTypeName;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLength()
            throws IOException {
        long lPrecision = -1;
        MetaAttribute ma = getMetaAttribute();
        if (ma != null)
            lPrecision = ma.getLength();
        else // array element
        {
            MetaColumn mcParent = getParentMetaColumn();
            if (mcParent != null)
                lPrecision = mcParent.getLength();
            else {
                MetaField mfParent = getParentMetaField();
                lPrecision = mfParent.getLength();
            }
        }
        return lPrecision;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public int getScale()
            throws IOException {
        int iScale = -1;
        MetaAttribute ma = getMetaAttribute();
        if (ma != null)
            iScale = ma.getScale();
        else // array element
        {
            MetaColumn mcParent = getParentMetaColumn();
            if (mcParent != null)
                iScale = mcParent.getScale();
            else {
                MetaField mfParent = getParentMetaField();
                iScale = mfParent.getScale();
            }
        }
        return iScale;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaType getMetaType()
            throws IOException {
        MetaType mt = null;
        MetaAttribute ma = getMetaAttribute();
        if (ma != null)
            mt = ma.getMetaType();
        else // array element
        {
            MetaColumn mcParent = getParentMetaColumn();
            MetaField mfParent = getParentMetaField();
            if (mcParent != null)
                mt = mcParent.getMetaType();
            else
                mt = mfParent.getMetaType();
        }
        return mt;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCardinality()
            throws IOException {
        int iCardinality = -1;
        MetaAttribute ma = getMetaAttribute();
        if (ma != null)
            iCardinality = ma.getCardinality();
        return iCardinality;
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMimeType(String sMimeType) {
        if (getArchiveImpl().isMetaDataDifferent(getMimeType(), sMimeType))
            _ft.setMimeType(XU.toXml(sMimeType));
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMimeType() {
        return XU.fromXml(_ft.getMimeType());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String sDescription) {
        if (getArchiveImpl().isMetaDataDifferent(getDescription(), sDescription))
            _ft.setDescription(XU.toXml(sDescription));
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return XU.fromXml(_ft.getDescription());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMetaFields()
            throws IOException {
        return getMetaFieldsMap().size();
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaField getMetaField(int iField)
            throws IOException {
        MetaField mf = null;
        /* dynamically expand the number of array elements */
        if (getCardinality() > 0) {
            for (int i = _mapMetaFields.size(); i < iField + 1; i++)
                createMetaField();
        }
        for (Iterator<String> iterField = getMetaFieldsMap().keySet()
                                                            .iterator(); (mf == null) && iterField.hasNext(); ) {
            String sName = iterField.next();
            MetaFieldImpl mfi = (MetaFieldImpl) getMetaField(sName);
            if (_ft.getFields()
                   .getField()
                   .get(iField) == mfi._ft)
                mf = mfi;
        }
        return mf;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaField getMetaField(String sName)
            throws IOException {
        /* dynamically expand the number of array elements */
        if (getCardinality() > 0) {
            Matcher match = _patARRAY_INDEX.matcher(sName);
            if (match.matches()) {
                int iIndex = Integer.parseInt(match.group(1));
                for (int i = _mapMetaFields.size(); i < iIndex; i++)
                    createMetaField();
            }
        }
        return getMetaFieldsMap().get(sName);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaField createMetaField()
            throws IOException {
        MetaField mf = null;
        if (getArchiveImpl().canModifyPrimaryData()) {
            FieldsType fts = _ft.getFields();
            if (fts == null) {
                fts = _of.createFieldsType();
                _ft.setFields(fts);
            }
            FieldType ft = _of.createFieldType();
            fts.getField()
               .add(ft);
            int iPosition = _mapMetaFields.size() + 1;
            String sName = getName() + "[" + iPosition + "]";
            if (getCardinality() < 0) {
                MetaType mt = getMetaType();
                MetaAttribute ma = mt.getMetaAttribute(iPosition - 1);
                sName = ma.getName();
            }
            ft.setName(XU.toXml(sName));
            mf = MetaFieldImpl.newInstance(this, ft, _sFolder, iPosition);
            _mapMetaFields.put(mf.getName(), mf);
            getArchiveImpl().isMetaDataDifferent(null, mf);
            if (_ftTemplate != null) {
                FieldsType ftsTemplate = _ftTemplate.getFields();
                if (ftsTemplate != null) {
                    FieldType ftTemplate = null;
                    for (int iField = 0; iField < ftsTemplate.getField()
                                                             .size(); iField++) {
                        FieldType ftTry = ftsTemplate.getField()
                                                     .get(iField);
                        if (sName.equals(XU.fromXml(ftTry.getName())))
                            ftTemplate = ftTry;
                    }
                    if (ftTemplate != null) {
                        MetaFieldImpl mfi = (MetaFieldImpl) mf;
                        mfi.setTemplate(ftTemplate);
                    }
                }
            }
        } else
            throw new IOException("New field cannot be added!");
        return mf;
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
                        getLobFolder() == null ? "" : getLobFolder().toString(),
                        getMimeType(),
                        getType(),
                        (getCardinality() <= 0) ? "" : String.valueOf(getCardinality()),
                        getDescription()
                };
    } 

    /**
     * {@inheritDoc}
     * toString() returns the name of the field which is to be displayed
     * as the label of the column node of the tree displaying the archive.
     */
    @Override
    public String toString() {
        return getName();
    }
} 
