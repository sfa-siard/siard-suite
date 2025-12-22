package ch.admin.bar.siard2.sample;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.DU;
import ch.enterag.utils.cli.Arguments;
import ch.enterag.utils.configuration.ManifestAttributes;

import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;

public class SampleArchive {
    public static final int iRETURN_OK = 0;
    public static final int iRETURN_WARNING = 4;
    public static final int iRETURN_ERROR = 8;
    public static final int iRETURN_FATAL = 12;

    private static final ManifestAttributes _ma = ManifestAttributes.getInstance();
    private static final DU _du = DU.getInstance("en", "yyyy-MM-dd");
    private int _iReturn = iRETURN_ERROR;


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

    public static void printValue(String sLabel, String sValue) {
        System.out.println(sLabel + ": " + sValue);
    } 

    private int readMetaData(MetaData md) {
        int iReturn = iRETURN_ERROR;
        printValue("Meta data version", md.getVersion());
        printValue("Database name", md.getDbName());
        printValue("Description", md.getDescription());
        printValue("Archiver", md.getArchiver());
        printValue("Archiver contact", md.getArchiverContact());
        printValue("Data owner", md.getDataOwner());
        printValue("Data origin timespan", md.getDataOriginTimespan());
        printValue("Producer application", md.getProducerApplication());
        printValue("ArchivalDate", _du.fromGregorianCalendar((GregorianCalendar) md.getArchivalDate()));
        printValue("Client machine", md.getClientMachine());
        printValue("Database product", md.getDatabaseProduct());
        printValue("Connection", md.getConnection());
        printValue("Database user", md.getDatabaseUser());
        printValue("Number of database users", String.valueOf(md.getMetaUsers()));
        for (int iUser = 0; iUser < md.getMetaUsers(); iUser++) {
            MetaUser mu = md.getMetaUser(iUser);
            String sUser = "User[" + iUser + "]";
            printValue(sUser + " name", mu.getName());
            printValue(sUser + " description", mu.getDescription());
        }
        printValue("Number of database roles", String.valueOf(md.getMetaRoles()));
        for (int iRole = 0; iRole < md.getMetaRoles(); iRole++) {
            MetaRole mr = md.getMetaRole(iRole);
            String sRole = "Role[" + iRole + "]";
            printValue(sRole + " name", mr.getName());
            printValue(sRole + " admin", mr.getAdmin());
            printValue(sRole + " description", mr.getDescription());
        }
        printValue("Number of database privileges", String.valueOf(md.getMetaPrivileges()));
        for (int iPrivilege = 0; iPrivilege < md.getMetaPrivileges(); iPrivilege++) {
            MetaPrivilege mp = md.getMetaPrivilege(iPrivilege);
            String sPrivilege = "Privilege[" + iPrivilege + "]";
            printValue(sPrivilege + " type", mp.getType());
            printValue(sPrivilege + " object", mp.getObject());
            printValue(sPrivilege + " grantor", mp.getGrantor());
            printValue(sPrivilege + " grantee", mp.getGrantor());
            printValue(sPrivilege + " option", mp.getOption());
            printValue(sPrivilege + " description", mp.getDescription());
        }
        iReturn = iRETURN_OK;
        return iReturn;
    } 

