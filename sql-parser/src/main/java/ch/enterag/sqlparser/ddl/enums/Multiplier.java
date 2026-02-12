package ch.enterag.sqlparser.ddl.enums;
public enum Multiplier
{
  K(ch.enterag.sqlparser.K.K.getKeyword(), getK()), 
  M(ch.enterag.sqlparser.K.M.getKeyword(), getM()), 
  G(ch.enterag.sqlparser.K.G.getKeyword(), getG());
  private static final int _iK = 1024;
  private static int getK() { return _iK; }
  private static final int _iM = _iK*_iK;
  private static int getM() { return _iM; }
  private static final int _iG = _iM*_iK;
  private static int getG() { return _iG; }
  private String _sKeyword = null;
  public String getKeyword() { return _sKeyword; }
  private int _iValue = -1;
  public int getValue() { return _iValue; }
  private Multiplier(String sKeyword, int iValue) 
  {
    _sKeyword = sKeyword;
    _iValue = iValue;
  }
}
