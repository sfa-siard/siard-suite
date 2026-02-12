package ch.enterag.utils.base;

import java.io.*;

public class TestInputStream
  extends InputStream
{
  private static int _iBUFSIZ = 32768;
  private long _lSize = -1;
  private long _lPosition = 0;
  private byte[] _buf = TestUtils.getBytes(_iBUFSIZ);
  private int _iOffset = 0;
  
  public TestInputStream(long lSize)
  {
    _lSize = lSize;
  } /* constructor */
  
  @Override
  public int read() throws IOException
  {
    int iByte = -1;
    if (_lPosition < _lSize)
    {
      if (_iOffset >= _iBUFSIZ)
        _iOffset = 0;
      iByte = _buf[_iOffset];
      if (iByte < 0)
        iByte = iByte + 256;
      _iOffset++;
      _lPosition++;
    }
    return iByte;
  } /* read */
  
  @Override
  public int read(byte[] bufRead, int iOffset, int iLength)
    throws IOException
  {
    int iRead = 0;
    long l = _lPosition;
    int i = _iOffset;
    for (int iByte = read(); (iByte != -1) && (iRead < iLength); iByte = read())
    {
      bufRead[iOffset+iRead] = (byte)iByte;
      iRead++;
      l = _lPosition;
      i = _iOffset;
    }
    if (iRead == 0)
      iRead = -1;
    _lPosition = l;
    _iOffset = i;
    return iRead;
  } /* read */
  
  @Override
  public int read(byte[] bufRead)
    throws IOException
  {
    int iRead = read(bufRead,0,bufRead.length);
    return iRead;
  } /* read */
  
} /* TestInputStream */
