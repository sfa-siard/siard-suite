/*== zip64Tester.java ==================================================
zip64Tester implements JUnit tests for the command-line zip64 utility.
Version     : $Id: zip64Tester.java 51 2016-09-07 17:11:10Z hartwigthomas $
Application : ZIP Utilities
Description : zip64Tester implements JUnit tests for the command-line
              zip64 utility.
              It tests the portions of zip64 that have not been tested
              by Zip64FileTester.java. 
------------------------------------------------------------------------
Copyright  : Enter AG, Zurich, Switzerland, 2010
Created    : 14.04.2010, Hartwig Thomas
------------------------------------------------------------------------
The class ch.enterag.zip.zip64Tester is free software; you can 
redistribute it and/or modify it under the terms of the GNU General Public 
License version 2 or later as published by the Free Software Foundation.

ch.enterag.zip.zip64Tester is distributed in the hope that it will 
be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

If you have a need for licensing ch.enterag.zip.zip64Tester without 
some of the restrictions specified in the GNU General Public License,
it is possible to negotiate a different license with the copyright holder.
======================================================================*/
package ch.enterag.utils.zip;

import ch.enterag.utils.EU;
import ch.enterag.utils.lang.Execute;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.text.*;
import java.util.Date;

import static org.junit.Assert.fail;

/*====================================================================*/

/**
 * Tests zip64.
 *
 * @author Hartwig Thomas
 */
// TODO: fix tests - they fail due to new file locations (?)
@Ignore
public class zip64Tester {
    /**
     * small file size for test file
     */
    private final static int iSMALL_SIZE = 12345;
    /**
     * temp location
     */
    private final static String sTEMP_LOCATION = "tmp";
    /**
     * directory for unzipped files
     */
    private final static String sFOLDER_UNZIP = sTEMP_LOCATION + "/unzip64";
    /**
     * empty folder
     */
    private final static String sFOLDER_EMPTY = sFOLDER_UNZIP + "/empty";
    /**
     * full folder
     */
    private final static String sFOLDER_FULL = sFOLDER_UNZIP + "/full";
    /**
     * empty file
     */
    private final static String sFILE_EMPTY = sFOLDER_FULL + "/empty.txt";
    /**
     * full file
     */
    private final static String sFILE_FULL = sFOLDER_FULL + "/full.txt";
    /**
     * directory for zipped files
     */
    private final static String sFOLDER_ZIP = sTEMP_LOCATION + "/zip64";
    /**
     * zip file
     */
    private final static String sFILE_ZIP = sFOLDER_ZIP + "/test.zip";
    /**
     * directory for test files
     */
    private final static String sFOLDER_TEST = sTEMP_LOCATION + "/test";
    /**
     * test zip file
     */
    private final static String sFILE_TEST = sFOLDER_TEST + "/test.zip";
    /**
     * list file
     */
    private final static String sFILE_LIST = sTEMP_LOCATION + "/list.txt";
    /**
     * global file comment
     */
    private final static String sZIP_COMMENT = "a global ZIP file comment";
    /**
     * buffer size
     */
    private final static int iBUFFER_SIZE = 4096;
    /**
     * date format of zip64
     */
    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    /**
     * size format of zip64
     */
    private static final NumberFormat SIZE_FORMAT = new DecimalFormat("#,##0", new DecimalFormatSymbols());
    /**
     * zip executables
     */
    private ZipProperties _zp = ZipProperties.getInstance();
    private String _sPkZipC = _zp.getPkzipc();
    private String _sZip30 = _zp.getZip30();

