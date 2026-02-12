package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum CycleFunction
{
  CYCLE(K.CYCLE.getKeyword()),
  SET(K.SET.getKeyword()),
  DEFAULT(K.DEFAULT.getKeyword()),
  USING(K.USING.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private CycleFunction(String sKeywords) { _sKeywords = sKeywords; }
}
