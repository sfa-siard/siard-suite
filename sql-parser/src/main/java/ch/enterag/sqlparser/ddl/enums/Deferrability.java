package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum Deferrability
{
  DEFERRABLE(K.DEFERRABLE.getKeyword()),
  NOT_DEFERRABLE(K.NOT.getKeyword()+" "+K.DEFERRABLE.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private Deferrability(String sKeywords) { _sKeywords = sKeywords; }
}
