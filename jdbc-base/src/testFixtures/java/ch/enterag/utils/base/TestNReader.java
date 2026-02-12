package ch.enterag.utils.base;

public class TestNReader
  extends TestReader
{
  public TestNReader(long lSize)
  {
    super(lSize);
    _cbuf = TestUtils.getNString(_iBUFSIZ).toCharArray();
  }
}
