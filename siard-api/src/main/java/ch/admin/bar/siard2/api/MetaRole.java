/*== MetaRole.java =====================================================
MetaRole interface provides access to role meta data.
Application : SIARD 2.0
Description : MetaRole interface provides access to role meta data.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 24.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import java.io.IOException;


/**
 * MetaRole interface provides access to role meta data.
 *
 */
public interface MetaRole
        extends MetaSearch {
    /**
     * return the global meta data instance to which these role
     * meta data belong.
     *
     * @return global meta data instance.
     */
    MetaData getParentMetaData();
  
  /*====================================================================
  role properties
  ====================================================================*/

    /**
     * get role name.
     *
     * @return role name.
     */
    String getName();

    /**
     * set name of administrator (user or role) of this role.
     * Can only be set if the SIARD archive is open for modification
     * of primary data.
     *
     * @param sAdminName name of user or role of administrator of this role.
     * @throws IOException if the value could not be set.
     */
    void setAdmin(String sAdminName)
            throws IOException;

    /**
     * get name of administrator of this role.
     *
     * @return name of administrator of this role.
     */
    String getAdmin();

    /**
     * set description of the role.
     *
     * @param sDescription description of the role.
     */
    void setDescription(String sDescription);

    /**
     * get description of the role.
     *
     * @return description of the role.
     */
    String getDescription();

} 
