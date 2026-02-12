/*== MetaValue.java ====================================================
MetaValue interface declares meta data common to columns and fields.
Application : SIARD 2.0
Description : MetaValue interface declares meta data common to columns 
              and fields ("values"). 
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 01.10.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.IOException;
import java.net.URI;
import java.util.List;


/**
 * MetaValue interface declares meta data common to columns and fields
 * ("values").
 *
 */
public interface MetaValue extends MetaSearch {
    /**
     * return the ancestor column meta data with which this field meta data
     * is ultimately associated.
     *
     * @return ancestor meta column.
     */
    MetaColumn getAncestorMetaColumn();

    /**
     * get name of value.
     * The name of a simple predefined value is the name of the column
     * or of the corresponding attribute.
     * The name of an array element is the name of the parent array value
     * followed by the element position (1-based) in brackets.
     * The name of a field of a ROW or UDT value is the name of the
     * corresponding attribute of the type.
     *
     * @return name of field.
     */
    String getName();

    /**
     * get position of field  in parent value (1-based!).
     *
     * @return position of field in parent value.
     */
    int getPosition();

    /**
     * set folder for external LOB files for this value.
     * If the LOB folder is not yet set, it can only be set if the database
     * is open for modification and the table is still empty.
     * If the LOB folder is set, then it must not be set to
     * null.
     * If the given URI starts with "file:/", it refers to a remote absolute
     * folder.
     * If it starts with "/" it refers to an absolute local folder.
     * Otherwise it is a relative URI.
     * If the global lobFolder is set, it is to be resolved relative to it.
     * Otherwise it must start with ".." which refers to the folder containing
     * the SIARD file.
     *
     * @param uriLobFolder local URI for external LOB folder for this value.
     * @throws IOException if the value could not be set.
     */
    void setLobFolder(URI uriLobFolder)
            throws IOException;

    /**
     * folder for external LOB files for this value.
     *
     * @return root folder for external LOB files.
     */
    URI getLobFolder();

    /**
     * absolute folder for external LOB files (after resolving it with
     * global URI if it is available).
     *
     * @return absolute folder for external LOB files for this column first
     * resolving it with the global URI if it is available.
     */
    URI getAbsoluteLobFolder();

    /**
     * get SQL:2008 predefined data type of the value.
     *
     * @return SQL:2008 predefined data type of the value, or null if
     * this field does not have a predefined type.
     * @throws IOException if an I/O error occurred.
     */
    String getType()
            throws IOException;

    /**
     * get predefined data type of the value as a java.sql.Types integer
     * using this mapping:
     * null                               NULL
     * "CHAR[(&lt;Length&gt;)]"                 CHAR
     * "VARCHAR[(&lt;Length&gt;)]"              VARCHAR
     * "CLOB[(&lt;LOB Length&gt;)]"             CLOB
     * "NCHAR[(&lt;Length&gt;)]"                NCHAR
     * "NVARCHAR[(&lt;Length&gt;)]"             NVARCHAR
     * "NCLOB[(&lt;LOB Length&gt;)]"            NCLOB
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
     * @return parsed predefined data type of the field,
     * or its base type (DISTINCT), or Types.NULL otherwise.
     * @throws IOException if an I/O error occurred.
     */
    int getPreType() throws IOException;

    /**
     * get original data type of the column.
     *
     * @return original data type of the column.
     * @throws IOException if an I/O error occurred.
     */
    String getTypeOriginal()
            throws IOException;

    /**
     * get schema of UDT type for this column.
     *
     * @return schema of UDT type for this column.
     * @throws IOException if an I/O error occurred.
     */
    String getTypeSchema()
            throws IOException;

    /**
     * get name of UDT type for this column.
     *
     * @return name of UDT type for this column.
     * @throws IOException if an I/O error occurred.
     */
    String getTypeName()
            throws IOException;

    /**
     * get (maximum) length of type or -1 if it is not defined.
     *
     * @return (maximum) length of type.
     * @throws IOException if an I/O error occurred.
     */
    long getLength() throws IOException;

    /**
     * get scale of type or -1 if it is not defined.
     *
     * @return scale of type
     * @throws IOException if an I/O error occurred.
     */
    int getScale() throws IOException;

