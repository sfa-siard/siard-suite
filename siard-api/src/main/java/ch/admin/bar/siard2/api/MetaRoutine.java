/*== MetaRoutine.java ==================================================
MetaRoutine interface provides access to routine meta data.
Application : SIARD 2.0
Description : MetaRoutine interface provides access to routine meta data.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 29.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.IOException;


/**
 * MetaRoutine interface provides access to routine meta data.
 *
 */
public interface MetaRoutine
        extends MetaSearch {
    /**
     * return the associated schema meta data to which these meta data belong.
     *
     * @return associated schema meta data.
     */
    MetaSchema getParentMetaSchema();

    /**
     * return true, if the routine meta data is valid.
     *
     * @return true, if the routine meta data is valid.
     */
    boolean isValid();
  
  /*====================================================================
  routine properties
  ====================================================================*/
    

    /**
     * get specific routine name which is unique within its schema.
     *
     * @return routine name.
     */
    String getSpecificName();

    

    /**
     * set the routine name to a value possibly different from the
     * specific name.
     *
     * @param sName possibly overloaded routine name.
     */
    void setName(String sName);

    /**
     * get (possibly overloaded) routine name.
     *
     * @return routine name.
     */
    String getName();

    

    /**
     * set the SQL:2008 (ISO 9075) routine body.
     *
     * @param sBody the SQL:2008 (ISO 9075) routine body.
     */
    void setBody(String sBody);

    /**
     * get the SQL:2008 (ISO 9075) routine body.
     *
     * @return the SQL:2008 (ISO 9075) routine body.
     */
    String getBody();

    

    /**
     * set the original source code (VBA, PL/SQL, ...) defining the routine
     * The source can only be set if the SIARD archive
     * is open for modification of primary data.
     *
     * @param sSource the original source code (VBA, PL/SQL, ...) defining the routine.
     * @throws IOException if the source cannot be set.
     */
    void setSource(String sSource)
            throws IOException;

    /**
     * get the original source code (VBA, PL/SQL, ...) defining the routine.
     *
     * @return the original source code (VBA, PL/SQL, ...) defining the routine.
     */
    String getSource();

    

    /**
     * set description of the routine.
     *
     * @param sDescription description of the routine.
     */
    void setDescription(String sDescription);

    /**
     * get description of the routine.
     *
     * @return description of the routine.
     */
    String getDescription();

    

    /**
     * set the routine characteristic.
     * The characteristic can only be set if the SIARD archive
     * is open for modification of primary data.
     *
     * @param sCharacteristic the routine characteristic.
     * @throws IOException if the characteristic cannot be set.
     */
    void setCharacteristic(String sCharacteristic)
            throws IOException;

    /**
     * get the routine characteristic.
     *
     * @return the routine characteristic.
     */
    String getCharacteristic();

    

    /**
     * set the SQL:2008 data type of the return value (for functions)
     * The return type can only be set if the SIARD archive
     * is open for modification of primary data.
     *
     * @param sReturnType the SQL:2008 data type of the return value (for functions).
     * @throws IOException if the return type cannot be set.
     */
    void setReturnType(String sReturnType)
            throws IOException;

    /**
     * set SQL:2008 predefined return type.
     * The return type can only be set if the SIARD archive
     * is open for modification of primary data.
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
     * @param iReturnType one of the java.sql.Types values listed above.
     * @param lPrecision  length/precision of the base type.
     * @param iScale      scale of the base type.
     * @throws IOException if the value could not be set.
     */
    void setReturnPreType(int iReturnType, long lPrecision, int iScale)
            throws IOException;

    /**
     * get the SQL:2008 data type of the return value (for functions)
     *
     * @return the SQL:2008 data type of the return value (for functions)
     */
    String getReturnType();
  
  /*====================================================================
  list properties
  ====================================================================*/

    /**
     * get number of parameter meta data entries.
     *
     * @return number of parameter meta data entries.
     */
    int getMetaParameters();

    /**
     * get the parameter meta data with the given index.
     *
     * @param iParameter index of parameter meta data.
     * @return parameter meta data.
     */
    MetaParameter getMetaParameter(int iParameter);

    /**
     * get the parameter meta data with the given name.
     *
     * @param sName name of parameter.
     * @return parameter meta data.
     */
    MetaParameter getMetaParameter(String sName);

    /**
     * add new parameter to table meta data.
     * A new parameter can only be created if the SIARD archive is open for
     * modification of primary data.
     * N.B.: The resulting parameter meta data is invalid, until the type
     * or the type name have been set!
     *
     * @param sName name of new parameter.
     * @return parameter meta data.
     * @throws IOException if parameter meta data could not be created.
     */
    MetaParameter createMetaParameter(String sName)
            throws IOException;

} 
