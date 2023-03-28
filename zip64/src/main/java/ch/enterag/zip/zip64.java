/*== zip64.java ========================================================
zip64 implements the command-line zip64 utility.
Application : ZIP Utilities
Description : zip64 implements the command-line zip64 utility.
              It handles large ZIP files but does not (yet) support
              splitting and spanning or encryption.
------------------------------------------------------------------------
Copyright  : Enter AG, Zurich, Switzerland, 2010
Created    : 14.04.2010, Hartwig Thomas
======================================================================*/
package ch.enterag.zip;

import ch.enterag.utils.SU;
import ch.enterag.utils.cli.Arguments;
import ch.enterag.utils.logging.IndentLogger;
import ch.enterag.utils.zip.EntryInputStream;
import ch.enterag.utils.zip.EntryOutputStream;
import ch.enterag.utils.zip.FileEntry;
import ch.enterag.utils.zip.Zip64File;

import java.io.*;
import java.text.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/*====================================================================*/

/**
 * zip64 implements the command-line utility.
 *
 * @author Hartwig Thomas
 */
public class zip64 {
    /* banner */
    /**
     * program name
     */
    private static final String PROGRAM = "zip64";
    /**
     * program version (to be updated synchronously with version in build.xml)
     */
    private static final String VERSION = "2.1";
    /**
     * banner briefly states functionality
     */
    private static final String BANNER = "handles ZIP64 archives";
    /**
     * copyright notice
     */
    private static final String COPYRIGHT = "Copyright (c) 2010, Hartwig Thomas, Enter AG, Zurich, Switzerland";
    /**
     * GPL reference
     */
    private static final String CDDL = "This program comes with ABSOLUTELY NO WARRANTY.\n" +
            "This is open source software, and you are welcome to redistribute it.\n" +
            "See https://opensource.org/licenses/CDDL-1.0 for details.\n";
    /* return codes */
    /**
     * return code for success
     */
    private static final int RETURN_OK = 0;
    /**
     * return code for warning
     */
    private static final int RETURN_WARNING = 4;
    /**
     * return code for error
     */
    private static final int RETURN_ERROR = 8;

    /* commands */
    /**
     * list command
     */
    private static final String COMMAND_LIST = "l";
    /**
     * extract command
     */
    private static final String COMMAND_EXTRACT = "x";
    /**
     * inject command
     */
    private static final String COMMAND_INJECT = "n";

    /**
     * end-of-file constant
     */
    private int EOF = -1;
    /**
     * new-line constant
     */
    private char LF = 0x0A;

    /**
     * size of I/O buffer
     */
    private static final int BUFFER_SIZE = 4096;
    /**
     * date format to be used in display
     */
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    /**
     * size format to be used in display
     */
    private static final NumberFormat SIZE_FORMAT = new DecimalFormat("#,##0", new DecimalFormatSymbols());

    /* members */
    /**
     * Logger
     */
    private static IndentLogger m_il = IndentLogger.getIndentLogger(zip64.class.getName());
    /**
     * return code of process
     */
    private int m_iReturn = RETURN_OK;
    /**
     * list of files
     */
    private List<String> m_listFiles = new ArrayList<String>();
    /**
     * command
     */
    private String m_sCommand = COMMAND_LIST;
    /**
     * ZIP archive
     */
    private File m_fileZipArchive = null;
    /**
     * root folder for files
     */
    private File m_fileZipRoot = null;
    /**
     * comment in ZIP file
     */
    private String m_sZipComment = null;
    /**
     * quiet execution
     */
    private boolean m_bQuiet = false;
    /**
     * files to be replaced?
     */
    private boolean m_bReplace = false;
    /**
     * files to be compressed?
     */
    private boolean m_bCompress = false;

    /*--------------------------------------------------------------------*/

