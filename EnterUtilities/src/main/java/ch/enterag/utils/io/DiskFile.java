/*== DiskFile.java ======================================================
DiskFile implements a number of useful methods for RandomAccessFile.
Version     : $Id: DiskFile.java 34 2011-03-31 14:34:15Z hartwigthomas $
Application : Utilities
Description : DiskFile implements a number of useful methods for RandomAccessFile.
------------------------------------------------------------------------
Copyright  : Enter AG, Zurich, Switzerland, 2008
Created    : 27.02.2008, Hartwig Thomas
======================================================================*/

package ch.enterag.utils.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*====================================================================*/

/**
 * DiskFile implements a number of useful methods for RandomAccessFile.
 *
 * @author Hartwig Thomas
 */
public class DiskFile extends RandomAccessFile {
  /*====================================================================
  constants
  ====================================================================*/
    /**
     * read mode
     */
    private static final String sMODE_READ = "r";
    /**
     * read/write mode
     */
    private static final String sMODE_READ_WRITE = "rw";
    /**
     * size of I/O buffer
     */
    private static final int iBUFFER_SIZE = 4096;
	
  /*====================================================================
  (private) data members
  ====================================================================*/
    /**
     * file name
     */
    private String m_sFileName = null;
    /**
     * read only iFlags
     */
    private boolean m_bReadOnly = false;
	
  /*====================================================================
  Accessors
  ====================================================================*/

    /**
     * @return file name
     */
    public String getFileName() {
        return m_sFileName;
    }

    /**
     * @return read only iFlags
     */
    public boolean isReadOnly() {
        return m_bReadOnly;
    }
  
  /*====================================================================
  Constructors
  ====================================================================*/
    /*------------------------------------------------------------------*/

    /**
     * opens a disk file for reading (mode MODE_READ) or writing/updating
     * (mode MODE_READ_WRITE).
     *
     * @param sFileName name of file to be opened.
     * @param bReadOnly if true, file is opened for reading;
     *                  if false, file is opened for writing or updating.
     * @throws FileNotFoundException, if file to be opened for reading or
     *                                directory where file is to be written
     *                                does not exist.
     */
    public DiskFile(String sFileName, boolean bReadOnly)
            throws FileNotFoundException {
        super(sFileName, bReadOnly ? sMODE_READ : sMODE_READ_WRITE);
        File file = new File(sFileName);
        m_sFileName = file.getAbsolutePath();
        m_bReadOnly = bReadOnly;
    } /* constructor */

    /*------------------------------------------------------------------*/

    /**
     * opens a disk file for reading (mode MODE_READ) or writing/updating
     * (mode MODE_READ_WRITE).
     *
     * @param file      file to be opened.
     * @param bReadOnly if true, file is opened for reading;
     *                  if false, file is opened for writing or updating.
     * @throws FileNotFoundException, if file to be opened for reading or
     *                                directory where file is to be written
     *                                does not exist.
     */
    public DiskFile(File file, boolean bReadOnly)
            throws FileNotFoundException {
        super(file, bReadOnly ? sMODE_READ : sMODE_READ_WRITE);
        m_sFileName = file.getAbsolutePath();
        m_bReadOnly = bReadOnly;
    } /* constructor */

    /*------------------------------------------------------------------*/

    /**
     * opens a disk file for writing/updating.
     *
     * @param sFileName name of file to be opened.
     * @throws FileNotFoundException, if directory where file is to be written
     *                                does not exist.
     */
    public DiskFile(String sFileName)
            throws FileNotFoundException {
        super(sFileName, sMODE_READ_WRITE);
        File file = new File(sFileName);
        m_sFileName = file.getAbsolutePath();
    } /* constructor */

    /*------------------------------------------------------------------*/

    /**
     * opens a disk file for reading (mode MODE_READ) or writing/updating
     * (mode MODE_READ_WRITE).
     *
     * @param file file to be opened.
     * @throws FileNotFoundException, if directory where file is to be written
     *                                does not exist.
     */
    public DiskFile(File file)
            throws FileNotFoundException {
        super(file, sMODE_READ_WRITE);
        m_sFileName = file.getAbsolutePath();
    } /* constructor */

    /*------------------------------------------------------------------*/

    /**
     * digest returns a message digest for the bytes from lStart to lEnd
     * as a hex string prefixed by the algorithm.
     * File pointer remains unchanged.
     *
     * @param sAlgorithm must be "SHA-1" or "MD5".
     * @param lStart     start offset (included).
     * @param lEnd       end offset (excluded).
     * @return message digest.
     * @throws IOException if an I/O error occurred.
     */
    public byte[] digest(String sAlgorithm, long lStart, long lEnd)
            throws IOException {
        if (!(sAlgorithm.equals("MD5") || sAlgorithm.equals("SHA-1")))
            throw new IllegalArgumentException("Digest algorithm must be MD5 or SHA-1!");
        /* remember file pointer */
        long lFilePointer = getFilePointer();
        byte[] bufDigest = null;
        try {
            MessageDigest md = MessageDigest.getInstance(sAlgorithm);
            byte[] buf = new byte[iBUFFER_SIZE];
            int iRead = 0;
            for (long lPosition = lStart; lPosition < lEnd; lPosition += iRead) {
                seek(lPosition);
                int iLength = buf.length;
                if (lEnd - lPosition < iLength)
                    iLength = (int) (lEnd - lPosition);
                iRead = read(buf, 0, iLength);
                if (iRead != iLength)
                    throw new IOException("Could not read " + String.valueOf(iLength) + " bytes at position " + String.valueOf(
                            lPosition));
                md.update(buf, 0, iRead);
            }
            bufDigest = md.digest();
        } catch (NoSuchAlgorithmException nsae) {
            System.err.println(nsae.getClass().getName() + ": " + nsae.getMessage());
        }
        seek(lFilePointer);
        return bufDigest;
    } /* digest */

