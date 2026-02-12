package ch.enterag.utils.base;

import java.io.*;

public class TestReader extends Reader
{
  protected static int _iBUFSIZ = 32768;
  protected long _lSize = -1;
  protected long _lPosition = 0;
  protected char[] _cbuf = null;
  protected int _iOffset = 0;

  public TestReader(long lSize)
  {
    _lSize = lSize;
    _cbuf = TestUtils.getString(_iBUFSIZ).toCharArray();
  }

  @Override
  public int read() throws IOException
  {
    int iChar = -1;
    if (_lPosition < _lSize)
    {
      if (_iOffset >= _cbuf.length)
        _iOffset = 0;
      iChar = _cbuf[_iOffset];
      _iOffset++;
      _lPosition++;
    }
    return iChar;
  }
  
  @Override
  public int read(char[] cbufRead, int iOffset, int iLength) 
    throws IOException
  {
    int iRead = 0;
    long l = _lPosition;
    int i = _iOffset;
    for (int iChar = read(); (iChar != -1) && (iRead < iLength); iChar = read())
    {
      cbufRead[iOffset+iRead] = (char)iChar;
      iRead++;
      l = _lPosition;
      i = _iOffset;
    }
    if (iRead == 0)
      iRead = -1;
    _lPosition = l;
    _iOffset = i;
    return iRead;
  }

  @Override
  public int read(char[] cbufRead)
    throws IOException
  {
    return read(cbufRead,0,cbufRead.length);
  }
  
  @Override
  public void close() throws IOException
  {
  }
  
}
