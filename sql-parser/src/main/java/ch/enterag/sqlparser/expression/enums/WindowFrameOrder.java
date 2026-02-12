package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum WindowFrameOrder
{
  PRECEDING(K.PRECEDING.getKeyword()),
  FOLLOWING(K.FOLLOWING.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private WindowFrameOrder(String sKeywords) { _sKeywords = sKeywords; }
}
