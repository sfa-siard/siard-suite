package ch.enterag.sqlparser.expression.enums;
import ch.enterag.sqlparser.*;
public enum SearchFunction
{
  SEARCH_DEPTH_FIRST_BY(K.SEARCH.getKeyword()+" "+K.DEPTH.getKeyword()+" "+K.FIRST.getKeyword()+" "+K.BY.getKeyword()),
  SEARCH_BREADTH_FIRST_BY(K.SEARCH.getKeyword()+" "+K.BREADTH.getKeyword()+" "+K.FIRST.getKeyword()+" "+K.BY.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private SearchFunction(String sKeywords) { _sKeywords = sKeywords; }
}
