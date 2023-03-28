/*== Zip64File.java ====================================================
Zip64File implements an updateable ZIP file.
Version     : $Id: Zip64File.java 51 2016-09-07 17:11:10Z hartwigthomas $
Application : ZIP Utilities
Description : Zip64File implements an updateable ZIP file.
              It handles large ZIP files but does not (yet) support
              splitting and spanning or encryption.
------------------------------------------------------------------------
Copyright  : Enter AG, Zurich, Switzerland, 2008
Created    : 25.02.2008, Hartwig Thomas
======================================================================*/

package ch.enterag.utils.zip;

import ch.enterag.utils.BU;
import ch.enterag.utils.SU;
import ch.enterag.utils.io.DiskFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.ZipException;

/*====================================================================*/

/**
 * Zip64File implements an updateable ZIP file.
 *
 * @author Hartwig Thomas
 */
public class Zip64File {
  /*====================================================================
  constants
  ====================================================================*/
    /**
     * buffer size for I/O
     */
    private static int iBUFFER_SIZE = 4096;
    /**
     * maximum unsigned 16 bit value
     */
    private static int iMAX16 = 0x0000FFFF;
    /**
     * maximum unsigned 32 bit value
     */
    private static long lMAX32 = 0x00000000FFFFFFFFL;
    /**
     *
     */
    private static short wZIP64EIEF_ID = 0x0001;
	
  /*====================================================================
  (private) data members
  ====================================================================*/
    /**
     * associated disk file
     */
    private DiskFile m_df = null;
    /**
     * comment
     */
    private String m_sComment = null;
    /**
     * extensible data
     */
    private byte[] m_bufExtensibleData = new byte[0];
    /**
     * number of file entries
     */
    private int m_iFileEntries = 0;
    /**
     * start of central directory
     */
    private long m_lCentralDirectoryStart = 0;
    /**
     * file entry list: file entries
     */
    private final List<FileEntry> m_listFileEntries = new ArrayList<FileEntry>();
    /**
     * file entry map: file entry name / file entry
     */
    private final Map<String, FileEntry> m_mapFileEntries = new HashMap<String, FileEntry>();
    /**
     * indicator, if ZIP file was changed
     */
    private boolean m_bChanged = false;
    /**
     * deflater of open output stream
     */
    private final Deflater m_def = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
    /**
     * local file entry of open input/output stream
     */
    private FileEntry m_feLocal = null;
    /**
     * running crc computation of open output stream
     */
    private final CRC32 m_crc = new CRC32();
    /**
     * running count of bytes read
     */
    private long m_lCompressedSize = 0;
    /**
     * running count of bytes delivered
     */
    private long m_lSize = 0;
  
  /*====================================================================
  RandomAccessFile methods redefined for little-endian I/O
  ====================================================================*/
    /*------------------------------------------------------------------*/

    /**
     * like readUnsignedShort but little-endian.
     *
     * @return short read.
     * @throws IOException if an I/O error occurred.
     */
    private int readInt16()
            throws IOException {
        byte[] bufInt = new byte[4];
        Arrays.fill(bufInt, (byte) 0);
        m_df.readFully(bufInt, 0, 2);
        return BU.toInt(bufInt);
    } /* readInt16 */

    /*------------------------------------------------------------------*/

    /**
     * like writeUnsignedShort but little-endian.
     *
     * @param iInt int to be written.
     * @throws IOException if an I/O error occurred.
     */
    private void writeInt16(int iInt)
            throws IOException {
        byte[] bufInt = BU.fromInt(iInt);
        m_df.write(bufInt, 0, 2);
    } /* writeInt16 */

    /*------------------------------------------------------------------*/

    /**
     * like readInt but little-endian.
     *
     * @return int read.
     * @throws IOException if an I/O error occurred.
     */
    private int readInt32()
            throws IOException {
        byte[] bufInt = new byte[4];
        m_df.readFully(bufInt);
        return BU.toInt(bufInt);
    } /* readInt32 */

    /*------------------------------------------------------------------*/

    /**
     * like writeInt but little-endian.
     *
     * @param iInt int to be written.
     * @throws IOException if an I/O error occurred.
     */
    private void writeInt32(int iInt)
            throws IOException {
        byte[] bufInt = BU.fromInt(iInt);
        m_df.write(bufInt);
    } /* writeInt32 */

    /*------------------------------------------------------------------*/

    /**
     * like readUnsignedInt but little-endian.
     *
     * @return int read.
     * @throws IOException if an I/O error occurred.
     */
    private long readLong32()
            throws IOException {
        byte[] bufLong = new byte[8];
        Arrays.fill(bufLong, (byte) 0);
        m_df.readFully(bufLong, 0, 4);
        return BU.toLong(bufLong);
    } /* readLong32 */

    /*------------------------------------------------------------------*/

    /**
     * like writeUnsignedLong but little-endian.
     *
     * @param lLong long to be written.
     * @throws IOException if an I/O error occurred.
     */
    private void writeLong32(long lLong)
            throws IOException {
        byte[] bufInt = BU.fromLong(lLong);
        m_df.write(bufInt, 0, 4);
    } /* writeLong32 */

    /*------------------------------------------------------------------*/

    /**
     * like readLong but little-endian.
     *
     * @return int read.
     * @throws IOException if an I/O error occurred.
     */
    private long readLong64()
            throws IOException {
        byte[] bufLong = new byte[8];
        m_df.readFully(bufLong);
        return BU.toLong(bufLong);
    } /* readLong64 */

    /*------------------------------------------------------------------*/

    /**
     * like writeLong but little-endian.
     *
     * @param lLong long to be written.
     * @throws IOException if an I/O error occurred.
     */
    private void writeLong64(long lLong)
            throws IOException {
        byte[] bufLong = BU.fromLong(lLong);
        m_df.write(bufLong);
    } /* writeLong64 */
  
  /*====================================================================
  private methods
  ====================================================================*/
    /*------------------------------------------------------------------*/

