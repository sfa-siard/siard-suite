/*== EntryInputStreamTester.java =======================================
Tests EntryInputStream.
Version     : $Id: EntryInputStreamTester.java 51 2016-09-07 17:11:10Z hartwigthomas $
Application : Zip64File
Description : Tests EntryInputStream.
------------------------------------------------------------------------
Copyright  : Enter AG, Zurich, Switzerland, 2011
Created    : 08.02.2011, Hartwig Thomas
------------------------------------------------------------------------
The class ch.enterag.utils.zip.EntryInputStreamTester is free software;
you can redistribute it and/or modify it under the terms of the GNU General
Public License version 2 or later as published by the Free Software Foundation.

ch.enterag.utils.zip.EntryInputStreamTester is distributed in the hope 
that it will be useful, but WITHOUT ANY WARRANTY; without even the 
implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

If you have a need for licensing ch.enterag.utils.zip.EntryInputStreamTester
without some of the restrictions specified in the GNU General Public License,
it is possible to negotiate a different license with the copyright holder.
======================================================================*/
package ch.enterag.utils.zip;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.fail;

/*====================================================================*/

/**
 * Tests EntryOutputStream.
 *
 * @author Hartwig Thomas
 */
public class EntryOutputStreamTester {
    /**
     * buffer size for I/O
     */
    private final static int iBUFFER_SIZE = 8192;
    /**
     * zip file
     */
    private String m_sZipFile = null;
    /**
     * test files directory
     */
    private final static String sTESTFILES_DIRECTORY = "testfiles";
    /**
     * temp directory
     */
    private final static String sTEMP_DIRECTORY = "tmp";

    /*------------------------------------------------------------------*/
  /* (non-Javadoc)
   @see junit.framework.TestCase#setUp()
   */
    @Before
    public void setUp() throws Exception {
        /* create a file of moderate length in Temp and zip it */
        /* temp directory */
        File fileTemp = new File(sTEMP_DIRECTORY);
        File fileZip = new File(fileTemp.getAbsolutePath() + File.separator + "moderate.zip");
        if (fileZip.exists())
            fileZip.delete();
        m_sZipFile = fileZip.getAbsolutePath();
    } /* setUp */

    /*------------------------------------------------------------------*/
  /* (non-Javadoc)
   @see junit.framework.TestCase#tearDown()
   */
    @After
    public void tearDown() throws Exception {
    } /* tearDown */

    @Test
    public void testWriteRead() {
        System.out.println("testWriteRead");
        try {
            Zip64File zf = new Zip64File(m_sZipFile, false);
            byte[] buf = new byte[iBUFFER_SIZE];

            /* write moderate file to ZIP */
            String sModerateFile = "moderate.txt";
            this.getClass().getResourceAsStream("moderate.txt");
            //FileInputStream fis = new FileInputStream(sModerateFile)
            InputStream fis = this.getClass()
                                  .getClassLoader()
                                  .getResourceAsStream("moderate.txt");
            EntryOutputStream eos = zf.openEntryOutputStream("moderate.txt", FileEntry.iMETHOD_DEFLATED, null);
            for (int iRead = fis.read(buf); iRead != -1; iRead = fis.read(buf))
                eos.write(buf, 0, iRead);
            eos.close();
            fis.close();

            /* read moderate file from ZIP */
            EntryInputStream eis = zf.openEntryInputStream("moderate.txt");
            FileOutputStream fos = new FileOutputStream(sTEMP_DIRECTORY + File.separator + "moderate.txt");
            for (int iRead = eis.read(buf); iRead != -1; iRead = eis.read(buf))
                fos.write(buf, 0, iRead);
            fos.close();
            eis.close();

            zf.close();
        } catch (FileNotFoundException fnfe) {
            fail(fnfe.getClass().getName() + ": " + fnfe.getMessage());
        } catch (IOException ie) {
            fail(ie.getClass().getName() + ": " + ie.getMessage());
        }
    } /* testEntryInputStream */

} /* class EntryOutputStreamTester */