    /**
     * display usage information
     */
    private void displayHelp() {
        System.out.println("Usage:");
        System.out.println("java -jar zip64.jar [-h] | [<command> options <zipfile> files...]");
        System.out.println("with");
        System.out.println("<command>    " + COMMAND_INJECT + " for inject, " + COMMAND_EXTRACT + " for extract, " + COMMAND_LIST + " for list (default)");
        System.out.println("<zipfile>    name of ZIP archive to be read and/or written");
        System.out.println("files...     one or more file names relative to the ZIP root");
        System.out.println("             or '@' followed by the name of a text file (UTF-8!)");
        System.out.println("             with a file name on each line.");
        System.out.println("             File names may contain wildcards ?, * and **.");
        System.out.println("             Default: **");
        System.out.println("options      any combination of");
        System.out.println("             -d:<ziproot>, -z:<zipcomment> -r, -c, -q");
        System.out.println("<ziproot>    inject: injected files ... are relative to this");
        System.out.println("             extract: root folder for files to be extracted");
        System.out.println("<zipcomment> global ZIP file comment (inject only)");
        System.out.println("-r           replace target files (on inject or extract)");
        System.out.println("-c           compress files (inject only)");
        System.out.println("-q           quiet (ignored on list)");
        m_iReturn = RETURN_WARNING;
    } /* displayHelp */

    /*--------------------------------------------------------------------*/

    /**
     * handleException
     *
     * @param e exception.
     */
    private void handleException(Exception e) {
        String sMessage = e.getClass().getName() + ": " + e.getMessage();
        System.err.println(sMessage);
        m_il.warning(sMessage);
    } /* handleException */

    /*--------------------------------------------------------------------*/

    /**
     * displayMessage
     *
     * @param sMessage message
     */
    private void displayMessage(String sMessage) {
        if (!m_bQuiet)
            System.out.println(sMessage);
        // m_il.info(sMessage);
    } /* displayMessage */

    /*--------------------------------------------------------------------*/

    /**
     * readLine
     *
     * @param fr open file reader.
     * @return line read.
     * @throws IOException if an I/O error occurred.
     */
    private String readLine(FileReader fr)
            throws IOException {
        StringBuilder sbLine = new StringBuilder();
        for (int i = fr.read(); (i != EOF) && (i != LF); i = fr.read())
            sbLine.append((char) i);
        return sbLine.toString();
    } /* readLine */

    /*--------------------------------------------------------------------*/

    /**
     * addFile adds the given file name to the list
     *
     * @param sFileName name of file to be added.
     */
    private void addFile(String sFileName) {
        m_il.enter(sFileName);
        sFileName = sFileName.trim();
        if (File.separatorChar != '/')
            sFileName = sFileName.replace(File.separatorChar, '/');
        /* convert it into a regular expression pattern */
        StringBuilder sbPattern = new StringBuilder();
        sbPattern.append('^');
        for (int iPosition = 0; iPosition < sFileName.length(); iPosition++) {
            char ch = sFileName.charAt(iPosition);
            if (ch == '?')
                sbPattern.append(".");
            else if (ch == '*') {
                if ((iPosition < sFileName.length() - 1) && (sFileName.charAt(iPosition + 1) == '*')) {
                    sbPattern.append(".*");
                    iPosition++;
                } else
                    sbPattern.append("[^/]*");
            } else {
                /* escape special characters */
                if ("([{\\^-$|}])?*+.".indexOf(ch) >= 0)
                    sbPattern.append('\\');
                sbPattern.append(ch);
            }
        }
        sbPattern.append('$');
        /* add the pattern to the list of files */
        m_il.event("Pattern: " + sbPattern.toString());
        m_listFiles.add(sbPattern.toString());
        m_il.exit();
    } /* addFile */

    /*--------------------------------------------------------------------*/

    /**
     * parseFiles gets file names from text file.
     *
     * @param sFileName text file (default file.encoding!) with file names.
     */
    private void parseFiles(String sFileName) {
        try {
            FileReader fr = new FileReader(sFileName);
            for (String sLine = readLine(fr); sLine.length() > 0; sLine = readLine(fr))
                addFile(sLine);
        } catch (FileNotFoundException fnfe) {
            handleException(fnfe);
        } catch (IOException ie) {
            handleException(ie);
        }
    } /* parseFiles */

    /*--------------------------------------------------------------------*/

    /**
     * parseFiles reads additional files from remaining arguments.
     *
     * @param args      arguments.
     * @param iPosition next unparsed argument.
     */
    private void parseFiles(Arguments args, int iPosition) {
        int iArguments = args.getArguments();
        for (; iPosition < iArguments; iPosition++)
            addFile(args.getArgument(iPosition));
    } /* parseFiles */

    /*--------------------------------------------------------------------*/