    /**
     * analyzes the extra field and extracts size and compressed size.
     *
     * @param fe file entry.
     * @return true, if extended information was available.
     */
    private static boolean analyzeExtraField(FileEntry fe) {
        boolean bExtendedInformation = false;
        for (int iPos = 0;
             (iPos < fe.getExtra().length) &&
                     ((fe.getSize() == lMAX32) ||
                             (fe.getCompressedSize() == lMAX32) ||
                             (fe.getOffset() == lMAX32)); ) {
            short wHeaderId = BU.toShort(fe.getExtra(), iPos);
            iPos += 2;
            int iDataLength = BU.toShort(fe.getExtra(), iPos) & iMAX16;
            iPos += 2;
            if (wHeaderId == wZIP64EIEF_ID) {
                bExtendedInformation = true;
                int iOffset = iPos;
                if ((iOffset + 8 <= iPos + iDataLength) && (fe.getSize() == lMAX32)) {
                    fe.setSize(BU.toLong(fe.getExtra(), iOffset));
                    iOffset += 8;
                }
                if ((iOffset + 8 <= iPos + iDataLength) && (fe.getCompressedSize() == lMAX32)) {
                    fe.setCompressedSize(BU.toLong(fe.getExtra(), iOffset));
                    iOffset += 8;
                }
                if ((iOffset + 8 <= iPos + iDataLength) && (fe.getOffset() == lMAX32)) {
                    fe.setOffset(BU.toLong(fe.getExtra(), iOffset));
                    iOffset += 8;
                }
            }
            iPos += iDataLength;
        }
        return bExtendedInformation;
    } /* analyzeExtraField */

    /*------------------------------------------------------------------*/

    /**
     * updates the extra field based on member values of the file entry.
     *
     * @param fe file entry.
     */
    private static void updateExtraField(FileEntry fe) {
        byte[] bufExtra = fe.getExtra();
        /* remove all instances of a Zip64 extension field */
        for (int iPos = 0; iPos < bufExtra.length; ) {
            short wHeaderId = BU.toShort(bufExtra, iPos);
            iPos += 2;
            int iDataLength = BU.toShort(bufExtra, iPos) & iMAX16;
            iPos += 2;
            if (wHeaderId == wZIP64EIEF_ID) {
                /* cut iPos-4 to iPos + iDatalength from bufExtra and set iPos to iPos - 4*/
                byte[] buf = new byte[bufExtra.length - (iDataLength + 4)];
                System.arraycopy(bufExtra, 0, buf, 0, iPos - 4);
                System.arraycopy(bufExtra, iPos + iDataLength, buf, iPos - 4, bufExtra.length - (iPos + iDataLength));
                bufExtra = buf;
            } else
                iPos += iDataLength;
        }
        /* generate the new Zip64 extension field */
        short wDataSize = 0;
        if (fe.getSize() >= lMAX32)
            wDataSize += 8;
        if (fe.getCompressedSize() >= lMAX32)
            wDataSize += 8;
        if (fe.getOffset() >= lMAX32)
            wDataSize += 8;
        if (wDataSize > 0) {
            /* id + len + data + rest of bufExtra */
            byte[] buf = new byte[2 + 2 + wDataSize + bufExtra.length];
            short wHeaderId = wZIP64EIEF_ID; /* Zip64 extended information extra field */
            int iPos = 0;
            byte[] bufHeaderId = BU.fromShort(wHeaderId);
            System.arraycopy(bufHeaderId, 0, buf, iPos, bufHeaderId.length);
            iPos += bufHeaderId.length;
            byte[] bufDataSize = BU.fromShort(wDataSize);
            System.arraycopy(bufDataSize, 0, buf, iPos, bufDataSize.length);
            iPos += bufDataSize.length;
            if (fe.getSize() >= lMAX32) {
                byte[] bufOriginalSize = BU.fromLong(fe.getSize());
                System.arraycopy(bufOriginalSize, 0, buf, iPos, bufOriginalSize.length);
                iPos += bufOriginalSize.length;
            }
            if (fe.getCompressedSize() >= lMAX32) {
                byte[] bufCompressedSize = BU.fromLong(fe.getCompressedSize());
                System.arraycopy(bufCompressedSize, 0, buf, iPos, bufCompressedSize.length);
                iPos += bufCompressedSize.length;
            }
            if (fe.getOffset() >= lMAX32) {
                byte[] bufOffset = BU.fromLong(fe.getOffset());
                System.arraycopy(bufOffset, 0, buf, iPos, bufOffset.length);
                iPos += bufOffset.length;
            }
            /* now iPos must be equal to 2+2+wDataSize */
            /* append bufExtra */
            System.arraycopy(bufExtra, 0, buf, iPos, bufExtra.length);
            /* this is the new extra field buffer */
            bufExtra = buf;
        }
        fe.setExtra(bufExtra);
    } /* updateExtraField */

    /*------------------------------------------------------------------*/

    /**
     * Reads data descriptor, if present.
     * Package-private method is used by EntryInputStream.
     *
     * @param fe file entry to be completed.
     * @throws IOException if an I/O error occurred.
     */
    void getDataDescriptor(FileEntry fe)
            throws IOException {
        /* EXT descriptor present */
        boolean bZip64 = analyzeExtraField(fe);
        long lFilePointer = m_df.getFilePointer();
        /* the flags of the file entry indicate, if a data descriptor is to
         * be expected */
        if ((fe.getFlags() & FileEntry.iFLAG_DEFERRED) != 0) {
            long l = readLong32();
            if (l == 0x08074b50)
                fe.setCrc(readLong32());
            else
                fe.setCrc(l);
            if (!bZip64) {
                fe.setCompressedSize(readLong32());
                fe.setSize(readLong32());
            } else {
                fe.setCompressedSize(readLong64());
                fe.setSize(readLong64());
            }
        }
        /* if we find a signature, we support old erroneous flag handling */
        else if (lFilePointer <= m_lCentralDirectoryStart - 4) {
            long l = readLong32();
            if (l == 0x08074b50) {
                fe.setCrc(readLong32());
                if (!bZip64) {
                    fe.setCompressedSize(readLong32());
                    fe.setSize(readLong32());
                } else {
                    fe.setCompressedSize(readLong64());
                    fe.setSize(readLong64());
                }
            } else /* backup */
                m_df.seek(lFilePointer);
        }
    } /* getDataDescriptor */

