package ch.enterag.sqlparser.expression.enums;
public enum Sign
{
  PLUS_SIGN("+"),
  MINUS_SIGN("-");
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private Sign(String sKeywords) { _sKeywords = sKeywords; }
}