    /**
     * parseArguments
     *
     * @param args command-line arguments.
     */
    private void parseArguments(Arguments args) {
        addFile("**");
        /* unnamed arguments */
        int iPosition = 0;
        int iArguments = args.getArguments();
        if (iPosition < iArguments) {
            m_sCommand = args.getArgument(iPosition);
            iPosition++;
            if (COMMAND_INJECT.equalsIgnoreCase(m_sCommand) ||
                    COMMAND_EXTRACT.equalsIgnoreCase(m_sCommand) ||
                    COMMAND_LIST.equalsIgnoreCase(m_sCommand)) {
                m_sCommand = m_sCommand.toLowerCase();
                if (iPosition < iArguments) {
                    m_fileZipArchive = new File(args.getArgument(iPosition));
                    iPosition++;
                }
            } else {
                m_fileZipArchive = new File(m_sCommand);
                m_sCommand = COMMAND_LIST;
            }
        }
        if (iPosition < iArguments) {
            /* get first file name */
            String sFileName = args.getArgument(iPosition);
            iPosition++;
            if (sFileName.startsWith("@")) {
                m_listFiles.clear();
                parseFiles(sFileName.substring(1));
            } else {
                m_listFiles.clear();
                addFile(sFileName);
                parseFiles(args, iPosition);
            }
        }
    } /* parseArguments */

    /*--------------------------------------------------------------------*/

    /**
     * parseOptions
     *
     * @param args command-line arguments.
     */
    private void parseOptions(Arguments args) {
        if (args.getOption("q") != null)
            m_bQuiet = true;
        if (args.getOption("r") != null)
            m_bReplace = true;
        if (args.getOption("c") != null)
            m_bCompress = true;
        String s = args.getOption("z");
        if (s != null)
            m_sZipComment = s;
        s = args.getOption("d");
        m_fileZipRoot = new File("");
        if (s != null)
            m_fileZipRoot = new File(s);
        m_fileZipRoot = new File(m_fileZipRoot.getAbsoluteFile() + File.separator + ".");
    } /* parseOptions */

    /*--------------------------------------------------------------------*/

    /**
     * matchFiles returns true, if the file name matches one of the
     * file templates in the list.
     *
     * @param listFiles list of file name templates.
     * @param sFileName file name to be tested.
     * @return true, if file name matches at least one file name template.
     */
    private boolean matchFiles(List<String> listFiles, String sFileName) {
        boolean bMatch = false;
        for (Iterator<String> iterFiles = listFiles.iterator(); (!bMatch) && iterFiles.hasNext(); ) {
            String sPattern = iterFiles.next();
            bMatch = sFileName.matches(sPattern);
        }
        return bMatch;
    } /* matchFiles */

    /*--------------------------------------------------------------------*/

    /**
     * addRelativeFileNames adds all relative file/folder names immediately
     * under this folder to the list.
     *
     * @param fileRoot     root folder relative to which names are relative.
     * @param fileFolder   folder from which files are taken.
     * @param listRelative list of relative file/folder names.
     * @throws IOException in case of I/O error.
     */
    private void addRelativeFileNames(File fileRoot, File fileFolder,
                                      List<String> listRelative)
            throws IOException {
        m_il.enter(fileRoot, fileFolder);
        String[] asEntry = fileFolder.list();
        if (asEntry != null) {
            for (int iEntry = 0; iEntry < asEntry.length; iEntry++) {
                File fileEntry = new File(fileFolder.getAbsolutePath()
                                                    .substring(0,
                                                               fileFolder.getAbsolutePath()
                                                                         .length() - 1) + asEntry[iEntry]);
                String sRelative = fileEntry.getAbsolutePath().substring(fileRoot.getAbsolutePath().length() - 1);
                if (File.separatorChar != '/')
                    sRelative = sRelative.replace(File.separatorChar, '/');
                if (fileEntry.isDirectory()) {
                    if (!sRelative.endsWith("/"))
                        sRelative = sRelative + "/";
                    m_il.event("Folder entry: " + sRelative);
                    listRelative.add(sRelative);
                    String sFolder = fileEntry.getAbsolutePath();
                    if (sFolder.endsWith("."))
                        sFolder = sFolder.substring(0, sFolder.length() - 1);
                    if (!sFolder.endsWith(File.separator))
                        sFolder = sFolder + File.separator + ".";
                    addRelativeFileNames(fileRoot, new File(sFolder), listRelative);
                } else {
                    m_il.event("File entry: " + sRelative);
                    listRelative.add(sRelative);
                }
            }
        } else
            throw new IOException("Folder \"" + fileFolder.getAbsolutePath() + "\" cannot be accessed!");
        m_il.exit();
    } /* addRelativeFileNames */

