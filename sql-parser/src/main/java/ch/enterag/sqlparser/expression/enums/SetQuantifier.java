package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum SetQuantifier
{
  DISTINCT(K.DISTINCT.getKeyword()),
  ALL(K.ALL.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private SetQuantifier(String sKeywords) { _sKeywords = sKeywords; }
}