    /*------------------------------------------------------------------*/

    /**
     * reads a local file entry based on a global file entry and
     * leaves the file pointer positioned at the beginning of the
     * file data.
     * Package-private method is used by EntryInputStream.
     *
     * @param feGlobal global file entry.
     * @return local file entry read.
     * @throws IOException  if an error occurred.
     * @throws ZipException if ZIP file format not valid.
     */
    FileEntry getLocalFileEntry(FileEntry feGlobal)
            throws IOException, ZipException {
        /* set file pointer */
        m_df.seek(feGlobal.getOffset());
        FileEntry fe = null;
        int iSignature = readInt32();
        if (iSignature != 0x04034b50)
            throw new ZipException("Invalid signature of local  file header entry!");
        int iVersionNeeded = readInt16();
        int iFlags = readInt16();
        if ((iFlags & FileEntry.iFLAG_ENCRYPTED) != 0)
            throw new ZipException("Encryption not supported!");
        int iCompressionMethod = readInt16();
        if ((iCompressionMethod != FileEntry.iMETHOD_STORED) && (iCompressionMethod != FileEntry.iMETHOD_DEFLATED))
            throw new ZipException("Compression method " + String.valueOf(iCompressionMethod) + " not supported!");
        long lLastModFileDateTime = readLong32();
        long lCrc = readLong32();
        /* if -1 then look at extra field */
        long lCompressedSize = readLong32();
        /* if -1 then look at extra field */
        long lSize = readLong32();
        int iFileNameLength = readInt16();
        int iExtraFieldLength = readInt16();
        byte[] bufFileName = new byte[iFileNameLength];
        m_df.readFully(bufFileName);
        String sFileName;
        if ((iFlags & FileEntry.iFLAG_EFS) != 0)
            sFileName = SU.getUtf8String(bufFileName);
        else
            sFileName = SU.getCp437String(bufFileName);
        byte[] bufExtraField = new byte[iExtraFieldLength];
        m_df.readFully(bufExtraField);
        fe = new FileEntry(sFileName);
        fe.setOffset(feGlobal.getOffset());
        fe.setVersionNeeded(iVersionNeeded);
        fe.setDateTime(lLastModFileDateTime);
        fe.setFlags(iFlags);
        fe.setCrc(lCrc);
        fe.setExtra(bufExtraField);
        fe.setMethod(iCompressionMethod);
        fe.setCompressedSize(lCompressedSize);
        fe.setSize(lSize);
        /* analyze extra field if needed: 0001 + len2 + Zip64 extended information extra field  */
        if ((lSize == lMAX32) || (lCompressedSize == lMAX32)) {
            Zip64File.analyzeExtraField(fe);
            /* restore the necessary lMAX32 for final analysis in getDataDescriptor */
            /***
             if ((fe.getFlags() & FileEntry.iFLAG_DEFERRED) != 0)
             {
             ***/
            fe.setCompressedSize(lCompressedSize);
            fe.setSize(lSize);
            /***
             }
             ***/
        }
        return fe;
    } /* getLocalFileEntry */

    /*------------------------------------------------------------------*/

    /**
     * reads a central directory entry.
     *
     * @return central directory entry.
     * @throws IOException if an error occurred.
     */
    private FileEntry getCentralDirectoryEntry()
            throws IOException {
        FileEntry fe = null;
        int iSignature = readInt32();
        if (iSignature != 0x02014b50)
            throw new ZipException("Invalid signature of central directory entry!");
        /* int iVersionMadeBy = */
        readInt16();
        int iVersionNeeded = readInt16();
        int iFlags = readInt16();
        if ((iFlags & FileEntry.iFLAG_ENCRYPTED) != 0)
            throw new ZipException("Encryption not supported!");
        int iCompressionMethod = readInt16();
        if ((iCompressionMethod != FileEntry.iMETHOD_STORED) && (iCompressionMethod != FileEntry.iMETHOD_DEFLATED))
            throw new ZipException("Compression method " + String.valueOf(iCompressionMethod) + " not supported!");
        long lLastModFileDateTime = readLong32();
        long lCrc = readLong32();
        /* if -1 then look at extra field */
        long lCompressedSize = readLong32();
        /* if -1 then look at extra field */
        long lSize = readLong32();
        int iFileNameLength = readInt16();
        int iExtraFieldLength = readInt16();
        int iFileCommentLength = readInt16();
        int iDiskNumberStart = readInt16();
        if (iDiskNumberStart != 0)
            throw new ZipException("Split or spanned ZIP files are not supported!");
        /* int iInternalFileAttributes = */
        readInt16(); /* currently not supported */
        /* long lExternalFileAttributes = */
        readLong32(); /* currently not supported */
        /* if -1 then look at extra field */
        long lOffsetLocalHeader = readLong32();
        byte[] bufFileName = new byte[iFileNameLength];
        m_df.readFully(bufFileName);
        String sFileName;
        if ((iFlags & FileEntry.iFLAG_EFS) != 0)
            sFileName = SU.getUtf8String(bufFileName);
        else
            sFileName = SU.getCp437String(bufFileName);
        byte[] bufExtraField = new byte[iExtraFieldLength];
        m_df.readFully(bufExtraField);
        byte[] bufFileComment = new byte[iFileCommentLength];
        m_df.readFully(bufFileComment);
        String sFileComment;
        if ((iFlags & FileEntry.iFLAG_EFS) != 0)
            sFileComment = SU.getUtf8String(bufFileComment);
        else
            sFileComment = SU.getCp437String(bufFileComment);
        fe = new FileEntry(sFileName);
        fe.setComment(sFileComment);
        fe.setCompressedSize(lCompressedSize);
        fe.setCrc(lCrc);
        fe.setExtra(bufExtraField);
        fe.setOffset(lOffsetLocalHeader);
        fe.setMethod(iCompressionMethod);
        fe.setSize(lSize);
        fe.setDateTime(lLastModFileDateTime);
        fe.setFlags(iFlags);
        fe.setVersionNeeded(iVersionNeeded);
        /* analyze extra field if needed: 0001 + len2 + Zip64 extended information extra field  */
        if ((lSize == lMAX32) ||
                (lCompressedSize == lMAX32) ||
                (lOffsetLocalHeader == lMAX32))
            analyzeExtraField(fe);
        return fe;
    } /* getCentralDirectoryEntry */

