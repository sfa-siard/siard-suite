/*== EntryInputStream.java =============================================
InputStream for reading data from a file entry of a Zip64File.
Version     : $Id: EntryInputStream.java 34 2011-03-31 14:34:15Z hartwigthomas $
Application : ZIP Utilities
Description : InputStream for reading data from a file entry of a Zip64File.
------------------------------------------------------------------------
Copyright  : Enter AG, Zurich, Switzerland, 2008
Created    : 07.03.2008, Hartwig Thomas
======================================================================*/

package ch.enterag.utils.zip;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.ZipException;

/*====================================================================*/
/** InputStream for reading data from a file entry of a Zip64File.
 @author Hartwig Thomas
 */
public class EntryInputStream extends InputStream
{
  /*====================================================================
  (private) constants
  ====================================================================*/
  /** buffer size for i/o */
	private final static int iBUFFER_SIZE = 4096;
	
  /*====================================================================
  (private) data members
  ====================================================================*/
  /** Zip64File */
  private Zip64File m_zf = null;
  /** local file entry of open input stream */
  private FileEntry m_feLocal = null;
  /** package-private accessor
   * @return local file entry of open input stream */
  FileEntry getFileEntryLocal() {return m_feLocal; }
  /** inflater of open input stream */
  private final Inflater m_inf = new Inflater(true);
  /** running crc computation of open output stream */
  private final CRC32 m_crc = new CRC32();
  /** remaining bytes to be read */
  private long m_lRemainingSize = 0;
  /** remaining bytes to be read */
  private long m_lRemainingCompressedSize = 0;
  /** running count of bytes read */
  private long m_lCompressedSize = 0;
  /** running count of bytes delivered */
  private long m_lSize = 0;
  /** file pointer */
  private long m_lFilePointer = 0;
  /** package-private accessor 
   * @return file pointer */
  long getFilePointer() { return m_lFilePointer; }

  /*====================================================================
  Constructors
  ====================================================================*/
	/*------------------------------------------------------------------*/
  /** package-private constructor is called by Zip64File.
   @param zf random-access ZIP64 file from which file entry is read.
   @param sEntryName
   * @throws ZipException if ZIP file format is not valid.
   * @throws IOException if an I/O error occurs.
   * @throws FileNotFoundException if the entry cannot be found in the
   *         ZIP file.
   */
  EntryInputStream(Zip64File zf, String sEntryName)
    throws ZipException, IOException, FileNotFoundException
  {
  	m_zf = zf;
  	FileEntry fe = m_zf.getFileEntry(sEntryName);
  	if (fe != null)
  	{
  		long lFilePointer = m_zf.getDiskFile().getFilePointer();
  		try
  		{
  	  	m_feLocal = m_zf.getLocalFileEntry(fe);
  	  	/* now it is positioned after local header */
  	  	m_lFilePointer = m_zf.getDiskFile().getFilePointer();
  	  	m_inf.reset();
  	  	m_crc.reset();
  	  	m_lRemainingSize = fe.getSize();
  	  	m_lRemainingCompressedSize = fe.getCompressedSize();
  	  	m_lCompressedSize = 0;
  	  	m_lSize = 0;
  		}
  		finally
  		{
  			m_zf.getDiskFile().seek(lFilePointer);
  		}
  	}
  	else
  	  throw new FileNotFoundException("File entry "+sEntryName+" not in ZIP file!");
  } /* constructor EntryInputStream */
  
  /*====================================================================
  (public) methods
  ====================================================================*/
	/*------------------------------------------------------------------*/
	/* (non-Javadoc)
	 @see java.io.InputStream#available()
	 */
  @Override
  public int available() 
	  throws IOException
  {
		int iAvailable = Integer.MAX_VALUE;
		if (m_lRemainingSize <= iAvailable)
			iAvailable = (int) m_lRemainingSize;
		return iAvailable;
  } /* available */

