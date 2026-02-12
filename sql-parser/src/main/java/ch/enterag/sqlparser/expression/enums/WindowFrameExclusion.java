package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum WindowFrameExclusion
{
  EXCLUDE_CURRENT_ROW(K.EXCLUDE.getKeyword()+" "+K.CURRENT.getKeyword()+" "+K.ROW.getKeyword()),
  EXCLUDE_GROUP(K.EXCLUDE.getKeyword()+" "+K.GROUP.getKeyword()),
  EXCLUDE_TIES(K.EXCLUDE.getKeyword()+" "+K.TIES.getKeyword()), 
  EXCLUDE_NO_OTHERS(K.EXCLUDE.getKeyword()+" "+K.NO.getKeyword()+" "+K.OTHERS.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private WindowFrameExclusion(String sKeywords) { _sKeywords = sKeywords; }
}
