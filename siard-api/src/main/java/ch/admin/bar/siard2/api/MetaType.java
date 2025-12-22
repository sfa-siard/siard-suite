/*== MetaType.java =====================================================
MetaType interface provides access to a schema's UDT type data.
Application : SIARD 2.0
Description : MetaType interface provides access to a schema's UDT type data.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 27.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import ch.admin.bar.siard2.api.generated.CategoryType;

import java.io.IOException;


/**
 * MetaType interface provides access to a schema's UDT type data.
 *
 */
public interface MetaType
        extends MetaSearch {
    /**
     * return the associated schema instance to which these meta data belong.
     *
     * @return associated schema instance.
     */
    MetaSchema getParentMetaSchema();

    /**
     * return true, if the type meta data is valid.
     *
     * @return true, if the type meta data is valid.
     */
    boolean isValid();
  
  /*====================================================================
  type properties
  ====================================================================*/

    /**
     * get type name.
     *
     * @return type name.
     */
    String getName();

    /**
     * set category (distinct or udt) of the type.
     * Can only be set if the SIARD archive is open for modification
     * of primary data.
     * N.B.: setting the category clears base, cardinality and fields!
     *
     * @param sCategory category (distinct, row, or udt) of the type.
     * @throws IOException if the value could not be set.
     */
    void setCategory(String sCategory)
            throws IOException;

    /**
     * get category (distinct or udt) of the type.
     *
     * @return category (distinct or udt) of the type.
     */
    String getCategory();

    /**
     * get category (distinct or udt) of the type.
     *
     * @return category (distinct or udt) of the type.
     */
    CategoryType getCategoryType();


    /**
     * set schema of super type.
     * Can only be set if the SIARD archive is open for modification
     * of primary data and empty.
     *
     * @param sUnderSchema schema of super type.
     * @throws IOException if the value could not be set.
     */
    void setUnderSchema(String sUnderSchema)
            throws IOException;

    /**
     * get schema of super type.
     *
     * @return schema of super type.
     */
    String getUnderSchema();

    /**
     * set name of super type.
     * Can only be set if the SIARD archive is open for modification
     * of primary data and empty.
     *
     * @param sUnderType name of super type.
     * @throws IOException if the value could not be set.
     */
    void setUnderType(String sUnderType)
            throws IOException;

    /**
     * get name of super type.
     *
     * @return name of super type.
     */
    String getUnderType();

    /**
     * set instantiability of the type.
     * Can only be set if the SIARD archive is open for modification
     * of primary data and empty.
     *
     * @param bInstantiable true, if the type is instantiable (not "abstract").
     * @throws IOException if the value could not be set.
     */
    void setInstantiable(boolean bInstantiable)
            throws IOException;

    /**
     * get instantiability of the type.
     *
     * @return instantiability of the type.
     */
    boolean isInstantiable();

    /**
     * set finality of the type.
     * Can only be set if the SIARD archive is open for modification
     * of primary data and empty.
     *
     * @param bFinal true, if the type is "final" (no sub types allowed).
     * @throws IOException if the value could not be set.
     */
    void setFinal(boolean bFinal)
            throws IOException;

    /**
     * get finality of the type.
     *
     * @return finality of the type.
     */
    boolean isFinal();

    /**
     * set name of base type (predefined type) of DISTINCT type.
     * N.B.: more general base types are not supported by SIARD Format 2.1
     * (would need base_schema as well as base_type entries).
     * Can only be set if the SIARD archive is open for modification
     * of primary data and empty and is a DISTINCT type.
     *
     * @param sBase name of base type (predefined type) of DISTINCT type.
     * @throws IOException if the value could not be set.
     */
    void setBase(String sBase)
            throws IOException;

    /**
     * set SQL:2008 predefined base type of DISTINCT type.
     * N.B.: more general base types are not supported by SIARD Format 2.1
     * (would need base_schema as well as base_type entries).
     * Can only be set if the SIARD archive is open for modification
     * of primary data and is a DISTINCT type.
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
     * @param iBaseType  one of the java.sql.Types values listed above.
     * @param lPrecision length/precision of the base type.
     * @param iScale     scale of the base type.
     * @throws IOException if the value could not be set.
     */
    void setBasePreType(int iBaseType, long lPrecision, int iScale)
            throws IOException;

    /**
     * get name of base type (predefined type) of DISTINCT type.
     *
     * @return name of base type (predefined type) of DISTINCT type.
     */
    String getBase();

    /**
     * get predefined data type of the base of DISTINCT type
     * as a java.sql.Types integer using this mapping:
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
     * @return parsed predefined data type of the base of DISTINCT type,
     * or null otherwise.
     */
    int getBasePreType();

    /**
     * get (maximum) length/precision of the base of DISTINCT type
     * or -1 if it is not defined.
     *
     * @return (maximum) length/precision of the base of DISTINCT type.
     */
    long getBaseLength();

    /**
     * get scale of the base of DISTINCT type or -1 if it is not defined.
     *
     * @return scale of type
     */
    int getBaseScale();

    /**
     * get number of attributes of the structured type.
     *
     * @return number of attributes of the structured type.
     */
    int getMetaAttributes();

    /**
     * get attribute meta data of the structured type with the given index.
     *
     * @param iAttribute index of attribute
     * @return attribute meta data of the structured type with the given index.
     */
    MetaAttribute getMetaAttribute(int iAttribute);

    /**
     * get attribute meta data of the structured type with the given name.
     *
     * @param sName name of attribute.
     * @return attribute meta data.
     */
    MetaAttribute getMetaAttribute(String sName);

    /**
     * add attribute to the structured type.
     * N.B.: attributes can only be created for UDT types!
     * N.B.: The resulting attribute meta data is invalid, until the type
     * or the type name have been set!
     *
     * @param sName name of attribute.
     * @return attribute meta data.
     * @throws IOException if the attribute meta data could not be created.
     */
    MetaAttribute createMetaAttribute(String sName)
            throws IOException;

    /**
     * set description of the type.
     *
     * @param sDescription description of the type.
     */
    void setDescription(String sDescription);

    /**
     * get description of the type.
     *
     * @return description of the type.
     */
    String getDescription();

} 
