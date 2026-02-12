package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum Match
{
  FULL(K.MATCH.getKeyword() + " " + K.FULL.getKeyword()),
  PARTIAL(K.MATCH.getKeyword() + " " + K.PARTIAL.getKeyword()),
  SIMPLE(K.MATCH.getKeyword() + " " + K.SIMPLE.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private Match(String sKeywords) { _sKeywords = sKeywords; }
}
