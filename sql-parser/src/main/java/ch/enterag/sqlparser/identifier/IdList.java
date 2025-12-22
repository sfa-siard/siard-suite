package ch.enterag.sqlparser.identifier;

import java.text.*;
import java.util.*;
import ch.enterag.sqlparser.*;

public class IdList
{
  private List<String> _listId = new ArrayList<String>();
  protected int size() { return _listId.size(); }
  protected String get(int i) { return _listId.get(i); }
  public List<String> get() { return _listId; }
  public void parseAdd(String sDelimited) throws ParseException { _listId.add(SqlLiterals.parseId(sDelimited)); }
  public String quote() { return SqlLiterals.quoteIdentifierCommaList(_listId); }
  public String format() { return SqlLiterals.formatIdentifierCommaList(_listId); }
  public boolean isSet() { return (_listId.size() > 0); }
  public IdList() {}
}