  /*------------------------------------------------------------------*/
  /** reads iLength bytes into buffer starting at iOffset.
   * NB.: Inflater never sets its finished flag!
   * (Its package friend InflaterInputStream probably does ...)
  @param buf buffer to receive input.
  @param iOffset offset in buffer where to store bytes.
  @param iLength number of bytes to be read.
  @return number of bytes actually read.
  @throws IOException if an I/O error occurred.
  @throws DataFormatException if deflated data could not be decoded.
  */
  private int readDeflated(byte[] buf, int iOffset, int iLength)
    throws IOException, DataFormatException
  {
  	int iRead = -1;
  	if (iLength > m_lRemainingSize)
  		iLength = (int)m_lRemainingSize;
  	iRead = m_inf.inflate(buf,iOffset,iLength);
  	if ((iRead <= 0) && m_inf.needsInput())
  	{
      int iCompressedSize = iBUFFER_SIZE;
      if (iCompressedSize > m_lRemainingCompressedSize)
        iCompressedSize = (int)m_lRemainingCompressedSize;
      if (iCompressedSize > 0)
      {
        byte[] bufCompressed = new byte[iCompressedSize];
        int iReadCompressed = m_zf.getDiskFile().read(bufCompressed);
        if (iReadCompressed >= 0)
        {
          m_lRemainingCompressedSize -= iReadCompressed;
          m_lCompressedSize += iReadCompressed;
          m_inf.setInput(bufCompressed,0,iCompressedSize);
        }
        else
          throw new ZipException("Unexpected end of file!");
        iRead = m_inf.inflate(buf,iOffset,iLength);
      }
      else
      {
        if (m_inf.getRemaining() == 0)
          iRead = -1; /* end of file */
        else
          throw new ZipException("All bytes produced before all were consumed!");
      }
  	}
  	return iRead;
  } /* readDeflated */
  
  /*------------------------------------------------------------------*/
  /** reads iLength bytes into buffer starting at iOffset.
  @param buf buffer to receive input.
  @param iOffset offset in buffer where to store bytes.
  @param iLength number of bytes to be read.
  @return number of bytes actually read.
  @throws IOException if an I/O error occurred.
  */
  private int readStored(byte[] buf, int iOffset, int iLength)
    throws IOException
  {
  	int iRead = -1;
  	if (iLength > m_lRemainingSize)
  		iLength = (int)m_lRemainingSize;
  	if (iLength > 0)
  	{
  	  iRead = m_zf.getDiskFile().read(buf,iOffset,iLength);
  	  if (iRead >= 0)
  	  {
				m_lRemainingCompressedSize -= iRead;
				m_lCompressedSize += iRead;
  	  }
  	}
  	return iRead;
  } /* readStored */
  
  /*------------------------------------------------------------------*/
  /** reads iLength bytes into buffer starting at iOffset.
   * This package-private method is called by EntryInputStream.
  @param buf buffer to receive input.
  @param iOffset offset in buffer where to store bytes.
  @param iLength number of bytes to be read.
  @return number of bytes actually read.
  @throws IOException if an I/O error occurred.
  */
  @Override
  public int read(byte[] buf, int iOffset, int iLength) 
	  throws IOException
  {
  	int iRead = 0;
		long lFilePointer = m_zf.getDiskFile().getFilePointer();
		try
		{
			if (iOffset < 0 || iLength < 0 || iOffset > buf.length - iLength)
				throw new IndexOutOfBoundsException();
			else if (iLength != 0)
			{
				iRead = -1;
				if (m_lSize + m_lRemainingSize > 0)
				{
					if (m_feLocal != null)
					{
						/* set file pointer */
						m_zf.getDiskFile().seek(m_lFilePointer);
			  		/* here we read the next iLength bytes */
			  		switch (m_feLocal.getMethod())
			  		{
			  			case FileEntry.iMETHOD_DEFLATED:
			  				iRead = readDeflated(buf, iOffset, iLength);
			  			  break;
			  			case FileEntry.iMETHOD_STORED:
			  				iRead = readStored(buf, iOffset, iLength);
			  				break;
			  		}
			  		/* get file pointer */
				  	m_lFilePointer = m_zf.getDiskFile().getFilePointer();
			    	if (iRead >= 0)
			    	{
			    	  m_lRemainingSize -= iRead;
			  	    m_lSize += iRead;
			  		  m_crc.update(buf, iOffset, iRead);
			    	}
					}
					else
						throw new ZipException("File not open for reading!");
				}
			}
			else
				iRead = 0;
		}
		catch (DataFormatException dfe)
		{
			throw new IOException(dfe.getClass().getName()+": "+dfe.getMessage()); 
	  }
		finally
		{
			m_zf.getDiskFile().seek(lFilePointer);
		}
		return iRead;
  } /* read */

