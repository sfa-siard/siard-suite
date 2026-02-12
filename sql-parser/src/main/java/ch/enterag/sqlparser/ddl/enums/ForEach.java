package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum ForEach
{
  FOR_EACH_ROW(K.FOR.getKeyword()+" "+K.EACH.getKeyword()+" "+K.ROW.getKeyword()),
  FOR_EACH_STATEMENT(K.FOR.getKeyword()+" "+K.EACH.getKeyword()+" "+K.STATEMENT.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private ForEach(String sKeywords) { _sKeywords = sKeywords; }
}
