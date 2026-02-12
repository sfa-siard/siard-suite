package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum GeneralValue
{
  QUESTION_MARK("?"),
  CURRENT_PATH(K.CURRENT_PATH.getKeyword()),
  CURRENT_ROLE(K.CURRENT_ROLE.getKeyword()),
  CURRENT_USER(K.CURRENT_USER.getKeyword()),
  SESSION_USER(K.SESSION_USER.getKeyword()),
  SYSTEM_USER(K.SYSTEM_USER.getKeyword()),
  USER(K.USER.getKeyword()),
  VALUE(K.VALUE.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private GeneralValue(String sKeywords) { _sKeywords = sKeywords; }
}
