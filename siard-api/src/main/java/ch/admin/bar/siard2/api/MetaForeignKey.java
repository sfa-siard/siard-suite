/*== MetaForeignKey.java ===============================================
MetaForeignKey interface provides access to foreign key meta data.
Application : SIARD 2.0
Description : MetaForeignKey interface provides access to foreign key 
              meta data. 
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 27.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.IOException;


/**
 * MetaForeignKey interface provides access to foreign key meta data.
 *
 */
public interface MetaForeignKey
        extends MetaSearch {
    /**
     * return the table meta data instance to which these candidate key
     * meta data belong.
     *
     * @return table meta data instance.
     */
    MetaTable getParentMetaTable();

    /**
     * return true, if the key has at least one reference.
     *
     * @return true if the key has at least one reference.
     */
    boolean isValid();
  
  /*====================================================================
  candidate key properties
  ====================================================================*/

    /**
     * get name of candidate key.
     *
     * @return name of candidate key.
     */
    String getName();

    /**
     * set schema referenced by foreign key.
     * Can only be set if the SIARD archive is open for modification
     * of primary data.
     *
     * @param sReferencedSchema schema referenced by foreign key.
     * @throws IOException if the value could not be set.
     */
    void setReferencedSchema(String sReferencedSchema)
            throws IOException;

    /**
     * get schema referenced by foreign key.
     *
     * @return schema referenced by foreign key.
     */
    String getReferencedSchema();

    /**
     * set table referenced by foreign key.
     * Can only be set if the SIARD archive is open for modification
     * of primary data.
     *
     * @param sReferencedTable table referenced by foreign key.
     * @throws IOException if the value could not be set.
     */
    void setReferencedTable(String sReferencedTable)
            throws IOException;

    /**
     * get table referenced by foreign key.
     *
     * @return table referenced by foreign key.
     */
    String getReferencedTable();

    /**
     * get number of references in foreign key.
     *
     * @return number of references in foreign key.
     */
    int getReferences();

    /**
     * get the column of the foreign key reference with the given index.
     *
     * @param iReference index of the foreign key reference.
     * @return column of the reference.
     */
    String getColumn(int iReference);

    /**
     * get the referenced column of the foreign key reference with the given index.
     *
     * @param iReference index of the foreign key reference.
     * @return referenced column of the reference.
     */
    String getReferenced(int iReference);

    /**
     * add a reference to the foreign key.
     * A reference can only be added if the SIARD archive is open for modification
     * of primary data.
     *
     * @param sColumn     column of the reference.
     * @param sReferenced referenced column of the reference.
     * @throws IOException if the value could not be set.
     */
    void addReference(String sColumn, String sReferenced)
            throws IOException;

    /**
     * return the column names as a comma-separated string.
     *
     * @return column names.
     */
    String getColumnsString();

    /**
     * return the referenced column names as a comma-separated string.
     *
     * @return referenced column names.
     */
    String getReferencesString();

    /**
     * set match type of foreign key.
     * Can only be set if the SIARD archive is open for modification
     * of primary data.
     *
     * @param sMatchType match type (FULL, PARTIAL, or SIMPLE).
     * @throws IOException if the value could not be set.
     */
    void setMatchType(String sMatchType)
            throws IOException;

    /**
     * get Match type of foreign key.
     *
     * @return match type of foreign key.
     */
    String getMatchType();

    /**
     * set ON DELETE action (CASCADE, SET NULL, SET DEFAULT, RESTRICT, or
     * NO ACTION) of foreign key.
     * Can only be set if the SIARD archive is open for modification
     * of primary data.
     *
     * @param sDeleteAction ON DELETE action (CASCADE, SET NULL, SET DEFAULT,
     *                      RESTRICT, or NO ACTION).
     * @throws IOException if the value could not be set.
     */
    void setDeleteAction(String sDeleteAction)
            throws IOException;

    /**
     * get ON DELETE action of foreign key.
     *
     * @return ON DELETE action of foreign key.
     */
    String getDeleteAction();

    /**
     * set ON UPDATE action (CASCADE, SET NULL, SET DEFAULT, RESTRICT, or
     * NO ACTION) of foreign key.
     * Can only be set if the SIARD archive is open for modification
     * of primary data.
     *
     * @param sUpdateAction ON UPDATE action (CASCADE, SET NULL, SET DEFAULT,
     *                      RESTRICT, or NO ACTION).
     * @throws IOException if the value could not be set.
     */
    void setUpdateAction(String sUpdateAction)
            throws IOException;

    /**
     * get ON UPDATE action of foreign key.
     *
     * @return ON UPDATE action of foreign key.
     */
    String getUpdateAction();

    /**
     * set description of the foreign key's meaning and content.
     *
     * @param sDescription description of the foreign key's meaning and content.
     */
    void setDescription(String sDescription);

    /**
     * get description of the foreign key's meaning and content.
     *
     * @return description of the foreign key's meaning and content.
     */
    String getDescription();

} 
