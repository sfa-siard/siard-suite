package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum NullOrdering
{
  NULLS_FIRST(K.NULLS.getKeyword()+" "+K.FIRST.getKeyword()),
  NULL_LAST(K.NULLS.getKeyword()+" "+K.LAST.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private NullOrdering(String sKeywords) { _sKeywords = sKeywords; }
}
