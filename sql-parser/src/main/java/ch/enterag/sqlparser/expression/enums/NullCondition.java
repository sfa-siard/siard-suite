package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum NullCondition
{
  IS_NULL(K.IS.getKeyword() + " " + K.NULL.getKeyword()),
  IS_NOT_NULL(K.IS.getKeyword() + " " + K.NOT.getKeyword()+" "+K.NULL.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private NullCondition(String sKeywords) { _sKeywords = sKeywords; }
}
