/*== EntryOutputStream.java ============================================
OutputStream for writing data to a file entry of a Zip64File.
Version     : $Id: EntryOutputStream.java 34 2011-03-31 14:34:15Z hartwigthomas $
Application : ZIP Utilities
Description : OutputStream for writing data to a file entry of a Zip64File.
------------------------------------------------------------------------
Copyright  : Enter AG, Zurich, Switzerland, 2008
Created    : 07.03.2008, Hartwig Thomas
======================================================================*/

package ch.enterag.utils.zip;

import java.io.IOException;
import java.io.OutputStream;

/*====================================================================*/
/** OutputStream for writing data to a file entry of a Zip64File.
 @author Hartwig Thomas
 */
public class EntryOutputStream extends OutputStream
{

  /*====================================================================
  (private) data members
  ====================================================================*/
  /** Zip64File */
  private Zip64File m_zf = null;

  /*====================================================================
  Constructors
  ====================================================================*/
	/*------------------------------------------------------------------*/
  /** package-private constructor is called by Zip64File.
   @param zf random-access ZIP64 file from which file entry is read. 
   */
  EntryOutputStream(Zip64File zf)
  {
  	m_zf = zf;
  } /* constructor EntryOutputStream */
  
  /*====================================================================
  (public) methods
  ====================================================================*/
	/*------------------------------------------------------------------*/
	/* (non-Javadoc)
	 @see java.io.OutputStream#write(byte[], int, int)
	 */
  @Override
  public void write(byte[] b, int off, int len) throws IOException
  {
		m_zf.write(b, off, len);
  } /* write */

	/*------------------------------------------------------------------*/
	/* (non-Javadoc)
	 @see java.io.OutputStream#write(byte[])
	 */
  @Override
  public void write(byte[] b) throws IOException
  {
		write(b,0,b.length);
  } /* write */

	/*------------------------------------------------------------------*/
	/* (non-Javadoc)
	 @see java.io.OutputStream#write(int)
	 */
	@Override
  public void write(int b) throws IOException
	{
		byte[] buf = new byte[1];
		if (b < 128)
			buf[0] = (byte)b;
		else
			buf[0] = (byte)(b-256);
		write(buf);
	} /* write */

	/*------------------------------------------------------------------*/
	/* (non-Javadoc)
	 @see java.io.OutputStream#write(int)
	 */
  @Override
  public void close() throws IOException
  {
		m_zf.closeWrite();
  } /* close */

} /* class EntryOutputStream */