    /*------------------------------------------------------------------*/

    /**
     * reads the Zip64 end of central directory record and returns
     * position of start of central directory.
     *
     * @param lStart start of the Zip64 end of central directory record.
     * @return position of start of central directory.
     * @throws IOException if an error occurred.
     */
    private long getZip64CenDirStart(long lStart)
            throws IOException {
        long lCenDirStart = -1;
        m_df.seek(lStart);
        int iSignature = readInt32();
        if (iSignature != 0x06064b50)
            throw new ZipException("Invalid signature of Zip64 end of central directory record!");
        long lSizeRecord = readLong64(); // size of this record
        /* int iVersionMadeBy = */
        readInt16();
        /* int iVersionNeeded = */
        readInt16();
        int iDiskNumber = readInt32(); // number if this disk
        if (iDiskNumber != 0)
            throw new ZipException("Split or spanned ZIP files are not supported!");
        int iDiskCenDirStart = readInt32(); // number of disk of start of central directory
        if (iDiskCenDirStart != 0)
            throw new ZipException("Split or spanned ZIP files are not supported!");
        /* long lCdEntriesOnDisk = */
        readLong64();
        long lFileEntries = readLong64();
        if (lFileEntries > Integer.MAX_VALUE)
            throw new ZipException("Too many files! Cannot handle " + String.valueOf(lFileEntries) + ".");
        m_iFileEntries = (int) lFileEntries;
        /* long lCdSize = */
        readLong64();
        lCenDirStart = readLong64();
        int iExtensibleDataSize = (int) lSizeRecord - 44;
        m_bufExtensibleData = new byte[iExtensibleDataSize];
        m_df.read(m_bufExtensibleData);
        return lCenDirStart;
    } /* getZip64CenDirStart */

    /*------------------------------------------------------------------*/

    /**
     * reads the Zip64 end of central directory locator to determine
     * the start of the Zip 64 end of central directory record.
     *
     * @param lEndCenDirStart position of start of end of central directory record.
     * @return position of start of the Zip 64 end of central directory record.
     * @throws IOException if an error occurred.
     */
    private long getZip64EndOfCenDirStart(long lEndCenDirStart)
            throws IOException {
        long lZip64EndOfCenDirStart = -1;
        /* size of locator is 20 bytes, first 4 bytes are signature */
        m_df.seek(lEndCenDirStart - 20);
        int iSignature = readInt32();
        if (iSignature != 0x07064b50)
            throw new ZipException("Invalid signature of Zip64 end of central directory locator!");
        int iDiskZip64Eocdr = readInt32();
        if (iDiskZip64Eocdr != 0)
            throw new ZipException("Split or spanned ZIP files are not supported!");
        lZip64EndOfCenDirStart = readLong64();
        int iTotalNumberOfDisks = readInt32();
        if (iTotalNumberOfDisks != 1)
            throw new ZipException("Split or spanned ZIP files are not supported!");
        return lZip64EndOfCenDirStart;
    } /* getZip64EndOfCenDirStart */

    /*------------------------------------------------------------------*/

    /**
     * reads the End of central directory record and - if needed -
     * the Zip64 end of central directory locator as well as the
     * Zip64 end of central directory record.
     *
     * @return position of start of central directory.
     * @throws IOException if an error occurred.
     */
    private long getCenDirStart()
            throws IOException {
        long lCenDirStart = -1;
        /* go to the end of the file */
        m_df.seek(m_df.length());
        /* search for signature of the end of central directory record (comment can be of variable length)*/
        long lEndCenDirRecord = m_df.lastIndexOf(BU.fromInt(0x06054b50));
        if ((m_sComment == null) && (lEndCenDirRecord >= 0)) {
            /* get length of comment */
            m_df.seek(lEndCenDirRecord + 20);
            int iCommentLength = readInt16();
            if (m_df.length() == lEndCenDirRecord + 22 + iCommentLength) {
                byte[] bufComment = new byte[iCommentLength];
                m_df.readFully(bufComment, 0, iCommentLength);
                m_sComment = SU.getUtf8String(bufComment);
            }
        }
        if (m_sComment == null)
            throw new ZipException("End of central directory not found! (Invalid ZIP64 file?)");
        /* go the the beginning of the end of central directory record, skipping the signature */
        m_df.seek(lEndCenDirRecord + 4);
        /* read number of this disk (must be 0) */
        int iLastDisk = readInt16();
        /* read number of the disk where the central directory starts (must be 0) */
        int iCdDisk = readInt16();
        if ((iLastDisk != 0) || (iCdDisk != 0))
            throw new ZipException("Split or spanned ZIP files are not supported!");
        /* read number of entries on this disk */
        /* int iDiskEntries = */
        readInt16();
        m_iFileEntries = readInt16();
        long lCdSize = readLong32();
        lCenDirStart = readLong32();
        if ((m_iFileEntries == iMAX16) || (lCdSize == lMAX32) || (lCenDirStart == lMAX32)) {
            long lPos = getZip64EndOfCenDirStart(lEndCenDirRecord);
            lPos = getZip64CenDirStart(lPos);
            if (lCenDirStart == lMAX32)
                lCenDirStart = lPos;
        }
        return lCenDirStart;
    } /* getCenDirStart */

    /*------------------------------------------------------------------*/

    /**
     * reads the central directory.
     *
     * @throws IOException if an error occurred.
     */
    private void getCentralDirectory()
            throws IOException {
        if (m_df.length() > 0) {
            m_lCentralDirectoryStart = getCenDirStart();
            m_df.seek(m_lCentralDirectoryStart);
            for (int i = 0; i < m_iFileEntries; i++) {
                FileEntry fe = getCentralDirectoryEntry();
                m_listFileEntries.add(fe);
                m_mapFileEntries.put(fe.getName(), fe);
            }
            m_df.seek(m_lCentralDirectoryStart);
        } else
            m_sComment = "";
    } /* getCentralDirectory */

