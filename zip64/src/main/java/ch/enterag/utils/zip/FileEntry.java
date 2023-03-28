/*== FileEntry.java ====================================================
FileEntry represents a file entry in the central directory.
Version     : $Id: FileEntry.java 34 2011-03-31 14:34:15Z hartwigthomas $
Application : ZIP Utilities
Description : FileEntry is inspired by java.util.zip.ZipEntry.
              It represents a file header entry in the central directory
              or a local file header.
------------------------------------------------------------------------
Copyright  : Enter AG, Zurich, Switzerland, 2008
Created    : 25.02.2008, Hartwig Thomas
======================================================================*/

package ch.enterag.utils.zip;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/*====================================================================*/

/**
 * FileEntry is inspired by java.util.zip.ZipEntry.
 * It represents a file header entry in the central directory
 * or a local file header.
 *
 * @author Hartwig Thomas
 */
public class FileEntry
        implements Cloneable {
  /*====================================================================
  (private) data members
  ====================================================================*/
    /**
     * version needed to extract
     */
    private int m_iVersionNeeded;

    /**
     * @return version needed to extract
     */
    public int getVersionNeeded() {
        return m_iVersionNeeded;
    }

    /**
     * @param iVersionNeeded version needed to extract
     */
    public void setVersionNeeded(int iVersionNeeded) {
        m_iVersionNeeded = iVersionNeeded;
    }

    /**
     * bit flags
     */
    private int m_iFlags = 0;

    /**
     * @return bit flags
     */
    public int getFlags() {
        return m_iFlags;
    }

    /**
     * @param iFlags bit flags
     */
    public void setFlags(int iFlags) {
        m_iFlags = iFlags;
    }

    /**
     * compression method (must be set)
     */
    private int m_iMethod = -1;

    /**
     * @return compression method
     */
    public int getMethod() {
        return m_iMethod;
    }

    /**
     * @param iMethod compression method
     */
    public void setMethod(int iMethod) {
        if ((iMethod != iMETHOD_STORED) && (iMethod != iMETHOD_DEFLATED))
            throw new IllegalArgumentException("invalid compression method");
        m_iMethod = iMethod;
    }

    /**
     * modification time (in DOS time)
     */
    private long m_lDateTime = 0;

    /**
     * @return modification time (in DOS time)
     */
    public long getDateTime() {
        return m_lDateTime;
    }

    /**
     * @return modification time (in JAVA time)
     */
    public Date getTimestamp() {
        return new Date(dosToJavaTime(m_lDateTime));
    }

    /**
     * @param lDateTime modification time (in DOS time)
     */
    public void setDateTime(long lDateTime) {
        m_lDateTime = lDateTime;
    }

    /**
     * @param dateTimestamp modification time (in JAVA time)
     */
    public void setTimeStamp(Date dateTimestamp) {
        m_lDateTime = javaToDosTime(dateTimestamp.getTime());
    }

    /**
     * entry name
     */
    private String m_sName = null;

    /**
     * @return entry name
     */
    public String getName() {
        return m_sName;
    }

    /**
     * @param sName entry name
     */
    public void setName(String sName) {
        m_sName = sName;
    }

    /**
     * crc-32 of entry data
     */
    private long m_lCrc = 0;

    /**
     * @return crc-32 of entry data
     */
    public long getCrc() {
        return m_lCrc;
    }

    /**
     * @param lCrc crc-32 of entry data
     */
    public void setCrc(long lCrc) {
        if ((lCrc < 0) || (lCrc > 0xFFFFFFFFL))
            throw new IllegalArgumentException("invalid entry crc-32");
        m_lCrc = lCrc;
    } /* setCrc */

    /**
     * uncompressed size of entry data
     */
    private long m_lSize = -1;

    /**
     * @return uncompressed size of entry data
     */
    public long getSize() {
        return m_lSize;
    }

    /**
     * @param lSize uncompressed size of entry data
     */
    public void setSize(long lSize) {
        m_lSize = lSize;
    }

    /**
     * compressed size of entry data
     */
    private long m_lCompressedSize = -1;

    /**
     * @return compressed size of entry data
     */
    public long getCompressedSize() {
        return m_lCompressedSize;
    }

    /**
     * @param lCompressedSize compressed size of entry data
     */
    public void setCompressedSize(long lCompressedSize) {
        m_lCompressedSize = lCompressedSize;
    }

    /**
     * optional extra field data for entry
     */
    private byte[] m_bufExtra = new byte[]{};

    /**
     * @return optional extra field data for entry
     */
    public byte[] getExtra() {
        return m_bufExtra;
    }

    /**
     * @param bufExtra optional extra field data for entry
     */
    public void setExtra(byte[] bufExtra) {
        if ((bufExtra != null) && (bufExtra.length > 0xFFFF))
            throw new IllegalArgumentException("invalid extra field length");
        m_bufExtra = bufExtra;
    } /* setExtra */

    /**
     * optional comment string for entry
     */
    private String m_sComment = "";

    /**
     * @return optional comment string for entry
     */
    public String getComment() {
        return m_sComment;
    }

    /**
     * @param sComment optional comment string for entry
     */
    public void setComment(String sComment) {
        if ((sComment != null) && (sComment.length() > 0xFFFF))
            throw new IllegalArgumentException("invalid entry comment length");
        m_sComment = sComment;
    }

    /**
     * offset of loc header
     */
    private long m_lOffset = -1;

    /**
     * @return offset of loc header
     */
    public long getOffset() {
        return m_lOffset;
    }

    /**
     * @param lOffset return offset of loc header
     */
    public void setOffset(long lOffset) {
        m_lOffset = lOffset;
    }

  /*====================================================================
  constants
  ====================================================================*/
    /**
     * Compression method for uncompressed entries.
     */
    public static final int iMETHOD_STORED = 0;
    /**
     * Compression method for compressed (deflated) entries.
     */
    public static final int iMETHOD_DEFLATED = 8;
    /**
     * flag for encryption: Bit 0
     */
    public static final int iFLAG_ENCRYPTED = 0x00000001;
    /**
     * flag for deferred data (crc, size, compressed size) in local header: Bit 3
     */
    public static final int iFLAG_DEFERRED = 0x00000008;
    /**
     * flag for UTF8: Bit 11
     */
    public static final int iFLAG_EFS = 0x00000800;
    /**
     * version needed for reading ZIP files produced by Zip64File
     */
    public static final int iVERSION_NEEDED_ZIP64 = 0x0000002d; /* 4.5 */
    /**
     * version needed for reading ZIP files produced by ZIP32 tools
     */
    public static final int iVERSION_NEEDED_ZIP = 0x00000014; /* 2.0 */

  /*====================================================================
  Methods
  ====================================================================*/
    /*------------------------------------------------------------------*/

    /**
     * returns a string representation of the ZIP entry.
     *
     * @return name as string representation.
     */
    @Override
    public String toString() {
        return getName();
    } /* toString */

    /*------------------------------------------------------------------*/

    /**
     * clones the FileEntry instance.
     *
     * @return cloned instance.
     */
    @Override
    public Object clone()
            throws CloneNotSupportedException {
        return super.clone();
    } /* clone */
  
  /*====================================================================
  Constructors
  ====================================================================*/
    /*------------------------------------------------------------------*/

    /**
     * Creates a new file entry with the specified name.
     *
     * @param sName the entry name
     * @throws NullPointerException     if the entry name is null
     * @throws IllegalArgumentException if the entry name is longer than
     *                                  0xFFFF bytes
     */
    public FileEntry(String sName) {
        if (sName == null)
            throw new NullPointerException();
        if (sName.length() > 0xFFFF)
            throw new IllegalArgumentException("entry name too long");
        m_sName = sName;
    } /* constructor */

    /*------------------------------------------------------------------*/

    /**
     * returns true if this is a directory entry. A directory entry is
     * defined to be one whose name ends with a '/'.
     *
     * @return true if this is a directory entry
     */
    public boolean isDirectory() {
        return m_sName.endsWith("/");
    }

    /*------------------------------------------------------------------*/

    /**
     * Converts DOS time to Java time (number of milliseconds since epoch).
     *
     * @param dtime DOS time.
     * @return JAVA time (number of milliseconds since epoch).
     */
    private static long dosToJavaTime(long dtime) {
        Calendar gc = new GregorianCalendar();
        int iYear = (int) ((dtime >> 25) & 0x7f); /* 1980 = 0 */
        int iMonth = (int) ((dtime >> 21) & 0x0f); /* 1 .. 12 */
        int iDate = (int) ((dtime >> 16) & 0x1f); /* 1 .. 31 */
        int iHour = (int) ((dtime >> 11) & 0x1f); /* 0 .. 23 */
        int iMinute = (int) ((dtime >> 5) & 0x3f); /* 0 .. 59 */
        int iSecond = (int) ((dtime << 1) & 0x3e); /* 0 .. 59 */
        gc.clear();
        gc.set(
                iYear + 1980,
                iMonth - 1,
                iDate,
                iHour,
                iMinute,
                iSecond);
        /**
         Original code replaced by Calendar to get rid of the stupid deprecated messages:
         Date d =
         new Date((int)(((dtime >> 25) & 0x7f) + 80),
         (int)(((dtime >> 21) & 0x0f) - 1),
         (int)((dtime >> 16) & 0x1f), (int)((dtime >> 11) & 0x1f),
         (int)((dtime >> 5) & 0x3f), (int)((dtime << 1) & 0x3e));
         **/
        return gc.getTimeInMillis();
    } /* dosToJavaTime */

    /*------------------------------------------------------------------*/

    /**
     * Converts Java time to DOS time.
     *
     * @param time JAVA time (number of milliseconds since epoch).
     * @return DOS time.
     */
    private static long javaToDosTime(long time) {
        long dtime = (1 << 21) | (1 << 16); // default value before 1980
        Calendar gc = new GregorianCalendar();
        gc.clear();
        /* pkzip compatible rounding */
        gc.setTimeInMillis(2000 * (long) Math.ceil(time / 2000.0));
        if (gc.get(Calendar.YEAR) >= 1980) {
            dtime = (gc.get(Calendar.YEAR) - 1980) << 25 |
                    (gc.get(Calendar.MONTH) + 1) << 21 |
                    gc.get(Calendar.DATE) << 16 |
                    gc.get(Calendar.HOUR_OF_DAY) << 11 |
                    gc.get(Calendar.MINUTE) << 5 |
                    gc.get(Calendar.SECOND) >> 1;
        }
        return dtime;
        /***
         Original code replaced by Calendar to get rid of the stupid deprecated messages:
         Date d = new Date(time);
         int year = d.getYear() + 1900;
         if (year < 1980)
         {
         return (1 << 21) | (1 << 16);
         }
         return (year - 1980) << 25 | (d.getMonth() + 1) << 21
         | d.getDate() << 16 | d.getHours() << 11 | d.getMinutes() << 5
         | d.getSeconds() >> 1;
         ***/
    } /* javaToDosTime */

} /* FileEntry */