    private int readSiardFile(File fileSiard) {
        int iReturn = iRETURN_ERROR;
        System.out.println("Reading SIARD file " + fileSiard.getAbsolutePath());
        Archive archive = ArchiveImpl.newInstance();
        try {
            archive.open(fileSiard);
            int iResult = readMetaData(archive.getMetaData());
            for (int iSchema = 0; (iResult == iRETURN_OK) && (iSchema < archive.getSchemas()); iSchema++) {
                System.out.println("Schema[" + iSchema + "]");
                SampleSchema ss = new SampleSchema(archive.getSchema(iSchema));
                iResult = ss.readSchema();
            }
            archive.close();
            iReturn = iResult;
        } catch (IOException ie) {
            System.err.println(getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private static final String sDB_NAME = "Sample Database";
    private static final String sDESCRIPTION = "SIARD File generated using the SIARD API";
    private static final String sARCHIVER = "Hartwig Thomas";
    private static final String sARCHIVER_CONTACT = "info@enterag.ch";
    private static final String sDATA_OWNER = "Public";
    private static final String sDATA_ORIGIN_TIMESPAN = "August 2016";
    private static final String sPRODUCER_APPLICATION = "SampleArchive";
    private static final String sCLIENT_MACHINE = "localhost";
    private static final String sDATABASE_PRODUCT = "Fictitious SIARD database";
    private static final String sCONNECTION = "jdbc:siard:sample";
    private static final String sDATABASE_USER = "SampleUser";
    private static final String sUSER1_NAME = "User1";
    private static final String sUSER2_NAME = "User2";
    private static final String sUSER1_DESCRIPTION = "First database user";
    private static final String sPRIVILEGE_TYPE = "SELECT";
    private static final String sPRIVILEGE_OBJECT = "ALL TABLES";
    private static final String sPRIVILEGE_GRANTOR = "Grantor";
    private static final String sPRIVILEGE_GRANTEE = "Grantee";

    private int createMetaData(MetaData md) {
        int iReturn = iRETURN_ERROR;
        try {
            md.setDbName(sDB_NAME);
            md.setDescription(sDESCRIPTION);
            md.setArchiver(sARCHIVER);
            md.setArchiverContact(sARCHIVER_CONTACT);
            md.setDataOwner(sDATA_OWNER);
            md.setDataOriginTimespan(sDATA_ORIGIN_TIMESPAN);
            md.setProducerApplication(sPRODUCER_APPLICATION);
            md.setClientMachine(sCLIENT_MACHINE);
            md.setDatabaseProduct(sDATABASE_PRODUCT);
            md.setConnection(sCONNECTION);
            md.setDatabaseUser(sDATABASE_USER);
            // 2 users
            MetaUser mu = md.createMetaUser(sUSER1_NAME);
            mu.setDescription(sUSER1_DESCRIPTION);
            mu = md.createMetaUser(sUSER2_NAME);
            // no roles
            // 1 privilege
            md.createMetaPrivilege(sPRIVILEGE_TYPE, sPRIVILEGE_OBJECT, sPRIVILEGE_GRANTOR, sPRIVILEGE_GRANTEE);
            iReturn = iRETURN_OK;
        } catch (IOException ie) {
            System.err.println(getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int createSiardFile(File fileSiard) {
        int iReturn = iRETURN_ERROR;
        System.out.println("Creating SIARD file " + fileSiard.getAbsolutePath());
        Archive archive = ArchiveImpl.newInstance();
        try {
            archive.create(fileSiard);
            int iResult = createMetaData(archive.getMetaData());
            if (iResult == iRETURN_OK) {
                SampleSchema ss = new SampleSchema(archive.createSchema(SampleSchema.sSCHEMA_NAME));
                iResult = ss.createSchema();
            }
            archive.close();
            iReturn = iResult;
        } catch (IOException ie) {
            System.err.println(getExceptionMessage(ie));
        }
        return iReturn;
    } 

    private int displaySyntax() {
        System.out.println();
        System.out.println("Syntax:");
        System.out.println("java ch.admin.bar.siard.api.Sample [-h | -i:<siard file> | [-f] -o:<siard file>]");
        System.out.println("with the following arguments:");
        System.out.println("-h (or no arguments): display this help text.");
        System.out.println("-i:<siard file>: read and display the contents of the given SIARD file.");
        System.out.println("-o:<siard file>: write a full sample SIARD file - force overwrite, if -f option is given.");
        System.out.println();
        return iRETURN_WARNING;
    } 

    public SampleArchive(String[] args) {
        Arguments arguments = new Arguments(args);
        String sInput = arguments.getOption("i");
        if (sInput != null) {
            File fileInput = new File(sInput);
            if ((fileInput.isFile() && fileInput.exists()))
                _iReturn = readSiardFile(fileInput);
            else
                System.err.println("File " + fileInput.getAbsolutePath() + " does not exist!");
        } else {
            String sOutput = arguments.getOption("o");
            if (sOutput != null) {
                boolean bForce = arguments.getOption("f") != null;
                File fileOutput = new File(sOutput);
                if (bForce && fileOutput.exists()) {
                    if (!fileOutput.delete())
                        System.err.println("File " + fileOutput.getAbsolutePath() + " could not be deleted!");
                }
                if (!fileOutput.exists())
                    _iReturn = createSiardFile(fileOutput);
                else if (!bForce)
                    System.err.println("File " + fileOutput.getAbsolutePath() + " exists already! Use option -f for forcing overwrite.");
            } else
                _iReturn = displaySyntax();
        }
    } 

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Sample application of " + _ma.getImplementationTitle() + " " + _ma.getImplementationVersion());
        int iReturn = iRETURN_FATAL;
        try {
            SampleArchive sample = new SampleArchive(args);
            iReturn = sample._iReturn;
        } catch (Exception e) {
            System.err.println(getExceptionMessage(e));
        } catch (Error e) {
            System.err.println(getErrorMessage(e));
        }
        System.out.print("Sample application terminates ");
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
