package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum SampleMethod
{
  BERNOULLI(K.BERNOULLI.getKeyword()),
  SYSTEM(K.SYSTEM.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private SampleMethod(String sKeywords) { _sKeywords = sKeywords; }
}
