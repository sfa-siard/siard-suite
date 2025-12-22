/*== SchemaImpl.java ===================================================
SchemaImpl implements the interface Schema.
Application : SIARD 2.0
Description : SchemaImpl implements the interface Schema.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 27.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.primary;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.SchemaType;
import ch.admin.bar.siard2.api.generated.SchemasType;
import ch.admin.bar.siard2.api.generated.TableType;
import ch.admin.bar.siard2.api.generated.TablesType;
import ch.admin.bar.siard2.api.meta.MetaDataImpl;
import ch.admin.bar.siard2.api.meta.MetaSchemaImpl;
import ch.admin.bar.siard2.api.meta.MetaTableImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * SchemaImpl implements the interface Schema.
 *
 */
public class SchemaImpl
        implements Schema {
    public static final String _sSCHEMA_FOLDER_PREFIX = "schema";
    private Archive _archiveParent = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public Archive getParentArchive() {
        return _archiveParent;
    }

    private MetaSchema _ms = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaSchema getMetaSchema() {
        return _ms;
    }

    private final Map<String, Table> _mapTables = new HashMap<String, Table>();

    public void registerTable(String sName, Table table) {
        _mapTables.put(sName, table);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        boolean bEmpty = true;
        for (Iterator<String> iterTable = _mapTables.keySet()
                                                    .iterator(); iterTable.hasNext(); ) {
            String sTable = iterTable.next();
            Table table = _mapTables.get(sTable);
            if (!table.isEmpty())
                bEmpty = false;
        }
        return bEmpty;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        boolean bValid = getMetaSchema().isValid();
        for (int iTable = 0; bValid && (iTable < getTables()); iTable++) {
            if (!getTable(iTable).isValid())
                bValid = false;
        }
        return bValid;
    } 

    /**
     * return full entry name of schema folder.
     *
     * @return full entry name of schema folder.
     */
    String getSchemaFolder() {
        return ArchiveImpl.getContentFolder() + getMetaSchema().getFolder() + "/";
    } 

    /**
     * constructor
     *
     * @param archiveParent archive to which this Schema instance belongs.
     * @param sName         schema name.
     * @throws IOException if the schema cannot be created.
     */
    private SchemaImpl(Archive archiveParent, String sName)
            throws IOException {
        _archiveParent = archiveParent;
        MetaDataImpl mdi = (MetaDataImpl) getParentArchive().getMetaData();
        SchemasType sts = mdi.getSiardArchive()
                             .getSchemas();
        SchemaType st = null;
        for (int iSchema = 0; (st == null) && (iSchema < sts.getSchema()
                                                            .size()); iSchema++) {
            SchemaType stTry = sts.getSchema()
                                  .get(iSchema);
            if (sName.equals(stTry.getName()))
                st = stTry;
        }
        ArchiveImpl ai = (ArchiveImpl) getParentArchive();
        if (st == null) {
            String sFolder = _sSCHEMA_FOLDER_PREFIX + sts.getSchema()
                                                         .size();
            ai.createFolderEntry(ArchiveImpl.getContentFolder() + sFolder + "/");
            st = MetaSchemaImpl.createSchemaType(sName, sFolder);
            sts.getSchema()
               .add(st);
        }
        _ms = MetaSchemaImpl.newInstance(this, st);
        ai.registerSchema(sName, this);
        /* open tables (including meta tables) */
        TablesType tts = st.getTables();
        if (tts != null) {
            for (int iTable = 0; iTable < tts.getTable()
                                             .size(); iTable++) {
                TableType tt = tts.getTable()
                                  .get(iTable);
                TableImpl.newInstance(this, tt.getName());
            }
        }
    } 

    /**
     * factory
     *
     * @param archiveParent archive to which the Schema instance belongs.
     * @param sName         name of the Schema instance.
     * @return new Schema instance.
     */
    public static Schema newInstance(Archive archiveParent, String sName)
            throws IOException {
        return new SchemaImpl(archiveParent, sName);
    } 
  
  /*====================================================================
  methods
  ====================================================================*/

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTables() {
        return getMetaSchema().getMetaTables();
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public Table getTable(int iTable) {
        Table table = null;
        MetaTable mt = getMetaSchema().getMetaTable(iTable);
        if (mt != null)
            table = getTable(mt.getName());
        return table;
    } 

    /**
     * {@inheritDoc}
     */
    public Table getTable(String sName) {
        return _mapTables.get(sName);
    } 

    /**
     * {@inheritDoc}
     */
    public Table createTable(String sName)
            throws IOException {
        Table table = null;
        if (getParentArchive().canModifyPrimaryData()) {
            if (_mapTables.get(sName) == null) {
                /* meta table is created when table is instantiated */
                table = TableImpl.newInstance(this, sName);
                // handle template
                MetaSchemaImpl msi = (MetaSchemaImpl) getMetaSchema();
                SchemaType stTemplate = msi.getTemplate();
                if (stTemplate != null) {
                    TablesType tts = stTemplate.getTables();
                    if (tts != null) {
                        TableType ttTemplate = null;
                        for (int iTable = 0; iTable < tts.getTable()
                                                         .size(); iTable++) {
                            TableType ttTry = tts.getTable()
                                                 .get(iTable);
                            if (sName.equals(ttTry.getName()))
                                ttTemplate = ttTry;
                        }
                        if (ttTemplate != null) {
                            MetaTableImpl mti = (MetaTableImpl) table.getMetaTable();
                            mti.setTemplate(ttTemplate);
                        }
                    }
                }
            } else
                throw new IOException("Table name must be unique within schema!");
        } else
            throw new IOException("Table cannot be created!\r\n" +
                                          "SIARD archive is not open for modification of primary data!");
        return table;
    } 

} 
