/*== RandomArchive.java ================================================
RandomArchive generates an archive of random data based on a valid SIARD metadata XML.
Application : SIARD 2.0
Description : RandomArchive generates an archive of random data based on a valid SIARD metadata XML.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2019
Created    : 20.03.2019, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.generate;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.*;
import ch.admin.bar.siard2.api.primary.MetaDataXml;
import ch.enterag.utils.cli.Arguments;
import ch.enterag.utils.configuration.ManifestAttributes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RandomArchive {
    public static final int iRETURN_OK = 0;
    public static final int iRETURN_WARNING = 4;
    public static final int iRETURN_ERROR = 8;
    public static final int iRETURN_FATAL = 12;

    private static final ManifestAttributes _ma = ManifestAttributes.getInstance();
    private int _iReturn = iRETURN_ERROR;
    private SiardArchive _sa = null;


    /* constructs a full message with all causes from a throwable.
     * @return full message.
     */
    private static String getThrowableMessage(Throwable t) {
        String sMessage = "";
        Throwable tException = t;
        for (; tException != null; tException = tException.getCause()) {
            if (tException == t)
                sMessage = sMessage + tException.getClass()
                                                .getName() + ": ";
            else
                sMessage = sMessage + "< " + tException.getClass()
                                                       .getName() + ": ";
            if (tException.getMessage() != null)
                sMessage = sMessage + tException.getMessage();
        }
        return "  " + sMessage;
    } 

        /**
     * retrieves a full error message.
     *
     * @param e error.
     * @return full message.
     */
    public static String getErrorMessage(Error e) {
        return getThrowableMessage(e);
    } 

        /**
     * retrieves a full exception message.
     *
     * @param e exception.
     * @return full message.
     */
    public static String getExceptionMessage(Exception e) {
        return getThrowableMessage(e);
    } 


    private int readMetaData(File fileInput) {
        int iReturn = iRETURN_ERROR;
        try {
            FileInputStream fis = new FileInputStream(fileInput);
            _sa = MetaDataXml.readSiard22Xml(fis);
            fis.close();
            if (_sa != null)
                iReturn = iRETURN_OK;
            else {
                fis = new FileInputStream(fileInput);
                _sa = MetaDataXml.readAndConvertSiard10Xml(fis);
                fis.close();
                if (_sa != null)
                    iReturn = iRETURN_OK;
            }
        } catch (FileNotFoundException fnfe) {
            System.err.println(getExceptionMessage(fnfe));
        } catch (IOException ie) {
            System.err.println(getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int createMetaData(MetaData md) {
        int iReturn = iRETURN_ERROR;
        try {
            md.setDbName(_sa.getDbname() + "-randomized");
            md.setDescription(_sa.getDescription());
            md.setArchiver(_sa.getArchiver());
            md.setArchiverContact(_sa.getArchiverContact());
            md.setDataOwner(_sa.getDataOwner());
            md.setDataOriginTimespan(_sa.getDataOriginTimespan());
            md.setProducerApplication(_sa.getProducerApplication());
            md.setClientMachine(_sa.getClientMachine());
            md.setDatabaseProduct(_sa.getDatabaseProduct());
            md.setConnection(_sa.getConnection());
            md.setDatabaseUser(_sa.getDatabaseUser());
            iReturn = iRETURN_OK;
            // users
            UsersType ut = _sa.getUsers();
            if (ut != null) {
                for (int iUser = 0; (iReturn == iRETURN_OK) && (iUser < ut.getUser()
                                                                          .size()); iUser++) {
                    UserType u = ut.getUser()
                                   .get(iUser);
                    MetaUser mu = md.createMetaUser(u.getName());
                    mu.setDescription(u.getDescription());
                }
            }
            // roles
            RolesType rt = _sa.getRoles();
            if (rt != null) {
                for (int iRole = 0; (iReturn == iRETURN_OK) && (iRole < rt.getRole()
                                                                          .size()); iRole++) {
                    RoleType r = rt.getRole()
                                   .get(iRole);
                    MetaRole mr = md.createMetaRole(r.getName(), r.getAdmin());
                    mr.setDescription(r.getDescription());
                }
            }
            // privileges
            PrivilegesType pt = _sa.getPrivileges();
            if (pt != null) {
                for (int iPrivilege = 0; (iReturn == iRETURN_OK) && (iPrivilege < pt.getPrivilege()
                                                                                    .size()); iPrivilege++) {
                    PrivilegeType p = pt.getPrivilege()
                                        .get(iPrivilege);
                    MetaPrivilege mp = md.createMetaPrivilege(p.getType(), p.getObject(), p.getGrantor(), p.getGrantee());
                    if (p.getOption() != null)
                        mp.setOption(p.getOption()
                                      .toString());
                    mp.setDescription(p.getDescription());
                }
            }
        } catch (IOException ie) {
            System.err.println(getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int createRandomFile(File fileOutput, double dFraction) {
        int iReturn = iRETURN_ERROR;
        try {
            Archive archive = ch.admin.bar.siard2.api.primary.ArchiveImpl.newInstance();
            archive.create(fileOutput);
            /** here we walk the _sa and generate random objects in the table **/
            iReturn = createMetaData(archive.getMetaData());
            if (iReturn == iRETURN_OK) {
                SchemasType st = _sa.getSchemas();
                if (st != null) {
                    for (int iSchema = 0; (iReturn == iRETURN_OK) && (iSchema < st.getSchema()
                                                                                  .size()); iSchema++) {
                        SchemaType s = st.getSchema()
                                         .get(iSchema);
                        System.out.println("Schema: " + s.getName());
                        Schema schema = archive.createSchema(s.getName());
                        schema.getMetaSchema()
                              .setDescription(s.getDescription());
                        RandomSchema rs = new RandomSchema(schema, s, dFraction);
                        iReturn = rs.createSchema();
                    }
                }
            }
            /***
             String sOutput = fileOutput.getAbsolutePath();
             sOutput = sOutput.substring(0,sOutput.length()-".siard".length())+".xml";
             File fileXml = new File(sOutput);
             FileOutputStream fosXml = new FileOutputStream(fileXml);
             archive.exportMetaData(fosXml);
             fosXml.close();
             ***/
            archive.close();
            iReturn = iRETURN_OK;
        } catch (IOException ie) {
            System.err.println(getExceptionMessage(ie));
        }
        return iReturn;
    }

    private int displaySyntax() {
        System.out.println();
        System.out.println("Syntax:");
        System.out.println("java ch.admin.bar.siard.generate.RandomArchive [-h | [-f] -i:<metadata file> -o:<siard file>]");
        System.out.println("with the following arguments:");
        System.out.println("-h (or no arguments): display this help text.");
        System.out.println("-f: overwrite output file");
        System.out.println("-p:<percent>: percent of size (default: 100.0)");
        System.out.println("-i:<metadata file>: use this meta data for creating the random SIARD file.");
        System.out.println("-o:<siard file>: random SIARD file - force overwrite, if -f option is given.");
        System.out.println();
        return iRETURN_WARNING;
    } 

    public RandomArchive(String[] args) {
        Arguments arguments = new Arguments(args);
        String sInput = arguments.getOption("i");
        if (sInput != null) {
            File fileInput = new File(sInput);
            if ((fileInput.isFile() && fileInput.exists())) {
                String sOutput = arguments.getOption("o");
                if (sOutput != null) {
                    String sPercent = arguments.getOption("p");
                    double dFraction = 1.0;
                    if (sPercent != null)
                        dFraction = Double.parseDouble(sPercent) / 100.0;
                    if ((dFraction >= 0.01) && (dFraction < 100.00)) {
                        boolean bForce = arguments.getOption("f") != null;
                        File fileOutput = new File(sOutput);
                        if (bForce && fileOutput.exists()) {
                            if (!fileOutput.delete())
                                System.err.println("File " + fileOutput.getAbsolutePath() + " could not be deleted!");
                        }
                        if (!fileOutput.exists()) {
                            _iReturn = readMetaData(fileInput);
                            if (_iReturn == iRETURN_OK)
                                _iReturn = createRandomFile(fileOutput, dFraction);
                        } else if (!bForce)
                            System.err.println("File " + fileOutput.getAbsolutePath() + " exists already! Use option -f for forcing overwrite.");
                    } else
                        System.err.println("Percent " + sPercent + " must be [1.0,10000.0]!");
                } else
                    System.err.println("Ouput file was not given!");
            } else
                System.err.println("File " + fileInput.getAbsolutePath() + " does not exist!");
            if (_iReturn != iRETURN_OK)
                _iReturn = displaySyntax();
        }
    } 

    public static void main(String[] args) {
        System.out.println("Random application of " + _ma.getImplementationTitle() + " " + _ma.getImplementationVersion());
        int iReturn = iRETURN_FATAL;
        try {
            RandomArchive random = new RandomArchive(args);
            iReturn = random._iReturn;
        } catch (Exception e) {
            System.err.println(getExceptionMessage(e));
        } catch (Error e) {
            System.err.println(getErrorMessage(e));
        }
        System.out.print("Random application terminates ");
        switch (iReturn) {
            case iRETURN_OK:
                System.out.println("successfully");
                break;
            case iRETURN_WARNING:
                System.out.println();
                break;
            case iRETURN_ERROR:
                System.out.println("with errors!");
                break;
            case iRETURN_FATAL:
                System.out.println("with fatal errors!");
        }
        System.exit(iReturn);
    }

}