    /**
     * get (maximum) length of string (in characters) or byte
     * array (in bytes) value, or Long.MAX_VALUE if it is not defined.
     * N.B.: Difference to getLength():
     * - getLength(CHAR) = -1,
     * - getMaxLength(CHAR) = 1
     *
     * @return (maximum) length of value.
     * @throws IOException if an I/O error occurred.
     */
    long getMaxLength() throws IOException;

    /**
     * get the type meta data for this value or null if its type name
     * is not set.
     *
     * @return type meta data for this value.
     * @throws IOException if an I/O error occurred.
     */
    MetaType getMetaType()
            throws IOException;

    /**
     * set MIME type of BLOBs in the value.
     * Can only be set if the SIARD archive is open for modification
     * of primary data, the table is still empty, and the type of this
     * value is a BLOB.
     *
     * @param sMimeType MIME type of BLOBs in the value.
     * @throws IOException if the value could not be set.
     */
    void setMimeType(String sMimeType)
            throws IOException;

    /**
     * get MIME type of BLOBs in the column.
     *
     * @return MIME type of BLOBs in the column.
     */
    String getMimeType();

    /**
     * get number of array elements of this array value or the number of
     * fields of this values's ROW or UDT.
     * N.B.: The fields of a value must match the attributes of the ROW or UDT.
     *
     * @return number of array elements of this array value or the number of
     * fields of this value.
     * @throws IOException if default field meta data could not be created.
     */
    int getMetaFields()
            throws IOException;

    /**
     * get meta data of array element of this array value or the field
     * of this value's ROW or UDT with the given index.
     * N.B.: The fields of a column or field must match the attributes of
     * the ROW or UDT.
     *
     * @param iField index of the field.
     * @return meta data of array element of this array value or the field of
     * this value's ROW or UDT with the given index.
     * @throws IOException if default field meta data could not be created.
     */
    MetaField getMetaField(int iField)
            throws IOException;

    /**
     * get meta data of array element of this array value or the field
     * of this value's ROW or UDT associated with the given attribute name.
     * N.B.: The fields of a column or field must match the attributes of
     * the ROW or UDT.
     *
     * @param sName name of associated attribute.
     * @return meta data of array element of this array value or the field of
     * this value's ROW or UDT with the given attribute name.
     * @throws IOException if default field meta data could not be created.
     */
    MetaField getMetaField(String sName)
            throws IOException;

    /**
     * add meta data of array element of this array value ir the field
     * of this value's ROW or UDT associated with the given attribute name.
     * N.B.: The fields of a column or field must match the attributes of
     * the ROW or UDT.
     * N.B.: The resulting field meta data is invalid, until the type
     * or the type name have been set!
     *
     * @return field meta data.
     * @throws IOException if the field meta data could not be created.
     */
    MetaField createMetaField()
            throws IOException;

    /**
     * return the cardinality (maximum length) if this value is an array.
     *
     * @return cardinality or -1 if it is not an array.
     * @throws IOException if an I/O error occurred.
     */
    int getCardinality()
            throws IOException;

    /**
     * set description of the value's meaning and content.
     *
     * @param sDescription description of the value's meaning and content.
     */
    void setDescription(String sDescription);

    /**
     * get description of the value's meaning and content.
     *
     * @return description of the value's meaning and content.
     */
    String getDescription();

    /**
     * return a list of "flattened" field names contained in this value,
     * each given as a list of field names.
     *
     * @param bSupportsArrays list is for database system which supports arrays.
     * @param bSupportsUdts   list is for database system which supports UDT types.
     * @return list of "flattened" field names.
     * @throws IOException if an I/O error occurred.
     */
    List<List<String>> getNames(
            boolean bSupportsArrays, boolean bSupportsUdts)
            throws IOException;

    /**
     * retrieve the (predefined) type for this "flattened" field name.
     *
     * @param listNames field name list indicating the sub field for which
     *                  the type is to be retrieved.
     * @return type type of this field if the field name list is empty or
     * of sub field given by the list.
     * @throws IOException if an I/O error occurred.
     */
    String getType(List<String> listNames)
            throws IOException;

} 
