package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum Levels
{
  CASCADED(K.CASCADED.getKeyword()),
  LOCAL(K.LOCAL.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private Levels(String sKeywords) { _sKeywords = sKeywords; }
}
