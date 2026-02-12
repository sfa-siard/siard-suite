package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum TimeZoneField
{
  TIMEZONE_HOUR(K.TIMEZONE_HOUR.getKeyword()),
  TIMEZONE_MINUTE(K.TIMEZONE_MINUTE.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private TimeZoneField(String sKeywords) { _sKeywords = sKeywords; }
}
