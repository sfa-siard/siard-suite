/*== MetaTableImpl.java ================================================
MetaTableImpl implements the interface MetaTable.
Application : SIARD 2.0
Description : MetaTableImpl implements the interface MetaTable.
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * MetaTableImpl implements the interface MetaTable.
 *
 */
public class MetaTableImpl
        extends MetaSearchImpl
        implements MetaTable {
    private static final ObjectFactory _of = new ObjectFactory();
    private MetaUniqueKey _mukPrimaryKey = null;
    private final Map<String, MetaColumn> _mapMetaColumns = new HashMap<String, MetaColumn>();
    private final Map<String, MetaForeignKey> _mapMetaForeignKeys = new HashMap<String, MetaForeignKey>();
    private final Map<String, MetaUniqueKey> _mapMetaCandidateKeys = new HashMap<String, MetaUniqueKey>();
    private final Map<String, MetaCheckConstraint> _mapMetaCheckConstraints = new HashMap<String, MetaCheckConstraint>();
    private final Map<String, MetaTrigger> _mapMetaTriggers = new HashMap<String, MetaTrigger>();

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaSchema getParentMetaSchema() {
        return getTable().getParentSchema()
                         .getMetaSchema();
    } 

    private final Table _table;

    /**
     * {@inheritDoc}
     */
    @Override
    public Table getTable() {
        return _table;
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
        if (bValid && (getMetaPrimaryKey() != null)) {
            if (!getMetaPrimaryKey().isValid())
                bValid = false;
        }
        for (int iCandidateKey = 0; bValid && (iCandidateKey < getMetaCandidateKeys()); iCandidateKey++) {
            if (!getMetaCandidateKey(iCandidateKey).isValid())
                bValid = false;
        }
        for (int iCheckConstraint = 0; bValid && (iCheckConstraint < getMetaCheckConstraints()); iCheckConstraint++) {
            if (!getMetaCheckConstraint(iCheckConstraint).isValid())
                bValid = false;
        }
        for (int iTrigger = 0; bValid && (iTrigger < getMetaTriggers()); iTrigger++) {
            if (!getMetaTrigger(iTrigger).isValid())
                bValid = false;
        }
        return bValid;
    } 

    private TableType _tt = null;

    public TableType getTableType()
            throws IOException {
        for (int iColumn = 0; iColumn < getMetaColumns(); iColumn++) {
            MetaColumn mc = getMetaColumn(iColumn);
            ((MetaColumnImpl) mc).getColumnType();
        }
        return _tt;
    } 

    /**
     * get archive
     *
     * @return archive.
     */
    private ArchiveImpl getArchiveImpl() {
        return (ArchiveImpl) getTable().getParentSchema()
                                       .getParentArchive();
    } 

    private TableType _ttTemplate = null;

    /**
     * set template meta data.
     *
     * @param ttTemplate
     */
    public void setTemplate(TableType ttTemplate)
            throws IOException {
        _ttTemplate = ttTemplate;
        if (!SU.isNotEmpty(getDescription()))
            setDescription(XU.fromXml(_ttTemplate.getDescription()));
        ColumnsType cts = _ttTemplate.getColumns();
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
        UniqueKeyType uktTemplate = _ttTemplate.getPrimaryKey();
        if (uktTemplate != null) {
            MetaUniqueKey muk = getMetaPrimaryKey();
            if (muk != null) {
                MetaUniqueKeyImpl muki = (MetaUniqueKeyImpl) muk;
                muki.setTemplate(uktTemplate);
            }
        }
        ForeignKeysType fkts = _ttTemplate.getForeignKeys();
        if (fkts != null) {
            for (int iForeignKey = 0; iForeignKey < fkts.getForeignKey()
                                                        .size(); iForeignKey++) {
                ForeignKeyType fktTemplate = fkts.getForeignKey()
                                                 .get(iForeignKey);
                String sName = XU.fromXml(fktTemplate.getName());
                MetaForeignKey mfk = getMetaForeignKey(sName);
                if (mfk != null) {
                    MetaForeignKeyImpl mfki = (MetaForeignKeyImpl) mfk;
                    mfki.setTemplate(fktTemplate);
                }
            }
        }
        CandidateKeysType ckts = _ttTemplate.getCandidateKeys();
        if (ckts != null) {
            for (int iCandidateKey = 0; iCandidateKey < ckts.getCandidateKey()
                                                            .size(); iCandidateKey++) {
                uktTemplate = ckts.getCandidateKey()
                                  .get(iCandidateKey);
                String sName = XU.fromXml(uktTemplate.getName());
                MetaUniqueKey muk = getMetaCandidateKey(sName);
                if (muk != null) {
                    MetaUniqueKeyImpl muki = (MetaUniqueKeyImpl) muk;
                    muki.setTemplate(uktTemplate);
                }
            }
        }
        CheckConstraintsType ccts = _ttTemplate.getCheckConstraints();
        if (ccts != null) {
            for (int iCheckConstraint = 0; iCheckConstraint < ccts.getCheckConstraint()
                                                                  .size(); iCheckConstraint++) {
                CheckConstraintType cctTemplate = ccts.getCheckConstraint()
                                                      .get(iCheckConstraint);
                String sName = XU.fromXml(cctTemplate.getName());
                MetaCheckConstraint mcc = getMetaCheckConstraint(sName);
                if (mcc != null) {
                    MetaCheckConstraintImpl mcci = (MetaCheckConstraintImpl) mcc;
                    mcci.setTemplate(cctTemplate);
                }
            }
        }
        TriggersType tts = _ttTemplate.getTriggers();
        if (tts != null) {
            for (int iTrigger = 0; iTrigger < tts.getTrigger()
                                                 .size(); iTrigger++) {
                TriggerType trtTemplate = tts.getTrigger()
                                             .get(iTrigger);
                String sName = XU.fromXml(trtTemplate.getName());
                MetaTrigger mt = getMetaTrigger(sName);
                if (mt != null) {
                    MetaTriggerImpl mti = (MetaTriggerImpl) mt;
                    mti.setTemplate(trtTemplate);
                }
            }
        }
    } 

    /**
     * create an empty TableType instance.
     *
     * @param sName   name of table
     * @param sFolder folder name of table in ZIP file.
     * @return new empty TableType instance.
     */
    public static TableType createTableType(String sName, String sFolder) {
        TableType tt = _of.createTableType();
        tt.setName(XU.toXml(sName));
        tt.setFolder(XU.toXml(sFolder));
        tt.setColumns(_of.createColumnsType());
        tt.setRows(BigInteger.ZERO);
        return tt;
    } 

    /**
     * constructor
     *
     * @param table associated table instance of SIARD archive.
     * @param tt    TableType instance (JAXB).
     * @throws IOException of default field meta data could not be created.
     */
    private MetaTableImpl(Table table, TableType tt)
            throws IOException {
        _table = table;
        _tt = tt;
        /* open all column meta data */
        ColumnsType cts = _tt.getColumns();
        for (int iColumn = 0; iColumn < cts.getColumn()
                                           .size(); iColumn++) {
            ColumnType ct = cts.getColumn()
                               .get(iColumn);
            MetaColumn mc = MetaColumnImpl.newInstance(this, iColumn + 1, ct);
            _mapMetaColumns.put(XU.fromXml(ct.getName()), mc);
        }
        /* open the primary key meta data */
        UniqueKeyType uktPrimary = _tt.getPrimaryKey();
        if (uktPrimary != null)
            _mukPrimaryKey = MetaUniqueKeyImpl.newInstance(this, uktPrimary);
        /* open all foreign key meta data */
        ForeignKeysType fkts = _tt.getForeignKeys();
        if (fkts != null) {
            for (int iForeignKey = 0; iForeignKey < fkts.getForeignKey()
                                                        .size(); iForeignKey++) {
                ForeignKeyType fkt = fkts.getForeignKey()
                                         .get(iForeignKey);
                MetaForeignKey mfk = MetaForeignKeyImpl.newInstance(this, fkt);
                _mapMetaForeignKeys.put(XU.fromXml(fkt.getName()), mfk);
            }
        }
        /* open all candidate key meta data */
        CandidateKeysType ckts = _tt.getCandidateKeys();
        if (ckts != null) {
            for (int iCandidateKey = 0; iCandidateKey < ckts.getCandidateKey()
                                                            .size(); iCandidateKey++) {
                UniqueKeyType ukt = ckts.getCandidateKey()
                                        .get(iCandidateKey);
                MetaUniqueKey muk = MetaUniqueKeyImpl.newInstance(this, ukt);
                _mapMetaCandidateKeys.put(XU.fromXml(ukt.getName()), muk);
            }
        }
        /* open all check constraint meta data */
        CheckConstraintsType ccts = _tt.getCheckConstraints();
        if (ccts != null) {
            for (int iCheckConstraint = 0; iCheckConstraint < ccts.getCheckConstraint()
                                                                  .size(); iCheckConstraint++) {
                CheckConstraintType cct = ccts.getCheckConstraint()
                                              .get(iCheckConstraint);
                MetaCheckConstraint mcc = MetaCheckConstraintImpl.newInstance(this, cct);
                _mapMetaCheckConstraints.put(XU.fromXml(cct.getName()), mcc);
            }
        }
        /* open all trigger meta data */
        TriggersType tts = _tt.getTriggers();
        if (tts != null) {
            for (int iTrigger = 0; iTrigger < tts.getTrigger()
                                                 .size(); iTrigger++) {
                TriggerType trt = tts.getTrigger()
                                     .get(iTrigger);
                MetaTrigger mt = MetaTriggerImpl.newInstance(this, trt);
                _mapMetaTriggers.put(XU.fromXml(trt.getName()), mt);
            }
        }
    } 

    /**
     * factory
     *
     * @param table associated table instance of SIARD archive.
     * @param tt    TableType instance (JAXB).
     * @return new MetaTable instance.
     * @throws IOException of default field meta data could not be created.
     */
    public static MetaTable newInstance(Table table, TableType tt)
            throws IOException {
        return new MetaTableImpl(table, tt);
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
    public String getFolder() {
        return XU.fromXml(_tt.getFolder());
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
    public void setRows(long lRows)
            throws IOException {
        if (getArchiveImpl().canModifyPrimaryData()) {
            if (getArchiveImpl().isMetaDataDifferent(_tt.getRows(), BigInteger.valueOf(lRows)))
                _tt.setRows(BigInteger.valueOf(lRows));
        } else
            throw new IOException("Rows cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRows() {
        return _tt.getRows()
                  .longValue();
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
        ColumnsType cts = _tt.getColumns();
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
        if (getArchiveImpl().canModifyPrimaryData() && getTable().isEmpty()) {
            if (getMetaColumn(sName) == null) {
                ColumnsType cts = _tt.getColumns();
                if (cts == null) {
                    cts = _of.createColumnsType();
                    _tt.setColumns(cts);
                }
                ColumnType ct = _of.createColumnType();
                ct.setName(XU.toXml(sName));
                cts.getColumn()
                   .add(ct);
                mc = MetaColumnImpl.newInstance(this, _mapMetaColumns.size() + 1, ct);
                _mapMetaColumns.put(sName, mc);
                getArchiveImpl().isMetaDataDifferent(null, mc);
                if (_ttTemplate != null) {
                    ColumnsType ctsTemplate = _ttTemplate.getColumns();
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
    public MetaUniqueKey getMetaPrimaryKey() {
        return this._mukPrimaryKey;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaUniqueKey createMetaPrimaryKey(String sName) {
        if (getArchiveImpl().canModifyPrimaryData()) {
            UniqueKeyType uktPrimary = _of.createUniqueKeyType();
            uktPrimary.setName(XU.toXml(sName));
            _tt.setPrimaryKey(uktPrimary);
            _mukPrimaryKey = MetaUniqueKeyImpl.newInstance(this, uktPrimary);
            getArchiveImpl().isMetaDataDifferent(null, _mukPrimaryKey);
        }
        return _mukPrimaryKey;
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMetaForeignKeys() {
        return _mapMetaForeignKeys.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaForeignKey getMetaForeignKey(int iForeignKey) {
        MetaForeignKey mfk = null;
        ForeignKeysType fkts = _tt.getForeignKeys();
        if (fkts != null) {
            ForeignKeyType fkt = fkts.getForeignKey()
                                     .get(iForeignKey);
            String sName = XU.fromXml(fkt.getName());
            mfk = getMetaForeignKey(sName);
        }
        return mfk;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaForeignKey getMetaForeignKey(String sName) {
        return _mapMetaForeignKeys.get(sName);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaForeignKey createMetaForeignKey(String sName)
            throws IOException {
        MetaForeignKey mfk = null;
        if (getArchiveImpl().canModifyPrimaryData()) {
            if (getMetaForeignKey(sName) == null) {
                ForeignKeysType fkts = _tt.getForeignKeys();
                if (fkts == null) {
                    fkts = _of.createForeignKeysType();
                    _tt.setForeignKeys(fkts);
                }
                ForeignKeyType fkt = _of.createForeignKeyType();
                fkt.setName(XU.toXml(sName));
                fkts.getForeignKey()
                    .add(fkt);
                mfk = MetaForeignKeyImpl.newInstance(this, fkt);
                _mapMetaForeignKeys.put(sName, mfk);
                getArchiveImpl().isMetaDataDifferent(null, mfk);
                if (_ttTemplate != null) {
                    ForeignKeysType fktsTemplate = _ttTemplate.getForeignKeys();
                    if (fktsTemplate != null) {
                        ForeignKeyType fktTemplate = null;
                        for (int iForeignKey = 0; (fktTemplate == null) && (iForeignKey < fktsTemplate.getForeignKey()
                                                                                                      .size()); iForeignKey++) {
                            ForeignKeyType fktTry = fktsTemplate.getForeignKey()
                                                                .get(iForeignKey);
                            if (sName.equals(XU.fromXml(fktTry.getName())))
                                fktTemplate = fktTry;
                        }
                        if ((fktTemplate != null) && (mfk instanceof MetaForeignKeyImpl)) {
                            MetaForeignKeyImpl mfki = (MetaForeignKeyImpl) mfk;
                            mfki.setTemplate(fktTemplate);
                        }
                    }
                }
            } else
                throw new IOException("Only one foreign key with the same name allowed per table!");
        } else
            throw new IOException("New foreign keys can only be created if archive is open for modification of primary data.");
        return mfk;
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMetaCandidateKeys() {
        return _mapMetaCandidateKeys.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaUniqueKey getMetaCandidateKey(int iCandidateKey) {
        MetaUniqueKey muk = null;
        CandidateKeysType ckts = _tt.getCandidateKeys();
        if (ckts != null) {
            UniqueKeyType ukt = ckts.getCandidateKey()
                                    .get(iCandidateKey);
            String sName = XU.fromXml(ukt.getName());
            muk = getMetaCandidateKey(sName);
        }
        return muk;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaUniqueKey getMetaCandidateKey(String sName) {
        return _mapMetaCandidateKeys.get(sName);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaUniqueKey createMetaCandidateKey(String sName)
            throws IOException {
        MetaUniqueKey muk = null;
        if (getArchiveImpl().canModifyPrimaryData()) {
            if (getMetaCandidateKey(sName) == null) {
                CandidateKeysType ckts = _tt.getCandidateKeys();
                if (ckts == null) {
                    ckts = _of.createCandidateKeysType();
                    _tt.setCandidateKeys(ckts);
                }
                UniqueKeyType ukt = _of.createUniqueKeyType();
                ukt.setName(XU.toXml(sName));
                ckts.getCandidateKey()
                    .add(ukt);
                muk = MetaUniqueKeyImpl.newInstance(this, ukt);
                _mapMetaCandidateKeys.put(sName, muk);
                getArchiveImpl().isMetaDataDifferent(null, muk);
                if (_ttTemplate != null) {
                    CandidateKeysType fktsTemplate = _ttTemplate.getCandidateKeys();
                    if (fktsTemplate != null) {
                        UniqueKeyType uktTemplate = null;
                        for (int iCandidateKey = 0; (uktTemplate == null) && (iCandidateKey < fktsTemplate.getCandidateKey()
                                                                                                          .size()); iCandidateKey++) {
                            UniqueKeyType uktTry = fktsTemplate.getCandidateKey()
                                                               .get(iCandidateKey);
                            if (sName.equals(XU.fromXml(uktTry.getName())))
                                uktTemplate = uktTry;
                        }
                        if ((uktTemplate != null) && (muk instanceof MetaUniqueKeyImpl)) {
                            MetaUniqueKeyImpl muki = (MetaUniqueKeyImpl) muk;
                            muki.setTemplate(uktTemplate);
                        }
                    }
                }
            } else
                throw new IOException("Only one candidate key with the same name allowed per table!");
        } else
            throw new IOException("New candidaten keys can only be created if archive is open for modification of primary data.");
        return muk;
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMetaCheckConstraints() {
        return _mapMetaCheckConstraints.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaCheckConstraint getMetaCheckConstraint(int iCheckConstraint) {
        MetaCheckConstraint mcc = null;
        CheckConstraintsType ccts = _tt.getCheckConstraints();
        if (ccts != null) {
            CheckConstraintType cct = ccts.getCheckConstraint()
                                          .get(iCheckConstraint);
            String sName = XU.fromXml(cct.getName());
            mcc = getMetaCheckConstraint(sName);
        }
        return mcc;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaCheckConstraint getMetaCheckConstraint(String sName) {
        return _mapMetaCheckConstraints.get(sName);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaCheckConstraint createMetaCheckConstraint(String sName)
            throws IOException {
        MetaCheckConstraint mcc = null;
        if (getArchiveImpl().canModifyPrimaryData()) {
            if (getMetaCheckConstraint(sName) == null) {
                CheckConstraintsType ccts = _tt.getCheckConstraints();
                if (ccts == null) {
                    ccts = _of.createCheckConstraintsType();
                    _tt.setCheckConstraints(ccts);
                }
                CheckConstraintType cct = _of.createCheckConstraintType();
                cct.setName(XU.toXml(sName));
                ccts.getCheckConstraint()
                    .add(cct);
                mcc = MetaCheckConstraintImpl.newInstance(this, cct);
                _mapMetaCheckConstraints.put(sName, mcc);
                getArchiveImpl().isMetaDataDifferent(null, mcc);
                if (_ttTemplate != null) {
                    CheckConstraintsType cctsTemplate = _ttTemplate.getCheckConstraints();
                    if (cctsTemplate != null) {
                        CheckConstraintType cctTemplate = null;
                        for (int iCheckConstraint = 0; (cctTemplate == null) && (iCheckConstraint < cctsTemplate.getCheckConstraint()
                                                                                                                .size()); iCheckConstraint++) {
                            CheckConstraintType cctTry = cctsTemplate.getCheckConstraint()
                                                                     .get(iCheckConstraint);
                            if (sName.equals(XU.fromXml(cctTry.getName())))
                                cctTemplate = cctTry;
                        }
                        if ((cctTemplate != null) && (mcc instanceof MetaCheckConstraintImpl)) {
                            MetaCheckConstraintImpl mcci = (MetaCheckConstraintImpl) mcc;
                            mcci.setTemplate(cctTemplate);
                        }
                    }
                }
            } else
                throw new IOException("Only one check constraint with the same name allowed per table!");
        } else
            throw new IOException("New check constraints can only be created if archive is open for modification of primary data.");
        return mcc;
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMetaTriggers() {
        return _mapMetaTriggers.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaTrigger getMetaTrigger(int iTrigger) {
        MetaTrigger mt = null;
        TriggersType tts = _tt.getTriggers();
        if (tts != null) {
            TriggerType tt = tts.getTrigger()
                                .get(iTrigger);
            String sName = XU.fromXml(tt.getName());
            mt = getMetaTrigger(sName);
        }
        return mt;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaTrigger getMetaTrigger(String sName) {
        return _mapMetaTriggers.get(sName);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaTrigger createMetaTrigger(String sName)
            throws IOException {
        MetaTrigger mt = null;
        if (getArchiveImpl().canModifyPrimaryData()) {
            if (getMetaTrigger(sName) == null) {
                TriggersType tts = _tt.getTriggers();
                if (tts == null) {
                    tts = _of.createTriggersType();
                    _tt.setTriggers(tts);
                }
                TriggerType tt = _of.createTriggerType();
                tt.setName(XU.toXml(sName));
                tts.getTrigger()
                   .add(tt);
                mt = MetaTriggerImpl.newInstance(this, tt);
                _mapMetaTriggers.put(sName, mt);
                getArchiveImpl().isMetaDataDifferent(null, mt);
                if (_ttTemplate != null) {
                    TriggersType ttsTemplate = _ttTemplate.getTriggers();
                    if (ttsTemplate != null) {
                        TriggerType ttTemplate = null;
                        for (int iTrigger = 0; (ttTemplate == null) && (iTrigger < ttsTemplate.getTrigger()
                                                                                              .size()); iTrigger++) {
                            TriggerType ttTry = ttsTemplate.getTrigger()
                                                           .get(iTrigger);
                            if (sName.equals(XU.fromXml(ttTry.getName())))
                                ttTemplate = ttTry;
                        }
                        if ((ttTemplate != null) && (mt instanceof MetaTriggerImpl)) {
                            MetaTriggerImpl mti = (MetaTriggerImpl) mt;
                            mti.setTemplate(ttTemplate);
                        }
                    }
                }
            } else
                throw new IOException("Only one trigger with the same name allowed per table!");
        } else
            throw new IOException("New triggers can only be created if archive is open for modification of primary data.");
        return mt;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public List<List<String>> getColumnNames(
            boolean bSupportsArrays, boolean bSupportsUdts)
            throws IOException {
        List<List<String>> llNames = new ArrayList<List<String>>();
        for (int iColumn = 0; iColumn < getMetaColumns(); iColumn++) {
            MetaColumn mc = getMetaColumn(iColumn);
            llNames.addAll(mc.getNames(bSupportsArrays, bSupportsUdts));
        }
        return llNames;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType(List<String> listNames)
            throws IOException {
        MetaColumn mc = getMetaColumn(listNames.get(0));
        return mc.getType(listNames);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected MetaSearch[] getSubMetaSearches()
            throws IOException {
        int iPrimaryKeys = 0;
        if (getMetaPrimaryKey() != null)
            iPrimaryKeys = 1;
        MetaSearch[] ams = new MetaSearch[
                getMetaColumns() +
                        iPrimaryKeys +
                        getMetaCandidateKeys() +
                        getMetaForeignKeys() +
                        getMetaCheckConstraints() +
                        getMetaTriggers()];
        for (int iColumn = 0; iColumn < getMetaColumns(); iColumn++)
            ams[iColumn] = getMetaColumn(iColumn);
        for (int iPrimaryKey = 0; iPrimaryKey < iPrimaryKeys; iPrimaryKey++)
            ams[getMetaColumns() + iPrimaryKey] = getMetaPrimaryKey();
        for (int iCandidateKey = 0; iCandidateKey < getMetaCandidateKeys(); iCandidateKey++)
            ams[getMetaColumns() + iPrimaryKeys + iCandidateKey] = getMetaCandidateKey(iCandidateKey);
        for (int iForeignKey = 0; iForeignKey < getMetaForeignKeys(); iForeignKey++)
            ams[getMetaColumns() + iPrimaryKeys + getMetaCandidateKeys() + iForeignKey] =
                    getMetaForeignKey(iForeignKey);
        for (int iCheckConstraint = 0; iCheckConstraint < getMetaCheckConstraints(); iCheckConstraint++)
            ams[getMetaColumns() + iPrimaryKeys + getMetaCandidateKeys() + getMetaForeignKeys() + iCheckConstraint] =
                    getMetaCheckConstraint(iCheckConstraint);
        for (int iTrigger = 0; iTrigger < getMetaTriggers(); iTrigger++)
            ams[getMetaColumns() + iPrimaryKeys + getMetaCandidateKeys() + getMetaForeignKeys() + getMetaCheckConstraints() + iTrigger] =
                    getMetaTrigger(iTrigger);
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
                        String.valueOf(getRows()),
                        getDescription()
                };
    } 

    /**
     * {@inheritDoc}
     * toString() returns the name of the table which is to be displayed
     * as the label of the table node of the tree displaying the archive.
     */
    @Override
    public String toString() {
        return getName();
    }
} /* class MetaTableImpl */
