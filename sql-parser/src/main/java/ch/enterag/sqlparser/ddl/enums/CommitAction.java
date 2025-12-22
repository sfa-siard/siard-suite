package ch.enterag.sqlparser.ddl.enums;
import ch.enterag.sqlparser.*;
public enum CommitAction
{
  PRESERVE(K.ON.getKeyword()+" "+K.COMMIT.getKeyword()+" "+K.PRESERVE.getKeyword()+" "+K.ROWS.getKeyword()),
  DELETE(K.ON.getKeyword()+" "+K.COMMIT.getKeyword()+" "+K.DELETE.getKeyword()+" "+K.ROWS.getKeyword());
  private String _sKeywords = null;
  public String getKeywords() { return _sKeywords; }
  private CommitAction(String sKeywords) { _sKeywords = sKeywords; }
}
