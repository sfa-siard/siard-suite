package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum StringFunction
{
  SUBSTRING(K.SUBSTRING.getKeyword()),
  UPPER(K.UPPER.getKeyword()),
  LOWER(K.LOWER.getKeyword()),
  TRIM(K.TRIM.getKeyword()),
  NORMALIZE(K.NORMALIZE.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private StringFunction(String sKeywords) { _sKeywords = sKeywords; }
}
