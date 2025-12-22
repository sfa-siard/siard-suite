package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum BooleanCondition
{
  BETWEEN(K.BETWEEN.getKeyword()),
  IN(K.IN.getKeyword()),
  LIKE(K.LIKE.getKeyword()),
  SIMILAR(K.SIMILAR.getKeyword()),
  EXISTS(K.EXISTS.getKeyword()),
  UNIQUE(K.UNIQUE.getKeyword()),
  NORMALIZED(K.NORMALIZED.getKeyword()),
  MATCH(K.MATCH.getKeyword()),
  OVERLAPS(K.OVERLAPS.getKeyword()),
  IS_DISTINCT_FROM(K.IS.getKeyword() + " " + K.DISTINCT.getKeyword() + " " + K.FROM.getKeyword()),
  MEMBER_OF(K.MEMBER.getKeyword()+" "+K.OF.getKeyword()),
  SUBMULTISET(K.SUBMULTISET.getKeyword()+" "+K.OF.getKeyword()),
  OF(K.OF.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private BooleanCondition(String sKeywords) { _sKeywords = sKeywords; }
}
