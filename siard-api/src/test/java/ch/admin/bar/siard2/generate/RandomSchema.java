package ch.admin.bar.siard2.generate;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.*;

import java.io.IOException;

public class RandomSchema {
    private Schema _schema = null;
    private SchemaType _s = null;
    private double _dFraction = 1.0;

    RandomSchema(Schema schema, SchemaType s, double dFraction) {
        _schema = schema;
        _s = s;
        _dFraction = dFraction;
    } 

    private int createTrigger(MetaTrigger mt, TriggerType tt) {
        int iReturn = RandomArchive.iRETURN_ERROR;
        try {
            mt.setTriggerEvent(tt.getTriggerEvent());
            if (tt.getActionTime() != null)
                mt.setActionTime(tt.getActionTime()
                                   .toString());
            mt.setTriggeredAction(tt.getTriggeredAction());
            mt.setAliasList(tt.getAliasList());
            mt.setDescription(tt.getDescription());
            iReturn = RandomArchive.iRETURN_OK;
        } catch (IOException ie) {
            System.err.println(RandomArchive.getExceptionMessage(ie));
        }
        return iReturn;
    }

    private int createCheckConstraint(MetaCheckConstraint mcc, CheckConstraintType cc) {
        int iReturn = RandomArchive.iRETURN_ERROR;
        try {
            mcc.setCondition(cc.getCondition());
            mcc.setDescription(cc.getDescription());
            iReturn = RandomArchive.iRETURN_OK;
        } catch (IOException ie) {
            System.err.println(RandomArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int createForeignKey(MetaForeignKey mfk, ForeignKeyType fk) {
        int iReturn = RandomArchive.iRETURN_ERROR;
        try {
            if (fk.getMatchType() != null)
                mfk.setMatchType(fk.getMatchType()
                                   .toString());
            if (fk.getDeleteAction() != null)
                mfk.setDeleteAction(fk.getDeleteAction()
                                      .toString());
            if (fk.getUpdateAction() != null)
                mfk.setUpdateAction(fk.getUpdateAction()
                                      .toString());
            mfk.setDescription(fk.getDescription());
            mfk.setReferencedSchema(fk.getReferencedSchema());
            mfk.setReferencedTable(fk.getReferencedTable());
            for (int iReference = 0; iReference < fk.getReference()
                                                    .size(); iReference++) {
                ReferenceType ref = fk.getReference()
                                      .get(iReference);
                mfk.addReference(ref.getColumn(), ref.getReferenced());
            }
            iReturn = RandomArchive.iRETURN_OK;
        } catch (IOException ie) {
            System.err.println(RandomArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int createUniqueKey(MetaUniqueKey muk, UniqueKeyType uk) {
        int iReturn = RandomArchive.iRETURN_ERROR;
        try {
            muk.setDescription(uk.getDescription());
            for (int iColumn = 0; iColumn < uk.getColumn()
                                              .size(); iColumn++) {
                String sColumn = uk.getColumn()
                                   .get(iColumn);
                muk.addColumn(sColumn);
            }
            iReturn = RandomArchive.iRETURN_OK;
        } catch (IOException ie) {
            System.err.println(RandomArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int createParameter(MetaParameter mp, ParameterType p) {
        int iReturn = RandomArchive.iRETURN_ERROR;
        try {
            mp.setMode(p.getMode());
            mp.setType(p.getType());
            if (p.getTypeName() != null) {
                mp.setTypeName(p.getTypeName());
                mp.setTypeSchema(p.getTypeSchema());
            }
            mp.setTypeOriginal(p.getTypeOriginal());
            if (p.getCardinality() != null)
                mp.setCardinality(p.getCardinality()
                                   .intValue());
            mp.setDescription(p.getDescription());
            iReturn = RandomArchive.iRETURN_OK;
        } catch (IOException ie) {
            System.err.println(RandomArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int createField(MetaField mf, FieldType f) {
        int iReturn = RandomArchive.iRETURN_ERROR;
        try {
            mf.setMimeType(f.getMimeType());
            mf.setDescription(f.getDescription());
            iReturn = RandomArchive.iRETURN_OK;
            // fields
            FieldsType ft = f.getFields();
            if (ft != null) {
                for (int iField = 0; (iReturn == RandomArchive.iRETURN_OK) && (iField < ft.getField()
                                                                                          .size()); iField++) {
                    FieldType fSub = ft.getField()
                                       .get(iField);
                    MetaField mfSub = mf.createMetaField();
                    iReturn = createField(mfSub, fSub);
                }
            }
        } catch (IOException ie) {
            System.err.println(RandomArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int createColumn(MetaColumn mc, ColumnType c) {
        int iReturn = RandomArchive.iRETURN_ERROR;
        try {
            mc.setType(c.getType());
            if (c.getTypeName() != null) {
                mc.setTypeName(c.getTypeName());
                mc.setTypeSchema(c.getTypeSchema());
            }
            mc.setTypeOriginal(c.getTypeOriginal());
            mc.setMimeType(c.getMimeType());
            mc.setNullable(c.isNullable());
            mc.setDefaultValue(c.getDefaultValue());
            if (c.getCardinality() != null)
                mc.setCardinality(c.getCardinality()
                                   .intValue());
            mc.setDescription(c.getDescription());
            iReturn = RandomArchive.iRETURN_OK;
            // fields
            FieldsType ft = c.getFields();
            if (ft != null) {
                for (int iField = 0; (iReturn == RandomArchive.iRETURN_OK) && (iField < ft.getField()
                                                                                          .size()); iField++) {
                    FieldType f = ft.getField()
                                    .get(iField);
                    MetaField mf = mc.createMetaField();
                    iReturn = createField(mf, f);
                }
            }
        } catch (IOException ie) {
            System.err.println(RandomArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int createAttribute(MetaAttribute ma, AttributeType a) {
        int iReturn = RandomArchive.iRETURN_ERROR;
        try {
            ma.setType(a.getType());
            if (a.getTypeName() != null) {
                ma.setTypeName(a.getTypeName());
                ma.setTypeSchema(a.getTypeSchema());
            }
            ma.setTypeOriginal(a.getTypeOriginal());
            ma.setNullable(a.isNullable());
            ma.setDefaultValue(a.getDefaultValue());
            if (a.getCardinality() != null)
                ma.setCardinality(a.getCardinality()
                                   .intValue());
            ma.setDescription(a.getDescription());
            iReturn = RandomArchive.iRETURN_OK;
        } catch (IOException ie) {
            System.err.println(RandomArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int createTable(MetaTable mt, TableType t) {
        int iReturn = RandomArchive.iRETURN_ERROR;
        try {
            int iRows = t.getRows()
                         .intValue();
            iRows = (int) Math.ceil(_dFraction * iRows);
            mt.setRows(iRows);
            mt.setDescription(t.getDescription());
            iReturn = RandomArchive.iRETURN_OK;
            // primary key
            UniqueKeyType uk = t.getPrimaryKey();
            if (uk != null) {
                MetaUniqueKey muk = mt.createMetaPrimaryKey(uk.getName());
                iReturn = createUniqueKey(muk, uk);
            }
            // candidate keys
            CandidateKeysType ckt = t.getCandidateKeys();
            if (ckt != null) {
                for (int iCandidateKey = 0; (iReturn == RandomArchive.iRETURN_OK) && (iCandidateKey < ckt.getCandidateKey()
                                                                                                         .size()); iCandidateKey++) {
                    uk = ckt.getCandidateKey()
                            .get(iCandidateKey);
                    MetaUniqueKey muk = mt.createMetaCandidateKey(uk.getName());
                    iReturn = createUniqueKey(muk, uk);
                }
            }
            // foreign keys
            ForeignKeysType fkt = t.getForeignKeys();
            if (fkt != null) {
                for (int iForeignKey = 0; (iReturn == RandomArchive.iRETURN_OK) && (iForeignKey < fkt.getForeignKey()
                                                                                                     .size()); iForeignKey++) {
                    ForeignKeyType fk = fkt.getForeignKey()
                                           .get(iForeignKey);
                    MetaForeignKey mfk = mt.createMetaForeignKey(fk.getName());
                    iReturn = createForeignKey(mfk, fk);
                }
            }
            // check constraints
            CheckConstraintsType cct = t.getCheckConstraints();
            if (cct != null) {
                for (int iCheckConstraint = 0; (iReturn == RandomArchive.iRETURN_OK) && (iCheckConstraint < cct.getCheckConstraint()
                                                                                                               .size()); iCheckConstraint++) {
                    CheckConstraintType cc = cct.getCheckConstraint()
                                                .get(iCheckConstraint);
                    MetaCheckConstraint mcc = mt.createMetaCheckConstraint(cc.getName());
                    iReturn = createCheckConstraint(mcc, cc);
                }
            }
            // triggers
            TriggersType tt = t.getTriggers();
            if (tt != null) {
                for (int iTrigger = 0; (iReturn == RandomArchive.iRETURN_OK) && (iTrigger < tt.getTrigger()
                                                                                              .size()); iTrigger++) {
                    TriggerType tr = tt.getTrigger()
                                       .get(iTrigger);
                    MetaTrigger mtr = mt.createMetaTrigger(tr.getName());
                    iReturn = createTrigger(mtr, tr);
                }
            }
            // columns
            ColumnsType ct = t.getColumns();
            if (ct != null) {
                for (int iColumn = 0; (iReturn == RandomArchive.iRETURN_OK) && (iColumn < ct.getColumn()
                                                                                            .size()); iColumn++) {
                    ColumnType c = ct.getColumn()
                                     .get(iColumn);
                    MetaColumn mc = mt.createMetaColumn(c.getName());
                    iReturn = createColumn(mc, c);
                }
            }
        } catch (IOException ie) {
            System.err.println(RandomArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int createRoutine(MetaRoutine mr, RoutineType r) {
        int iReturn = RandomArchive.iRETURN_ERROR;
        try {
            mr.setName(r.getName());
            mr.setCharacteristic(r.getCharacteristic());
            mr.setSource(r.getSource());
            mr.setBody(r.getBody());
            mr.setDescription(r.getDescription());
            mr.setReturnType(r.getReturnType());
            iReturn = RandomArchive.iRETURN_OK;
            // parameters
            ParametersType pt = r.getParameters();
            if (pt != null) {
                for (int iParameter = 0; (iReturn == RandomArchive.iRETURN_OK) && (iParameter < pt.getParameter()
                                                                                                  .size()); iParameter++) {
                    ParameterType p = pt.getParameter()
                                        .get(iParameter);
                    MetaParameter mp = mr.createMetaParameter(p.getName());
                    iReturn = createParameter(mp, p);
                }
            }
        } catch (IOException ie) {
            System.err.println(RandomArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int createView(MetaView mv, ViewType v) {
        int iReturn = RandomArchive.iRETURN_ERROR;
        try {
            mv.setQuery(v.getQuery());
            mv.setQueryOriginal(v.getQueryOriginal());
            mv.setRows(v.getRows()
                        .longValue());
            mv.setDescription(v.getDescription());
            iReturn = RandomArchive.iRETURN_OK;
            // columns
            ColumnsType ct = v.getColumns();
            if (ct != null) {
                for (int iColumn = 0; (iReturn == RandomArchive.iRETURN_OK) && (iColumn < ct.getColumn()
                                                                                            .size()); iColumn++) {
                    ColumnType c = ct.getColumn()
                                     .get(iColumn);
                    MetaColumn mc = mv.createMetaColumn(c.getName());
                    iReturn = createColumn(mc, c);
                }
            }
        } catch (IOException ie) {
            System.err.println(RandomArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int createType(MetaType mt, TypeType t) {
        int iReturn = RandomArchive.iRETURN_ERROR;
        try {
            if (t.getCategory() != null)
                mt.setCategory(t.getCategory()
                                .toString());
            mt.setBase(t.getBase());
            mt.setUnderType(t.getUnderType());
            mt.setUnderSchema(t.getUnderSchema());
            mt.setFinal(t.isFinal());
            mt.setInstantiable(t.isInstantiable());
            mt.setDescription(t.getDescription());
            iReturn = RandomArchive.iRETURN_OK;
            // attributes
            AttributesType at = t.getAttributes();
            if (at != null) {
                for (int iAttribute = 0; (iReturn == RandomArchive.iRETURN_OK) && (iAttribute < at.getAttribute()
                                                                                                  .size()); iAttribute++) {
                    AttributeType a = at.getAttribute()
                                        .get(iAttribute);
                    MetaAttribute ma = mt.createMetaAttribute(a.getName());
                    iReturn = createAttribute(ma, a);
                }
            }
        } catch (IOException ie) {
            System.err.println(RandomArchive.getExceptionMessage(ie));
        }
        return iReturn;
    } 

    public int createSchema() {
        int iReturn = RandomArchive.iRETURN_OK;
        try {
            MetaSchema ms = _schema.getMetaSchema();
            // types
            TypesType tt = _s.getTypes();
            if (tt != null) {
                for (int iType = 0; (iReturn == RandomArchive.iRETURN_OK) && (iType < tt.getType()
                                                                                        .size()); iType++) {
                    TypeType t = tt.getType()
                                   .get(iType);
                    MetaType mt = ms.createMetaType(t.getName());
                    iReturn = createType(mt, t);
                }
            }
            // views
            ViewsType vt = _s.getViews();
            if (vt != null) {
                for (int iView = 0; (iReturn == RandomArchive.iRETURN_OK) && (iView < vt.getView()
                                                                                        .size()); iView++) {
                    ViewType v = vt.getView()
                                   .get(iView);
                    MetaView mv = ms.createMetaView(v.getName());
                    iReturn = createView(mv, v);
                }
            }
            // routines
            RoutinesType rt = _s.getRoutines();
            if (rt != null) {
                for (int iRoutine = 0; (iReturn == RandomArchive.iRETURN_OK) && (iRoutine < rt.getRoutine()
                                                                                              .size()); iRoutine++) {
                    RoutineType r = rt.getRoutine()
                                      .get(iRoutine);
                    MetaRoutine mr = ms.createMetaRoutine(r.getSpecificName());
                    iReturn = createRoutine(mr, r);
                }
            }
            // tables
            TablesType tabt = _s.getTables();
            if (tabt != null) {
                for (int iTable = 0; (iReturn == RandomArchive.iRETURN_OK) && (iTable < tabt.getTable()
                                                                                            .size()); iTable++) {
                    TableType t = tabt.getTable()
                                      .get(iTable);
                    System.out.println("  Table: " + t.getName());
                    Table table = _schema.createTable(t.getName());
                    // meta data
                    iReturn = createTable(table.getMetaTable(), t);
                    if (iReturn == RandomArchive.iRETURN_OK) {
                        // primary data
                        RandomTable rtab = new RandomTable(table);
                        iReturn = rtab.createTable();
                    }
                }
            }

        } catch (IOException ie) {
            System.err.println(RandomArchive.getExceptionMessage(ie));
        }
        return iReturn;
    }

}
