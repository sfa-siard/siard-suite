/*== MetaDataImpl.java =================================================
MetaDataImpl implements the interface MetaData.
Application : SIARD 2.0
Description : MetaDataImpl implements the interface MetaData.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 23.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.meta;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.*;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.DU;
import ch.enterag.utils.SU;
import ch.enterag.utils.TZ;
import ch.enterag.utils.xml.XU;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


/**
 * MetaDataImpl implements the interface MetaData.
 *
 */
public class MetaDataImpl
        extends MetaSearchImpl
        implements MetaData {
    public static final String _sURI_SCHEME_FILE = "file";
    private static final DU _du = DU.getInstance("en", "yyyy-MM-dd");
    private static final ObjectFactory _of = new ObjectFactory();
    private final Map<String, MetaUser> _mapMetaUsers = new HashMap<String, MetaUser>();
    private final Map<String, MetaRole> _mapMetaRoles = new HashMap<String, MetaRole>();
    private final List<MetaPrivilege> _listMetaPrivileges = new ArrayList<MetaPrivilege>();

    private Archive _archive = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public Archive getArchive() {
        return _archive;
    }

    private ArchiveImpl getArchiveImpl() {
        return (ArchiveImpl) getArchive();
    }

    private SiardArchive _sa = null;

    public SiardArchive getSiardArchive()
            throws IOException {
        for (int iSchema = 0; iSchema < getMetaSchemas(); iSchema++) {
            MetaSchema ms = getMetaSchema(iSchema);
            if (ms != null)
                ((MetaSchemaImpl) ms).getSchemaType();
        }
        return _sa;
    } 

    private SiardArchive _saTemplate = null;

    public SiardArchive getTemplate() {
        return _saTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTemplate(MetaData md)
            throws IOException {
        if (md != null)
            setTemplate(((MetaDataImpl) md).getSiardArchive());
    } 

    /**
     * set template meta data from which descriptions for matching database
     * objects are copied
     *
     * @param saTemplate template meta data.
     * @throws IOException if an I/O error occurred.
     */
    public void setTemplate(SiardArchive saTemplate)
            throws IOException {
        _saTemplate = saTemplate;
        if ((!SU.isNotEmpty(getDbName())) || (getDbName().equals(sPLACE_HOLDER)))
            setDbName(XU.fromXml(_saTemplate.getDbname()));
        if (!SU.isNotEmpty(getDescription()))
            setDescription(XU.fromXml(_saTemplate.getDescription()));
        if (!SU.isNotEmpty(getArchiver()))
            setArchiver(XU.fromXml(_saTemplate.getArchiver()));
        if (!SU.isNotEmpty(getArchiverContact()))
            setArchiverContact(XU.fromXml(_saTemplate.getArchiverContact()));
        if ((!SU.isNotEmpty(getDataOwner())) || (getDataOwner().equals(sPLACE_HOLDER)))
            setDataOwner(XU.fromXml(_saTemplate.getDataOwner()));
        if ((!SU.isNotEmpty(getDataOriginTimespan())) || (getDataOriginTimespan().equals(sPLACE_HOLDER)))
            setDataOriginTimespan(XU.fromXml(_saTemplate.getDataOriginTimespan()));
        if ((getLobFolder() == null) && (SU.isNotEmpty(_saTemplate.getLobFolder())))
            setLobFolder(URI.create(XU.fromXml(_saTemplate.getLobFolder())));
        SchemasType sts = _saTemplate.getSchemas();
        if (sts != null) {
            for (int iSchema = 0; iSchema < sts.getSchema()
                                               .size(); iSchema++) {
                SchemaType stTemplate = sts.getSchema()
                                           .get(iSchema);
                String sName = XU.fromXml(stTemplate.getName());
                MetaSchema ms = getMetaSchema(sName);
                if (ms != null) {
                    MetaSchemaImpl msi = (MetaSchemaImpl) ms;
                    msi.setTemplate(stTemplate);
                }
            }
        }
        UsersType uts = _saTemplate.getUsers();
        if (uts != null) {
            for (int iUser = 0; iUser < uts.getUser()
                                           .size(); iUser++) {
                UserType utTemplate = uts.getUser()
                                         .get(iUser);
                String sName = XU.fromXml(utTemplate.getName());
                MetaUser mu = getMetaUser(sName);
                if (mu != null) {
                    MetaUserImpl mui = (MetaUserImpl) mu;
                    mui.setTemplate(utTemplate);
                }
            }
        }
        RolesType rts = _saTemplate.getRoles();
        if (rts != null) {
            for (int iRole = 0; iRole < rts.getRole()
                                           .size(); iRole++) {
                RoleType rtTemplate = rts.getRole()
                                         .get(iRole);
                String sName = XU.fromXml(rtTemplate.getName());
                MetaRole mr = getMetaRole(sName);
                if (mr != null) {
                    MetaRoleImpl mri = (MetaRoleImpl) mr;
                    mri.setTemplate(rtTemplate);
                }
            }
        }
        PrivilegesType pts = _saTemplate.getPrivileges();
        if (pts != null) {
            for (int iPrivilege = 0; iPrivilege < pts.getPrivilege()
                                                     .size(); iPrivilege++) {
                PrivilegeType ptTemplate = pts.getPrivilege()
                                              .get(iPrivilege);
                String sType = XU.fromXml(ptTemplate.getType());
                String sObject = XU.fromXml(ptTemplate.getObject());
                String sGrantor = XU.fromXml(ptTemplate.getGrantor());
                String sGrantee = XU.fromXml(ptTemplate.getGrantee());
                MetaPrivilege mp = getMetaPrivilege(sType, sObject, sGrantor, sGrantee);
                if (mp != null) {
                    MetaPrivilegeImpl mpi = (MetaPrivilegeImpl) mp;
                    mpi.setTemplate(ptTemplate);
                }
            }
        }
    } 

    /**
     * create an empty SiardArchive instance.
     *
     * @return new empty SiardArchive instance.
     */
    public static SiardArchive createSiardArchive() {
        SiardArchive sa = _of.createSiardArchive();
        sa.setVersion(XU.toXml(Archive.sMETA_DATA_VERSION));
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeZone(TZ.getUtcTimeZone());
        sa.setArchivalDate(_du.toXmlGregorianCalendar(gc));
        sa.setSchemas(_of.createSchemasType());
        sa.setUsers(_of.createUsersType());
        return sa;
    } 

    /**
     * constructor
     *
     * @param archive SIARD archive associated with this meta data instance.
     * @param sa      SiardArchive instance (JAXB).
     * @throws IOException if an I/O error occurred.
     */
    private MetaDataImpl(Archive archive, SiardArchive sa)
            throws IOException {
        _archive = archive;
        _sa = sa;
        /* meta schemas will be opened by Schema creation */
        /* open all user meta data */
        UsersType uts = _sa.getUsers();
        if (uts != null) {
            for (int iUser = 0; iUser < uts.getUser()
                                           .size(); iUser++) {
                UserType ut = uts.getUser()
                                 .get(iUser);
                MetaUser mu = MetaUserImpl.newInstance(this, ut);
                _mapMetaUsers.put(XU.fromXml(ut.getName()), mu);
            }
        }
        /* open all role meta data */
        RolesType rts = _sa.getRoles();
        if (rts != null) {
            for (int iRole = 0; iRole < rts.getRole()
                                           .size(); iRole++) {
                RoleType rt = rts.getRole()
                                 .get(iRole);
                MetaRole mr = MetaRoleImpl.newInstance(this, rt);
                _mapMetaRoles.put(XU.fromXml(rt.getName()), mr);
            }
        }
        /* open all privilege meta data */
        PrivilegesType pts = _sa.getPrivileges();
        if (pts != null) {
            for (int iPrivilege = 0; iPrivilege < pts.getPrivilege()
                                                     .size(); iPrivilege++) {
                PrivilegeType pt = pts.getPrivilege()
                                      .get(iPrivilege);
                MetaPrivilege mp = MetaPrivilegeImpl.newInstance(this, pt);
                _listMetaPrivileges.add(mp);
            }
        }
    } 

    /**
     * factory
     *
     * @param archive SIARD archive associated with this meta data instance.
     * @param sa      SiardArchive instance (JAXB).
     * @return new MetaData instance.
     * @throws IOException if an I/O error occurred.
     */
    public static MetaData newInstance(Archive archive, SiardArchive sa)
            throws IOException {
        return new MetaDataImpl(archive, sa);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVersion() {
        return ((ArchiveImpl) getArchive()).getPreviousMetaDataVersion();
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDbName(String sDbname) {
        if (getArchiveImpl().isMetaDataDifferent(getDbName(), sDbname))
            _sa.setDbname(XU.toXml(sDbname));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDbName() {
        return XU.fromXml(_sa.getDbname());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String sDescription) {
        if (getArchiveImpl().isMetaDataDifferent(getDescription(), sDescription))
            _sa.setDescription(XU.toXml(sDescription));
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return XU.fromXml(_sa.getDescription());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setArchiver(String sArchiver) {
        if (getArchiveImpl().isMetaDataDifferent(getArchiver(), sArchiver))
            _sa.setArchiver(XU.toXml(sArchiver));
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getArchiver() {
        return XU.fromXml(_sa.getArchiver());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setArchiverContact(String sArchiverContact) {
        if (getArchiveImpl().isMetaDataDifferent(getArchiverContact(), sArchiverContact))
            _sa.setArchiverContact(XU.toXml(sArchiverContact));
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getArchiverContact() {
        return XU.fromXml(_sa.getArchiverContact());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataOwner(String sDataOwner) {
        if (getArchiveImpl().isMetaDataDifferent(getDataOwner(), sDataOwner))
            _sa.setDataOwner(XU.toXml(sDataOwner));
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDataOwner() {
        return XU.fromXml(_sa.getDataOwner());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataOriginTimespan(String sDataOriginTimespan) {
        if (getArchiveImpl().isMetaDataDifferent(getDataOriginTimespan(), sDataOriginTimespan))
            _sa.setDataOriginTimespan(XU.toXml(sDataOriginTimespan));
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDataOriginTimespan() {
        return XU.fromXml(_sa.getDataOriginTimespan());
    }

    @Override

    /** {@inheritDoc} */
    public void setLobFolder(URI uriLobFolder)
            throws IOException {
        boolean bMayBeSet = false;
        if (getLobFolder() == null) {
            if (getArchive().isEmpty())
                bMayBeSet = true;
        } else
            bMayBeSet = true;
        if (bMayBeSet) {
            if (getArchiveImpl().isMetaDataDifferent(getLobFolder(), uriLobFolder)) {
                if (uriLobFolder != null) {
                    if (uriLobFolder.getPath()
                                    .endsWith("/")) {
                        if (uriLobFolder.isAbsolute()) {
                            if (uriLobFolder.getScheme() == null) {
                                try {
                                    uriLobFolder = new URI(_sURI_SCHEME_FILE, "", uriLobFolder.getPath(), null);
                                } catch (URISyntaxException use) {
                                }
                            }
                            if (!uriLobFolder.getScheme()
                                             .equals(_sURI_SCHEME_FILE))
                                throw new IllegalArgumentException("Only URIs with scheme \"" + _sURI_SCHEME_FILE + "\" allowed for LOB folder!");
                        } else {
                            if (!uriLobFolder.getPath()
                                             .startsWith("../"))
                                throw new IllegalArgumentException("Relative LOB folder URIs must start with \"..\"!");
                        }
                        _sa.setLobFolder(XU.toXml(uriLobFolder.toString()));
                    } else
                        throw new IllegalArgumentException("Path of LOB folder URI must denote a folder (end with \"/\")!");
                } else
                    throw new IllegalArgumentException("LOB folder URI must not be null!");
            }
        } else
            throw new IOException("LOB folder value cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getLobFolder() {
        URI uriLobFolder = null;
        String sLobFolder = XU.fromXml(_sa.getLobFolder());
        if (sLobFolder != null) {
            try {
                uriLobFolder = new URI(sLobFolder);
            } catch (URISyntaxException use) {
            }
        }
        return uriLobFolder;
    } 

    /**
     * resolve the given LOB folder URI against the current folder where
     * the SIARD archive is located.
     *
     * @param uriLobFolder LOB folder URI.
     * @return absolute URI.
     */
    public URI getAbsoluteUri(URI uriLobFolder) {
        URI uriAbsolute = null;
        if (!uriLobFolder.isAbsolute()) {
            if (uriLobFolder.getPath()
                            .startsWith("..")) {
                String sPathRelativeToSiard = uriLobFolder.getPath()
                                                          .substring(3);
                if (sPathRelativeToSiard.length() == 0)
                    sPathRelativeToSiard = "./";
                try {
                    uriLobFolder = new URI(sPathRelativeToSiard);
                    URI uriResolver = getArchive().getFile()
                                                  .getAbsoluteFile()
                                                  .getParentFile()
                                                  .toURI();
                    uriAbsolute = uriResolver.resolve(uriLobFolder);
                } catch (URISyntaxException use) {
                }
            } else
                throw new IllegalArgumentException("LOB folder is relative and does not start with \"../\"!");
        } else
            uriAbsolute = uriLobFolder;
        return uriAbsolute;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getAbsoluteLobFolder() {
        URI uriAbsolute = null;
        URI uriLobFolder = getLobFolder();
        if (uriLobFolder != null)
            uriAbsolute = getAbsoluteUri(uriLobFolder);
        return uriAbsolute;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProducerApplication(String sProducerApplication)
            throws IOException {
        if (getArchive().canModifyPrimaryData()) {
            if (getArchiveImpl().isMetaDataDifferent(getProducerApplication(), sProducerApplication))
                _sa.setProducerApplication(XU.toXml(sProducerApplication));
        } else
            throw new IOException("Producer application value cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProducerApplication() {
        return XU.fromXml(_sa.getProducerApplication());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Calendar getArchivalDate() {
        return _du.toGregorianCalendar(_sa.getArchivalDate());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MessageDigestType> getMessageDigest() {
        return _sa.getMessageDigest();
    }

    public void setMessageDigest(MessageDigestType md) {
        getArchiveImpl().isMetaDataDifferent(null, md);
        _sa.getMessageDigest()
           .clear();
        _sa.getMessageDigest()
           .add(md);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClientMachine(String sClientMachine)
            throws IOException {
        if (getArchive().canModifyPrimaryData()) {
            if (getArchiveImpl().isMetaDataDifferent(getClientMachine(), sClientMachine))
                _sa.setClientMachine(XU.toXml(sClientMachine));
        } else
            throw new IOException("Client machine name cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientMachine() {
        return XU.fromXml(_sa.getClientMachine());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDatabaseProduct(String sDatabaseProduct)
            throws IOException {
        if (getArchive().canModifyPrimaryData()) {
            if (getArchiveImpl().isMetaDataDifferent(getDatabaseProduct(), sDatabaseProduct))
                _sa.setDatabaseProduct(XU.toXml(sDatabaseProduct));
        } else
            throw new IOException("Database product name cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDatabaseProduct() {
        return XU.fromXml(_sa.getDatabaseProduct());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setConnection(String sConnection)
            throws IOException {
        if (getArchive().canModifyPrimaryData()) {
            if (getArchiveImpl().isMetaDataDifferent(getConnection(), sConnection))
                _sa.setConnection(XU.toXml(sConnection));
        } else
            throw new IOException("Connection string cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConnection() {
        return XU.fromXml(_sa.getConnection());
    }

    @Override

    /** {@inheritDoc} */
    public void setDatabaseUser(String sDatabaseUser)
            throws IOException {
        if (getArchive().canModifyPrimaryData()) {
            if (getArchiveImpl().isMetaDataDifferent(getDatabaseUser(), sDatabaseUser))
                _sa.setDatabaseUser(XU.toXml(sDatabaseUser));
        } else
            throw new IOException("Database user name cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDatabaseUser() {
        return XU.fromXml(_sa.getDatabaseUser());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMetaSchemas() {
        SchemasType sts = _sa.getSchemas();
        int iMetaSchemas = sts.getSchema()
                              .size();
        return iMetaSchemas;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaSchema getMetaSchema(int iSchema) {
        String sName = XU.fromXml(_sa.getSchemas()
                                     .getSchema()
                                     .get(iSchema)
                                     .getName());
        return getMetaSchema(sName);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaSchema getMetaSchema(String sName) {
        MetaSchema ms = null;
        Schema schema = getArchive().getSchema(sName);
        if (schema != null)
            ms = schema.getMetaSchema();
        return ms;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMetaUsers() {
        return _mapMetaUsers.size();
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaUser getMetaUser(int iUser) {
        MetaUser mu = null;
        UsersType uts = _sa.getUsers();
        if (uts != null) {
            UserType ut = uts.getUser()
                             .get(iUser);
            String sName = ut.getName();
            mu = getMetaUser(sName);
        }
        return mu;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaUser getMetaUser(String sName) {
        return _mapMetaUsers.get(sName);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaUser createMetaUser(String sName)
            throws IOException {
        MetaUser mu = null;
        if (getArchive().canModifyPrimaryData()) {
            if (getMetaUser(sName) == null) {
                UsersType uts = _sa.getUsers();
                if (uts == null) {
                    uts = _of.createUsersType();
                    _sa.setUsers(uts);
                }
                UserType ut = _of.createUserType();
                ut.setName(XU.toXml(sName));
                uts.getUser()
                   .add(ut);
                mu = MetaUserImpl.newInstance(this, ut);
                _mapMetaUsers.put(sName, mu);
                getArchiveImpl().isMetaDataDifferent(null, mu);
                if (_saTemplate != null) {
                    UsersType utsTemplate = _saTemplate.getUsers();
                    if (utsTemplate != null) {
                        UserType utTemplate = null;
                        for (int iUser = 0; (utTemplate == null) && (iUser < utsTemplate.getUser()
                                                                                        .size()); iUser++) {
                            UserType utTry = utsTemplate.getUser()
                                                        .get(iUser);
                            if (sName.equals(utTry.getName()))
                                utTemplate = utTry;
                        }
                        if (utTemplate != null) {
                            MetaUserImpl mui = (MetaUserImpl) mu;
                            mui.setTemplate(utTemplate);
                        }
                    }
                }
            } else
                throw new IOException("Only one user with the same name allowed!");
        } else
            throw new IOException("Users can only be created if archive is open for modification of primary data.");
        return mu;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMetaRoles() {
        return _mapMetaRoles.size();
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaRole getMetaRole(int iRole) {
        MetaRole mr = null;
        RolesType rts = _sa.getRoles();
        if (rts != null) {
            RoleType rt = rts.getRole()
                             .get(iRole);
            String sName = rt.getName();
            mr = getMetaRole(sName);
        }
        return mr;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaRole getMetaRole(String sName) {
        return _mapMetaRoles.get(sName);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaRole createMetaRole(String sName, String sAdmin)
            throws IOException {
        MetaRole mr = null;
        if (getArchive().canModifyPrimaryData()) {
            if (getMetaRole(sName) == null) {
                RolesType rts = _sa.getRoles();
                if (rts == null) {
                    rts = _of.createRolesType();
                    _sa.setRoles(rts);
                }
                RoleType rt = _of.createRoleType();
                rt.setName(XU.toXml(sName));
                rt.setAdmin(XU.toXml(sAdmin));
                rts.getRole()
                   .add(rt);
                mr = MetaRoleImpl.newInstance(this, rt);
                _mapMetaRoles.put(sName, mr);
                getArchiveImpl().isMetaDataDifferent(null, mr);
                if (_saTemplate != null) {
                    RolesType rtsTemplate = _saTemplate.getRoles();
                    if (rtsTemplate != null) {
                        RoleType rtTemplate = null;
                        for (int iRole = 0; (rtTemplate == null) && (iRole < rtsTemplate.getRole()
                                                                                        .size()); iRole++) {
                            RoleType rtTry = rtsTemplate.getRole()
                                                        .get(iRole);
                            if (sName.equals(rtTry.getName()))
                                rtTemplate = rtTry;
                        }
                        if (rtTemplate != null) {
                            MetaRoleImpl mri = (MetaRoleImpl) mr;
                            mri.setTemplate(rtTemplate);
                        }
                    }
                }
            } else
                throw new IOException("Only one role with the same name allowed!");
        } else
            throw new IOException("Roles can only be created if archive is open for modification of primary data.");
        return mr;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMetaPrivileges() {
        return _listMetaPrivileges.size();
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaPrivilege getMetaPrivilege(int iIndex) {
        return _listMetaPrivileges.get(iIndex);
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaPrivilege getMetaPrivilege(String sType, String sObject, String sGrantor, String sGrantee) {
        MetaPrivilege mp = null;
        for (int iPrivilege = 0; (mp == null) && (iPrivilege < getMetaPrivileges()); iPrivilege++) {
            MetaPrivilege mpTry = getMetaPrivilege(iPrivilege);
            if (Objects.equals(sType, mpTry.getType()) &&
                    Objects.equals(sObject, mpTry.getObject()) &&
                    Objects.equals(sGrantor, mpTry.getGrantor()) &&
                    Objects.equals(sGrantee, mpTry.getGrantee()))
                mp = mpTry;
        }
        return mp;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaPrivilege createMetaPrivilege(String sType, String sObject, String sGrantor, String sGrantee)
            throws IOException {
        MetaPrivilege mp = null;
        if (getArchive().canModifyPrimaryData()) {
            if (getMetaPrivilege(sType, sObject, sGrantor, sGrantee) == null) {
                PrivilegesType pts = _sa.getPrivileges();
                if (pts == null) {
                    pts = _of.createPrivilegesType();
                    _sa.setPrivileges(pts);
                }
                PrivilegeType pt = _of.createPrivilegeType();
                pt.setType(XU.toXml(sType));
                pt.setObject(XU.toXml(sObject));
                pt.setGrantor(XU.toXml(sGrantor));
                pt.setGrantee(XU.toXml(sGrantee));
                pts.getPrivilege()
                   .add(pt);
                mp = MetaPrivilegeImpl.newInstance(this, pt);
                _listMetaPrivileges.add(mp);
                getArchiveImpl().isMetaDataDifferent(null, mp);
                if (_saTemplate != null) {
                    PrivilegesType ptsTemplate = _saTemplate.getPrivileges();
                    if (ptsTemplate != null) {
                        PrivilegeType ptTemplate = null;
                        for (int iPrivilege = 0; (ptTemplate == null) && (iPrivilege < ptsTemplate.getPrivilege()
                                                                                                  .size()); iPrivilege++) {
                            PrivilegeType ptTry = ptsTemplate.getPrivilege()
                                                             .get(iPrivilege);
                            if (Objects.equals(sType, ptTry.getType()) &&
                                    Objects.equals(sObject, ptTry.getObject()) &&
                                    Objects.equals(sGrantor, ptTry.getGrantor()) &&
                                    Objects.equals(sGrantee, ptTry.getGrantee()))
                                ptTemplate = ptTry;
                        }
                        if (ptTemplate != null) {
                            MetaPrivilegeImpl mpi = (MetaPrivilegeImpl) mp;
                            mpi.setTemplate(ptTemplate);
                        }
                    }
                }
            } else
                throw new IOException("Only one privilege with the same type, object, grantor and grantee allowed!");
        } else
            throw new IOException("Privileges can only be created if archive is open for modification of primary data.");
        return mp;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        boolean bValid = true;
        /* a valid archive must have version 2.1 or 2.2 */
        String metadataVersion = XU.fromXml(_sa.getVersion());
        if (bValid &&
                (!Archive.sMETA_DATA_VERSION.equals(metadataVersion)) && !Archive.sMETA_DATA_VERSION_2_1.equals(metadataVersion)) {

            bValid = false;
        }
        /* a valid archive must have a non-empty dbname */
        if (bValid &&
                (!SU.isNotEmpty(_sa.getDbname())))
            bValid = false;
        /* a valid archive must have a non-empty data owner */
        if (bValid &&
                (!SU.isNotEmpty(_sa.getDataOwner())))
            bValid = false;
        /* a valid archive must have a non-empty data origin time span */
        if (bValid &&
                (!SU.isNotEmpty(_sa.getDataOriginTimespan())))
            bValid = false;
        /* a valid archive must have a non-empty archival date */
        if (bValid &&
                (_sa.getArchivalDate() == null))
            bValid = false;
        /* a valid archive must have at least one user */
        if (bValid && (getMetaUsers() <= 0))
            bValid = false;
        /* a valid archive must have at least one schema */
        if (bValid && (getMetaSchemas() <= 0))
            bValid = false;
        for (int iSchema = 0; bValid && (iSchema < getMetaSchemas()); iSchema++) {
            MetaSchema ms = getMetaSchema(iSchema);
            if (ms == null)
                bValid = false;
            else if (!ms.isValid())
                bValid = false;
        }
        return bValid;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    protected MetaSearch[] getSubMetaSearches()
            throws IOException {
        MetaSearch[] ams = new MetaSearch[getMetaSchemas() + getMetaUsers() + getMetaRoles() + getMetaPrivileges()];
        for (int iSchema = 0; iSchema < getMetaSchemas(); iSchema++)
            ams[iSchema] = getMetaSchema(iSchema);
        for (int iUser = 0; iUser < getMetaUsers(); iUser++)
            ams[getMetaSchemas() + iUser] = getMetaUser(iUser);
        for (int iRole = 0; iRole < getMetaRoles(); iRole++)
            ams[getMetaSchemas() + getMetaUsers() + iRole] = getMetaRole(iRole);
        for (int iPrivilege = 0; iPrivilege < getMetaPrivileges(); iPrivilege++)
            ams[getMetaSchemas() + getMetaUsers() + getMetaRoles() + iPrivilege] = getMetaPrivilege(iPrivilege);
        return ams;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getSearchElements(DU du)
            throws IOException {
        return new String[]{
                getVersion(),
                getDbName(),
                getDescription(),
                getArchiver(),
                getArchiverContact(),
                getDataOwner(),
                getDataOriginTimespan(),
                getLobFolder() == null ? "" : getLobFolder().toString(),
                getProducerApplication(),
                du.fromGregorianCalendar((GregorianCalendar) getArchivalDate()),
                getClientMachine(),
                getDatabaseProduct(),
                getConnection(),
                getDatabaseUser()
        };
    } 

    /**
     * {@inheritDoc}
     * toString() returns the file name of the archive which is to be
     * displayed as the label of the root of the tree displaying the
     * archive.
     */
    @Override
    public String toString() {
        return getArchive().getFile()
                           .getName();
    }
} 
