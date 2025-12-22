package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum RankFunction
{
  RANK(K.RANK.getKeyword()),
  DENSE_RANK(K.DENSE_RANK.getKeyword()),
  PERCENT_RANK(K.PERCENT_RANK.getKeyword()),
  CUME_DIST(K.CUME_DIST.getKeyword());  
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private RankFunction(String sKeywords) { _sKeywords = sKeywords; }
}