    /*------------------------------------------------------------------*/

    /**
     * Writes data descriptor at the end of deflated file entry.
     *
     * @param fe              file entry to be completed.
     * @param lCompressedSize compressed size to be put.
     * @param lSize           size to be put.
     * @throws IOException if an I/O error occurred.
     */
    private void putDataDescriptor(FileEntry fe, long lCompressedSize, long lSize)
            throws IOException {
        /* the flags of the file entry indicate, if a data descriptor is to be written */
        if ((fe.getFlags() & FileEntry.iFLAG_DEFERRED) != 0) {
            /* EXT descriptor present */
            boolean bZip64 = analyzeExtraField(fe);
            fe.setSize(lSize);
            fe.setCompressedSize(lCompressedSize);
            writeInt32(0x08074b50);
            writeLong32(fe.getCrc());
            if (!bZip64) {
                writeLong32(fe.getCompressedSize());
                writeLong32(fe.getSize());
            } else {
                writeLong64(fe.getCompressedSize());
                writeLong64(fe.getSize());
            }
        }
    } /* putDataDescriptor */

    /*------------------------------------------------------------------*/

    /**
     * writes a local file entry at the current position.
     *
     * @param fe file entry to be written.
     * @throws IOException  if an error occurred.
     * @throws ZipException if ZIP file format not valid.
     */
    private void putLocalFileEntry(FileEntry fe)
            throws IOException, ZipException {
        fe.setOffset(m_df.getFilePointer());
        /* set file pointer */
        int iSignature = 0x04034b50;
        writeInt32(iSignature);
        writeInt16(fe.getVersionNeeded());
        writeInt16(fe.getFlags());
        writeInt16(fe.getMethod());
        writeLong32(fe.getDateTime());
        writeLong32(fe.getCrc());
        writeLong32(fe.getCompressedSize());
        writeLong32(fe.getSize());
        byte[] bufFileName = null;
        if ((fe.getFlags() & FileEntry.iFLAG_EFS) != 0)
            bufFileName = SU.putUtf8String(fe.getName());
        else
            bufFileName = SU.putCp437String(fe.getName());
        writeInt16(bufFileName.length);
        writeInt16(fe.getExtra().length);
        m_df.write(bufFileName);
        m_df.write(fe.getExtra());
    } /* putLocalFileEntry */

    /*------------------------------------------------------------------*/

    /**
     * writes a central directory entry.
     *
     * @param fe file entry to be written.
     * @throws IOException if an error occurred.
     */
    private void putCentralDirectoryEntry(FileEntry fe)
            throws IOException {
        writeInt32(0x02014b50);
        writeInt16(FileEntry.iVERSION_NEEDED_ZIP64);
        writeInt16(FileEntry.iVERSION_NEEDED_ZIP64);
        writeInt16(fe.getFlags());
        writeInt16(fe.getMethod());
        writeLong32(fe.getDateTime());
        writeLong32(fe.getCrc());
        if (fe.getCompressedSize() < lMAX32)
            writeLong32(fe.getCompressedSize());
        else
            writeLong32(lMAX32);
        if (fe.getSize() < lMAX32)
            writeLong32(fe.getSize());
        else
            writeLong32(lMAX32);
        byte[] bufFileName = null;
        if ((fe.getFlags() & FileEntry.iFLAG_EFS) != 0)
            bufFileName = SU.putUtf8String(fe.getName());
        else
            bufFileName = SU.putCp437String(fe.getName());
        writeInt16(bufFileName.length);
        writeInt16(fe.getExtra().length);
        byte[] bufComment = null;
        if ((fe.getFlags() & FileEntry.iFLAG_EFS) != 0)
            bufComment = SU.putUtf8String(fe.getComment());
        else
            bufComment = SU.putCp437String(fe.getComment());
        writeInt16(bufComment.length);
        writeInt16(0);
        writeInt16(0);
        writeInt32(0);
        if (fe.getOffset() < lMAX32)
            writeLong32(fe.getOffset());
        else
            writeLong32(lMAX32);
        m_df.write(bufFileName);
        m_df.write(fe.getExtra());
        m_df.write(bufComment);
        m_bChanged = false;
    } /* putCentralDirectoryEntry */

    /*------------------------------------------------------------------*/

    /**
     * writes the end records of the central directory entry.
     *
     * @throws IOException if an error occurred.
     */
    private void putCentralDirectoryEnd()
            throws IOException {
        long lCentralDirectoryEnd = m_df.getFilePointer();
        long lCenDirSize = m_df.getFilePointer() - m_lCentralDirectoryStart;
        if ((m_iFileEntries >= iMAX16) || (lCenDirSize >= lMAX32) || (m_lCentralDirectoryStart >= lMAX32)) {
            /* Zip64 end of central directory record */
            writeInt32(0x06064b50);
            writeLong64(44 + m_bufExtensibleData.length); /* size of this record - 12 */
            writeInt16(FileEntry.iVERSION_NEEDED_ZIP64);
            writeInt16(FileEntry.iVERSION_NEEDED_ZIP64);
            writeInt32(0);
            writeInt32(0);
            writeLong64(m_iFileEntries);
            writeLong64(m_iFileEntries);
            writeLong64(lCenDirSize);
            writeLong64(m_lCentralDirectoryStart);
            m_df.write(m_bufExtensibleData);
            /* Zip64 end of central directory locator */
            writeInt32(0x07064b50);
            writeInt32(0);
            writeLong64(lCentralDirectoryEnd);
            writeInt32(1);
        }
        /* write the final central directory */
        writeInt32(0x06054b50);
        writeInt16(0);
        writeInt16(0);
        if (m_iFileEntries >= iMAX16) {
            writeInt16(iMAX16);
            writeInt16(iMAX16);
        } else {
            writeInt16(m_iFileEntries);
            writeInt16(m_iFileEntries);
        }
        if (lCenDirSize >= lMAX32)
            writeLong32(lMAX32);
        else
            writeLong32(lCenDirSize);
        if (m_lCentralDirectoryStart >= lMAX32)
            writeLong32(lMAX32);
        else
            writeLong32(m_lCentralDirectoryStart);
        byte[] bufComment = SU.putUtf8String(m_sComment);
        writeInt16(bufComment.length);
        if (bufComment.length > 0)
            m_df.write(bufComment);
    } /* putCentralDirectoryEnd */

