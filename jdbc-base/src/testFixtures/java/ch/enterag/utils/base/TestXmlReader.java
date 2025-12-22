package ch.enterag.utils.base;

import java.io.*;

public class TestXmlReader
  extends TestNReader
{
  private char[] _cbufStart = "<a>".toCharArray();
  private char[] _cbufEnd = "</a>".toCharArray();
  private int _iOffsetStart = 0;
  private int _iOffsetEnd = 0;

  public TestXmlReader(long lSize)
  {
    super(lSize);
    int iFrameSize = _cbufStart.length+_cbufEnd.length;
    if (lSize > iFrameSize)
    {
      // adjust lSize to the the buffer size
      int iBufferSize = _iBUFSIZ;
      lSize = lSize - iFrameSize;
      if (lSize < _iBUFSIZ)
        iBufferSize = (int)lSize;
      else
        lSize = iBufferSize*(lSize/iBufferSize);
      lSize = lSize + iFrameSize; 
      StringBuilder sb = new StringBuilder();
      String s = TestUtils.getNString(iBufferSize);
      for (int i = 0; sb.length() < iBufferSize; i++)
      {
        char c = s.charAt(i);
        if ((c != '<') && (c != '>') && (c != '&'))
          sb.append(c);
        else
          sb.append('\n');
      }
      _cbuf = sb.toString().toCharArray();
      _lSize = lSize;
    }
    else
      throw new IllegalArgumentException("Size of XML must be larger than "+String.valueOf(iFrameSize)+"!");
  }
  
  public int read()
    throws IOException
  {
    int iChar = -1;
    if (_lPosition < _lSize)
    {
      if (_iOffsetStart < _cbufStart.length)
      {
        iChar = _cbufStart[_iOffsetStart];
        _iOffsetStart++;
      }
      else if (_lPosition < _lSize - _cbufEnd.length)
      {
        if (_iOffset >= _cbuf.length)
          _iOffset = 0;
        iChar = _cbuf[_iOffset];
        _iOffset++;
      }
      else
      {
        iChar = _cbufEnd[_iOffsetEnd];
        _iOffsetEnd++;
      }
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
    int iStart = _iOffsetStart;
    int iEnd = _iOffsetEnd;
    for (int iChar = read(); (iChar != -1) && (iRead < iLength); iChar = read())
    {
      cbufRead[iOffset+iRead] = (char)iChar;
      iRead++;
      l = _lPosition;
      i = _iOffset;
      iStart = _iOffsetStart;
      iEnd = _iOffsetEnd;
    }
    if (iRead == 0)
      iRead = -1;
    _lPosition = l;
    _iOffset = i;
    _iOffsetStart = iStart;
    _iOffsetEnd = iEnd;
    return iRead;
  }

  public static void main(String[] args)
  {
    try
    {
      TestXmlReader txr = new TestXmlReader(10);
      Writer w = new FileWriter("D:\\Temp\\XmlTest.txt");
      char[] cbuf = new char[100];
      for (int iRead = txr.read(cbuf); iRead != -1; iRead = txr.read(cbuf))
        w.write(cbuf,0,iRead);
      w.close();
      txr.close();
    }
    catch (IOException ie) { System.err.println(ie.getClass().getName()+": "+ie.getMessage()); }
  }
  
}