    /**
     * delete folder with all test files
     *
     * @param file folder containing test files.
     * @return true, if folder could be deleted.
     */
    private boolean deleteAll(File file) {
        boolean bDeleted = true;
        if (file.isDirectory()) {
            File[] afile = file.listFiles();
            for (int iFile = 0; bDeleted && (iFile < afile.length); iFile++)
                bDeleted = deleteAll(afile[iFile]);
        }
        if (bDeleted)
            bDeleted = file.delete();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            fail(EU.getExceptionMessage(ie));
        }
        return bDeleted;
    } /* deleteAll */

    /**
     * compare the files in two folders.
     *
     * @param file1 first folder.
     * @param file2 second folder.
     * @return true, if files are identical.
     */
    private boolean compareAll(File file1, File file2) {
        boolean bEqual = false;
        if (file1.isDirectory() && file2.isDirectory()) {
            File[] afile1 = file1.listFiles();
            bEqual = (afile1.length == file2.listFiles().length);
            if (bEqual) {
                String sFolderName2 = file2.getAbsolutePath();
                if (sFolderName2.endsWith("."))
                    sFolderName2 = sFolderName2.substring(0, sFolderName2.length() - 1);
                if (sFolderName2.endsWith(File.separator))
                    sFolderName2 = sFolderName2.substring(0, sFolderName2.length() - 1);
                for (int iFile = 0; bEqual && (iFile < afile1.length); iFile++) {
                    File file = new File(sFolderName2 + File.separator + afile1[iFile].getName());
                    if (file.exists()) {
                        if (compareAll(afile1[iFile], file)) {
                            long l1 = afile1[iFile].lastModified() / 2000;
                            long l2 = file.lastModified() / 2000;
                            if (Math.abs(l1 - l2) > 1) {
                                Date date1 = new Date(afile1[iFile].lastModified());
                                Date date2 = new Date(file.lastModified());
                                fail("Time stamps of " + file1.getAbsolutePath() + " (" + String.valueOf(date1) + ") and " +
                                             file2.getAbsolutePath() + " (" + String.valueOf(date2) + ") are different!");
                                bEqual = false;
                            }
                        }
                    } else {
                        fail("File  " + file.getAbsolutePath() + " does not exist!");
                        bEqual = false;
                    }
                }
            }
        } else if (file1.isFile() && file2.isFile()) {
            if (file1.getName().equals(file2.getName())) {
                long l1 = file1.lastModified() / 2000;
                long l2 = file2.lastModified() / 2000;
                if (Math.abs(l1 - l2) <= 1) {
                    if (file1.length() == file2.length()) {
                        try {
                            FileInputStream fis1 = new FileInputStream(file1);
                            FileInputStream fis2 = new FileInputStream(file2);
                            byte[] buf1 = new byte[iBUFFER_SIZE];
                            byte[] buf2 = new byte[iBUFFER_SIZE];
                            int iRead1 = fis1.read(buf1);
                            int iRead2 = fis2.read(buf2);
                            boolean bEOF = false;
                            while (bEqual && (iRead1 == iRead2) && (!bEOF)) {
                                for (int iByte = 0; bEqual && (iByte < iRead1); iByte++) {
                                    if (buf1[iByte] != buf2[iByte]) {
                                        fail("Contents of " + file1.getAbsolutePath() + " and " + file2.getAbsolutePath() + " are different!");
                                        bEqual = false;
                                    }
                                }
                                if (bEqual) {
                                    if (iRead1 == -1)
                                        bEOF = true;
                                    else {
                                        iRead1 = fis1.read(buf1);
                                        iRead2 = fis2.read(buf2);
                                    }
                                }
                            }
                            fis1.close();
                            fis2.close();
                        } catch (FileNotFoundException fnfe) {
                            fail(fnfe.getClass().getName() + ": " + fnfe.getMessage());
                        } catch (IOException ie) {
                            fail(ie.getClass().getName() + ": " + ie.getMessage());
                        }

                    } else {
                        fail("Sizes of " + file1.getAbsolutePath() + " and " + file2.getAbsolutePath() + " are different!");
                        bEqual = false;
                    }
                } else {
                    Date date1 = new Date(file1.lastModified());
                    Date date2 = new Date(file2.lastModified());
                    fail("Time stamps of " + file1.getAbsolutePath() + " (" + String.valueOf(date1) + ") and " +
                                 file2.getAbsolutePath() + " (" + String.valueOf(date2) + ") are different!");
                    bEqual = false;
                }
            } else {
                fail("Names of " + file1.getAbsolutePath() + " and " + file2.getAbsolutePath() + " are different!");
                bEqual = false;
            }
        } else
            fail("directory entries are not both files or both folders!");
        return bEqual;
    } /* compareAll */

    private void zipPkZip(File fileFolderUnzip, File fileFileZip) {
        /* use pkzipc to create zip file in zip directory */
        String[] asProg = new String[]
                {
                        _sPkZipC,
                        "-add=all",
                        "-attr=all",
                        "-dir=specify",
                        "-silent=normal",
                        "-header=" + sZIP_COMMENT,
                        fileFileZip.getAbsolutePath(),
                        fileFolderUnzip.getAbsolutePath() + "/*"
                };
        Execute exec = Execute.execute(asProg);
        System.out.println(exec.getStdOut());
        int iExitCode = exec.getResult();
        if (iExitCode != 0) {
            System.err.println(exec.getStdErr());
            fail(_sPkZipC + " exit code: " + String.valueOf(iExitCode));
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    } /* zipPkZip */

    private void zipInfoZip(File fileFolderUnzip, File fileFileZip) {
        /* use Info-ZIP zip.exe to create zip file in zip directory */
        String[] asProg = new String[]
                {
                        _sZip30,
                        "-r",
                        "-z",
                        fileFileZip.getAbsolutePath(),
                        ".",
                        "-i",
                        "*"
                };

        StringReader rdrInput = new StringReader(sZIP_COMMENT + "\u001A");
        Execute exec = Execute.execute(asProg, fileFolderUnzip, rdrInput);
        rdrInput.close();
        System.out.println(exec.getStdOut());
        int iExitCode = exec.getResult();
        if (iExitCode != 0) {
            System.err.println(exec.getStdErr());
            fail(_sZip30 + " exit code: " + String.valueOf(iExitCode));
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    private void zip64Zip(File fileFolderUnzip, File fileFileZip) {
        /* use zip64 to create zip file in zip directory */
        String[] asProg = new String[]
                {
                        "java",
                        "-jar",
                        "dist/zip64.jar",
                        "n",
                        "-d=" + fileFolderUnzip.getAbsolutePath(),
                        "-c",
                        "-q",
                        "-r",
                        "-z=" + sZIP_COMMENT,
                        fileFileZip.getAbsolutePath()
                };
        Execute exec = Execute.execute(asProg);
        System.out.println(exec.getStdOut());
        int iExitCode = exec.getResult();
        if (iExitCode != 0) {
            System.err.println(exec.getStdErr());
            fail("zip64 exit code: " + String.valueOf(iExitCode));
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    @Before
    public void setUp()
            throws IOException {
        /* create two folders and two test files in a zip folder using pkzipc */
        File fileFolderUnzip = new File(sFOLDER_UNZIP);
        if (deleteAll(fileFolderUnzip))
            System.out.println("Deleted files in " + fileFolderUnzip.getAbsolutePath());
        else
            System.out.println("Cannot delete files in " + fileFolderUnzip.getAbsolutePath());
        /* in unzip directory ... */
        if (fileFolderUnzip.mkdirs()) {
            System.out.println("mkdirs " + fileFolderUnzip + " successful.");
            /* ... create empty folder ... */
            File fileFolderEmpty = new File(sFOLDER_EMPTY);
            if (fileFolderEmpty.mkdir()) {
                System.out.println("mkdir " + fileFolderEmpty + " successful.");
                /* ... and full folder ... */
                File fileFolderFull = new File(sFOLDER_FULL);
                if (fileFolderFull.mkdir()) {
                    System.out.println("mkdir " + fileFolderFull + " successful.");
                    /* ... and empty file ... */
                    File fileFileEmpty = new File(sFILE_EMPTY);
                    if (fileFileEmpty.createNewFile()) {
                        System.out.println("createNewFile " + fileFileEmpty + " successful.");
                        /* ... and full file */
                        File fileFileFull = new File(sFILE_FULL);
                        FileOutputStream fos = new FileOutputStream(fileFileFull);
                        /* random size 0 .. iSMALL_SIZE-1 */
                        int iLength = (int) Math.ceil(iSMALL_SIZE * Math.random());
                        byte[] buffer = new byte[iLength];
                        /* fill the buffer with random characters */
                        for (int i = 0; i < buffer.length; i++)
                            buffer[i] = (byte) (32 + (int) Math.ceil(96 * Math.random()));
                        fos.write(buffer);
                        fos.close();
                        System.out.println(String.valueOf(iLength) + " bytes written to " + fileFileFull.getAbsolutePath() + ".");
                        /* in zip directory ... */
                        File fileFolderZip = new File(sFOLDER_ZIP);
                        if (deleteAll(fileFolderZip))
                            System.out.println("Deleted files in " + fileFolderZip.getAbsolutePath());
                        else
                            System.out.println("Cannot delete files in " + fileFolderZip.getAbsolutePath());
                        if (fileFolderZip.mkdirs()) {
                            File fileFileZip = new File(sFILE_ZIP);
                            if (fileFileZip.exists())
                                fileFileZip.delete();
                            if (_sPkZipC != null)
                                zipPkZip(fileFolderUnzip, fileFileZip);
                            else if (_sZip30 != null)
                                zipInfoZip(fileFolderUnzip, fileFileZip);
                            else
                                zip64Zip(fileFolderUnzip, fileFileZip);
                        } else
                            fail("Cannot make directories for " + fileFolderZip.getAbsolutePath());
                    } else
                        fail("Cannot create " + fileFileEmpty.getAbsolutePath());
                } else
                    fail("Cannot make directory " + fileFolderFull.getAbsolutePath());
            } else
                fail("Cannot make directory " + fileFolderEmpty.getAbsolutePath());
        } else
            fail("Cannot make directories for " + fileFolderUnzip.getAbsolutePath());

    } /* setUp */

    @After
    public void tearDown()
            throws IOException {
        /***
         File fileFolderUnzip = new File(sFOLDER_UNZIP);
         deleteAll(fileFolderUnzip);
         File fileFolderZip = new File(sFOLDER_ZIP);
         deleteAll(fileFolderZip);
         ***/
    } /* tearDown */

    /**
     * Test method for help.
     */
    @Test
    public void testHelp() {
        System.out.println("testHelp");
        String[] asCommand = new String[]
                {
                        "java", /* must be in path */
                        "-jar",
                        "dist/zip64.jar",
                        "-h"
                };
        Execute exec = Execute.execute(asCommand);
        int iExitCode = exec.getResult();
        if (iExitCode != 4)
            fail("zip64 exit code: " + String.valueOf(iExitCode));
        if (exec.getStdErr().length() > 0)
            fail("Invalid error: " + exec.getStdErr());
        if (!exec.getStdOut().startsWith("Usage:"))
            fail("Invalid output: " + exec.getStdOut());
    } /* testHelp */

    /**
     * Test method for list.
     */
    @Test
    public void testList() {
        System.out.println("testList");
        String[] asCommand = new String[]
                {
                        "java", /* must be in path */
                        "-jar",
                        "dist/zip64.jar",
                        sFILE_ZIP
                };
        Execute exec = Execute.execute(asCommand);
        int iExitCode = exec.getResult();
        if (iExitCode != 0)
            fail("zip64 exit code: " + String.valueOf(iExitCode));
        if (exec.getStdErr().length() > 0)
            fail("Invalid error: " + exec.getStdErr());
        /* analyze output */
        String sOutput = exec.getStdOut();
        /* analyze file comment */
        String sPrompt = "ZIP64 archive comment: ";
        int iPosition = sOutput.indexOf(sPrompt);
        sOutput = sOutput.substring(iPosition + sPrompt.length());
        if ((iPosition < 0) || (!sOutput.startsWith(sZIP_COMMENT)))
            fail("Incorrect ZIP file comment!");
        /* analyze file name */
        sOutput = sOutput.substring(sZIP_COMMENT.length());
        File fileEntry = null;
        int iEntries = 4;
        for (int iEntry = 0; iEntry < iEntries; iEntry++) {
            sOutput = sOutput.trim();
            iPosition = sOutput.indexOf(' ');
            String sEntry = sOutput.substring(0, iPosition).trim();
            sOutput = sOutput.substring(iPosition).trim();
            if (sEntry.equals("empty/") || sEntry.equals("full/") || sEntry.equals("full/empty.txt") || sEntry.equals(
                    "full/full.txt")) {
                if (sEntry.equals("empty/"))
                    fileEntry = new File(sFOLDER_EMPTY);
                else if (sEntry.equals("full/"))
                    fileEntry = new File(sFOLDER_FULL);
                else if (sEntry.equals("full/empty.txt"))
                    fileEntry = new File(sFILE_EMPTY);
                else if (sEntry.equals("full/full.txt"))
                    fileEntry = new File(sFILE_FULL);
                if (!sOutput.startsWith("- ZIP32 entry"))
                    fail("Entry " + sEntry + " must be ZIP32 entry!");
                /* file date/time */
                iPosition = sOutput.indexOf(": ");
                sOutput = sOutput.substring(iPosition + 2);
                /* 2 seconds is maximum resolution of DOS date */
                boolean bDateIncorrect = true;
                double dLastModifiedSec = fileEntry.lastModified() / 1000.0;
                for (long lTime = (long) Math.floor(dLastModifiedSec - 2.0); bDateIncorrect && (lTime <= (long) Math.ceil(
                        dLastModifiedSec + 2.0)); lTime = lTime + 1L) {
                    String sFileDate = DATE_FORMAT.format(new Date(1000 * lTime));
                    if (!sOutput.startsWith(sFileDate))
                        bDateIncorrect = false;
                }
                if (bDateIncorrect)
                    fail("Date of " + sEntry + " incorrect!");
                /* size */
                iPosition = sOutput.indexOf(": ");
                sOutput = sOutput.substring(iPosition + 2);
                String sSize = "0";
                if (sEntry.equals("full/full.txt"))
                    sSize = SIZE_FORMAT.format(fileEntry.length());
                if (!sOutput.startsWith(sSize))
                    fail("Size of " + sEntry + " incorrect!");
                iPosition = sOutput.indexOf('\n');
                sOutput = sOutput.substring(iPosition).trim();
                /* compressed size */
                if (sEntry.equals("full/full.txt") || sEntry.equals("full/empty.txt")) {
                    if (sOutput.startsWith("Compressed size")) {
                        iPosition = sOutput.indexOf(": ");
                        sOutput = sOutput.substring(iPosition + 2);
                        iPosition = sOutput.indexOf('\n');
                        try {
                            SIZE_FORMAT.parse(sOutput.substring(0, iPosition).trim());
                        } catch (ParseException pe) {
                            fail("Compressed size of " + sEntry + " could not be parsed!");
                        }
                        sOutput = sOutput.substring(iPosition);
                    }
                }
                /* crc */
                iPosition = sOutput.indexOf(": ");
                sOutput = sOutput.substring(iPosition + 2);
                if (!sOutput.startsWith("0x"))
                    fail("Invalid Crc!");
                if (!sEntry.equals("full/full.txt")) {
                    if (!sOutput.startsWith("0x00000000"))
                        fail("Invalid zero Crc!");
                }
                /* end of line */
                iPosition = sOutput.indexOf('\n');
                sOutput = sOutput.substring(iPosition);
            }
        }
        /* number of entries */
        sOutput = sOutput.trim();
        String sSize = SIZE_FORMAT.format(iEntries);
        if (!sOutput.startsWith(sSize))
            fail("Invalid number of entries!");
    } /* testList */

    /**
     * Test method for basic injection case.
     */
    @Test
    public void testInject() {
        /* compress contents of UNZIP folder to new test ZIP file */
        File fileFolderTest = new File(sFOLDER_TEST);
        /* recover from unclean test termination */
        deleteAll(fileFolderTest);
        /* now create folder */
        fileFolderTest.mkdir();
        System.out.println("testInject");
        String[] asCommand = new String[]
                {
                        "java", /* must be in path */
                        "-jar",
                        "dist/zip64.jar",
                        "n",
                        "-d=" + sFOLDER_UNZIP,
                        sFILE_TEST
                };
        Execute exec = Execute.execute(asCommand);
        int iExitCode = exec.getResult();
        if (iExitCode != 0)
            fail("zip64 exit code: " + String.valueOf(iExitCode));
        if (exec.getStdErr().length() > 0)
            fail("Invalid error: " + exec.getStdErr());
        /* clean up */
        deleteAll(fileFolderTest);
    } /* testInject */

    /**
     * Test method for basic injection case without replacement.
     */
    @Test
    public void testInjectNoReplace() {
        System.out.println("testInjectNoReplace");
        String[] asCommand = new String[]
                {
                        "java", /* must be in path */
                        "-jar",
                        "dist/zip64.jar",
                        "n",
                        "-d=" + sFOLDER_UNZIP,
                        sFILE_ZIP
                };
        Execute exec = Execute.execute(asCommand);
        int iExitCode = exec.getResult();
        if (iExitCode != 0)
            fail("zip64 exit code: " + String.valueOf(iExitCode));
        if (exec.getStdErr().length() <= 0)
            fail("Error must list unreplaced entries!");
    } /* testInjectNoReplace */

    /**
     * Test method for basic injection case with replacement.
     */
    @Test
    public void testInjectReplace() {
        System.out.println("testInjectReplace");
        String[] asCommand = new String[]
                {
                        "java", /* must be in path */
                        "-jar",
                        "dist/zip64.jar",
                        "n",
                        "-d=" + sFOLDER_UNZIP,
                        "-r",
                        sFILE_ZIP
                };
        Execute exec = Execute.execute(asCommand);
        System.out.println(exec.getStdOut());
        int iExitCode = exec.getResult();
        System.out.println(exec.getStdOut());
        if (iExitCode != 0) {
            System.err.println(exec.getStdErr());
            fail("zip64 exit code: " + String.valueOf(iExitCode));
        }
        if (exec.getStdErr().length() > 0)
            fail("Invalid error: " + exec.getStdErr());
    } /* testInjectReplace */

    /**
     * Test method for injection of a single empty file.
     */
    @Test
    public void testInjectFile() {
        /* compress contents of UNZIP folder to new test ZIP file */
        File fileFolderTest = new File(sFOLDER_TEST);
        /* recover from unclean test termination */
        deleteAll(fileFolderTest);
        /* now create folder */
        fileFolderTest.mkdir();
        System.out.println("testInjectFile");
        String[] asCommand = new String[]
                {
                        "java", /* must be in path */
                        "-jar",
                        "dist/zip64.jar",
                        "n",
                        "-d=" + sFOLDER_UNZIP,
                        sFILE_TEST,
                        "full/empty.txt"
                };
        Execute exec = Execute.execute(asCommand);
        int iExitCode = exec.getResult();
        if (iExitCode != 0)
            fail("zip64 exit code: " + String.valueOf(iExitCode));
        if (exec.getStdErr().length() > 0)
            fail("Invalid error: " + exec.getStdErr());
        /* clean up */
        deleteAll(fileFolderTest);
    } /* testInjectFile */

    /**
     * Test method for injection of a single full folder.
     */
    @Test
    public void testInjectFolder() {
        /* compress contents of UNZIP folder to new test ZIP file */
        File fileFolderTest = new File(sFOLDER_TEST);
        /* recover from unclean test termination */
        deleteAll(fileFolderTest);
        /* now create folder */
        fileFolderTest.mkdir();
        System.out.println("testInjectFolder");
        String[] asCommand = new String[]
                {
                        "java", /* must be in path */
                        "-jar",
                        "dist/zip64.jar",
                        "n",
                        "-d=" + sFOLDER_UNZIP,
                        sFILE_TEST,
                        "full/*"
                };
        Execute exec = Execute.execute(asCommand);
        int iExitCode = exec.getResult();
        if (iExitCode != 0)
            fail("zip64 exit code: " + String.valueOf(iExitCode));
        if (exec.getStdErr().length() > 0)
            fail("Invalid error: " + exec.getStdErr());
        if (exec.getStdOut().indexOf("3 matching file entries injected") < 0)
            fail("Not 3 matching file entries injected!");
        /* clean up */
        deleteAll(fileFolderTest);
    } /* testInjectFolder */

    /**
     * Test method for injection of a folder and a file.
     */
    @Test
    public void testInjectSet() {
        /* compress contents of UNZIP folder to new test ZIP file */
        File fileFolderTest = new File(sFOLDER_TEST);
        /* recover from unclean test termination */
        deleteAll(fileFolderTest);
        /* now create folder */
        fileFolderTest.mkdir();
        System.out.println("testInjectSet");
        String[] asCommand = new String[]
                {
                        "java", /* must be in path */
                        "-jar",
                        "dist/zip64.jar",
                        "n",
                        "-d=" + sFOLDER_UNZIP,
                        sFILE_TEST,
                        "empty/*",
                        "full/full.txt"
                };
        Execute exec = Execute.execute(asCommand);
        int iExitCode = exec.getResult();
        if (iExitCode != 0)
            fail("zip64 exit code: " + String.valueOf(iExitCode));
        if (exec.getStdErr().length() > 0)
            fail("Invalid error: " + exec.getStdErr());
        if (exec.getStdOut().indexOf("2 matching file entries injected") < 0)
            fail("Not 2 matching file entries injected!");
        /* clean up */
        deleteAll(fileFolderTest);
    } /* testInjectSet */

    /**
     * Test method for injection of two files specified in a file list.
     */
    @Test
    public void testInjectList() {
        File fileList = new File(sFILE_LIST);
        try {
            FileWriter fw = new FileWriter(fileList);
            fw.write("full/empty.txt\n");
            fw.write("full/full.txt\n");
            fw.close();
        } catch (IOException ie) {
            fail(ie.getClass().getName() + ": " + ie.getMessage());
        }
        /* compress contents of UNZIP folder to new test ZIP file */
        File fileFolderTest = new File(sFOLDER_TEST);
        /* recover from unclean test termination */
        deleteAll(fileFolderTest);
        /* now create folder */
        fileFolderTest.mkdir();
        System.out.println("testInjectSet");
        String[] asCommand = new String[]
                {
                        "java", /* must be in path */
                        "-jar",
                        "dist/zip64.jar",
                        "n",
                        "-d=" + sFOLDER_UNZIP,
                        sFILE_TEST,
                        "@" + sFILE_LIST
                };
        Execute exec = Execute.execute(asCommand);
        int iExitCode = exec.getResult();
        if (iExitCode != 0)
            fail("zip64 exit code: " + String.valueOf(iExitCode));
        if (exec.getStdErr().length() > 0)
            fail("Invalid error: " + exec.getStdErr());
        if (exec.getStdOut().indexOf("2 matching file entries injected") < 0)
            fail("Not 2 matching file entries injected!");
        /* clean up */
        deleteAll(fileFolderTest);
        fileList.delete();
    } /* testInjectList */

    /**
     * Test method for basic extraction case.
     */
    @Test
    public void testExtract() {
        /* extract contents of ZIP file to new test folder */
        File fileFolderTest = new File(sFOLDER_TEST);
        /* recover from unclean test termination */
        deleteAll(fileFolderTest);
        /* now create folder */
        fileFolderTest.mkdir();
        System.out.println("testExtract");
        String[] asCommand = new String[]
                {
                        "java", /* must be in path */
                        "-jar",
                        "dist/zip64.jar",
                        "x",
                        "-d=" + sFOLDER_TEST,
                        sFILE_ZIP
                };
        Execute exec = Execute.execute(asCommand);
        int iExitCode = exec.getResult();
        if (iExitCode != 0)
            fail("zip64 exit code: " + String.valueOf(iExitCode));
        if (exec.getStdErr().length() > 0)
            fail("Invalid error: " + exec.getStdErr());
        /* compare the contents of UNZIP and TEST */
        File fileFolderUnzip = new File(sFOLDER_UNZIP);
        if (!compareAll(fileFolderUnzip, fileFolderTest))
            fail("Folder " + fileFolderUnzip.getAbsolutePath() + " is not equal to folder " + fileFolderTest.getAbsolutePath() + "!");
        /* clean up */
        deleteAll(fileFolderTest);
    } /* testExtract */

    /**
     * Test method for basic extraction case without replacement.
     */
    @Test
    public void testExtractNoReplace() {
        System.out.println("testExtractNoReplace");
        String[] asCommand = new String[]
                {
                        "java", /* must be in path */
                        "-jar",
                        "dist/zip64.jar",
                        "x",
                        "-d=" + sFOLDER_UNZIP,
                        sFILE_ZIP
                };
        Execute exec = Execute.execute(asCommand);
        int iExitCode = exec.getResult();
        if (iExitCode != 0)
            fail("zip64 exit code: " + String.valueOf(iExitCode));
        if (exec.getStdErr().length() <= 0)
            fail("Error must list unreplaced files!");
        /* message must be; 0 matching files extracted */
        if (exec.getStdOut().indexOf("0 matching file entries extracted") < 0)
            fail("Not 0 matching file entries extracted!");
    } /* testExtractNoReplace */

    /**
     * Test method for basic extraction case with replacement.
     */
    @Test
    public void testExtractReplace() {
        System.out.println("testExtractReplace");
        String[] asCommand = new String[]
                {
                        "java", /* must be in path */
                        "-jar",
                        "dist/zip64.jar",
                        "x",
                        "-d=" + sFOLDER_UNZIP,
                        "-r",
                        sFILE_ZIP
                };
        Execute exec = Execute.execute(asCommand);
        int iExitCode = exec.getResult();
        if (iExitCode != 0)
            fail("zip64 exit code: " + String.valueOf(iExitCode));
        if (exec.getStdErr().length() > 0)
            fail("Invalid error: " + exec.getStdErr());
        /* message must be; 4 matching files extracted */
        if (exec.getStdOut().indexOf("2 matching file entries extracted") < 0)
            fail("Not 2 matching file entries extracted!");
    } /* testExtractReplace */

    /**
     * Test method for extracting single (empty) file.
     */
    @Test
    public void testExtractFile() {
        /* extract contents of ZIP file to new test folder */
        File fileFolderTest = new File(sFOLDER_TEST);
        /* recover from unclean test termination */
        deleteAll(fileFolderTest);
        /* now create folder */
        fileFolderTest.mkdir();
        System.out.println("testExtractFile");
        String[] asCommand = new String[]
                {
                        "java", /* must be in path */
                        "-jar",
                        "dist/zip64.jar",
                        "x",
                        "-d=" + sFOLDER_TEST,
                        sFILE_ZIP,
                        "full/empty.txt"
                };
        Execute exec = Execute.execute(asCommand);
        int iExitCode = exec.getResult();
        if (iExitCode != 0)
            fail("zip64 exit code: " + String.valueOf(iExitCode));
        if (exec.getStdErr().length() > 0)
            fail("Invalid error: " + exec.getStdErr());
        /* compare the contents of UNZIP and TEST */
        File fileFolderEmpty = new File(sFOLDER_EMPTY);
        fileFolderEmpty.delete();
        File fileFileFull = new File(sFILE_FULL);
        fileFileFull.delete();
        File fileFolderUnzip = new File(sFOLDER_UNZIP);
        if (!compareAll(fileFolderUnzip, fileFolderTest))
            fail("Folder " + fileFolderUnzip.getAbsolutePath() + " is not equal to folder " + fileFolderTest.getAbsolutePath() + "!");
        /* clean up */
        deleteAll(fileFolderTest);
    } /* testExtractFile */

    /**
     * Test method for extracting single (empty) file.
     */
    @Test
    public void testExtractFolder() {
        /* extract contents of ZIP file to new test folder */
        File fileFolderTest = new File(sFOLDER_TEST);
        /* recover from unclean test termination */
        deleteAll(fileFolderTest);
        /* now create folder */
        fileFolderTest.mkdir();
        System.out.println("testExtractFolder");
        String[] asCommand = new String[]
                {
                        "java", /* must be in path */
                        "-jar",
                        "dist/zip64.jar",
                        "x",
                        "-d=" + sFOLDER_TEST,
                        sFILE_ZIP,
                        "full/*"
                };
        Execute exec = Execute.execute(asCommand);
        int iExitCode = exec.getResult();
        if (iExitCode != 0)
            fail("zip64 exit code: " + String.valueOf(iExitCode));
        if (exec.getStdErr().length() > 0)
            fail("Invalid error: " + exec.getStdErr());
        /* compare the contents of UNZIP and TEST */
        File fileFolderEmpty = new File(sFOLDER_EMPTY);
        fileFolderEmpty.delete();
        File fileFolderUnzip = new File(sFOLDER_UNZIP);
        if (!compareAll(fileFolderUnzip, fileFolderTest))
            fail("Folder " + fileFolderUnzip.getAbsolutePath() + " is not equal to folder " + fileFolderTest.getAbsolutePath() + "!");
        /* clean up */
        deleteAll(fileFolderTest);
    } /* testExtractFolder */

    /**
     * Test method for extracting of a folder and a file.
     */
    @Test
    public void testExtractSet() {
        /* extract contents of ZIP file to new test folder */
        File fileFolderTest = new File(sFOLDER_TEST);
        /* recover from unclean test termination */
        deleteAll(fileFolderTest);
        /* now create folder */
        fileFolderTest.mkdir();
        System.out.println("testExtractSet");
        String[] asCommand = new String[]
                {
                        "java", /* must be in path */
                        "-jar",
                        "dist/zip64.jar",
                        "x",
                        "-d=" + sFOLDER_TEST,
                        sFILE_ZIP,
                        "empty/*",
                        "full/full.txt"
                };
        Execute exec = Execute.execute(asCommand);
        int iExitCode = exec.getResult();
        if (iExitCode != 0)
            fail("zip64 exit code: " + String.valueOf(iExitCode));
        if (exec.getStdErr().length() > 0)
            fail("Invalid error: " + exec.getStdErr());
        /* compare the contents of UNZIP and TEST */
        File fileFileEmpty = new File(sFILE_EMPTY);
        fileFileEmpty.delete();
        File fileFolderUnzip = new File(sFOLDER_UNZIP);
        if (!compareAll(fileFolderUnzip, fileFolderTest))
            fail("Folder " + fileFolderUnzip.getAbsolutePath() + " is not equal to folder " + fileFolderTest.getAbsolutePath() + "!");
        /* clean up */
        deleteAll(fileFolderTest);
    } /* testExtractSet */

    /**
     * Test method for extracting of two files specified in a file list.
     */
    @Test
    public void testExtractList() {
        File fileList = new File(sFILE_LIST);
        try {
            FileWriter fw = new FileWriter(fileList);
            fw.write("full/empty.txt\n");
            fw.write("full/full.txt\n");
            fw.close();
        } catch (IOException ie) {
            fail(ie.getClass().getName() + ": " + ie.getMessage());
        }
        /* extract contents of ZIP file to new test folder */
        File fileFolderTest = new File(sFOLDER_TEST);
        /* recover from unclean test termination */
        deleteAll(fileFolderTest);
        /* now create folder */
        fileFolderTest.mkdir();
        System.out.println("testExtractList");
        String[] asCommand = new String[]
                {
                        "java", /* must be in path */
                        "-jar",
                        "dist/zip64.jar",
                        "x",
                        "-d=" + sFOLDER_TEST,
                        sFILE_ZIP,
                        "@" + sFILE_LIST
                };
        Execute exec = Execute.execute(asCommand);
        int iExitCode = exec.getResult();
        if (iExitCode != 0)
            fail("zip64 exit code: " + String.valueOf(iExitCode));
        if (exec.getStdErr().length() > 0)
            fail("Invalid error: " + exec.getStdErr());
        /* compare the contents of UNZIP and TEST */
        File fileFolderEmpty = new File(sFOLDER_EMPTY);
        fileFolderEmpty.delete();
        File fileFolderUnzip = new File(sFOLDER_UNZIP);
        if (!compareAll(fileFolderUnzip, fileFolderTest))
            fail("Folder " + fileFolderUnzip.getAbsolutePath() + " is not equal to folder " + fileFolderTest.getAbsolutePath() + "!");
        /* clean up */
        deleteAll(fileFolderTest);
        fileList.delete();
    } /* testExtractList */

} /* class zip64Tester */
