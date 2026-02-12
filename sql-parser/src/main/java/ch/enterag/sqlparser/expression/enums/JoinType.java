package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum JoinType
{
  INNER(K.INNER.getKeyword()),
  LEFT_OUTER(K.LEFT.getKeyword()+" "+K.OUTER.getKeyword()),
  RIGHT_OUTER(K.RIGHT.getKeyword()+" "+K.OUTER.getKeyword()),
  FULL_OUTER(K.FULL.getKeyword()+" "+K.OUTER.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private JoinType(String sKeywords) { _sKeywords = sKeywords; }
}
