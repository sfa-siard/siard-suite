package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum ReferentialAction
{
  CASCADE(K.CASCADE.getKeyword()),
  SET_NULL(K.SET.getKeyword() + " " + K.NULL.getKeyword()),
  SET_DEFAULT(K.SET.getKeyword() + " " + K.DEFAULT.getKeyword()),
  RESTRICT(K.RESTRICT.getKeyword()),
  NO_ACTION(K.NO.getKeyword() + " " + K.ACTION.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private ReferentialAction(String sKeywords) { _sKeywords = sKeywords; }
}
