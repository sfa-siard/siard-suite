/*== MetaField.java ====================================================
MetaField interface provides access to field meta data.
Application : SIARD 2.0
Description : MetaField interface provides access to field meta data. 
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 28.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.IOException;


/**
 * MetaField interface provides access to field meta data.
 *
 */
public interface MetaField
        extends MetaValue {
    /**
     * return the parent column meta data to which these meta data belong.
     *
     * @return parent column meta data, or null, if parent is a field.
     */
    MetaColumn getParentMetaColumn();

    /**
     * return the parent field meta data to which these meta data belong.
     *
     * @return parent field meta data, or null, if parent is a column.
     */
    MetaField getParentMetaField();

    /**
     * return the associated attribute.
     *
     * @return associated attribute or null for DISTINCT or ARRAY types.
     * @throws IOException if an I/O error occurred.
     */
    MetaAttribute getMetaAttribute()
            throws IOException;
  
  /*====================================================================
  field properties
  ====================================================================*/
} 
