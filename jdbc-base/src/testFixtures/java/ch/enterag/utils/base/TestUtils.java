package ch.enterag.utils.base;

import java.io.*;

import ch.enterag.utils.SU;

public abstract class TestUtils
{
  public static byte[] getBytes(int iLength)
  {
    byte[] buf = new byte[iLength];
    for (int i = 0; i < iLength; i++)
    {
      int j = i % 256;
      if (j > 127)
        j = j - 256;
      buf[i] = (byte)j;
    }
    return buf;
  } /* getBytes */
  
  public static byte[] getBytes(InputStream is)
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try
    {
      for (int iRead = is.read(); iRead != -1; iRead = is.read())
        baos.write(iRead);
      baos.close();
    }
    catch(IOException ie) { System.err.println(ie.getClass().getName()+": "+ie.getMessage()); }
    return baos.toByteArray();
  } /* getBytes */
  
  public static String getString(int iLength)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < iLength; i++)
    {
      int j = i % 96;
      sb.appendCodePoint(32+j);
    }
    return sb.toString();
  } /* getString */
  
  public static String getString(Reader rdr)
  {
    StringWriter sw = new StringWriter();
    try
    {
      for (int iRead = rdr.read(); iRead != -1; iRead = rdr.read())
        sw.write((char)iRead);
      sw.close();
    }
    catch(IOException ie) { System.err.println(ie.getClass().getName()+": "+ie.getMessage()); }
    return sw.getBuffer().toString();
  } /* getString */

  public static String getNString(int iLength)
  {
    /* generate random bytes in the range [x20,xFF] */
    byte[] buf = new byte[iLength];
    for (int i = 0; i < iLength; i++)
    {
      int j = i % 192;
      if (j < 96)
        buf[i] = (byte)(32+j);
      else
        buf[i] = (byte)(64+j);
    }
    /* read them into a string as 1252 */
    return SU.getIsoLatin1String(buf);
  } /* getNString */
  
  public static String getNString(Reader rdr)
  {
    StringWriter sw = new StringWriter();
    try
    {
      for (int iRead = rdr.read(); iRead != -1; iRead = rdr.read())
        sw.write((char)iRead);
      sw.close();
    }
    catch(IOException ie) { System.err.println(ie.getClass().getName()+": "+ie.getMessage()); }
    return sw.getBuffer().toString();
  } /* getNString */

}
