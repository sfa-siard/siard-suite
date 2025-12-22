/*== MetaTable.java ====================================================
MetaTable interface provides access to table meta data.
Application : SIARD 2.0
Description : MetaTable interface provides access to table meta data.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 24.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.IOException;
import java.util.List;


/**
 * MetaTable interface provides access to table meta data.
 *
 */
public interface MetaTable
        extends MetaSearch {
    /**
     * return the schema meta data.
     *
     * @return schema meta data.
     */
    MetaSchema getParentMetaSchema();

    /**
     * return the associated table instance to which these meta data belong.
     *
     * @return associated table instance.
     */
    Table getTable();

    /**
     * return true, if the table meta data is valid.
     *
     * @return true, if the table meta data is valid.
     */
    boolean isValid();
  
  /*====================================================================
  table properties
  ====================================================================*/
    

    /**
     * get table name.
     *
     * @return table name.
     */
    String getName();

    

    /**
     * get table folder.
     *
     * @return table folder.
     */
    String getFolder();

    

    /**
     * set description of the table.
     *
     * @param sDescription description of the table.
     */
    void setDescription(String sDescription);

    /**
     * get description of the table.
     *
     * @return description of the table.
     */
    String getDescription();

    

    /**
     * set number of rows of the table.
     *
     * @param lRows number of rows of the table.
     * @throws IOException if an I/O error occurred.
     */
    void setRows(long lRows)
            throws IOException;

    /**
     * get the number of rows of the table.
     *
     * @return number of rows of the table.
     */
    long getRows();
  
  /*====================================================================
  list properties
  ====================================================================*/

    /**
     * get number of column meta data entries.
     * @deprecated use MetaTableFacade.getMetaColums() instead
     * @return number of column meta data entries.
     *
     */
    @Deprecated
    int getMetaColumns();

    /**
     * get the column meta data with the given index.
     *
     * @deprecated use MetaTableFacade.getMetaColums() instead
     * @param iColumn index of column meta data.
     * @return column meta data.
     */
    @Deprecated
    MetaColumn getMetaColumn(int iColumn);

    /**
     * get the column meta data with the given name.
     *
     * @deprecated use MetaTableFacade.getMetaColums() instead
     * @param sName name of column.
     * @return column meta data.
     */
    @Deprecated
    MetaColumn getMetaColumn(String sName);

    /**
     * add new column to table meta data.
     * A new table can only be created if the SIARD archive is open for
     * modification of primary data and the table is empty.
     *
     * @param sName name of new column.
     * @return column meta data.
     * @throws IOException if column meta data could not be created.
     */
    MetaColumn createMetaColumn(String sName)
            throws IOException;

    /**
     * get meta data entry of primary key of the table.
     *
     * @return meta data of primary key of null, if no primary key has been created.
     */
    MetaUniqueKey getMetaPrimaryKey();

    /**
     * create primary key meta data of table.
     * A new primary key can only be created if the SIARD archive is open for
     * modification of primary data.
     *
     * @param sName name of new primary key.
     * @return primary key meta data.
     * @throws IOException if primary key meta data could not be created.
     */
    MetaUniqueKey createMetaPrimaryKey(String sName)
            throws IOException;

    /**
     * get number of foreign key meta data entries.
     *
     * @return number of foreign key meta data entries.
     */
    int getMetaForeignKeys();

    /**
     * get the foreign key meta data with the given index.
     *
     * @param iForeignKey index of foreign key meta data.
     * @return foreign key meta data.
     */
    MetaForeignKey getMetaForeignKey(int iForeignKey);

    /**
     * get the foreign key meta data with the given name.
     *
     * @param sName name of foreign key.
     * @return foreign key meta data.
     */
    MetaForeignKey getMetaForeignKey(String sName);

    /**
     * add new foreign key to meta data of table.
     * A new foreign key can only be created if the SIARD archive is open for
     * modification of primary data.
     *
     * @param sName name of foreign key.
     * @return foreign key meta data.
     * @throws IOException if foreign key meta data could not be created.
     */
    MetaForeignKey createMetaForeignKey(String sName)
            throws IOException;

    /**
     * get number of candidate key meta data entries.
     *
     * @return number of candidate key meta data entries.
     */
    int getMetaCandidateKeys();

    /**
     * get the candidate key meta data with the given index.
     *
     * @param iCandidateKey index of candidate key meta data.
     * @return candidate key meta data.
     */
    MetaUniqueKey getMetaCandidateKey(int iCandidateKey);

    /**
     * get the candidate key meta data with the given name.
     *
     * @param sName name of candidate key.
     * @return candidate key meta data.
     */
    MetaUniqueKey getMetaCandidateKey(String sName);

    /**
     * add new candidate key to meta data of table.
     * A new candidate key can only be created if the SIARD archive is open for
     * modification of primary data.
     *
     * @param sName name of new candidate key.
     * @return candidate key meta data.
     * @throws IOException if candidate key meta data could not be created.
     */
    MetaUniqueKey createMetaCandidateKey(String sName)
            throws IOException;

    /**
     * get number of check constraint meta data entries.
     *
     * @return number of check constraint meta data entries.
     */
    int getMetaCheckConstraints();

    /**
     * get the check constraint meta data with the given index.
     *
     * @param iCheckConstraint index of check constraint meta data.
     * @return check constraint meta data.
     */
    MetaCheckConstraint getMetaCheckConstraint(int iCheckConstraint);

    /**
     * get the check constraint meta data with the given name.
     *
     * @param sName name of check constraint.
     * @return check constraint meta data.
     */
    MetaCheckConstraint getMetaCheckConstraint(String sName);

    /**
     * add new check constraint to meta data of table.
     * A new check constraint can only be created if the SIARD archive is open for
     * modification of primary data.
     *
     * @param sName name of new check constraint.
     * @return check constraint meta data.
     * @throws IOException if check constraint meta data could not be created.
     */
    MetaCheckConstraint createMetaCheckConstraint(String sName)
            throws IOException;

    /**
     * get number of trigger meta data entries.
     *
     * @return number of trigger meta data entries.
     */
    int getMetaTriggers();

    /**
     * get the trigger meta data with the given index.
     *
     * @param iTrigger index of trigger meta data.
     * @return trigger meta data.
     */
    MetaTrigger getMetaTrigger(int iTrigger);

    /**
     * get the trigger meta data with the given name.
     *
     * @param sName name of trigger.
     * @return trigger meta data.
     */
    MetaTrigger getMetaTrigger(String sName);

    /**
     * add new trigger to meta data of table.
     * A new trigger can only be created if the SIARD archive is open for
     * modification of primary data.
     *
     * @param sName name of new trigger.
     * @return trigger meta data.
     * @throws IOException if trigger meta data could not be created.
     */
    MetaTrigger createMetaTrigger(String sName)
            throws IOException;

    /**
     * return a list of "flattened" column names contained in this table,
     * each given as a list of column and field names.
     *
     * @param bSupportsArrays list is for database system which supports arrays.
     * @param bSupportsUdts   list is for database system which supports UDT types.
     * @return list of "flattened" column names.
     * @throws IOException if an I/O error occurred.
     */
    List<List<String>> getColumnNames(
            boolean bSupportsArrays, boolean bSupportsUdts)
            throws IOException;

    /**
     * retrieve the (predefined) type for this "flattened" column name.
     *
     * @param listNames column/field name list indicating the column or sub
     *                  field for which the type is to be retrieved.
     * @return type type for this "flattened" column name.
     * @throws IOException if an I/O error occurred.
     */
    String getType(List<String> listNames)
            throws IOException;

} 
