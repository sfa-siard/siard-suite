package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum SetCondition
{
  IS_A_SET(K.IS.getKeyword() + " " + K.A.getKeyword() + " " + K.SET.getKeyword()),
  IS_NOT_A_SET(K.IS.getKeyword() + " " + K.NOT.getKeyword() + " " + K.A.getKeyword() + " " + K.SET.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private SetCondition(String sKeywords) { _sKeywords = sKeywords; }
}
