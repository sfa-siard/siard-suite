/*== MetaData.java =====================================================
MetaData interface provides access to global meta data.
Application : SIARD 2.0
Description : MetaData interface provides access to global meta data.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 23.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api;

import ch.admin.bar.siard2.api.generated.MessageDigestType;

import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.List;


/**
 * MetaData interface provides access to global meta data.
 *
 */
public interface MetaData
        extends MetaSearch {
    /**
     * place holder for mandatory meta data
     */
    String sPLACE_HOLDER = "(...)";

    /**
     * get archive with which this MetaData instance is associated
     * (null for template meta data).
     *
     * @return open archive associated with these meta data.
     */
    Archive getArchive();

    /**
     * set template meta data from which descriptions for matching database
     * objects are copied
     *
     * @param mdTemplate template meta data.
     * @throws IOException if an I/O error occurred.
     */
    void setTemplate(MetaData mdTemplate)
            throws IOException;
  
  /*====================================================================
  global properties
  ====================================================================*/

    /**
     * get current version of SIARD format of XML.
     * If an file of an older SIARD format is opened, the older version
     * is returned until a change in the meta data is saved, at which point
     * the meta data are saved in the current format.
     *
     * @return version of SIARD format of XML.
     */
    String getVersion();

    /**
     * set name of the archived database (must not be null or empty!).
     *
     * @param sDbName name of the archived database.
     */
    void setDbName(String sDbName);

    /**
     * get name of the archived database.
     *
     * @return name of the archived database.
     */
    String getDbName();

    /**
     * set short free form description of the database content.
     *
     * @param sDescription short free form description of the database content.
     */
    void setDescription(String sDescription);

    /**
     * get short free form description of the database content.
     *
     * @return short free form description of the database content.
     */
    String getDescription();

    /**
     * set name of person responsible for archiving the database.
     *
     * @param sArchiver name of person responsible for archiving the database.
     */
    void setArchiver(String sArchiver);

    /**
     * get name of person responsible for archiving the database.
     *
     * @return name of person responsible for archiving the database.
     */
    String getArchiver();

    /**
     * set contact data (telephone number or email address) of archiver.
     *
     * @param sArchiverContact contact data (telephone number or email address)
     *                         of archiver.
     */
    void setArchiverContact(String sArchiverContact);

    /**
     * get contact data (telephone number or email address) of archiver.
     *
     * @return contact data (telephone number or email address) of archiver.
     */
    String getArchiverContact();

    /**
     * set name of data owner (section and institution responsible for data)
     * of database when it was archived.
     *
     * @param sDataOwner name of data owner.
     */
    void setDataOwner(String sDataOwner);

    /**
     * get name of data owner (section and institution responsible for data)
     * of database when it was archived.
     *
     * @return name of data owner.
     */
    String getDataOwner();

    /**
     * set time span during which data where entered into the database.
     *
     * @param sDataOriginTimespan time span during which data where entered
     *                            into the database.
     */
    void setDataOriginTimespan(String sDataOriginTimespan);

    /**
     * get time span during which data where entered into the database.
     *
     * @return time span during which data where entered into the database.
     */
    String getDataOriginTimespan();

    /**
     * set global folder for external LOB files.
     * Can only be set if the field is not null or the SIARD archive is
     * open for modification of primary data and still empty.
     * It must not be set to null.
     * It must start with "..", "/" or "file:/". It must be terminated
     * with "/" (because it denotes a folder).
     * If the given URI starts with "file:/", it refers to a remote absolute
     * folder.
     * If it starts with "/" it refers to an absolute local folder.
     * Otherwise it is a relative URI.
     * If the global lobFolder is set, it is to be resolved relative to it.
     * Otherwise it must start with ".." which refers to the folder containing
     * the SIARD file.
     *
     * @param uriLobFolder URI for global folder for external files.
     * @throws IOException if the value could not be set.
     */
    void setLobFolder(URI uriLobFolder)
            throws IOException;

    /**
     * get global folder for external LOB files.
     *
     * @return root folder for external LOB files.
     */
    URI getLobFolder();

    /**
     * absolute global folder for external LOB files or null, if no
     * global LOB folder is set.
     *
     * @return absolute global folder for external LOB files or null, if
     * no global LOB folder is set.
     */
    URI getAbsoluteLobFolder();

    /**
     * set name and version of program that generated the metadata file.
     * Can only be set if the SIARD archive is open for modification
     * of primary data.
     *
     * @param sProducerApplication name and version of program that
     *                             generated the metadata file.
     * @throws IOException if the value could not be set.
     */
    void setProducerApplication(String sProducerApplication)
            throws IOException;

    /**
     * get name and version of program that generated the metadata file.
     *
     * @return name and version of program that generated the metadata file.
     */
    String getProducerApplication();

    /**
     * get date of creation of archive (automatically generated by SIARD).
     *
     * @return date of creation of archive (automatically generated by SIARD).
     */
    Calendar getArchivalDate();

    /**
     * get message digest codes over all primary data in folder "content".
     *
     * @return message digest codes over all primary data in folder "content".
     */
    List<MessageDigestType> getMessageDigest();

    /**
     * set DNS name of client machine from which connection to the
     * database was established for archiving.
     * Can only be set if the SIARD archive is open for modification
     * of primary data.
     *
     * @param sClientMachine DNS name of client machine from which connection
     *                       to the database was established for archiving.
     * @throws IOException if the value could not be set.
     */
    void setClientMachine(String sClientMachine)
            throws IOException;

    /**
     * get DNS name of client machine from which connection to the
     * database was established for archiving.
     *
     * @return DNS name of client machine from which connection to the
     * database was established for archiving.
     */
    String getClientMachine();

    /**
     * set name of database product and version from which database originates.
     * Can only be set if the SIARD archive is open for modification
     * of primary data.
     *
     * @param sDatabaseProduct name of database product and version from
     *                         which database originates.
     * @throws IOException if the value could not be set.
     */
    void setDatabaseProduct(String sDatabaseProduct)
            throws IOException;

    /**
     * get name of database product and version from which database originates.
     *
     * @return name of database product and version from which database originates.
     */
    String getDatabaseProduct();

    /**
     * set connection string (JDBC URL) used for archiving.
     * Can only be set if the SIARD archive is open for modification
     * of primary data.
     *
     * @param sConnection connection string (JDBC URL) used for archiving.
     * @throws IOException if the value could not be set.
     */
    void setConnection(String sConnection)
            throws IOException;

    /**
     * get connection string (JDBC URL) used for archiving.
     *
     * @return connection string (JDBC URL) used for archiving.
     */
    String getConnection();

    /**
     * set database user used for archiving
     * Can only be set if the SIARD archive is open for modification
     * of primary data.
     *
     * @param sDatabaseUser database user used for archiving.
     * @throws IOException if the value could not be set.
     */
    void setDatabaseUser(String sDatabaseUser)
            throws IOException;

    /**
     * get database user used for archiving.
     *
     * @return database user used for archiving.
     */
    String getDatabaseUser();
  
  /*====================================================================
  list properties
  ====================================================================*/

    /**
     * get number of schema meta data entries.
     *
     * @return number of schema meta data entries.
     */
    int getMetaSchemas();

    /**
     * get the schema meta data with the given index.
     *
     * @param iSchema index of schema meta data.
     * @return schema meta data.
     */
    MetaSchema getMetaSchema(int iSchema);

    /**
     * get the schema meta data with the given name.
     *
     * @param sName name of schema meta data.
     * @return schema meta data.
     */
    MetaSchema getMetaSchema(String sName);

    /**
     * get number of user meta data entries.
     *
     * @return number of user meta data entries.
     */
    int getMetaUsers();

    /**
     * get the user meta data with the given index.
     *
     * @param iUser index of user meta data.
     * @return user meta data.
     */
    MetaUser getMetaUser(int iUser);

    /**
     * get the user meta data with the given user name.
     *
     * @param sName user name.
     * @return user meta data.
     */
    MetaUser getMetaUser(String sName);

    /**
     * add new user to meta data.
     * A new user can only be created if the SIARD archive is open for
     * modification of primary data.
     *
     * @param sName user name of the new user meta data.
     * @return user meta data.
     * @throws IOException if new user could be created.
     */
    MetaUser createMetaUser(String sName)
            throws IOException;

    /**
     * get number of role meta data entries.
     *
     * @return number of role meta data entries.
     */
    int getMetaRoles();

    /**
     * get the role meta data with the given index.
     *
     * @param iRole index of role meta data.
     * @return role meta data.
     */
    MetaRole getMetaRole(int iRole);

    /**
     * get the role meta data with the given role name.
     *
     * @param sName role name.
     * @return role meta data.
     */
    MetaRole getMetaRole(String sName);

    /**
     * add new role meta data.
     * A new role can only be created if the SIARD archive is open for
     * modification of primary data.
     *
     * @param sName  role name of the new role meta data.
     * @param sAdmin name of administrator (user or role) of the new role.
     * @return role meta data.
     * @throws IOException if new user could be created.
     */
    MetaRole createMetaRole(String sName, String sAdmin)
            throws IOException;

    /**
     * get number of privilege meta data entries.
     *
     * @return number of privilege meta data entries.
     */
    int getMetaPrivileges();

    /**
     * get the privilege meta data with the given index.
     *
     * @param iPrivilege index of privilege meta data.
     * @return privilege meta data.
     */
    MetaPrivilege getMetaPrivilege(int iPrivilege);

    /**
     * get the privilege meta data with the given content.
     *
     * @param sType    type of privilege including ROLE privilege or ALL PRIVILEGES.
     * @param sObject  object of privilege (or null for ROLE privilege).
     * @param sGrantor name of grantor of privilege (user or role).
     * @param sGrantee name od grantee of privilege (user or role).
     * @return privilege meta data.
     */
    MetaPrivilege getMetaPrivilege(String sType, String sObject, String sGrantor, String sGrantee);

    /**
     * add new privilege meta data.
     * A new privilege can only be created if the SIARD archive is open for
     * modification of primary data.
     *
     * @param sType    type of privilege including ROLE privilege or ALL PRIVILEGES.
     * @param sObject  privilege object (may be null for ROLE privilege)
     * @param sGrantor name of grantor (user or role).
     * @param sGrantee name of grantee (user or role).
     * @return privilge meta data.
     * @throws IOException if new user could be created.
     */
    MetaPrivilege createMetaPrivilege(String sType, String sObject, String sGrantor, String sGrantee)
            throws IOException;

  /*====================================================================
  methods
  ====================================================================*/

    /**
     * checks whether this is the meta data instance of a valid archive,
     * i.e. whether it contains at least one table containing at least one
     * record of primary data.
     *
     * @return true, if instance is valid.
     */
    boolean isValid();

} 
