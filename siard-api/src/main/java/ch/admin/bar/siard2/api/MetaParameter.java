/*== MetaParameter.java ================================================
MetaParameter interface provides access to parameter meta data.
Application : SIARD 2.0
Description : MetaParameter interface provides access to parameter meta data. 
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 29.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.IOException;


/**
 * MetaParameter interface provides access to parameter meta data.
 *
 */
public interface MetaParameter
        extends MetaSearch {
    /**
     * return the routine  meta data instance to which these parameter
     * meta data belong.
     *
     * @return routine meta data instance.
     */
    MetaRoutine getParentMetaRoutine();

    /**
     * return true, if attribute's type or type name is not null.
     *
     * @return true, if attribute's type or type name is not null.
     */
    boolean isValid();
  
  /*====================================================================
  parameter properties
  ====================================================================*/

    /**
     * get name of parameter.
     *
     * @return name of parameter.
     */
    String getName();

    /**
     * get position of parameter in routine (1-based!).
     *
     * @return position of parameter in routine.
     */
    int getPosition();

    /**
     * set mode (IN, OUT, INOUT) of parameter.
     * Can only be set if the SIARD archive is open for modification
     * of primary data.
     *
     * @param sMode parameter mode (IN, OUT, INOUT).
     * @throws IOException if the value could not be set.
     */
    void setMode(String sMode)
            throws IOException;

    /**
     * get mode (IN, OUT, INOUT) of parameter.
     *
     * @return mode (IN, OUT, INOUT) of parameter.
     */
    String getMode();

    /**
     * set SQL:2008 predefined data type of the parameter.
     * Can only be set if the SIARD archive is open for modification
     * of primary data.
     * If a UDT type schema or name for this parameter have been set they
     * are removed.
     *
     * @param sType SQL:2008 predefined data type of the parameter.
     * @throws IOException if the value could not be set.
     */
    void setType(String sType)
            throws IOException;

    /**
     * set SQL:2008 predefined data type of the parameter.
     * Can only be set if the SIARD archive is open for modification
     * of primary data.
     * If a UDT type schema or name for this parameter have been set they
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

    /**
     * get SQL:2008 predefined data type of the parameter.
     *
     * @return SQL:2008 predefined data type of the parameter.
     */
    String getType();

    /**
     * get predefined data type of the parameter as a java.sql.Types integer.
     * using this mapping:
     * null                               NULL
     * "CHAR[(&lt;Length&gt;)]"                 CHAR
     * "VARCHAR[(&lt;Length&gt;)]"              VARCHAR
     * "CLOB[(&lt;LOB Length&gt;)]"              CLOB
     * "NCHAR[(&lt;Length&gt;)]"                NCHAR
     * "NVARCHAR[(&lt;Length&gt;)]"             NVARCHAR
     * "NCLOB[(&lt;LOB Length&gt;)]"             NCLOB
     * "XML"                              SQLXML
     * "BINARY[(&lt;Length&gt;)]"               BINARY
     * "VARBINARY[(&lt;Length&gt;)]"            VARBINARY
     * "BLOB[(&lt;LOB Length&gt;)]"             BLOB
     * "BOOLEAN"                          BOOLEAN
     * "SMALLINT"                         SMALLINT
     * "INTEGER"                          INTEGER
     * "BIGINT"                           BIGINT
     * "DECIMAL[(&lt;Precision&gt;[,&lt;Scale&gt;])]" DECIMAL
     * "NUMERIC[(&lt;Precision&gt;[,&lt;Scale&gt;])]" NUMERIC
     * "REAL"                             REAL
     * "FLOAT[(&lt;Precision&gt;)]"             FLOAT
     * "DOUBLE PRECISION"                 DOUBLE
     * "DATE"                             DATE
     * "TIME[(&lt;Scale&gt;)]"                  TIME
     * "TIMESTAMP[(&lt;Scale&gt;)]"             TIMESTAMP
     * "INTERVAL ..."                     OTHER
     *
     * @return parsed predefined data type of the parameter,
     * or its base type (DISTINCT and ARRAY), or null otherwise.
     */
    int getPreType();

    /**
     * set original data type of the parameter.
     * Can only be set if the SIARD archive is open for modification
     * of primary data and no UDT type name for this parameter has been set.
     *
     * @param sTypeOriginal original data type of the parameter.
     * @throws IOException if the value could not be set.
     */
    void setTypeOriginal(String sTypeOriginal)
            throws IOException;

    /**
     * get original data type of the parameter.
     *
     * @return original data type of the parameter.
     */
    String getTypeOriginal();

    /**
     * set schema of UDT type for this parameter.
     * Can only be set if the SIARD archive is open for modification
     * of primary data.
     * If a predefined type for this parameter has been set, it is removed.
     *
     * @param sTypeSchema schema of UDT type for this parameter.
     * @throws IOException if the value could not be set.
     */
    void setTypeSchema(String sTypeSchema)
            throws IOException;

    /**
     * get schema of UDT type for this parameter.
     *
     * @return schema of UDT type for this parameter.
     */
    String getTypeSchema();

    /**
     * set name of UDT type for this parameter.
     * Can only be set if the SIARD archive is open for modification
     * of primary data.
     * If a predefined type for this parameter has been set, it is removed.
     *
     * @param sTypeName name of UDT type for this parameter.
     * @throws IOException if the value could not be set.
     */
    void setTypeName(String sTypeName)
            throws IOException;

    /**
     * get name of UDT type for this parameter.
     *
     * @return name of UDT type for this parameter.
     */
    String getTypeName();

    /**
     * get the type meta data for this parameter or null if its type name
     * is not set.
     *
     * @return type meta data for this parameter.
     */
    MetaType getMetaType();

    /**
     * set cardinality (maximum array length) of the parameter if it is
     * an ARRAY.
     * Can only be set if the SIARD archive is open for modification
     * of primary data.
     *
     * @param iCardinality cardinality of the array.
     * @throws IOException if the value could not be set.
     */
    void setCardinality(int iCardinality)
            throws IOException;

    /**
     * get cardinality or -1 if the parameter is not an ARRAY.
     *
     * @return cardinality of ARRAY or -1.
     */
    int getCardinality();

    /**
     * set description of the parameter's meaning and content.
     *
     * @param sDescription description of the parameter's meaning and content.
     */
    void setDescription(String sDescription);

    /**
     * get description of the parameter's meaning and content.
     *
     * @return description of the parameter's meaning and content.
     */
    String getDescription();

} 
