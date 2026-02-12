package ch.enterag.sqlparser.expression.enums;
public enum CompOp
{
  EQUALS_OPERATOR("="),
  NOT_EQUALS_OPERATOR("<>"),
  LESS_THAN_OPERATOR("<"),
  GREATER_THAN_OPERATOR(">"),
  LESS_THAN_OR_EQUALS_OPERATOR("<="),
  GREATER_THAN_OR_EQUALS_OPERATOR(">=");
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private CompOp(String sKeywords) { _sKeywords = sKeywords; }
}
