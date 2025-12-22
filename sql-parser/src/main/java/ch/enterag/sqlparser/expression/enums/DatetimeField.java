package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum DatetimeField
{
  YEAR(K.YEAR.getKeyword()),
  MONTH(K.MONTH.getKeyword()),
  DAY(K.DAY.getKeyword()),
  HOUR(K.HOUR.getKeyword()),
  MINUTE(K.MINUTE.getKeyword()),
  SECOND(K.SECOND.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private DatetimeField(String sKeywords) { _sKeywords = sKeywords; }
}
