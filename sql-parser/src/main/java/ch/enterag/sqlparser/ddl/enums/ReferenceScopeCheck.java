package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum ReferenceScopeCheck
{
  CHECKED(K.REFERENCES.getKeyword()+" "+K.ARE.getKeyword()+" "+K.CHECKED.getKeyword()), 
  NOT_CHECKED(K.REFERENCES.getKeyword()+" "+K.ARE.getKeyword()+" "+K.NOT.getKeyword()+" "+K.CHECKED.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private ReferenceScopeCheck(String sKeywords) { _sKeywords = sKeywords; }
}