    /*------------------------------------------------------------------*/

    /**
     * reads the central directory.
     *
     * @throws IOException if an error occurred.
     */
    private void putCentralDirectory()
            throws IOException {
        for (Iterator<FileEntry> iterFileEntries = m_listFileEntries.iterator(); iterFileEntries.hasNext(); ) {
            FileEntry fe = iterFileEntries.next();
            putCentralDirectoryEntry(fe);
        }
        putCentralDirectoryEnd();
    } /* putCentralDirectory */
  
  /*====================================================================
  Constructors
  ====================================================================*/
    /*------------------------------------------------------------------*/

    /**
     * opens a random access zip file and reads its directory and
     * truncates its length to the end of the last file entry if it was
     * not opened exclusively for reading.
     *
     * @param sFileName name of file to be opened.
     * @param bReadOnly if true, file is opened for reading;
     *                  if false, file is opened for writing or updating.
     * @throws FileNotFoundException if file to be opened for reading or
     *                               directory where file is to be written
     *                               does not exist.
     * @throws IOException           if central directory could not be read.
     */
    public Zip64File(String sFileName, boolean bReadOnly)
            throws FileNotFoundException, IOException {
        m_df = new DiskFile(sFileName, bReadOnly);
        getCentralDirectory();
    } /* constructor */

    /*------------------------------------------------------------------*/

    /**
     * opens a random access zip file and reads its directory and
     * truncates its length to the end of the last file entry if it was
     * not opened exclusively for reading.
     *
     * @param file      file to be opened.
     * @param bReadOnly if true, file is opened for reading;
     *                  if false, file is opened for writing or updating.
     * @throws FileNotFoundException if file to be opened for reading or
     *                               directory where file is to be written
     *                               does not exist.
     * @throws IOException           if central directory could not be read.
     */
    public Zip64File(File file, boolean bReadOnly)
            throws FileNotFoundException, IOException {
        m_df = new DiskFile(file, bReadOnly);
        getCentralDirectory();
    } /* constructor */

    /*------------------------------------------------------------------*/

    /**
     * opens a random access disk file for writing/updating and reads
     * its directory and truncates its length to the end of the last file
     * entry.
     *
     * @param sFileName name of file to be opened.
     * @throws FileNotFoundException if directory where file is to be written
     *                               does not exist.
     * @throws IOException           if central directory could not be read.
     */
    public Zip64File(String sFileName)
            throws FileNotFoundException, IOException {
        m_df = new DiskFile(sFileName);
        getCentralDirectory();
    } /* constructor */

    /*------------------------------------------------------------------*/

    /**
     * opens a random access disk file for writing/updating and reads
     * its directory and truncates its length to the end of the last file
     * entry.
     *
     * @param file file to be opened.
     * @throws FileNotFoundException if directory where file is to be written
     *                               does not exist.
     * @throws IOException           if central directory could not be read.
     */
    public Zip64File(File file)
            throws FileNotFoundException, IOException {
        m_df = new DiskFile(file);
        getCentralDirectory();
    } /* constructor */

    /*------------------------------------------------------------------*/

    /**
     * returns the underlying DiskFile of the Zip64 file.
     *
     * @return underlying DiskFile .
     */
    public DiskFile getDiskFile() {
        return m_df;
    } /* getDiskFile */

    /*------------------------------------------------------------------*/

    /**
     * returns the number of file entries.
     *
     * @return number of file entries.
     */
    public int getFileEntries() {
        return m_iFileEntries;
    } /* getFileEntries */

    /*------------------------------------------------------------------*/

    /**
     * returns a named file entry.
     *
     * @param sEntryName name of file entry to be retrieved.
     * @return file entry or null, if entry does not exist.
     */
    public FileEntry getFileEntry(String sEntryName) {
        return m_mapFileEntries.get(sEntryName);
    } /* getFileEntry */

    /*------------------------------------------------------------------*/

    /**
     * returns the list of file entries.
     *
     * @return list of file entries.
     */
    public List<FileEntry> getListFileEntries() {
        return m_listFileEntries;
    } /* getListFileEntries */

    /*------------------------------------------------------------------*/

    /**
     * returns ZIP file comment (header).
     *
     * @return ZIP file comment.
     */
    public String getComment() {
        return m_sComment;
    }

    /*------------------------------------------------------------------*/

    /**
     * sets ZIP file comment (header).
     *
     * @param sComment ZIP file comment.
     * @throws IOException if an I/O error occurred.
     */
    public void setComment(String sComment)
            throws IOException {
        if (!m_sComment.equals(sComment)) {
            m_bChanged = true;
            m_df.setLength(m_lCentralDirectoryStart);
            m_sComment = sComment;
        }
    } /* setComment */

    /*------------------------------------------------------------------*/

    /**
     * open a file entry stream for reading.
     *
     * @param sEntryName name of file entry in ZIP file.
     * @return open EntryInputStream.
     * @throws FileNotFoundException if file not in ZIP.
     * @throws IOException           if an I/O error occurred.
     * @throws ZipException          if ZIP file format not valid.
     */
    public EntryInputStream openEntryInputStream(String sEntryName)
            throws FileNotFoundException, IOException, ZipException {
        return new EntryInputStream(this, sEntryName);
    } /* openEntryInputStream */

    /*------------------------------------------------------------------*/

