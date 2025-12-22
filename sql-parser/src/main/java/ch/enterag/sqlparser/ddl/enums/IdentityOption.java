package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum IdentityOption
{
  INCLUDING_IDENTITY(K.INCLUDING.getKeyword()+" "+K.IDENTITY.getKeyword()),
  EXCLUDING_IDENTITY(K.EXCLUDING.getKeyword()+" "+K.IDENTITY.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private IdentityOption(String sKeywords) { _sKeywords = sKeywords; }
}
