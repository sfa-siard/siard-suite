package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum OldOrNew
{
  OLD_ROW_AS(K.OLD.getKeyword()+" "+K.ROW.getKeyword()+" "+K.AS.getKeyword()),
  NEW_ROW_AS(K.NEW.getKeyword()+" "+K.ROW.getKeyword()+" "+K.AS.getKeyword()),
  OLD_TABLE_AS(K.OLD.getKeyword()+" "+K.TABLE.getKeyword()+" "+K.AS.getKeyword()),
  NEW_TABLE_AS(K.NEW.getKeyword()+" "+K.TABLE.getKeyword()+" "+K.AS.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private OldOrNew(String sKeywords) { _sKeywords = sKeywords; }
}
