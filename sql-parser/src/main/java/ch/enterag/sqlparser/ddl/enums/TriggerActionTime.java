package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum TriggerActionTime
{
  BEFORE(K.BEFORE.getKeyword()),
  AFTER(K.AFTER.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private TriggerActionTime(String sKeywords) { _sKeywords = sKeywords; }
}
