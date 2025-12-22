package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum JoinOperator
{
  CROSS_JOIN(K.CROSS.getKeyword()+" "+K.JOIN.getKeyword()),
  JOIN(K.JOIN.getKeyword()),
  NATURAL(K.NATURAL.getKeyword()),
  UNION(K.UNION.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private JoinOperator(String sKeywords) { _sKeywords = sKeywords; }
}