    /**
     * open a file entry for writing.
     *
     * @param sEntryName   name of file entry in ZIP file.
     * @param iMethod      must be a FileEntry.iMETHOD_... constant.
     * @param dateModified time stamp for file entry or null.
     * @throws FileNotFoundException if file not in ZIP.
     * @throws IOException           if an I/O error occurred.
     * @throws ZipException          if file already stored in ZIP file.
     */
    private void openWrite(String sEntryName, int iMethod, Date dateModified)
            throws FileNotFoundException, IOException, ZipException {
        if ((iMethod != FileEntry.iMETHOD_DEFLATED) && (iMethod != FileEntry.iMETHOD_STORED))
            throw new IllegalArgumentException("Invalid method!");
        if (m_mapFileEntries.get(sEntryName) == null) {
            if (m_feLocal == null) {
                m_df.setLength(m_lCentralDirectoryStart);
                m_bChanged = true;
                /* create the file entry */
                m_feLocal = new FileEntry(sEntryName);
                m_feLocal.setOffset(m_lCentralDirectoryStart);
                if (dateModified != null)
                    m_feLocal.setTimeStamp(dateModified);
                if (!m_feLocal.isDirectory()) {
                    m_feLocal.setVersionNeeded(FileEntry.iVERSION_NEEDED_ZIP64);
                    m_feLocal.setFlags(FileEntry.iFLAG_DEFERRED | FileEntry.iFLAG_EFS);
                    m_feLocal.setSize(lMAX32);
                    m_feLocal.setCompressedSize(lMAX32);
                    m_feLocal.setMethod(iMethod);
                    /* create an extra field with 0 for size and compressed size and - if needed - with correct offset */
                    short wHeaderId = wZIP64EIEF_ID; /* Zip64 extended information extra field */
                    short wDataSize = 8 + 8; /* size, compressed size */
                    byte[] bufExtra = new byte[2 + 2 + wDataSize]; /* id, len, data */
                    int iPos = 0;
                    byte[] bufHeaderId = BU.fromShort(wHeaderId);
                    System.arraycopy(bufHeaderId, 0, bufExtra, iPos, bufHeaderId.length);
                    iPos += bufHeaderId.length;
                    byte[] bufDataSize = BU.fromShort(wDataSize);
                    System.arraycopy(bufDataSize, 0, bufExtra, iPos, bufDataSize.length);
                    iPos += bufDataSize.length;
                    byte[] bufOriginalSize = BU.fromLong(0);
                    System.arraycopy(bufOriginalSize, 0, bufExtra, iPos, bufOriginalSize.length);
                    iPos += bufOriginalSize.length;
                    byte[] bufCompressedSize = BU.fromLong(0);
                    System.arraycopy(bufCompressedSize, 0, bufExtra, iPos, bufCompressedSize.length);
                    iPos += bufCompressedSize.length;
                    /* now iPos must be equal to bufExtra.length */
                    m_feLocal.setExtra(bufExtra);
                } else {
                    m_feLocal.setVersionNeeded(FileEntry.iVERSION_NEEDED_ZIP);
                    m_feLocal.setFlags(FileEntry.iFLAG_EFS);
                    m_feLocal.setSize(0);
                    m_feLocal.setCompressedSize(0);
                    m_feLocal.setMethod(FileEntry.iMETHOD_STORED);
                    m_feLocal.setExtra(new byte[0]);
                }
                m_feLocal.setOffset(m_df.getFilePointer());
                /* write the local file entry with FFFFFFFF for size and compressed size and
                 * extra field with 0 for size and compressed size */
                putLocalFileEntry(m_feLocal);
                m_def.reset();
                m_crc.reset();
                m_lCompressedSize = 0;
                m_lSize = 0;
            } else
                throw new ZipException("Only one file entry can be opened for writing at a time!");
        } else
            throw new ZipException("File " + sEntryName + " cannot be added twice to ZIP file!");
    } /* openWrite */

    /*------------------------------------------------------------------*/

    /**
     * writes iLength bytes from buffer starting at iOffset.
     *
     * @param buf     buffer holding output.
     * @param iOffset offset in buffer where start writing.
     * @param iLength number of bytes to be written.
     * @throws IOException if an I/O error occurred.
     */
    private void writeDeflated(byte[] buf, int iOffset, int iLength)
            throws IOException {
        if (iLength > 0) {
            m_def.setInput(buf, iOffset, iLength);
            byte[] bufCompressed = new byte[iBUFFER_SIZE];
            while (!m_def.needsInput()) {
                int iWriteSize = m_def.deflate(bufCompressed);
                if (iWriteSize > 0) {
                    m_lCompressedSize += iWriteSize;
                    m_df.write(bufCompressed, 0, iWriteSize);
                }
            }
        }
    } /* writeDeflated */

    /*------------------------------------------------------------------*/

    /**
     * writes iLength bytes into buffer starting at iOffset.
     *
     * @param buf     buffer holding output.
     * @param iOffset offset in buffer where start writing.
     * @param iLength number of bytes to be written.
     * @throws IOException if an I/O error occurred.
     */
    private void writeStored(byte[] buf, int iOffset, int iLength)
            throws IOException {
        m_df.write(buf, iOffset, iLength);
        m_lCompressedSize += iLength;
    } /* writeStored */

    /*------------------------------------------------------------------*/

    /**
     * writes iLength bytes from buffer starting at iOffset.
     * This package-private method is called by EntryOutputStream.
     *
     * @param buf     buffer holding output.
     * @param iOffset offset in buffer where start writing.
     * @param iLength number of bytes to be written.
     * @throws IOException if an I/O error occurred.
     */
    void write(byte[] buf, int iOffset, int iLength)
            throws IOException {
        if (iOffset < 0 || iLength < 0 || iOffset > buf.length - iLength)
            throw new IndexOutOfBoundsException();
        else if (iLength != 0) {
            if (m_feLocal != null) {
                if (!m_feLocal.isDirectory()) {
                    /* CRC */
                    m_crc.update(buf, iOffset, iLength);
                    /* Size */
                    m_lSize += iLength;
                    /* here we write the next iLength bytes */
                    switch (m_feLocal.getMethod()) {
                        case FileEntry.iMETHOD_DEFLATED:
                            writeDeflated(buf, iOffset, iLength);
                            break;
                        case FileEntry.iMETHOD_STORED:
                            writeStored(buf, iOffset, iLength);
                            break;
                    }
                } else
                    throw new ZipException("Data cannot be written to directory entry!");
            } else
                throw new ZipException("File not open for writing!");
        }
    } /* write */

    /*------------------------------------------------------------------*/

