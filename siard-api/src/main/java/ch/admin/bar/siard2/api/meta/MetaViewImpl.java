/*== MetaViewImpl.java =================================================
MetaViewImpl implements the interface MetaView.
Application : SIARD 2.0
Description : MetaViewImpl implements the interface MetaView.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 29.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.meta;

import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.MetaSchema;
import ch.admin.bar.siard2.api.MetaSearch;
import ch.admin.bar.siard2.api.MetaView;
import ch.admin.bar.siard2.api.generated.ColumnType;
import ch.admin.bar.siard2.api.generated.ColumnsType;
import ch.admin.bar.siard2.api.generated.ObjectFactory;
import ch.admin.bar.siard2.api.generated.ViewType;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.DU;
import ch.enterag.utils.SU;
import ch.enterag.utils.xml.XU;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;


/**
 * MetaViewImpl implements the interface MetaView.
 *
 */
public class MetaViewImpl
        extends MetaSearchImpl
        implements MetaView {
    private static final ObjectFactory _of = new ObjectFactory();
    private final Map<String, MetaColumn> _mapMetaColumns = new HashMap<String, MetaColumn>();

    private MetaSchema _msParent = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaSchema getParentMetaSchema() {
        return _msParent;
    }

    private ViewType _vt = null;

    public ViewType getViewType()
            throws IOException {
        for (int iColumn = 0; iColumn < getMetaColumns(); iColumn++) {
            MetaColumn mc = getMetaColumn(iColumn);
            ((MetaColumnImpl) mc).getColumnType();
        }
        return _vt;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        boolean bValid = true;
        if (bValid && (getMetaColumns() < 1))
            bValid = false;
        for (int iColumn = 0; bValid && (iColumn < getMetaColumns()); iColumn++) {
            if (!getMetaColumn(iColumn).isValid())
                bValid = false;
        }
        return bValid;
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

    private ViewType _vtTemplate = null;

    /**
     * set template meta data.
     *
     * @param vtTemplate template meta data.
     */
    public void setTemplate(ViewType vtTemplate)
            throws IOException {
        _vtTemplate = vtTemplate;
        if (!SU.isNotEmpty(getQuery()))
            setQuery(XU.fromXml(_vtTemplate.getQuery()));
        if (!SU.isNotEmpty(getDescription()))
            setDescription(XU.fromXml(_vtTemplate.getDescription()));
        ColumnsType cts = _vtTemplate.getColumns();
        if (cts != null) {
            for (int iColumn = 0; iColumn < cts.getColumn()
                                               .size(); iColumn++) {
                ColumnType ctTemplate = cts.getColumn()
                                           .get(iColumn);
                String sName = XU.fromXml(ctTemplate.getName());
                MetaColumn mc = getMetaColumn(sName);
                if (mc != null) {
                    MetaColumnImpl mci = (MetaColumnImpl) mc;
                    mci.setTemplate(ctTemplate);
                }
            }
        }
    } 

    /**
     * constructor
     *
     * @param msParent schema meta data object of SIARD archive.
     * @param vt       ViewType instance (JAXB).
     * @throws IOException of default field meta data could not be created.
     */
    private MetaViewImpl(MetaSchema msParent, ViewType vt)
            throws IOException {
        _msParent = msParent;
        _vt = vt;
        /* open all column meta data */
        ColumnsType cts = _vt.getColumns();
        if (cts != null) {
            for (int iColumn = 0; iColumn < cts.getColumn()
                                               .size(); iColumn++) {
                ColumnType ct = cts.getColumn()
                                   .get(iColumn);
                MetaColumn mc = MetaColumnImpl.newInstance(this, iColumn + 1, ct);
                _mapMetaColumns.put(XU.fromXml(ct.getName()), mc);
            }
        }
    } 

    /**
     * factory
     *
     * @param msParent schema meta data object of SIARD archive.
     * @param vt       ViewType instance (JAXB).
     * @return new MetaView instance.
     * @throws IOException of default field meta data could not be created.
     */
    public static MetaView newInstance(MetaSchema msParent, ViewType vt)
            throws IOException {
        return new MetaViewImpl(msParent, vt);
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return XU.fromXml(_vt.getName());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setQuery(String sQuery) {
        if (getArchiveImpl().isMetaDataDifferent(getQuery(), sQuery))
            _vt.setQuery(XU.toXml(sQuery));
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getQuery() {
        return XU.fromXml(_vt.getQuery());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setQueryOriginal(String sQueryOriginal)
            throws IOException {
        if (getArchiveImpl().canModifyPrimaryData()) {
            if (getArchiveImpl().isMetaDataDifferent(getQueryOriginal(), sQueryOriginal))
                _vt.setQueryOriginal(XU.toXml(sQueryOriginal));
        } else
            throw new IOException("Original query cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getQueryOriginal() {
        return XU.fromXml(_vt.getQueryOriginal());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String sDescription) {
        if (getArchiveImpl().isMetaDataDifferent(getDescription(), sDescription))
            _vt.setDescription(XU.toXml(sDescription));
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return XU.fromXml(_vt.getDescription());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRows(long lRows)
            throws IOException {
        if (getArchiveImpl().canModifyPrimaryData()) {
            if (getArchiveImpl().isMetaDataDifferent(Long.valueOf(getRows()), Long.valueOf(lRows)))
                _vt.setRows(BigInteger.valueOf(lRows));
        } else
            throw new IOException("Rows cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRows() {
        long lRows = -1; /* rows is not defined in early SIARD files */
        if (_vt.getRows() != null)
            lRows = _vt.getRows()
                       .intValue();
        return lRows;
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMetaColumns() {
        return _mapMetaColumns.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaColumn getMetaColumn(int iColumn) {
        MetaColumn mc = null;
        ColumnsType cts = _vt.getColumns();
        if (cts != null) {
            ColumnType ct = cts.getColumn()
                               .get(iColumn);
            String sName = XU.fromXml(ct.getName());
            mc = getMetaColumn(sName);
        }
        return mc;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaColumn getMetaColumn(String sName) {
        return _mapMetaColumns.get(sName);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaColumn createMetaColumn(String sName)
            throws IOException {
        MetaColumn mc = null;
        if (getArchiveImpl().canModifyPrimaryData()) {
            if (getMetaColumn(sName) == null) {
                ColumnsType cts = _vt.getColumns();
                if (cts == null) {
                    cts = _of.createColumnsType();
                    _vt.setColumns(cts);
                }
                ColumnType ct = _of.createColumnType();
                ct.setName(XU.toXml(sName));
                cts.getColumn()
                   .add(ct);
                mc = MetaColumnImpl.newInstance(this, _mapMetaColumns.size() + 1, ct);
                _mapMetaColumns.put(sName, mc);
                getArchiveImpl().isMetaDataDifferent(null, mc);
                if (_vtTemplate != null) {
                    ColumnsType ctsTemplate = _vtTemplate.getColumns();
                    if (ctsTemplate != null) {
                        ColumnType ctTemplate = null;
                        for (int iColumn = 0; (ctTemplate == null) && (iColumn < ctsTemplate.getColumn()
                                                                                            .size()); iColumn++) {
                            ColumnType ctTry = ctsTemplate.getColumn()
                                                          .get(iColumn);
                            if (sName.equals(XU.fromXml(ctTry.getName())))
                                ctTemplate = ctTry;
                        }
                        if ((ctTemplate != null) && (mc instanceof MetaColumnImpl)) {
                            MetaColumnImpl mci = (MetaColumnImpl) mc;
                            mci.setTemplate(ctTemplate);
                        }
                    }
                }
            } else
                throw new IOException("Only one column with the same name allowed per table!");
        } else
            throw new IOException("New columns can only be created if archive is open for modification of primary data and table is empty.");
        return mc;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    protected MetaSearch[] getSubMetaSearches()
            throws IOException {
        MetaSearch[] ams = new MetaSearch[getMetaColumns()];
        for (int iColumn = 0; iColumn < getMetaColumns(); iColumn++)
            ams[iColumn] = getMetaColumn(iColumn);
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
                        getQueryOriginal(),
                        getQuery(),
                        String.valueOf(getRows()),
                        getDescription()
                };
    } 

    /**
     * {@inheritDoc}
     * toString() returns the name of the view which is to be displayed
     * as the label of the view node of the tree displaying the archive.
     */
    @Override
    public String toString() {
        return getName();
    }
} 
