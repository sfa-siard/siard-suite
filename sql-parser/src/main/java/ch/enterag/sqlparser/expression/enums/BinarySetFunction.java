package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum BinarySetFunction
{
  COVAR_POP(K.COVAR_POP.getKeyword()), 
  COVAR_SAMP(K.COVAR_SAMP.getKeyword()), 
  CORR(K.CORR.getKeyword()), 
  REGR_SLOPE(K.REGR_SLOPE.getKeyword()),
  REGR_INTERCEPT(K.REGR_INTERCEPT.getKeyword()),
  REGR_COUNT(K.REGR_COUNT.getKeyword()), 
  REGR_RSQUARED(K.REGR_R2.getKeyword()), 
  REGR_AVGX(K.REGR_AVGX.getKeyword()), 
  REGR_AVGY(K.REGR_AVGY.getKeyword()), 
  REGR_SXX(K.REGR_SXX.getKeyword()), 
  REGR_SYY(K.REGR_SYY.getKeyword()), 
  REGR_SXY(K.REGR_SXY.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private BinarySetFunction(String sKeywords) { _sKeywords = sKeywords; }
}
