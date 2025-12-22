package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum BooleanLiteral
{
  FALSE(K.FALSE.getKeyword()),
  TRUE(K.TRUE.getKeyword()),
  UNKNOWN(K.UNKNOWN.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private BooleanLiteral(String sKeywords) { _sKeywords = sKeywords; }
}
