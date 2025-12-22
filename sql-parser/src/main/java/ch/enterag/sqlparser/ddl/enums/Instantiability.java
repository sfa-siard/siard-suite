package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum Instantiability
{
  INSTANTIABLE(K.INSTANTIABLE.getKeyword()),
  NOT_INSTANTIABLE(K.NOT.getKeyword()+" "+K.INSTANTIABLE.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private Instantiability(String sKeywords) { _sKeywords = sKeywords; }
}
