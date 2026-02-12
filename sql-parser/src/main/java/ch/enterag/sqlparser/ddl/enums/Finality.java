package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum Finality
{
  FINAL(K.FINAL.getKeyword()),
  NOT_FINAL(K.NOT.getKeyword()+" "+K.FINAL.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private Finality(String sKeywords) { _sKeywords = sKeywords; }
}
