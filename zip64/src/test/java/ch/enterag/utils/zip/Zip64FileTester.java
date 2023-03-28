/*== Zip64FileTester.java ==============================================
Tests Zip64File.
Version     : $Id: Zip64FileTester.java 34 2011-03-31 14:34:15Z hartwigthomas $
Application : Zip64File
Description : Tests Zip64File.
------------------------------------------------------------------------
Copyright  : Enter AG, Zurich, Switzerland, 2008
Created    : 07.03.2008, Hartwig Thomas
======================================================================*/

package ch.enterag.utils.zip;

import ch.enterag.utils.EU;
import ch.enterag.utils.StopWatch;
import ch.enterag.utils.lang.Execute;
import org.junit.*;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipException;

import static org.junit.Assert.fail;

/*====================================================================*/

/**
 * Tests Zip64File.
 *
 * @author Hartwig Thomas
 */
// TODO: fix tests (they fail mostly because the file paths are different now...
@Ignore
public class Zip64FileTester {
    /**
     * buffer size for I/O
     */
    private final static int iBUFFER_SIZE = 8192;
    /**
     * small file size for I/O
     */
    private final static int iSMALL_SIZE = 16;
    /**
     * number of buffers for more than 4 GB
     */
    private final static int iLARGE_BUFFERS = 0x0A0000;
    /**
     * number of buffers for more than 65 KB
     */
    private final static int iMODERATE_BUFFERS = 20;
    /**
     * global file comment
     */
    private final static String sZIP_COMMENT = "a global ZIP file comment";
    /**
     * zip file produced by external zip
     */
    private static String m_sExtZipFile = null;
    /**
     * test zip file
     */
    private String m_sTestZipFile = null;
    /**
     * test files directory
     */
    private final static String sTESTFILES_DIRECTORY = "testfiles";
    /**
     * temp location with lots of free space which does not need to be backuped
     */
    private final static String sTEMP_LOCATION = "tmp";
    /**
     * temp directory
     */
    private final static String sTEMP_DIRECTORY = sTEMP_LOCATION + File.separator + "Temp";
    /**
     * extract directory
     */
    private final static String sEXTRACT_DIRECTORY = sTEMP_LOCATION + File.separator + "Extract";
    /**
     * zip executables
     */
    private static ZipProperties _zp = ZipProperties.getInstance();
    private static String _sPkZipC = _zp.getPkzipc();
    private static String _sZip30 = _zp.getZip30();
    private static String _sUnzip60 = _zp.getUnzip60();

    /*------------------------------------------------------------------*/