    /*--------------------------------------------------------------------*/

    /**
     * getRelativeFileNames puts all relative file/folder names under root
     * into a list.
     *
     * @param fileZipRoot root folder relative to which names are relative.
     * @return list of file names relative to the root with / as separator.
     * @throws IOException in case of I/O error.
     */
    private List<String> getRelativeFileNames(File fileZipRoot)
            throws IOException {
        m_il.enter(fileZipRoot);
        List<String> listRelative = new ArrayList<String>();
        addRelativeFileNames(fileZipRoot, fileZipRoot, listRelative);
        m_il.exit();
        return listRelative;
    } /* listFiles */

    /*--------------------------------------------------------------------*/

    /**
     * createFolder creates a folder entry in the ZIP file.
     *
     * @param zf           ZIP archive.
     * @param sFolderEntry folder entry to be created.
     * @return true, if folder entry could be created.
     * @throws FileNotFoundException
     * @throws IOException           in case of I/O error.
     */
    private boolean createFolder(Zip64File zf, String sFolderEntry)
            throws FileNotFoundException, IOException {
        boolean bCreated = false;
        if (sFolderEntry.endsWith("/")) {
            FileEntry feFolder = zf.getFileEntry(sFolderEntry);
            if (feFolder == null) {
                String sFolderName = m_fileZipRoot.getAbsolutePath()
                                                  .substring(0,
                                                             m_fileZipRoot.getAbsolutePath()
                                                                          .length() - 1) + sFolderEntry.replace('/',
                                                                                                                File.separatorChar);
                File fileFolder = new File(sFolderName);
                if (fileFolder.isDirectory()) {
                    EntryOutputStream eos = zf.openEntryOutputStream(sFolderEntry,
                                                                     FileEntry.iMETHOD_STORED,
                                                                     new Date(fileFolder.lastModified()));
                    eos.close();
                    bCreated = true;
                } else
                    throw new FileNotFoundException("Folder " + fileFolder.getAbsolutePath() + " cannot be injected, because it does not exist or is not a directory!");
            } else
                System.err.println("Folder entry " + sFolderEntry + " already exists and is left unreplaced!");
        } else
            throw new IOException("Folder entry " + sFolderEntry + " cannot be created (must be terminated with a slash)!");
        return bCreated;
    } /* createFolder */

    /*--------------------------------------------------------------------*/

    /**
     * createEntry creates an entry in the ZIP file.
     *
     * @param zf         ZIP archive.
     * @param sFileEntry folder entry to be created.
     * @return true, if entry could be created.
     * @throws FileNotFoundException
     * @throws IOException           in case of I/O error.
     */
    private boolean createFile(Zip64File zf, String sFileEntry)
            throws FileNotFoundException, IOException {
        boolean bCreated = false;
        if (!sFileEntry.endsWith("/")) {
            FileEntry feFile = zf.getFileEntry(sFileEntry);
            if (feFile == null) {
                String sFileName = m_fileZipRoot.getAbsolutePath()
                                                .substring(0,
                                                           m_fileZipRoot.getAbsolutePath()
                                                                        .length() - 1) + sFileEntry.replace('/',
                                                                                                            File.separatorChar);
                File fileInput = new File(sFileName);
                if (fileInput.isFile()) {
                    FileInputStream fis = null;
                    EntryOutputStream eos = null;
                    try {
                        eos = zf.openEntryOutputStream(sFileEntry,
                                                       m_bCompress ? FileEntry.iMETHOD_DEFLATED : FileEntry.iMETHOD_STORED,
                                                       new Date(2000 * (long) Math.ceil(fileInput.lastModified() / 2000.0)));
                        fis = new FileInputStream(fileInput);
                        byte[] buffer = new byte[BUFFER_SIZE];
                        for (int iRead = fis.read(buffer); iRead >= 0; iRead = fis.read(buffer))
                            eos.write(buffer, 0, iRead);
                        bCreated = true;
                    } finally {
                        if (fis != null)
                            fis.close();
                        if (eos != null)
                            eos.close();
                    }
                } else
                    throw new FileNotFoundException("File " + fileInput.getAbsolutePath() + " cannot be injected, because it does not exist or is not a file!");
            } else
                System.err.println("File entry " + sFileEntry + " already exists and is left unreplaced!");
        } else
            throw new IOException("File entry " + sFileEntry + " cannot be created (must not be terminated with a slash)!");
        return bCreated;
    } /* createFile */

