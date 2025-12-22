package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum TriggerEvent
{
  INSERT(K.INSERT.getKeyword()),
  DELETE(K.DELETE.getKeyword()),
  UPDATE(K.UPDATE.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private TriggerEvent(String sKeywords) { _sKeywords = sKeywords; }
}