    /**
     * append a file to the ZIP64 file.
     *
     * @param zf           ZIP64 file.
     * @param sFileName    name of file entry in ZIP64 file.
     * @param fileOriginal file to be appended.
     * @param iMethod      method to be used (stored or deflated).
     */
    private void appendFile(Zip64File zf, String sFileName, File fileOriginal, int iMethod) {
        byte[] buffer = new byte[iBUFFER_SIZE];
        try {
            Date dateModified = new Date(fileOriginal.lastModified());
            FileInputStream fis = new FileInputStream(fileOriginal);
            EntryOutputStream eos = zf.openEntryOutputStream(sFileName, iMethod, dateModified);
            for (int iRead = fis.read(buffer); iRead >= 0; iRead = fis.read(buffer))
                eos.write(buffer, 0, iRead);
            fis.close();
            eos.close();
        } catch (ZipException ze) {
            System.out.println(ze.getClass().getName() + ": " + ze.getMessage());
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getClass().getName() + ": " + fnfe.getMessage());
        } catch (IOException ie) {
            System.out.println(ie.getClass().getName() + ": " + ie.getMessage());
        }
    } /* appendFile */

    /*------------------------------------------------------------------*/

    /**
     * append a directory to the ZIP64 file.
     *
     * @param zf         ZIP64 file.
     * @param sDirectory name of the directory file entry in ZIP64 file.
     */
    private void appendDirectory(Zip64File zf, String sDirectory) {
        try {
            EntryOutputStream eos = zf.openEntryOutputStream(sDirectory, FileEntry.iMETHOD_STORED, null);
            eos.close();
        } catch (ZipException ze) {
            System.out.println(ze.getClass().getName() + ": " + ze.getMessage());
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getClass().getName() + ": " + fnfe.getMessage());
        } catch (IOException ie) {
            System.out.println(ie.getClass().getName() + ": " + ie.getMessage());
        }
    } /* appendDirectory */

    /*------------------------------------------------------------------*/

    /**
     * add the test files to the ZIP64 file.
     *
     * @param iMethod method to be used (stored or deflated).
     */
    private void zipTest(int iMethod) {
        try {
            /* temp directory */
            File fileTemp = new File(sTEMP_DIRECTORY);
            if (!fileTemp.exists())
                fileTemp.mkdirs();
            /* original of moderate file */
            File fileModerateOriginal = new File(fileTemp.getAbsolutePath() + File.separator + "moderate.txt");
            /* original of medium file */
            File fileMediumOriginal = new File(fileTemp.getAbsolutePath() + File.separator + "medium.txt");
            /* original of large file */
            File fileLargeOriginal = new File(fileTemp.getAbsolutePath() + File.separator + "large.txt");
            /* test zip file */
            System.out.println("Create " + m_sTestZipFile);
            StopWatch sw = StopWatch.getInstance();
            sw.start();
            Zip64File zf = new Zip64File(m_sTestZipFile);
            /* add moderate */
            System.out.println("add " + fileModerateOriginal.getAbsolutePath());
            appendFile(zf, "moderate.txt", fileModerateOriginal, iMethod);
            /* add medium */
            System.out.println("add " + fileMediumOriginal.getAbsolutePath());
            appendFile(zf, "medium.txt", fileMediumOriginal, iMethod);
            /* add large */
            System.out.println("add " + fileLargeOriginal.getAbsolutePath());
            appendFile(zf, "large.txt", fileLargeOriginal, iMethod);
            /* add directory many */
            appendDirectory(zf, "many/");
            /* add all small files */
            for (int iSmall = 0; iSmall < 0x00014000; iSmall++) {
                DecimalFormat df = new DecimalFormat("00000");
                String sSmallFile = "small" + df.format(Long.valueOf(iSmall)) + ".txt";
                File fileSmallSource = new File(fileTemp.getAbsolutePath() + File.separator + "many" + File.separator + sSmallFile);
                if ((iSmall % 10000) == 0)
                    System.out.println("add " + fileSmallSource.getAbsolutePath());
                appendFile(zf, "many/" + sSmallFile, fileSmallSource, iMethod);
            }
            /* write the central directory */
            zf.close();
            sw.stop();
            System.out.println("Closed " + m_sTestZipFile + " after " + sw.formatMs() + " ms");
        } catch (FileNotFoundException fnfe) {
            fail(fnfe.getClass().getName() + ": " + fnfe.getMessage());
        } catch (IOException ie) {
            fail(ie.getClass().getName() + ": " + ie.getMessage());
        }
    } /* zipTest */

    private static void zipPkZip(File fileFolderUnzip, File fileFileZip) {
        System.out.println("(pkzipc) zip all files in " + fileFolderUnzip.getAbsolutePath() + " to " + fileFileZip.getAbsolutePath());
        StopWatch sw = StopWatch.getInstance();
        sw.start();
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
        sw.stop();
        System.out.println("pkzipc finished in " + sw.formatMs() + " ms");
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    } /* zipPkZip */

    private static void zipInfoZip(File fileFolderUnzip, File fileFileZip) {
        System.out.println("(Info-Zip) zip all files in " + fileFolderUnzip.getAbsolutePath() + " to " + fileFileZip.getAbsolutePath());
        StopWatch sw = StopWatch.getInstance();
        sw.start();
        /* use Info-ZIP zip.exe to create zip file in zip directory */
        String[] asProg = new String[]
                {
                        _sZip30,
                        "-q",
                        "-r",
                        "-z",
                        fileFileZip.getAbsolutePath(),
                        ".",
                        "-i",
                        "*"
                };

        StringReader rdrInput = new StringReader(sZIP_COMMENT);
        Execute exec = Execute.execute(asProg, fileFolderUnzip, rdrInput);
        rdrInput.close();
        System.out.println(exec.getStdOut());
        int iExitCode = exec.getResult();
        if (iExitCode != 0) {
            System.err.println(exec.getStdErr());
            fail(_sZip30 + " exit code: " + String.valueOf(iExitCode));
        }
        sw.stop();
        System.out.println("zip finished in " + sw.formatMs() + " ms");
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    private boolean unzipPkZip(String sEntryName, File fileExtract) {
        System.out.println("(pkzipc) unzip file " + sEntryName + " from " + m_sTestZipFile + " to " + fileExtract.getAbsolutePath());
        /* extract the moderate file */
        String[] asProg = new String[]
                {
                        _sPkZipC,
                        "-extract",
                        "-attr=all",
                        "-directories",
                        "-silent=normal",
                        m_sTestZipFile,
                        sEntryName,
                        fileExtract.getAbsolutePath() + File.separator + ""
                };
        Execute exec = Execute.execute(asProg);
        System.out.println(exec.getStdOut());
        int iExitCode = exec.getResult();
        if (iExitCode != 0)
            System.err.println(exec.getStdErr());
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            fail(EU.getExceptionMessage(ie));
        }
        return (iExitCode == 0);
    } /* unzipPkZip */

    private boolean unzipZipInfo(String sEntryName, File fileExtract) {
        System.out.println("(Info-ZIP) unzip file " + sEntryName + " from " + m_sTestZipFile + " to " + fileExtract.getAbsolutePath());
        /* extract the moderate file */
        String[] asProg = new String[]
                {
                        _sUnzip60,
                        "-q",
                        "-o",
                        "-d",
                        fileExtract.getAbsolutePath(),
                        m_sTestZipFile,
                        sEntryName,
                };
        Execute exec = Execute.execute(asProg);
        System.out.println(exec.getStdOut());
        int iExitCode = exec.getResult();
        if (iExitCode != 0)
            System.err.println(exec.getStdErr());
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            fail(EU.getExceptionMessage(ie));
        }
        return (iExitCode == 0);
    } /* unzipZipInfo */

    /*------------------------------------------------------------------*/

    /**
     * extract a file entry from the ZIP64 file.
     *
     * @param sEntryName name of file entry.
     * @return true, if file entry could be extracted.
     */
    private boolean extractFile(String sEntryName) {
        boolean bExtracted = false;
        /* temp directory */
        File fileExtract = new File(sEXTRACT_DIRECTORY);
        if (!fileExtract.exists())
            fileExtract.mkdirs();
        if (fileExtract.exists())
            fileExtract.delete();
        if (_sPkZipC != null)
            bExtracted = unzipPkZip(sEntryName, fileExtract);
        else if (_sUnzip60 != null)
            bExtracted = unzipZipInfo(sEntryName, fileExtract);
        return bExtracted;
    }

    /*------------------------------------------------------------------*/

    /**
     * check if two buffers are equal.
     *
     * @param buffer1 first buffer.
     * @param iSize1  size of first buffer.
     * @param buffer2 second buffer.
     * @param iSize2  size of second buffer.
     * @return true, if buffers are equal.
     */
    private boolean equalBuffers(byte[] buffer1, int iSize1, byte[] buffer2, int iSize2) {
        boolean bEqual = true;
        if (iSize1 == iSize2) {
            for (int i = 0; bEqual && (i < iSize1); i++)
                if (buffer1[i] != buffer2[i])
                    bEqual = false;
        } else
            bEqual = false;

        return bEqual;
    } /* equalBuffers */

    /*------------------------------------------------------------------*/

    /**
     * check if two files are equal.
     *
     * @param file1 first file.
     * @param file2 second file.
     * @return true, if files are equal.
     */
    private boolean equalFiles(File file1, File file2) {
        boolean bEqual = true;
        byte[] buffer1 = new byte[iBUFFER_SIZE];
        byte[] buffer2 = new byte[iBUFFER_SIZE];
        try {
            FileInputStream fis1 = new FileInputStream(file1);
            FileInputStream fis2 = new FileInputStream(file2);
            int iRead1 = fis1.read(buffer1);
            int iRead2 = fis2.read(buffer2);
            while (bEqual && (iRead1 >= 0) && (iRead2 >= 0)) {
                if (!equalBuffers(buffer1, iRead1, buffer2, iRead2))
                    bEqual = false;
                else {
                    iRead1 = fis1.read(buffer1);
                    iRead2 = fis2.read(buffer2);
                }
            }
            fis1.close();
            fis2.close();
        } catch (FileNotFoundException fnfe) {
            bEqual = false;
            System.out.println(fnfe.getClass().getName() + ": " + fnfe.getMessage());
        } catch (IOException ie) {
            bEqual = false;
            System.out.println(ie.getClass().getName() + ": " + ie.getMessage());
        }
        return bEqual;
    } /* equalFiles */

    /*------------------------------------------------------------------*/

    /**
     * test if two large files are probably equal by randomly comparing
     * some portions.
     *
     * @param file1 first file.
     * @param file2 second file.
     * @return true, if files are probably equal.
     */
    private boolean equalTest(File file1, File file2) {
        boolean bEqual = true;
        long lLength = file1.length();
        if (lLength == file2.length()) {
            if (lLength <= 16 * iBUFFER_SIZE)
                bEqual = equalFiles(file1, file2);
            else {
                byte[] buffer1 = new byte[iBUFFER_SIZE];
                byte[] buffer2 = new byte[iBUFFER_SIZE];
                try {
                    RandomAccessFile raf1 = new RandomAccessFile(file1, "r");
                    RandomAccessFile raf2 = new RandomAccessFile(file2, "r");
                    for (int iTest = 0; iTest < 16; iTest++) {
                        long lRandomPosition = (long) Math.floor((lLength - iBUFFER_SIZE) * Math.random());
                        raf1.seek(lRandomPosition);
                        raf2.seek(lRandomPosition);
                        int iRead1 = raf1.read(buffer1);
                        int iRead2 = raf2.read(buffer2);
                        bEqual = equalBuffers(buffer1, iRead1, buffer2, iRead2);
                    }
                    raf1.close();
                    raf2.close();
                } catch (FileNotFoundException fnfe) {
                    bEqual = false;
                    System.out.println(fnfe.getClass().getName() + ": " + fnfe.getMessage());
                } catch (IOException ie) {
                    bEqual = false;
                    System.out.println(ie.getClass().getName() + ": " + ie.getMessage());
                }
            }

        } else
            bEqual = false;
        return bEqual;
    } /* equalTest */

    /*------------------------------------------------------------------*/

    /**
     * create a large file.
     *
     * @param fileLarge file to be created.
     * @throws FileNotFoundException if folder does not exist.
     * @throws IOException           if an I/O error occurred.
     */
    private static void createLarge(File fileLarge)
            throws FileNotFoundException, IOException {
        System.out.println("writing large file");
        FileOutputStream fos = new FileOutputStream(fileLarge);
        byte[] buffer = new byte[iBUFFER_SIZE];
        for (int iBuffer = 0; iBuffer < iLARGE_BUFFERS; iBuffer++) {
            /* fill the buffer with random characters */
            for (int i = 0; i < buffer.length; i++) {
                if (i % 76 == 75)
                    buffer[i] = 0x0A;
                else
                    buffer[i] = (byte) (32 + (int) Math.floor(96 * Math.random()));
            }
            fos.write(buffer);
        }
        fos.close();
    } /* createLarge */

    /*------------------------------------------------------------------*/

    /**
     * create a medium size file.
     *
     * @param fileMedium file to be created.
     * @throws FileNotFoundException if folder does not exist.
     * @throws IOException           if an I/O error occurred.
     */
    private static void createMedium(File fileMedium)
            throws FileNotFoundException, IOException {
        System.out.println("writing medium file");
        /* prepare 256 "words" */
        byte[][] abufWord = new byte[256][];
        for (int iWord = 0; iWord < abufWord.length; iWord++) {
            int iLength = 2 + (int) Math.floor(3 * Math.random());
            abufWord[iWord] = new byte[iLength];
            for (int i = 0; i < abufWord[iWord].length; i++)
                abufWord[iWord][i] = (byte) (32 + 96 * (int) Math.floor(Math.random()));
        }
        FileOutputStream fos = new FileOutputStream(fileMedium);
        byte[] buffer = new byte[iBUFFER_SIZE];
        for (int iBuffer = 0; iBuffer < iLARGE_BUFFERS; iBuffer++) {
            /* fill the buffer with randomly chosen prepared words */
            for (int iPos = 0; iPos < buffer.length; ) {
                int iWord = (int) Math.floor(256 * Math.random());
                if (Math.floor(iPos / 76) != Math.floor((iPos + abufWord[iWord].length) / 76)) {
                    buffer[iPos] = 0x0A;
                    iPos++;
                }
                for (int i = 0; (i < abufWord[iWord].length) && (iPos < buffer.length); i++) {
                    buffer[iPos] = abufWord[iWord][i];
                    iPos++;
                }
            }
            fos.write(buffer);
        }
        fos.close();
    } /* createMedium */

    /*------------------------------------------------------------------*/

    /**
     * create a moderate size file.
     *
     * @param fileModerate file to be created.
     * @throws FileNotFoundException if folder does not exist.
     * @throws IOException           if an I/O error occurred.
     */
    private static void createModerate(File fileModerate)
            throws FileNotFoundException, IOException {
        System.out.println("writing moderate file");
        FileOutputStream fos = new FileOutputStream(fileModerate);
        byte[] buffer = new byte[iBUFFER_SIZE];
        for (int iBuffer = 0; iBuffer < iMODERATE_BUFFERS; iBuffer++) {
            /* fill the buffer with random characters */
            for (int i = 0; i < buffer.length; i++) {
                if (i % 76 == 75)
                    buffer[i] = 0x0A;
                else
                    buffer[i] = (byte) (32 + (int) Math.floor(96 * Math.random()));
            }
            fos.write(buffer);
        }
        fos.close();
    } /* createModerate */

    /*------------------------------------------------------------------*/

    /**
     * create a small file.
     *
     * @param fileSmall file to be created.
     * @throws FileNotFoundException if folder does not exist.
     * @throws IOException           if an I/O error occurred.
     */
    private static void createSmall(File fileSmall)
            throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(fileSmall);
        /* random size 0 .. iSMALL_SIZE-1 */
        int iLength = (int) Math.floor(iSMALL_SIZE * Math.random());
        byte[] buffer = new byte[iLength];
        /* fill the buffer with random characters */
        for (int i = 0; i < buffer.length; i++)
            buffer[i] = (byte) (32 + (int) Math.floor(96 * Math.random()));
        fos.write(buffer);
        fos.close();
    } /* createLarge */

    @BeforeClass
    public static void setupClass() {
        try {
            /* in temp directory: */
            File fileTemp = new File(sTEMP_DIRECTORY);
            if (!fileTemp.exists())
                fileTemp.mkdirs();
            /* 1. write more than 4 GB incompressible random file "large" */
            String sLargeFile = fileTemp.getAbsolutePath() + File.separator + "large.txt";
            File fileLarge = new File(sLargeFile);
            if (!fileLarge.exists())
                createLarge(fileLarge);
            /* 2. write more than 4 GB random file which can be compressed to less than 4 GB "medium" */
            String sMediumFile = fileTemp.getAbsolutePath() + File.separator + "medium.txt";
            File fileMedium = new File(sMediumFile);
            if (!fileMedium.exists())
                createMedium(fileMedium);
            /* 2.a) write more than 65 KB random file */
            String sModerateFile = fileTemp.getAbsolutePath() + File.separator + "moderate.txt";
            File fileModerate = new File(sModerateFile);
            if (!fileModerate.exists())
                createModerate(fileModerate);
            /* 3. create sub directory "many" */
            String sManyFolder = fileTemp.getAbsolutePath() + File.separator + "many" + File.separator;
            File fileMany = new File(sManyFolder);
            if (!fileMany.exists()) {
                System.out.println("writing small files");
                fileMany.mkdir();
                /* 4. write more than 65'000 small files "small00001" - "smallxxxxx" */
                for (int i = 0; i < 0x014000; i++) {
                    DecimalFormat df = new DecimalFormat("00000");
                    String sSmallFile = fileMany.getAbsolutePath() + File.separator + "small" + df.format(Long.valueOf(i)) + ".txt";
                    File fileSmall = new File(sSmallFile);
                    if (!fileSmall.exists())
                        createSmall(fileSmall);
                }
            }
        } catch (FileNotFoundException fnfe) {
            fail(EU.getExceptionMessage(fnfe));
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    } /* setupClass */

    /*------------------------------------------------------------------*/
	/* (non-Javadoc)
	 @see junit.framework.TestCase#setUp()
	 */
    @Before
    public void setUp() throws Exception {
        File fileTemp = new File(sTEMP_DIRECTORY);
        /* 5. zip everything using external executable */
        File fileZip = new File(fileTemp.getParentFile().getAbsolutePath() + File.separator + "exttest.zip");
//    if (fileZip.exists())
//      fileZip.delete();
        if (!fileZip.exists()) {
            System.out.println("zipping everything");
            if (_sPkZipC != null)
                zipPkZip(fileTemp, fileZip);
            else if (_sZip30 != null)
                zipInfoZip(fileTemp, fileZip);
            m_sExtZipFile = fileZip.getAbsolutePath();
        }
        /* 0. delete all zip files created below */
        File fileTest = new File(fileTemp.getParentFile().getAbsolutePath() + File.separator + "test.zip");
        if (fileTest.exists())
            fileTest.delete();
        m_sTestZipFile = fileTest.getAbsolutePath();
    } /* setUp */

    /*------------------------------------------------------------------*/
	/* (non-Javadoc)
	 @see junit.framework.TestCase#tearDown()
	 */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link Zip64File#Zip64File(String, boolean)}.
     */
    @Test
    public void testZip64FileStringBoolean() {
        System.out.println("testZip64FileStringBoolean");
        /* open pkzip file read-only */
        try {
            Zip64File zf = new Zip64File(m_sExtZipFile, true);
            zf.close();
        } catch (FileNotFoundException fnfe) {
            fail(fnfe.getClass().getName() + ": " + fnfe.getMessage());
        } catch (IOException ie) {
            fail(ie.getClass().getName() + ": " + ie.getMessage());
        }
    }

    /**
     * Test method for {@link Zip64File#Zip64File(File, boolean)}.
     */
    @Test
    public void testZip64FileFileBoolean() {
        System.out.println("testZip64FileFileBoolean");
        /* open a non-existent zip file read-only (should fail) */
        try {
            File fileTest = new File(m_sTestZipFile);
            if (fileTest.exists())
                fileTest.delete();
            Zip64File zf = new Zip64File(fileTest, true);
            zf.close();
            fail("Opening non-existent file read-only should not succeed!");
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getClass().getName() + ": " + fnfe.getMessage());
        } catch (IOException ie) {
            fail(ie.getClass().getName() + ": " + ie.getMessage());
        }
    }

    /**
     * Test method for {@link Zip64File#Zip64File(String)}.
     */
    @Test
    public void testZip64FileString() {
        System.out.println("testZip64FileString");
        /* open a non-existent file read/write (should succeed) */
        try {
            File fileTest = new File(m_sTestZipFile);
            if (fileTest.exists())
                fileTest.delete();
            Zip64File zf = new Zip64File(m_sTestZipFile);
            zf.close();
        } catch (FileNotFoundException fnfe) {
            fail(fnfe.getClass().getName() + ": " + fnfe.getMessage());
        } catch (IOException ie) {
            fail(ie.getClass().getName() + ": " + ie.getMessage());
        }
    }

    /**
     * Test method for {@link Zip64File#Zip64File(File)}.
     */
    @Test
    public void testZip64FileFile() {
        System.out.println("testZip64FileFile");
        /* open pkzip file read/write */
        try {
            File fileZip = new File(m_sExtZipFile);
            Zip64File zf = new Zip64File(fileZip);
            zf.close();
        } catch (FileNotFoundException fnfe) {
            fail(fnfe.getClass().getName() + ": " + fnfe.getMessage());
        } catch (IOException ie) {
            fail(ie.getClass().getName() + ": " + ie.getMessage());
        }
    }

    /**
     * Test method for {@link Zip64File#close()}.
     */
    @Test
    public void testClose() {
        System.out.println("testClose");
        /* open non-existent file read/write */
        try {
            File fileTest = new File(m_sTestZipFile);
            if (fileTest.exists())
                fileTest.delete();
            Zip64File zf = new Zip64File(m_sTestZipFile);
            zf.close();
        } catch (FileNotFoundException fnfe) {
            fail(fnfe.getClass().getName() + ": " + fnfe.getMessage());
        } catch (IOException ie) {
            fail(ie.getClass().getName() + ": " + ie.getMessage());
        }
    }

    /**
     * Test method for {@link Zip64File#getComment()}.
     */
    @Test
    public void testGetComment() {
        System.out.println("testGetComment");
        /* open pkzip file read-only */
        try {
            Zip64File zf = new Zip64File(m_sExtZipFile, true);
            /* check the comment */
            String sComment = zf.getComment();
            if (!sZIP_COMMENT.equals(sComment))
                fail("Invalid ZIP comment found: " + sComment + "!");
            zf.close();
        } catch (FileNotFoundException fnfe) {
            fail(fnfe.getClass().getName() + ": " + fnfe.getMessage());
        } catch (IOException ie) {
            fail(ie.getClass().getName() + ": " + ie.getMessage());
        }
    }

    /**
     * Test method for {@link Zip64File#setComment(String)}.
     */
    @Test
    public void testSetComment() {
        System.out.println("testSetComment");
        /* open pkzip file read/write */
        try {
            Zip64File zf = new Zip64File(m_sTestZipFile);
            /* set the comment */
            String sComment = "a new comment";
            zf.setComment(sComment);
            zf.close();
            zf = new Zip64File(m_sTestZipFile, true);
            /* check the comment */
            if (!sComment.equals(zf.getComment()))
                fail("ZIP comment could not be set!");
            zf.close();
        } catch (FileNotFoundException fnfe) {
            fail(fnfe.getClass().getName() + ": " + fnfe.getMessage());
        } catch (IOException ie) {
            fail(ie.getClass().getName() + ": " + ie.getMessage());
        }
    }

    /**
     * Test method for {@link Zip64File#getFileEntries()}.
     */
    @Test
    public void testGetFileEntries() {
        System.out.println("testGetFileEntries");
        /* open pkzip file read-only */
        try {
            Zip64File zf = new Zip64File(m_sExtZipFile, true);
            /* get the number of file entries */
            int iFileEntries = zf.getFileEntries();
            if (iFileEntries != 0x00014004)
                fail("Invalid number of file entries found: " + String.valueOf(iFileEntries) + "!");
            zf.close();
        } catch (FileNotFoundException fnfe) {
            fail(fnfe.getClass().getName() + ": " + fnfe.getMessage());
        } catch (IOException ie) {
            fail(ie.getClass().getName() + ": " + ie.getMessage());
        }
    }

    /**
     * Test method for {@link Zip64File#getFileEntry(String)}.
     */
    @Test
    public void testGetFileEntry() {
        System.out.println("testGetFileEntry");
        /* open pkzip file read-only */
        try {
            Zip64File zf = new Zip64File(m_sExtZipFile, true);
            /* get the medium file entry */
            FileEntry feMedium = zf.getFileEntry("medium.txt");
            if (feMedium != null) {
                long lSize = iBUFFER_SIZE;
                lSize *= iLARGE_BUFFERS;
                if (feMedium.getSize() != lSize)
                    fail("Invalid size for medium.txt: " + String.valueOf(feMedium.getSize()));
            } else
                fail("file entry medium.txt not found!");
            zf.close();
        } catch (FileNotFoundException fnfe) {
            fail(fnfe.getClass().getName() + ": " + fnfe.getMessage());
        } catch (IOException ie) {
            fail(ie.getClass().getName() + ": " + ie.getMessage());
        }
    }

    /**
     * Test method for {@link Zip64File#getListFileEntries()}.
     */
    @Test
    public void testGetListFileEntriesOld() {
        System.out.println("testGetListFileEntriesOld");
        /* open external file read-only */
        try {
            Zip64File zf = new Zip64File("..\\SiardApi\\testfiles\\sql1999.siard", true);
            /* get the file entries */
            List<FileEntry> listFileEntries = zf.getListFileEntries();
            for (Iterator<FileEntry> iterFileEntry = listFileEntries.iterator(); iterFileEntry.hasNext(); ) {
                FileEntry fe = iterFileEntry.next();
                System.out.println(fe.getName() + " " + String.valueOf(fe.getSize()));
            }
            zf.close();
        } catch (FileNotFoundException fnfe) {
            fail(fnfe.getClass().getName() + ": " + fnfe.getMessage());
        } catch (IOException ie) {
            fail(ie.getClass().getName() + ": " + ie.getMessage());
        }
    }

    /**
     * Test method for {@link Zip64File#getListFileEntries()}.
     */
    @Test
    public void testGetListFileEntries() {
        System.out.println("testGetListFileEntries");
        /* open external file read-only */
        try {
            String sPrefix = "many/small";
            String sSuffix = ".txt";
            int iPrefixEnd = sPrefix.length();
            int iSuffixStart = iPrefixEnd + 5;
            Zip64File zf = new Zip64File(m_sExtZipFile, true);
            /* get the file entries */
            List<FileEntry> listFileEntries = zf.getListFileEntries();
            for (Iterator<FileEntry> iterFileEntry = listFileEntries.iterator(); iterFileEntry.hasNext(); ) {
                FileEntry fe = iterFileEntry.next();
                String sName = fe.getName();
                if ((!sName.equals("large.txt")) &&
                        (!sName.equals("medium.txt")) &&
                        (!sName.equals("moderate.txt")) &&
                        (!sName.equals("many/"))) {
                    if ((sName.startsWith(sPrefix)) && (sName.endsWith(sSuffix))) {
                        int i = Integer.parseInt(sName.substring(iPrefixEnd, iSuffixStart));
                        if ((i < 0) || (i >= 0x00014000))
                            fail("Invalid file number: " + sName);
                    } else
                        fail("Invalid file name: " + sName);
                }
            }
            zf.close();
        } catch (FileNotFoundException fnfe) {
            fail(fnfe.getClass().getName() + ": " + fnfe.getMessage());
        } catch (IOException ie) {
            fail(ie.getClass().getName() + ": " + ie.getMessage());
        }
    }

    /**
     * Test method for {@link Zip64File#openEntryInputStream(String)}.
     */
    @Test
    public void testOpenEntryInputStream() {
        System.out.println("testOpenEntryInputStream");
        /* extract directory */
        File fileExtract = new File(sEXTRACT_DIRECTORY/*SpecialFolder.getUserDataHome("Extract")*/);
        if (!fileExtract.exists())
            fileExtract.mkdirs();
        /* large file */
        File fileLarge = new File(fileExtract.getAbsolutePath() + File.separator + "large.txt");
        if (fileLarge.exists())
            fileLarge.delete();
        /* medium file */
        File fileMedium = new File(fileExtract.getAbsolutePath() + File.separator + "medium.txt");
        if (fileMedium.exists())
            fileMedium.delete();
        /* small file */
        File fileSmall = new File(fileExtract.getAbsolutePath() + File.separator + "small.txt");
        if (fileSmall.exists())
            fileSmall.delete();
        /* temp directory */
        File fileTemp = new File(sTEMP_DIRECTORY/*SpecialFolder.getUserDataHome("Temp")*/);
        if (!fileTemp.exists())
            fileTemp.mkdirs();
        /* original of large file */
        File fileLargeOriginal = new File(fileTemp.getAbsolutePath() + File.separator + "large.txt");
        /* original of medium file */
        File fileMediumOriginal = new File(fileTemp.getAbsolutePath() + File.separator + "medium.txt");
        /* original of small file */
        File fileSmallOriginal = new File(fileTemp.getAbsolutePath() + File.separator + "many" + File.separator + "small13854.txt");
        byte[] buffer = new byte[iBUFFER_SIZE];
        try {
            /* open pkzip file read-only */
            Zip64File zf = new Zip64File(m_sExtZipFile, true);
            /* extract a small file */
            EntryInputStream eis = zf.openEntryInputStream("many/small13854.txt");
            if (eis == null)
                fail("Input stream for small file could not be opened!");
            else {
                FileOutputStream fos = new FileOutputStream(fileSmall);
                for (int iRead = eis.read(buffer); iRead >= 0; iRead = eis.read(buffer))
                    fos.write(buffer, 0, iRead);
                fos.close();
                eis.close();
                /* compare it to its original */
                if (!equalTest(fileSmall, fileSmallOriginal))
                    fail("extracted small file is not equal to its original!");
                fileSmall.delete();
                /* extract the medium file */
                eis = zf.openEntryInputStream("medium.txt");
                if (eis == null)
                    fail("Input stream for medium file could not be opened!");
                else {
                    fos = new FileOutputStream(fileMedium);
                    for (int iRead = eis.read(buffer); iRead >= 0; iRead = eis.read(buffer))
                        fos.write(buffer, 0, iRead);
                    fos.close();
                    eis.close();
                    /* compare it to its original */
                    if (!equalTest(fileMedium, fileMediumOriginal))
                        fail("extracted medium file is not equal to its original!");
                    else {
                        fileMedium.delete();
                        /* extract the large file */
                        eis = zf.openEntryInputStream("large.txt");
                        if (eis == null)
                            fail("Input stream for large file could not be opened!");
                        else {
                            fos = new FileOutputStream(fileLarge);
                            for (int iRead = eis.read(buffer); iRead >= 0; iRead = eis.read(buffer))
                                fos.write(buffer, 0, iRead);
                            fos.close();
                            eis.close();
                            /* compare it to its original */
                            if (!equalTest(fileLarge, fileLargeOriginal))
                                fail("extracted large file is not equal to its original!");
                            else {
                                fileLarge.delete();
                                zf.close();
                                fileExtract.delete();
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException fnfe) {
            fail(fnfe.getClass().getName() + ": " + fnfe.getMessage());
        } catch (IOException ie) {
            fail(ie.getClass().getName() + ": " + ie.getMessage());
        }
    }

    /**
     * Test method for {@link Zip64File#openEntryOutputStream(String, int, Date)}.
     */
    @Test
    public void testOpenEntryOutputStream() {
        System.out.println("testOpenEntryOutputStream");
        /* This takes a terrible amount of space in the non-compressed case.
         * Therefore we delete the pkzip version first.
         */
        System.out.println("Deleting " + m_sExtZipFile);
        File filePk = new File(m_sExtZipFile);
        filePk.delete();
        File fileTest = new File(m_sTestZipFile);
        /* extract directory */
        File fileExtract = new File(sEXTRACT_DIRECTORY);
        if (!fileExtract.exists())
            fileExtract.mkdirs();
        /* moderate file */
        File fileModerate = new File(fileExtract.getAbsolutePath() + File.separator + "moderate.txt");
        if (fileModerate.exists())
            fileModerate.delete();
        /* medium file */
        File fileMedium = new File(fileExtract.getAbsolutePath() + File.separator + "medium.txt");
        if (fileMedium.exists())
            fileMedium.delete();
        /* large file */
        File fileLarge = new File(fileExtract.getAbsolutePath() + File.separator + "large.txt");
        if (fileLarge.exists())
            fileLarge.delete();
        /* small file */
        File fileSmall = new File(fileExtract.getAbsolutePath() + File.separator + "many" + File.separator + "small12345.txt");
        if (fileSmall.exists())
            fileSmall.delete();
        /* temp directory */
        File fileTemp = new File(sTEMP_DIRECTORY);
        if (!fileTemp.exists())
            fileTemp.mkdirs();
        /* original of moderate file */
        File fileModerateOriginal = new File(fileTemp.getAbsolutePath() + File.separator + "moderate.txt");
        /* original of medium file */
        File fileMediumOriginal = new File(fileTemp.getAbsolutePath() + File.separator + "medium.txt");
        /* original of large file */
        File fileLargeOriginal = new File(fileTemp.getAbsolutePath() + File.separator + "large.txt");
        /* original of small file */
        File fileSmallOriginal = new File(fileTemp.getAbsolutePath() + File.separator + "many" + File.separator + "small12345.txt");
        /*--- uncompressed ---------------------------------------------------------------*/
        zipTest(FileEntry.iMETHOD_STORED);
        /* extract the moderate file */
        if (!extractFile("moderate.txt"))
            fail("external unzip extract of uncompressed moderate.txt failed!");
        /* compare it to the original */
        if (!equalTest(fileModerate, fileModerateOriginal))
            fail("extracted compressed moderate file is not equal to its original!");
        fileModerate.delete();
        /* extract the medium file */
        if (!extractFile("medium.txt"))
            fail("pkzipc extract of uncompressed medium.txt failed!");
        /* compare it to the original */
        if (!equalTest(fileMedium, fileMediumOriginal))
            fail("extracted compressed medium file is not equal to its original!");
        fileMedium.delete();
        /* extract the large file */
        if (!extractFile("large.txt"))
            fail("pkzipc extract of uncompressed large.txt failed!");
        /* compare it to the original */
        if (!equalTest(fileLarge, fileLargeOriginal))
            fail("extracted compressed large file is not equal to its original!");
        fileLarge.delete();
        /* extract a small file */
        if (!extractFile("many/small12345.txt"))
            fail("pkzipc extract of uncompressed small12345.txt failed!");
        /* compare it to the original */
        if (!equalTest(fileSmall, fileSmallOriginal))
            fail("extracted compressed small file is not equal to its original!");
        fileSmall.delete();
        /*--- compressed ---------------------------------------------------------------*/
        fileTest.delete();
        /* zip everything to new non-existent file */
        zipTest(FileEntry.iMETHOD_DEFLATED);
        /* extract the moderate file */
        if (!extractFile("moderate.txt"))
            fail("pkzipc extract of compressed moderate.txt failed!");
        /* compare it to the original */
        if (!equalTest(fileModerate, fileModerateOriginal))
            fail("extracted compressed moderate file is not equal to its original!");
        fileModerate.delete();
        /* extract the medium file */
        if (!extractFile("medium.txt"))
            fail("pkzipc extract of compressed medium.txt failed!");
        /* compare it to the original */
        if (!equalTest(fileMedium, fileMediumOriginal))
            fail("extracted compressed medium file is not equal to its original!");
        fileMedium.delete();
        /* extract the large file */
        if (!extractFile("large.txt"))
            fail("pkzipc extract of compressed large.txt failed!");
        /* compare it to the original */
        if (!equalTest(fileLarge, fileLargeOriginal))
            fail("extracted compressed large file is not equal to its original!");
        fileLarge.delete();
        /* extract a small file */
        if (!extractFile("many/small12345.txt"))
            fail("pkzipc extract of compressed small12345.txt failed!");
        /* compare it to the original */
        if (!equalTest(fileSmall, fileSmallOriginal))
            fail("extracted compressed small file is not equal to its original!");
        fileSmall.delete();
    }

    @Test
    public void testWriteRead() {
        /* original of moderate file */
        File fileModerate = new File(sTESTFILES_DIRECTORY + File.separator + "moderate.txt");
        /* moderate zip file */
        try {
            File fileTemp = new File(sTEMP_DIRECTORY);
            File fileZip = new File(fileTemp.getParentFile().getAbsolutePath() + File.separator + "moderate.zip");

            Zip64File zf = new Zip64File(fileZip);
            byte[] buf = new byte[4096];

            /* write medium file to ZIP file */
            FileInputStream fis = new FileInputStream(fileModerate);
            OutputStream os = zf.openEntryOutputStream("moderate", FileEntry.iMETHOD_STORED, null);
            for (int iRead = fis.read(buf); iRead != -1; iRead = fis.read(buf))
                os.write(buf, 0, iRead);
            os.close();
            fis.close();

            /* read medium file from ZIP file */
            fileModerate = new File(fileTemp.getAbsolutePath() + File.separator + "moderate.txt");
            FileOutputStream fos = new FileOutputStream(fileModerate);
            InputStream is = zf.openEntryInputStream("moderate");
            for (int iRead = is.read(buf); iRead != -1; iRead = is.read(buf))
                fos.write(buf, 0, iRead);
            is.close();
            fos.close();

            /* close ZIP file */
            zf.close();
        } catch (IOException ie) {
            fail(EU.getExceptionMessage(ie));
        }
    }

    /**
     * Test method for {@link Zip64File#delete(String)}.
     */
    @Test
    public void testDelete() {
        System.out.println("testDelete");
        /* extract directory */
        File fileExtract = new File(sEXTRACT_DIRECTORY);
        if (!fileExtract.exists())
            fileExtract.mkdirs();
        /* small files */
        File fileSmall12344 = new File(fileExtract.getAbsolutePath() + File.separator + "many" + File.separator + "small12344.txt");
        if (fileSmall12344.exists())
            fileSmall12344.delete();
        File fileSmall12345 = new File(fileExtract.getAbsolutePath() + File.separator + "many" + File.separator + "small12345.txt");
        if (fileSmall12345.exists())
            fileSmall12345.delete();
        File fileSmall12346 = new File(fileExtract.getAbsolutePath() + File.separator + "many" + File.separator + "small12346.txt");
        if (fileSmall12346.exists())
            fileSmall12346.delete();
        /* temp directory */
        File fileTemp = new File(sTEMP_DIRECTORY);
        if (!fileTemp.exists())
            fileTemp.mkdirs();
        /* original of small files */
        File fileSmall12344Original = new File(fileTemp.getAbsolutePath() + File.separator + "many" + File.separator + "small12344.txt");
        File fileSmall12346Original = new File(fileTemp.getAbsolutePath() + File.separator + "many" + File.separator + "small12346.txt");
        /* zip everything to new non-existent file */
        zipTest(FileEntry.iMETHOD_DEFLATED);
        /* open it read/write and delete the medium file */
        try {
            Zip64File zf = new Zip64File(m_sTestZipFile);
            zf.delete("many/small12345.txt");
            FileEntry nonExistentDel = zf.delete("non-existent");
            zf.close();
            if (!extractFile("many/small12344.txt"))
                fail("many/small12344.txt could not be extracted!");
            if (!equalTest(fileSmall12344, fileSmall12344Original))
                fail("extracted compressed small12344 file is not equal to its original!");
            if (extractFile("many/small12345.txt"))
                fail("Deleted file could still be extracted!");
            if (!extractFile("many/small12346.txt"))
                fail("many/small12346.txt could not be extracted!");
            if (!equalTest(fileSmall12346, fileSmall12346Original))
                fail("extracted compressed small12346 file is not equal to its original!");
            if (nonExistentDel != null)
                fail("deleting a non-existent file should return null!");
        } catch (FileNotFoundException fnfe) {
            fail(fnfe.getClass().getName() + ": " + fnfe.getMessage());
        } catch (IOException ie) {
            fail(ie.getClass().getName() + ": " + ie.getMessage());
        }
    }

}