    /*--------------------------------------------------------------------*/

    /**
     * createParentFolders creates all missing parent folders in the ZIP file.
     *
     * @param zf         ZIP archive.
     * @param sEntryName entry whose parents folders need to be created.
     * @throws FileNotFoundException
     * @throws IOException           in case of I/O error.
     */
    private void createParentFolders(Zip64File zf, String sEntryName)
            throws FileNotFoundException, IOException {
        int iParent = sEntryName.substring(0, sEntryName.length() - 1).lastIndexOf('/');
        if (iParent >= 0) {
            String sEntryParent = sEntryName.substring(0, iParent + 1);
            FileEntry feParent = zf.getFileEntry(sEntryParent);
            if (feParent == null) {
                createParentFolders(zf, sEntryParent);
                createFolder(zf, sEntryParent);
            }
        }
    } /* createParentFolders */

    /*--------------------------------------------------------------------*/

    /**
     * listFileEntry lists the file entry.
     *
     * @param fe FileEntry to be listed.
     */
    private void listFileEntry(FileEntry fe) {
        System.out.print(fe.getName());
        int iVersion = fe.getVersionNeeded();
        if (iVersion < FileEntry.iVERSION_NEEDED_ZIP64)
            System.out.println(" - ZIP32 entry");
        else
            System.out.println();
        String sComment = fe.getComment();
        if (SU.isNotEmpty(sComment))
            System.out.println("  Comment        : " + sComment);
        Date dateFe = fe.getTimestamp();
        System.out.println("  Date           : " + DATE_FORMAT.format(dateFe));
        System.out.println("  Size           : " + SIZE_FORMAT.format(fe.getSize()));
        if (fe.getMethod() == FileEntry.iMETHOD_DEFLATED)
            System.out.println("  Compressed size: " + SIZE_FORMAT.format(fe.getCompressedSize()));
        String sCrc = Long.toHexString(fe.getCrc());
        while (sCrc.length() < 8)
            sCrc = "0" + sCrc;
        System.out.println("  Crc            : 0x" + sCrc);
        System.out.println("");
    } /* listFileEntry */

    /*--------------------------------------------------------------------*/

    /**
     * listArchive
     *
     * @param fileArchive ZIP archive.
     * @param listFiles   files to be listed.
     */
    private void listArchive(List<String> listFiles, File fileArchive) {
        try {
            System.out.println("Listing file entries in " + fileArchive.getAbsolutePath() + " ...\n");
            Zip64File zf = new Zip64File(fileArchive);
            m_sZipComment = zf.getComment();
            if (SU.isNotEmpty(m_sZipComment))
                System.out.println("ZIP64 archive comment: " + m_sZipComment + "\n");
            List<FileEntry> listFe = zf.getListFileEntries();
            long lMatches = 0;
            for (Iterator<FileEntry> iterFe = listFe.iterator(); iterFe.hasNext(); ) {
                FileEntry fe = iterFe.next();
                if (matchFiles(listFiles, fe.getName())) {
                    listFileEntry(fe);
                    lMatches++;
                }
            }
            zf.close();
            System.out.println(SIZE_FORMAT.format(lMatches) + " matching file entries found.");
            m_iReturn = RETURN_OK;
        } catch (FileNotFoundException fnfe) {
            handleException(fnfe);
        } catch (IOException ie) {
            handleException(ie);
        }
    } /* listArchive */

    /*--------------------------------------------------------------------*/

