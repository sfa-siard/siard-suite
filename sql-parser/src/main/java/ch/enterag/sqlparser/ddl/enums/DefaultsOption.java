package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum DefaultsOption
{
  INCLUDING_DEFAULTS(K.INCLUDING.getKeyword()+" "+K.DEFAULTS.getKeyword()),
  EXCLUDING_DEFAULTS(K.EXCLUDING.getKeyword()+" "+K.DEFAULTS.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private DefaultsOption(String sKeywords) { _sKeywords = sKeywords; }
}
