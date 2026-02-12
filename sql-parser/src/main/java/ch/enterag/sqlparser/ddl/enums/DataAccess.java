package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum DataAccess
{
  NO_SQL(K.NO.getKeyword()+" "+K.SQL.getKeyword()),
  CONTAINS_SQL(K.CONTAINS.getKeyword()+" "+K.SQL.getKeyword()),
  READS_SQL_DATA(K.READS.getKeyword()+" "+K.SQL.getKeyword()+" "+K.DATA.getKeyword()),
  MODIFIES_SQL_DATA(K.MODIFIES.getKeyword()+" "+K.SQL.getKeyword()+" "+K.DATA.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private DataAccess(String sKeywords) { _sKeywords = sKeywords; }
}