    /**
     * extractFileEntry
     *
     * @param zf ZIP archive.
     * @param fe FileEntry to be extracted.
     * @return true, if file entry could be extracted.
     * @throws FileNotFoundException
     * @throws IOException           in case of I/O error.
     */
    private boolean extractFileEntry(Zip64File zf, FileEntry fe)
            throws IOException, FileNotFoundException {
        boolean bExtracted = false;
        String sFileName = m_fileZipRoot.getAbsolutePath();
        if (sFileName.endsWith("."))
            sFileName = sFileName.substring(0, sFileName.length() - 1);
        if (sFileName.endsWith(File.separator))
            sFileName = sFileName.substring(0, sFileName.length() - 1);
        sFileName = sFileName + File.separator + fe.getName().replace('/', File.separatorChar);
        File fileOutput = new File(sFileName);
        /* create all missing folders */
        fileOutput.getParentFile().mkdirs();
        if (fe.isDirectory())
            bExtracted = fileOutput.mkdir();
        else {
            if (!fileOutput.exists() || m_bReplace) {
                if (fileOutput.exists())
                    displayMessage("replacing " + fileOutput.getAbsolutePath() + " by " + fe.getName());
                else
                    displayMessage("extracting " + fileOutput.getAbsolutePath() + " from " + fe.getName());
                /* extract the file entry */
                FileOutputStream fos = new FileOutputStream(fileOutput);
                EntryInputStream eis = zf.openEntryInputStream(fe.getName());
                byte[] buffer = new byte[BUFFER_SIZE];
                for (int iRead = eis.read(buffer); iRead >= 0; iRead = eis.read(buffer))
                    fos.write(buffer, 0, iRead);
                eis.close();
                fos.close();
                bExtracted = true;
            } else
                System.err.println("File " + fileOutput.getAbsolutePath() + " already exists and is left unreplaced!");
        }
        return bExtracted;
    } /* extractFileEntry */

    /*--------------------------------------------------------------------*/

    /**
     * stampFileEntry
     *
     * @param fe FileEntry to be stamped.
     * @return true, if file could be stamped.
     */
    private boolean stampFileEntry(FileEntry fe) {
        boolean bStamped = false;
        String sFileName = m_fileZipRoot.getAbsolutePath();
        if (sFileName.endsWith("."))
            sFileName = sFileName.substring(0, sFileName.length() - 1);
        if (sFileName.endsWith(File.separator))
            sFileName = sFileName.substring(0, sFileName.length() - 1);
        sFileName = sFileName + File.separator + fe.getName().replace('/', File.separatorChar);
        File fileOutput = new File(sFileName);
        Date dateFe = fe.getTimestamp();
        bStamped = fileOutput.setLastModified(dateFe.getTime());
        return bStamped;
    } /* stampFileEntry */

    /*--------------------------------------------------------------------*/

    /**
     * extractArchive
     *
     * @param fileArchive ZIP archive.
     * @param listFiles   files to be extracted.
     */
    private void extractArchive(List<String> listFiles, File fileArchive) {
        try {
            displayMessage("Extracting from " + fileArchive.getAbsolutePath() + " ...");
            Zip64File zf = new Zip64File(fileArchive);
            m_sZipComment = zf.getComment();
            if (SU.isNotEmpty(m_sZipComment))
                displayMessage("ZIP64 archive comment: " + m_sZipComment);
            List<FileEntry> listFe = zf.getListFileEntries();
            long lExtractions = 0;
            for (Iterator<FileEntry> iterFe = listFe.iterator(); iterFe.hasNext(); ) {
                FileEntry fe = iterFe.next();
                if (matchFiles(listFiles, fe.getName())) {
                    if (extractFileEntry(zf, fe))
                        lExtractions++;
                }
            }
      /* creating files in folders changes their time stamp.
         Therefore apply all time stamps in the end */
            for (Iterator<FileEntry> iterFe = listFe.iterator(); iterFe.hasNext(); ) {
                FileEntry fe = iterFe.next();
                if (matchFiles(listFiles, fe.getName()))
                    stampFileEntry(fe);
            }
            zf.close();
            displayMessage(SIZE_FORMAT.format(lExtractions) + " matching file entries extracted.");
            m_iReturn = RETURN_OK;
        } catch (FileNotFoundException fnfe) {
            handleException(fnfe);
        } catch (IOException ie) {
            handleException(ie);
        }
    } /* extractArchive */

    /*--------------------------------------------------------------------*/

