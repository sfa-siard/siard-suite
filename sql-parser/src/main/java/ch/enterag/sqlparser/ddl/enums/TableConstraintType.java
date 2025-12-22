package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum TableConstraintType
{
  PRIMARY_KEY(K.PRIMARY.getKeyword()+" "+K.KEY.getKeyword()),
  UNIQUE(K.UNIQUE.getKeyword()),
  FOREIGN_KEY(K.FOREIGN.getKeyword()+" "+K.KEY.getKeyword()),
  CHECK(K.CHECK.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private TableConstraintType(String sKeywords) { _sKeywords = sKeywords; }
}
