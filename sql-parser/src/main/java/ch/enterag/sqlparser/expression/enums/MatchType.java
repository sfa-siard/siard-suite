package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum MatchType
{
  FULL(K.FULL.getKeyword()),
  PARTIAL(K.PARTIAL.getKeyword()),
  SIMPLE(K.SIMPLE.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private MatchType(String sKeywords) { _sKeywords = sKeywords; }
}
