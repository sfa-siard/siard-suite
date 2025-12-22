/*== MetaColumn.java ===================================================
MetaColum interface provides access to column meta data.
Application : SIARD 2.0
Description : MetaColum interface provides access to column meta data. 
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 27.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.IOException;


/**
 * MetaColum interface provides access to column meta data.
 *
 */
public interface MetaColumn
        extends MetaValue {
    /**
     * return the table meta data instance to which these column
     * meta data belong or null if the belong to a view.
     *
     * @return table meta data instance.
     */
    MetaTable getParentMetaTable();

    /**
     * return the view meta data instance to which these column
     * meta data belong or null if they belong to a table.
     *
     * @return table meta data instance.
     */
    MetaView getParentMetaView();

    /**
     * return true, if column's type or type name is not null.
     *
     * @return true, if column's type or type name is not null.
     */
    boolean isValid();
  
  /*====================================================================
  column properties
  ====================================================================*/

    /**
     * set SQL:2008 predefined data type of the column.
     * Can only be set if the SIARD archive is open for modification
     * of primary data, the table is still empty.
     * If a UDT type schema or name for this column have been set they
     * are removed.
     *
     * @param sType SQL:2008 predefined data type of the column.
     * @throws IOException if the value could not be set.
     */
    void setType(String sType)
            throws IOException;

    /**
     * set SQL:2008 predefined data type of the column.
     * Can only be set if the SIARD archive is open for modification
     * of primary data, the table is still empty.
     * If a UDT type schema or name for this column have been set they
     * are removed.
     * The given data type (from java.sql.Types) is mapped to
     * an SQL:2008 predefined data type like this:
     * CHAR            "CHAR[(&lt;Precision&gt;)]"
     * VARCHAR         "VARCHAR[(&lt;Precision&gt;)]"
     * CLOB            "CLOB[(&lt;Precision&gt;)]"
     * NCHAR           "NCHAR[(&lt;Precision&gt;)]"
     * NVARCHAR        "NVARCHAR[(&lt;Precision&gt;)]"
     * NCLOB           "NCLOB[(&lt;Precision&gt;)]"
     * SQLXML          "XML"
     * BINARY          "BINARY[(&lt;Precision&gt;)]"
     * VARBINARY       "VARBINARY[(&lt;Precision&gt;)]"
     * BLOB            "BLOB[(&lt;Precision&gt;)]"
     * BOOLEAN         "BOOLEAN"
     * SMALLINT        "SMALLINT"
     * INTEGER         "INTEGER"
     * BIGINT          "BIGINT"
     * DECIMAL         "DECIMAL[(&lt;Precision&gt;[,&lt;Scale&gt;])]"
     * NUMERIC         "NUMERIC[(&lt;Precision&gt;[,&lt;Scale&gt;])]"
     * REAL            "REAL"
     * FLOAT           "FLOAT[(&lt;Precision&gt;)]"
     * DOUBLE          "DOUBLE PRECISION"
     * DATE            "DATE"
     * TIME            "TIME[(&lt;Scale&gt;)]"
     * TIMESTAMP       "TIMESTAMP[(&lt;Scale&gt;)]"
     * (not mapped)    "INTERVAL ..." must be specified using the string argument.
     * When precision or scale are less than zero, they are treated
     * as not given. Optional parts (in brackets) are dropped unless all
     * their content is given.
     *
     * @param iDataType  one of the java.sql.Types values listed above.
     * @param lPrecision length/precision of the data type.
     * @param iScale     scale of the data type.
     * @throws IOException if the value could not be set.
     */
    void setPreType(int iDataType, long lPrecision, int iScale)
            throws IOException;
    /** get SQL:2008 predefined data type of the column.
     * @return SQL:2008 predefined data type of the column or null for a
     * structured type.
     */
    /**
     * set original data type of the column.
     * Can only be set if the SIARD archive is open for modification
     * of primary data and no UDT type name for this column has been set.
     *
     * @param sTypeOriginal original data type of the column.
     * @throws IOException if the value could not be set.
     */
    void setTypeOriginal(String sTypeOriginal)
            throws IOException;

    /**
     * get original data type of the column.
     *
     * @return original data type of the column.
     */
    String getTypeOriginal();

    /**
     * set schema of UDT type for this column.
     * Can only be set if the SIARD archive is open for modification
     * of primary data, the table is still empty.
     * If a predefined type for this column has been set, it is removed.
     *
     * @param sTypeSchema schema of UDT type for this column.
     * @throws IOException if the value could not be set.
     */
    void setTypeSchema(String sTypeSchema)
            throws IOException;

    /**
     * get schema of UDT type for this column.
     *
     * @return schema of UDT type for this column.
     */
    String getTypeSchema();

    /**
     * set name of UDT type for this column.
     * Can only be set if the SIARD archive is open for modification
     * of primary data, the table is still empty.
     * If a predefined type for this column has been set, it is removed.
     *
     * @param sTypeName name of UDT type for this column.
     * @throws IOException if the value could not be set.
     */
    void setTypeName(String sTypeName)
            throws IOException;

    /**
     * get name of UDT type for this column.
     *
     * @return name of UDT type for this column.
     */
    String getTypeName();

    /**
     * set nullability of the column.
     * Can only be set if the SIARD archive is open for modification
     * of primary data, the table is still empty, and no UDT type name
     * for this column has been set.
     *
     * @param bNullable true if column can be NULL, otherwise false.
     * @throws IOException if the value could not be set.
     */
    void setNullable(boolean bNullable)
            throws IOException;

    /**
     * get nullability of the column.
     *
     * @return nullability of the column.
     */
    boolean isNullable();

    /**
     * set default value of the column.
     * Can only be set if the SIARD archive is open for modification
     * of primary data, the table is still empty, and no UDT type name
     * for this column has been set.
     *
     * @param sDefaultValue default value for this column.
     * @throws IOException if the value could not be set.
     */
    void setDefaultValue(String sDefaultValue)
            throws IOException;

    /**
     * get default value of the column.
     *
     * @return default value of the column.
     */
    String getDefaultValue();

    /**
     * set cardinality (maximum array length) of the column if it is
     * an ARRAY.
     * Can only be set if the SIARD archive is open for modification
     * of primary data, and table is empty.
     *
     * @param iCardinality cardinality of the array.
     * @throws IOException if the value could not be set.
     */
    void setCardinality(int iCardinality)
            throws IOException;

} 
