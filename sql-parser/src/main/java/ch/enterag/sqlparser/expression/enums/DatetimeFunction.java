package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum DatetimeFunction
{
  CURRENT_DATE(K.CURRENT_DATE.getKeyword()),
  CURRENT_TIME(K.CURRENT_TIME.getKeyword()),
  CURRENT_TIMESTAMP(K.CURRENT_TIMESTAMP.getKeyword()),
  LOCALTIME(K.LOCALTIME.getKeyword()),
  LOCALTIMESTAMP(K.LOCALTIMESTAMP.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private DatetimeFunction(String sKeywords) { _sKeywords = sKeywords; }
}
