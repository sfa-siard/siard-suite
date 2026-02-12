package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum ParameterStyle
{
  SQL(K.SQL.getKeyword()),
  GENERAL(K.GENERAL.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private ParameterStyle(String sKeywords) { _sKeywords = sKeywords; }
}