    /*------------------------------------------------------------------*/

    /**
     * moves shifts the whole file from lSource to lDestination, setting
     * the file pointer to the final write position (new end of file)
     *
     * @param lSource      position to copy from.
     * @param lDestination position to copy to.
     * @return number of bytes moved.
     * @throws IOException if an I/O error occurred.
     */
    public long move(long lSource, long lDestination)
            throws IOException {
        long lMoved = 0;
        byte[] buf = new byte[iBUFFER_SIZE];
        if (lSource < lDestination) {
            /* extend file size */
            setLength(length() + (lDestination - lSource));
            /* start at end */
            int iLength = buf.length;
            long lPos = length() - iLength;
            if (lPos < lSource) {
                iLength = iLength - (int) (lSource - lPos);
                lPos = lSource;
            }
            for (seek(lPos); iLength > 0; ) {
                seek(lPos);
                lMoved += iLength;
                readFully(buf, 0, iLength);
                seek(lPos + lDestination - lSource);
                write(buf, 0, iLength);
                lPos -= iLength;
                if (lPos < lSource) {
                    iLength = iLength - (int) (lSource - lPos);
                    lPos = lSource;
                }
            }
        } else if (lDestination < lSource) {
            /* start at lSource, reducing the file size */
            long lLength = length();
            int iLength = buf.length;
            long lPos = lSource;
            if (lPos + iLength > lLength)
                iLength = (int) (lLength - lPos);
            while (iLength > 0) {
                seek(lPos);
                lMoved += iLength;
                readFully(buf, 0, iLength);
                seek(lPos - lSource + lDestination);
                write(buf, 0, iLength);
                lPos += iLength;
                if (lPos + iLength > lLength)
                    iLength = (int) (lLength - lPos);
            }
            seek(lLength - lSource + lDestination);
        }
        setLength(getFilePointer());
        return lMoved;
    } /* move */

    /*------------------------------------------------------------------*/

    /**
     * returns the last position where the byte pattern is encountered
     * starting at the current file pointer position or -1 if the byte pattern
     * is not found.
     * The file pointer remains unchanged.
     *
     * @param bufPattern pattern to be searched for.
     * @return position of pattern.
     * @throws IOException if an I/O error occurred.
     */
    public long lastIndexOf(byte[] bufPattern)
            throws IOException {
        long lLastIndex = -1;
        int iBufferSize = 2 * bufPattern.length;
        if (iBufferSize < iBUFFER_SIZE)
            iBufferSize = iBUFFER_SIZE;
        byte[] buf = new byte[iBufferSize];
        long lFilePointer = getFilePointer();
        if (lFilePointer >= bufPattern.length) {
            long lPos = lFilePointer;
            int iStart = 0;
            int iEnd = 0;
            int iReadSize = iBufferSize / 2;
            if (lPos < iReadSize) {
                iStart = iReadSize - (int) lPos;
                iReadSize = (int) lPos;
            }
            lPos -= iReadSize;
            seek(lPos);
            readFully(buf, iStart, iReadSize);
            while ((lLastIndex < 0) && (iReadSize > 0)) {
                /* move the first half of the buffer to the second half */
                System.arraycopy(buf, iStart, buf, iBufferSize / 2, iReadSize);
                iEnd = iBufferSize / 2 + iReadSize;
                /* determine the new read size and the new start */
                if (lPos < iReadSize) {
                    iStart = iBufferSize / 2 - (int) lPos;
                    iReadSize = (int) lPos;
                }
                /* read the next iReadSize bytes into the buffer */
                lPos -= iReadSize;
                seek(lPos);
                readFully(buf, iStart, iReadSize);
                /* search for the occurrence of bufPattern in buf between iStart and buf.length */
                for (int iPos = iEnd - bufPattern.length; (iPos >= iStart) && (lLastIndex < 0); iPos--) {
                    boolean bMatch = true;
                    for (int i = 0; bMatch && (i < bufPattern.length); i++)
                        bMatch = buf[iPos + i] == bufPattern[i];
                    if (bMatch)
                        lLastIndex = lPos + iPos - iStart;
                }
            }
            seek(lFilePointer);
        }
        return lLastIndex;
    } /* lastIndexOf */

} /* class DiskFile */
