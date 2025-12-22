package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum SymmetricOption
{
  ASYMMETRIC(K.ASYMMETRIC.getKeyword()),
  SYMMETRIC(K.SYMMETRIC.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private SymmetricOption(String sKeywords) { _sKeywords = sKeywords; }
}
