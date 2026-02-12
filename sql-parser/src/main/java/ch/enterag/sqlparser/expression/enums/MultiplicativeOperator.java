package ch.enterag.sqlparser.expression.enums;
public enum MultiplicativeOperator
{
  ASTERISK("*"),
  SOLIDUS("/");
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private MultiplicativeOperator(String sKeywords) { _sKeywords = sKeywords; }
}
