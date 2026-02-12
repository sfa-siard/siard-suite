package ch.enterag.sqlparser.dml.enums;
import ch.enterag.sqlparser.*;
public enum SpecialValue
{
  NULL(K.NULL.getKeyword()),
  EMPTY_ARRAY(K.ARRAY.getKeyword()+"[]"),
  EMPTY_MULTISET(K.MULTISET.getKeyword()+"[]"),
  DEFAULT(K.DEFAULT.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private SpecialValue(String sKeywords) { _sKeywords = sKeywords; }
}
