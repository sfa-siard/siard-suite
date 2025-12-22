package ch.enterag.sqlparser.identifier;

import java.text.*;
import ch.enterag.sqlparser.*;

public class Identifier
{
  private String _sId = null;
  public String get() { return _sId; }
  public void set(String sId) { _sId = sId; }
  public String quote() { return SqlLiterals.quoteId(get()); }
  public String format() { return SqlLiterals.formatId(get()); }
  public void parse(String sDelimited) throws ParseException { set(SqlLiterals.parseId(sDelimited)); }
  public boolean isSet() { return (_sId != null); }
  public Identifier() {}
  public Identifier(String sIdentifier) { set(sIdentifier); }
}