	/*------------------------------------------------------------------*/
	/* (non-Javadoc)
	 @see java.io.InputStream#read(byte[])
	 */
  @Override
  public int read(byte[] b)
	  throws IOException
  {
		int iRead = read(b,0,b.length);
		return iRead;
  } /* read */

	/*------------------------------------------------------------------*/
	/* (non-Javadoc)
	 @see java.io.InputStream#read()
	 */
	@Override
  public int read()
	  throws IOException
	{
		byte[] buf = new byte[1];
		int iRead = read(buf);
		if (iRead == 1)
		{
			if (buf[0] >= 0)
			  iRead = buf[0];
			else
				iRead = 0x0100 + buf[0];
		}
		return iRead;
	} /* read */

	/*------------------------------------------------------------------*/
	/* (non-Javadoc)
	 @see java.io.InputStream#skip(long)
	 */
  @Override
  public long skip(long n) 
	  throws IOException
  {
		long lSkipped = 0;
		byte[] buf = new byte[iBUFFER_SIZE];
		int iLength = iBUFFER_SIZE;
		if (iLength > n)
		  iLength = (int)n;
		for (int iRead = read(buf,0,iLength); (iRead >= 0) && (n > 0); iRead = read(buf,0,iLength))
		{
			lSkipped += iRead;
			n -= iRead;
			if (iLength > n)
			  iLength = (int)n;
		}
		return lSkipped;
  } /* skip */

	/*------------------------------------------------------------------*/
	/* (non-Javadoc)
	 @see java.io.InputStream#close()
	 */
  @Override
  public void close()
	  throws IOException
  {
  	if (m_feLocal != null)
  	{
			/* file pointer */
  		long lFilePointer = m_zf.getDiskFile().getFilePointer();
  		try
  		{
  			/* set file pointer */
  			m_zf.getDiskFile().seek(m_lFilePointer);
      	/* read remaining bytes - if any */
      	if (m_lRemainingSize > 0)
      	{
      		byte[] buf = new byte[iBUFFER_SIZE];
      		for (int iRead = read(buf,0,buf.length); iRead >= 0; iRead = read(buf,0,buf.length)) { }
      		/* this has called close */
      	}
      	else
      	{
        	/* read data descriptor */
        	m_zf.getDataDescriptor(m_feLocal);
        	/* check for inconsistency */
        	if (m_feLocal.getSize() != m_lSize)
      			throw new ZipException("invalid size (expected from local header/data descriptor " + String.valueOf(m_feLocal.getSize()) +
      			    " but actually found " + String.valueOf(m_lSize) + " bytes)");
      		if (m_feLocal.getCompressedSize() != m_lCompressedSize)
      			throw new ZipException("invalid compressed size (expected from local header/data descriptor " + String.valueOf(m_feLocal.getCompressedSize()) +
      			    " but actually read " + String.valueOf(m_lCompressedSize) + " bytes)");
      		if (m_feLocal.getCrc() != m_crc.getValue())
      			throw new ZipException("invalid CRC (expected from local header/data descriptor 0x" + Long.toHexString(m_feLocal.getCrc()) +
      					" but got 0x" + Long.toHexString(m_crc.getValue()) + ")");
      		/* close it */
        	m_feLocal = null;
      	}
  		}
  		finally
  		{
      	m_zf.getDiskFile().seek(lFilePointer);
  		}
  	}
  } /* close */

} /* EntryInputStream */
