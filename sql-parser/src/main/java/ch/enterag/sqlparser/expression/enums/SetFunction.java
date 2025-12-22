package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum SetFunction
{
  AVG(K.AVG.getKeyword()), 
  MAX(K.MAX.getKeyword()), 
  MIN(K.MIN.getKeyword()), 
  SUM(K.SUM.getKeyword()), 
  EVERY(K.EVERY.getKeyword()),
  ANY(K.ANY.getKeyword()), 
  SOME(K.SOME.getKeyword()), 
  COUNT(K.COUNT.getKeyword()), 
  STDDEV_POP(K.STDDEV_POP.getKeyword()), 
  STDDEV_SAMP(K.STDDEV_SAMP.getKeyword()), 
  VAR_POP(K.VAR_POP.getKeyword()),
  VAR_SAMP(K.VAR_SAMP.getKeyword()), 
  COLLECT(K.COLLECT.getKeyword()), 
  FUSION(K.FUSION.getKeyword()), 
  INTERSECTION(K.INTERSECTION.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private SetFunction(String sKeywords) { _sKeywords = sKeywords; }
}
