/*== MetaSchemaImpl.java ===============================================
MetaSchemaImpl implements the interface MetaSchema.
Application : SIARD 2.0
Description : MetaSchemaImpl implements the interface MetaSchema.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 24.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.meta;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.*;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.DU;
import ch.enterag.utils.SU;
import ch.enterag.utils.xml.XU;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * MetaSchemaImpl implements the interface MetaSchema.
 *
 */
public class MetaSchemaImpl
        extends MetaSearchImpl
        implements MetaSchema {
    private static final ObjectFactory _of = new ObjectFactory();
    private final Map<String, MetaType> _mapMetaTypes = new HashMap<String, MetaType>();
    private final Map<String, MetaView> _mapMetaViews = new HashMap<String, MetaView>();
    private final Map<String, MetaRoutine> _mapMetaRoutines = new HashMap<String, MetaRoutine>();

    /**
     * get archive
     *
     * @return archive.
     */
    private ArchiveImpl getArchiveImpl() {
        return (ArchiveImpl) getSchema().getParentArchive();
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaData getParentMetaData() {
        return getArchiveImpl().getMetaData();
    }

    private final Schema _schema;

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return _schema;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        boolean bValid = true;
        if (bValid && (getMetaTables() < 1) && (getMetaTypes() < 1))
            bValid = false;
        for (int iType = 0; bValid && (iType < getMetaTypes()); iType++) {
            if (!getMetaType(iType).isValid())
                bValid = false;
        }
        for (int iTable = 0; bValid && (iTable < getMetaTables()); iTable++) {
            if (!getMetaTable(iTable).isValid())
                bValid = false;
        }
        for (int iView = 0; bValid && (iView < getMetaViews()); iView++) {
            if (!getMetaView(iView).isValid())
                bValid = false;
        }
        for (int iRoutine = 0; bValid && (iRoutine < getMetaRoutines()); iRoutine++) {
            if (!getMetaRoutine(iRoutine).isValid())
                bValid = false;
        }
        return bValid;
    } 

    private SchemaType _st = null;

    public SchemaType getSchemaType()
            throws IOException {
        for (int iType = 0; iType < getMetaTypes(); iType++) {
            MetaType mt = getMetaType(iType);
            ((MetaTypeImpl) mt).getTypeType();
        }
        for (int iRoutine = 0; iRoutine < getMetaRoutines(); iRoutine++) {
            MetaRoutine mr = getMetaRoutine(iRoutine);
            ((MetaRoutineImpl) mr).getRoutineType();
        }
        for (int iView = 0; iView < getMetaViews(); iView++) {
            MetaView mv = getMetaView(iView);
            ((MetaViewImpl) mv).getViewType();
        }
        for (int iTable = 0; iTable < getMetaTables(); iTable++) {
            MetaTable mt = getMetaTable(iTable);
            if (mt != null)
                ((MetaTableImpl) mt).getTableType();
        }
        return _st;
    } 

    private SchemaType _stTemplate = null;

    public SchemaType getTemplate() {
        return _stTemplate;
    }

    /**
     * set template meta data.
     *
     * @param stTemplate template meta data.
     */
    public void setTemplate(SchemaType stTemplate)
            throws IOException {
        _stTemplate = stTemplate;
        if (!SU.isNotEmpty(getDescription()))
            setDescription(XU.fromXml(_stTemplate.getDescription()));
        TypesType ttys = _stTemplate.getTypes();
        if (ttys != null) {
            for (int iType = 0; iType < ttys.getType()
                                            .size(); iType++) {
                TypeType ttTemplate = ttys.getType()
                                          .get(iType);
                String sName = XU.fromXml(ttTemplate.getName());
                MetaType mt = getMetaType(sName);
                if (mt != null) {
                    MetaTypeImpl mti = (MetaTypeImpl) mt;
                    mti.setTemplate(ttTemplate);
                }
            }
        }
        TablesType tts = _stTemplate.getTables();
        if (tts != null) {
            for (int iTable = 0; iTable < tts.getTable()
                                             .size(); iTable++) {
                TableType ttTemplate = tts.getTable()
                                          .get(iTable);
                String sName = XU.fromXml(ttTemplate.getName());
                MetaTable mt = getMetaTable(sName);
                if (mt != null) {
                    MetaTableImpl mti = (MetaTableImpl) mt;
                    mti.setTemplate(ttTemplate);
                }
            }
        }
        ViewsType vts = _stTemplate.getViews();
        if (vts != null) {
            for (int iView = 0; iView < vts.getView()
                                           .size(); iView++) {
                ViewType vtTemplate = vts.getView()
                                         .get(iView);
                String sName = XU.fromXml(vtTemplate.getName());
                MetaView mv = getMetaView(sName);
                if (mv != null) {
                    MetaViewImpl mvi = (MetaViewImpl) mv;
                    mvi.setTemplate(vtTemplate);
                }
            }
        }
        RoutinesType rts = _stTemplate.getRoutines();
        if (rts != null) {
            for (int iRoutine = 0; iRoutine < rts.getRoutine()
                                                 .size(); iRoutine++) {
                RoutineType rtTemplate = rts.getRoutine()
                                            .get(iRoutine);
                String sName = XU.fromXml(rtTemplate.getName());
                MetaRoutine mr = getMetaRoutine(sName);
                if ((mr != null) && (mr instanceof MetaRoutineImpl)) {
                    MetaRoutineImpl mri = (MetaRoutineImpl) mr;
                    mri.setTemplate(rtTemplate);
                }
            }
        }
    } 

    /**
     * create an empty SchemaType instance.
     *
     * @param sName   name of schema.
     * @param sFolder folder name of schema in ZIP file.
     * @return new empty SchemaType instance.
     */
    public static SchemaType createSchemaType(String sName, String sFolder) {
        SchemaType st = _of.createSchemaType();
        st.setName(XU.toXml(sName));
        st.setFolder(XU.toXml(sFolder));
        return st;
    } 

    /**
     * constructor
     *
     * @param schema associated schema instance of SIARD archive.
     * @param st     SchemaType instance (JAXB).
     * @throws IOException if an I/O error occurred.
     */
    private MetaSchemaImpl(Schema schema, SchemaType st)
            throws IOException {
        _schema = schema;
        _st = st;
        /* meta tables will be opened by Table creation */
        /* open all type meta data */
        TypesType ttys = _st.getTypes();
        if (ttys != null) {
            for (int iType = 0; iType < ttys.getType()
                                            .size(); iType++) {
                TypeType tt = ttys.getType()
                                  .get(iType);
                MetaType mt = MetaTypeImpl.newInstance(this, tt);
                _mapMetaTypes.put(XU.fromXml(tt.getName()), mt);

            }
        }
        /* open all view meta data */
        ViewsType vts = _st.getViews();
        if (vts != null) {
            for (int iView = 0; iView < vts.getView()
                                           .size(); iView++) {
                ViewType vt = vts.getView()
                                 .get(iView);
                MetaView mv = MetaViewImpl.newInstance(this, vt);
                _mapMetaViews.put(XU.fromXml(vt.getName()), mv);

            }
        }
        /* open all routine meta data */
        RoutinesType rts = _st.getRoutines();
        if (rts != null) {
            for (int iRoutine = 0; iRoutine < rts.getRoutine()
                                                 .size(); iRoutine++) {
                RoutineType rt = rts.getRoutine()
                                    .get(iRoutine);
                MetaRoutine mr = MetaRoutineImpl.newInstance(this, rt);
                _mapMetaRoutines.put(XU.fromXml(rt.getSpecificName()), mr);
            }
        }
    } 

    /**
     * factory
     *
     * @param schema associated schema instance of SIARD archive.
     * @param st     SchemaType instance (JAXB).
     * @return new MetaSchema instance.
     * @throws IOException if an I/O error occurred.
     */
    public static MetaSchema newInstance(Schema schema, SchemaType st)
            throws IOException {
        return new MetaSchemaImpl(schema, st);
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return XU.fromXml(_st.getName());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFolder() {
        return XU.fromXml(_st.getFolder());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String sDescription) {
        if (getArchiveImpl().isMetaDataDifferent(getDescription(), sDescription))
            _st.setDescription(XU.toXml(sDescription));
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return XU.fromXml(_st.getDescription());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMetaTables() {
        int iTables = 0;
        TablesType tts = _st.getTables();
        if (tts != null)
            iTables = tts.getTable()
                         .size();
        return iTables;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaTable getMetaTable(int iTable) {
        String sName = _st.getTables()
                          .getTable()
                          .get(iTable)
                          .getName();
        return getMetaTable(sName);
    } 

    /**
     * {@inheritDoc}
     */
    public MetaTable getMetaTable(String sName) {
        MetaTable mt = null;
        Table table = getSchema().getTable(sName);
        if (table != null)
            mt = table.getMetaTable();
        return mt;
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMetaViews() {
        return _mapMetaViews.size();
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaView getMetaView(int iView) {
        MetaView mv = null;
        ViewsType vts = _st.getViews();
        if (vts != null) {
            ViewType vt = vts.getView()
                             .get(iView);
            String sName = XU.fromXml(vt.getName());
            mv = getMetaView(sName);
        }
        return mv;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaView getMetaView(String sName) {
        return _mapMetaViews.get(sName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaView createMetaView(String sName)
            throws IOException {
        MetaView mv = null;
        if (getArchiveImpl().canModifyPrimaryData()) {
            if (getMetaView(sName) == null) {
                ViewsType vts = _st.getViews();
                if (vts == null) {
                    vts = _of.createViewsType();
                    _st.setViews(vts);
                }
                ViewType vt = _of.createViewType();
                vt.setName(XU.toXml(sName));
                vt.setColumns(_of.createColumnsType());
                vt.setRows(BigInteger.ZERO);
                vts.getView()
                   .add(vt);
                mv = MetaViewImpl.newInstance(this, vt);
                _mapMetaViews.put(sName, mv);
                getArchiveImpl().isMetaDataDifferent(null, mv);
                if (_stTemplate != null) {
                    ViewsType vtsTemplate = _stTemplate.getViews();
                    if (vtsTemplate != null) {
                        ViewType vtTemplate = null;
                        for (int iView = 0; (vtTemplate == null) && (iView < vtsTemplate.getView()
                                                                                        .size()); iView++) {
                            ViewType vtTry = vtsTemplate.getView()
                                                        .get(iView);
                            if (sName.equals(XU.fromXml(vtTry.getName())))
                                vtTemplate = vtTry;
                        }
                        if (vtTemplate != null) {
                            MetaViewImpl mvi = (MetaViewImpl) mv;
                            mvi.setTemplate(vtTemplate);
                        }
                    }
                }
            } else
                throw new IOException("Only one view with the same name allowed per schema!");
        } else
            throw new IOException("Views can only be created if archive is open for modification of primary data.");
        return mv;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeMetaView(MetaView mv)
            throws IOException {
        boolean bRemoved = false;
        if (getArchiveImpl().canModifyPrimaryData()) {
            ViewsType vts = _st.getViews();
            for (Iterator<ViewType> iterViewType = vts.getView()
                                                      .iterator(); iterViewType.hasNext(); ) {
                ViewType vt = iterViewType.next();
                if (vt.getName()
                      .equals(mv.getName())) {
                    iterViewType.remove();
                    _mapMetaViews.remove(mv.getName());
                    bRemoved = true;
                }
            }
            if (vts.getView()
                   .size() == 0)
                _st.setViews(null);
        } else
            throw new IOException("Views can only be removed if archive is open for modification of primary data.");
        return bRemoved;
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMetaRoutines() {
        return _mapMetaRoutines.size();
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaRoutine getMetaRoutine(int iRoutine) {
        MetaRoutine mr = null;
        RoutinesType rts = _st.getRoutines();
        if (rts != null) {
            RoutineType rt = rts.getRoutine()
                                .get(iRoutine);
            String sSpecificName = XU.fromXml(rt.getSpecificName());
            mr = getMetaRoutine(sSpecificName);
        }
        return mr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaRoutine getMetaRoutine(String sSpecificName) {
        return _mapMetaRoutines.get(sSpecificName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaRoutine createMetaRoutine(String sSpecificName)
            throws IOException {
        MetaRoutine mr = null;
        if (getArchiveImpl().canModifyPrimaryData()) {
            if (getMetaRoutine(sSpecificName) == null) {
                RoutinesType rts = _st.getRoutines();
                if (rts == null) {
                    rts = _of.createRoutinesType();
                    _st.setRoutines(rts);
                }
                RoutineType rt = _of.createRoutineType();
                rt.setName(XU.toXml(sSpecificName));
                rt.setSpecificName(XU.toXml(sSpecificName));
                rts.getRoutine()
                   .add(rt);
                mr = MetaRoutineImpl.newInstance(this, rt);
                _mapMetaRoutines.put(sSpecificName, mr);
                getArchiveImpl().isMetaDataDifferent(null, mr);
                if (_stTemplate != null) {
                    RoutinesType rtsTemplate = _stTemplate.getRoutines();
                    if (rtsTemplate != null) {
                        RoutineType rtTemplate = null;
                        for (int iRoutine = 0; (rtTemplate == null) && (iRoutine < rtsTemplate.getRoutine()
                                                                                              .size()); iRoutine++) {
                            RoutineType rtTry = rtsTemplate.getRoutine()
                                                           .get(iRoutine);
                            if (sSpecificName.equals(XU.fromXml(rtTry.getSpecificName())))
                                rtTemplate = rtTry;
                        }
                        if (rtTemplate != null) {
                            MetaRoutineImpl mri = (MetaRoutineImpl) mr;
                            mri.setTemplate(rtTemplate);
                        }
                    }
                }
            } else
                throw new IOException("Only one view with the same name allowed per schema!");
        } else
            throw new IOException("Views can only be created if archive is open for modification of primary data.");
        return mr;
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMetaTypes() {
        return _mapMetaTypes.size();
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaType getMetaType(int iType) {
        MetaType mt = null;
        TypesType ttys = _st.getTypes();
        if (ttys != null) {
            TypeType tt = ttys.getType()
                              .get(iType);
            String sName = XU.fromXml(tt.getName());
            mt = getMetaType(sName);
        }
        return mt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaType getMetaType(String sName) {
        return _mapMetaTypes.get(sName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaType createMetaType(String sName)
            throws IOException {
        MetaType mt = null;
        if (getArchiveImpl().canModifyPrimaryData()) {
            if (getMetaType(sName) == null) {
                TypesType ttys = _st.getTypes();
                if (ttys == null) {
                    ttys = _of.createTypesType();
                    _st.setTypes(ttys);
                }
                TypeType tt = _of.createTypeType();
                tt.setName(XU.toXml(sName));
                /* default: DISTINCT type */
                tt.setCategory(CategoryType.DISTINCT);
                tt.setInstantiable(true);
                tt.setFinal(true);
                ttys.getType()
                    .add(tt);
                mt = MetaTypeImpl.newInstance(this, tt);
                _mapMetaTypes.put(sName, mt);
                getArchiveImpl().isMetaDataDifferent(null, mt);
                if (_stTemplate != null) {
                    TypesType ttysTemplate = _stTemplate.getTypes();
                    if (ttysTemplate != null) {
                        TypeType ttTemplate = null;
                        for (int iType = 0; (ttTemplate == null) && (iType < ttysTemplate.getType()
                                                                                         .size()); iType++) {
                            TypeType ttTry = ttysTemplate.getType()
                                                         .get(iType);
                            if (sName.equals(XU.fromXml(ttTry.getName())))
                                ttTemplate = ttTry;
                        }
                        if (ttTemplate != null) {
                            MetaTypeImpl mti = (MetaTypeImpl) mt;
                            mti.setTemplate(ttTemplate);
                        }
                    }
                }
            } else
                throw new IOException("Only one type with the same name allowed per schema!");
        } else
            throw new IOException("Types can only be created if archive is open for modification of primary data.");
        return mt;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    protected MetaSearch[] getSubMetaSearches()
            throws IOException {
        MetaSearch[] ams = new MetaSearch[getMetaTypes() + getMetaTables() + getMetaViews() + getMetaRoutines()];
        for (int iType = 0; iType < getMetaTypes(); iType++)
            ams[iType] = getMetaType(iType);
        for (int iTable = 0; iTable < getMetaTables(); iTable++)
            ams[getMetaTypes() + iTable] = getMetaTable(iTable);
        for (int iView = 0; iView < getMetaViews(); iView++)
            ams[getMetaTypes() + getMetaTables() + iView] = getMetaView(iView);
        for (int iRoutine = 0; iRoutine < getMetaRoutines(); iRoutine++)
            ams[getMetaTypes() + getMetaTables() + getMetaViews() + iRoutine] = getMetaRoutine(iRoutine);
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
                        getDescription()
                };
    } 

    /**
     * {@inheritDoc}
     * toString() returns the name of the schema which is to be displayed
     * as the label of the schema node of the tree displaying the archive.
     */
    @Override
    public String toString() {
        return getName();
    }
} 
