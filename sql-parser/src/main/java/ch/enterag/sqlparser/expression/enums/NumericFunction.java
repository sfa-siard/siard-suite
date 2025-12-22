package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum NumericFunction
{
  POSITION(K.POSITION.getKeyword()),
  EXTRACT(K.EXTRACT.getKeyword()),
  CHARACTER_LENGTH(K.CHARACTER_LENGTH.getKeyword()),
  OCTET_LENGTH(K.OCTET_LENGTH.getKeyword()),
  CARDINALITY(K.CARDINALITY.getKeyword()),
  ABS(K.ABS.getKeyword()),
  MOD(K.MOD.getKeyword()),
  LN(K.LN.getKeyword()),
  EXP(K.EXP.getKeyword()),
  POWER(K.POWER.getKeyword()),
  SQRT(K.SQRT.getKeyword()),
  FLOOR(K.FLOOR.getKeyword()),
  CEILING(K.CEILING.getKeyword()),
  WIDTH_BUCKET(K.WIDTH_BUCKET.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private NumericFunction(String sKeywords) { _sKeywords = sKeywords; }
}
