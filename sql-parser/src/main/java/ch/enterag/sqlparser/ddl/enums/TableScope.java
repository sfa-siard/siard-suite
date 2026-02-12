package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum TableScope
{
  GLOBAL(K.GLOBAL.getKeyword()+" "+K.TEMPORARY.getKeyword()),
  LOCAL(K.LOCAL.getKeyword()+" "+K.TEMPORARY.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private TableScope(String sKeywords) { _sKeywords = sKeywords; }
}
