package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum MethodType
{
  INSTANCE(K.INSTANCE.getKeyword()),
  STATIC(K.STATIC.getKeyword()),
  CONSTRUCTOR(K.CONSTRUCTOR.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private MethodType(String sKeywords) { _sKeywords = sKeywords; }
}