    /**
     * closes a file, appending the data descriptor.
     * This package-private method is called by EntryOutputStream.
     *
     * @throws IOException if an I/O error occurred.
     */
    void closeWrite()
            throws IOException {
        if (m_feLocal != null) {
            /* write remaining bytes - if any */
            if ((m_lSize > 0) && (m_feLocal.getMethod() == FileEntry.iMETHOD_DEFLATED)) {
                m_def.finish();
                byte[] bufCompressed = new byte[iBUFFER_SIZE];
                while (!m_def.finished()) {
                    int iWriteSize = m_def.deflate(bufCompressed);
                    if (iWriteSize > 0) {
                        m_lCompressedSize += iWriteSize;
                        m_df.write(bufCompressed, 0, iWriteSize);
                    }
                }
            }
            /* write data descriptor */
            m_feLocal.setCrc(m_crc.getValue());
            putDataDescriptor(m_feLocal, m_lCompressedSize, m_lSize);
            /* update extra field for general directory */
            updateExtraField(m_feLocal);
            /* append file entry to central directory */
            m_iFileEntries++;
            m_listFileEntries.add(m_feLocal);
            m_mapFileEntries.put(m_feLocal.getName(), m_feLocal);
            /* close it */
            m_feLocal = null;
            /* new end of file position */
            m_lCentralDirectoryStart = m_df.getFilePointer();
        }
    } /* closeWrite */

    /*------------------------------------------------------------------*/

    /**
     * open a file entry output stream for writing.
     *
     * @param sEntryName   name of file entry in ZIP file.
     * @param iMethod      must be a FileEntry.iMETHOD_... constant.
     * @param dateModified time stamp for file entry or null.
     * @return open file entry output stream
     * @throws FileNotFoundException if file not in ZIP.
     * @throws IOException           if an I/O error occurred.
     * @throws ZipException          if file already stored in ZIP file.
     */
    public EntryOutputStream openEntryOutputStream(String sEntryName,
                                                   int iMethod, Date dateModified)
            throws FileNotFoundException, IOException, ZipException {
        openWrite(sEntryName, iMethod, dateModified);
        return new EntryOutputStream(this);
    } /* openEntryOutputStream */

    /*------------------------------------------------------------------*/

    /**
     * determines the size of a ZIP file entry and positions file pointer
     * at the start of the next ZIP file entry.
     *
     * @param feGlobal file entry for which size needs to be determined.
     * @return size (local file header + compressed size + data descriptor size).
     * @throws ZipException if some entry is open for writing.
     * @throws IOException  if an I/O error occurred.
     */
    private long getSize(FileEntry feGlobal)
            throws ZipException, IOException {
        long lSize = 0;
        /* clone global entry */
        try {
            feGlobal = (FileEntry) feGlobal.clone();
        } catch (CloneNotSupportedException cnse) {
            System.err.println(cnse.getMessage());
        }
        EntryInputStream eis = openEntryInputStream(feGlobal.getName());
        FileEntry feLocal = eis.getFileEntryLocal();
        /* clone local entry */
        try {
            feLocal = (FileEntry) feLocal.clone();
        } catch (CloneNotSupportedException cnse) {
            System.err.println(cnse.getMessage());
        }
        lSize = eis.getFilePointer() - feGlobal.getOffset(); /* header size */
        m_df.seek(eis.getFilePointer() + feGlobal.getCompressedSize());
        lSize += feGlobal.getCompressedSize(); /* header size + compressed size */
        getDataDescriptor(feLocal); /* positions file pointer at start of next file entry */
        /* save file pointer */
        long lFilePointer = m_df.getFilePointer();
        lSize += m_df.getFilePointer() - (feGlobal.getOffset() + lSize); /* header size + compressed size + data descriptor size */
        eis.close();
        /* file pointer points at start of next ZIP file entry */
        m_df.seek(lFilePointer);
        return lSize;
    } /* getSize */

    /*------------------------------------------------------------------*/

    /**
     * deletes the ZIP file entry.
     *
     * @param sEntryName name of file entry to be deleted.
     * @return deleted file entry or null, if the file did not exist
     * @throws IOException if an I/O error occurred.
     */
    public FileEntry delete(String sEntryName)
            throws IOException {
        FileEntry feDelete = m_mapFileEntries.get(sEntryName);
        if (feDelete != null) {
            /* set length (truncates the file) */
            m_df.setLength(m_lCentralDirectoryStart);
            m_bChanged = true;
            long lDeleteSize = getSize(feDelete);
            for (Iterator<FileEntry> iterFileEntry = m_listFileEntries.iterator(); iterFileEntry.hasNext(); ) {
                FileEntry fe = iterFileEntry.next();
                if (fe.getOffset() > feDelete.getOffset()) {
                    fe.setOffset(fe.getOffset() - lDeleteSize);
                    updateExtraField(fe);
                }
            }
            /* move the whole file from offset+size to offset */
            m_df.move(feDelete.getOffset() + lDeleteSize, feDelete.getOffset());
            /* start of directory: after the move file pointer is at end of file */
            m_lCentralDirectoryStart -= lDeleteSize;
            /* check move */
            if ((m_lCentralDirectoryStart != m_df.getFilePointer()) ||
                    (m_lCentralDirectoryStart != m_df.length()))
                throw new ZipException("move produced unexpected result!");
            /* make sure, file pointer and end of file are there */
            m_df.seek(m_lCentralDirectoryStart);
            m_df.setLength(m_lCentralDirectoryStart);
            /* remove the file entry */
            m_iFileEntries--;
            m_listFileEntries.remove(feDelete);
            m_mapFileEntries.remove(feDelete.getName());
        }
        return feDelete;
    } /* delete */

    /*------------------------------------------------------------------*/

    /**
     * closes the ZIP file and writes the central directory, if it has changed.
     * This <em>must</em> be called if the file was modified.
     * It <em>can</em> be called if it was not modified.
     * <strong>Once a ZIP file has been modified it must be closed, else
     * the central directory won't be updated and the file will be corrupted.</strong>.
     *
     * @throws IOException if an I/O error occurred.
     */
    public void close()
            throws IOException {
        /* write central directory */
        if (m_bChanged)
            putCentralDirectory();
        m_df.close();
    } /* close */

} /* Zip64File */