    /**
     * injectFile
     *
     * @param zf         ZIP archive.
     * @param sEntryName name of file entry.
     * @return true, if file could be injected successfully.
     * @throws FileNotFoundException
     * @throws IOException           in case of I/O error.
     */
    private boolean injectFileEntry(Zip64File zf, String sEntryName)
            throws FileNotFoundException, IOException {
        boolean bInjected = false;
        /* create missing folder entries */
        createParentFolders(zf, sEntryName);
        /* replace? */
        FileEntry fe = zf.getFileEntry(sEntryName);
        if (fe != null) {
            if (m_bReplace) {
                displayMessage("replacing " + sEntryName);
                zf.delete(sEntryName);
            }
        } else
            displayMessage("inserting " + sEntryName);
        if (sEntryName.endsWith("/"))
            bInjected = createFolder(zf, sEntryName);
        else
            bInjected = createFile(zf, sEntryName);
        return bInjected;
    } /* injectFileEntry */

    /*--------------------------------------------------------------------*/

    /**
     * injectArchive
     *
     * @param listFiles   files to be listed.
     * @param fileArchive ZIP archive.
     */
    private void injectArchive(List<String> listFiles, File fileArchive) {
        Zip64File zf = null;
        try {
            displayMessage("Injecting into " + fileArchive.getAbsolutePath() + " ...");
            zf = new Zip64File(fileArchive);
            if (SU.isNotEmpty(m_sZipComment))
                zf.setComment(m_sZipComment);
            List<String> listRelative = getRelativeFileNames(m_fileZipRoot);
            long lInjections = 0;
            for (Iterator<String> iterRelative = listRelative.iterator(); iterRelative.hasNext(); ) {
                String sRelative = iterRelative.next();
                if (matchFiles(listFiles, sRelative)) {
                    if (injectFileEntry(zf, sRelative))
                        lInjections++;
                }
            }
            displayMessage(SIZE_FORMAT.format(lInjections) + " matching file entries injected.");
            m_iReturn = RETURN_OK;
        } catch (FileNotFoundException fnfe) {
            handleException(fnfe);
        } catch (IOException ie) {
            handleException(ie);
        } finally {
            if (zf != null) {
                try {
                    zf.close();
                } catch (IOException ie) {
                }
            }
        }
    } /* injectArchive */

    /*--------------------------------------------------------------------*/

    /**
     * constructor
     *
     * @param args command-line arguments.
     */
    private zip64(Arguments args) {
        m_iReturn = RETURN_ERROR;
        if (args.getOption("h") != null)
            displayHelp();
        else {
            /* global format */
            /* argument processing */
            parseArguments(args);
            parseOptions(args);
            if (COMMAND_LIST.equals(m_sCommand))
                m_bQuiet = false;
            displayMessage(PROGRAM + " " + VERSION + " - " + BANNER);
            displayMessage(COPYRIGHT);
            displayMessage(CDDL);
            if ((m_fileZipArchive != null) && (m_listFiles.size() > 0)) {
                if (COMMAND_LIST.equals(m_sCommand)) {
                    if (m_fileZipArchive.exists())
                        listArchive(m_listFiles, m_fileZipArchive);
                    else
                        System.err.println("File " + m_fileZipArchive.getAbsolutePath() + " does not exist!");
                } else if (m_fileZipRoot.isDirectory()) {
                    if (COMMAND_EXTRACT.equals(m_sCommand)) {
                        if (m_fileZipArchive.exists())
                            extractArchive(m_listFiles, m_fileZipArchive);
                        else
                            System.err.println("File " + m_fileZipArchive.getAbsolutePath() + " does not exist!");
                    } else if (COMMAND_INJECT.equals(m_sCommand))
                        injectArchive(m_listFiles, m_fileZipArchive);
                } else
                    System.err.println("Root " + m_fileZipRoot.getAbsolutePath() + " must be a folder!");
            } else
                displayHelp();
        }
    } /* constructor zip64 */

    /*--------------------------------------------------------------------*/

    /**
     * main class expects arguments zip archive and files.
     *
     * @param args none for defaults, -h for help,
     *             or command, options, zip archive and files.
     */
    public static void main(String[] args) {
        zip64 z64 = new zip64(Arguments.newInstance(args));
        System.exit(z64.m_iReturn);
    } /* main */

} /* class zip64 */
