package ch.enterag.sqlparser.identifier;

import ch.enterag.sqlparser.*;

public class IdChain extends IdList
{
  private static final String sDOT = ".";
  public IdChain() { super(); }
  @Override
  public String format()
  {
    StringBuilder sbFormatted = new StringBuilder();
    for (int i = 0; i < size(); i++)
    {
      if (i > 0)
        sbFormatted.append(sDOT);
      sbFormatted.append(SqlLiterals.formatId(get(i)));
    }
    return sbFormatted.toString();
  }
  @Override
  public String quote()
  {
    StringBuilder sbFormatted = new StringBuilder();
    for (int i = 0; i < size(); i++)
    {
      if (i > 0)
        sbFormatted.append(sDOT);
      sbFormatted.append(SqlLiterals.quoteId(get(i)));
    }
    return sbFormatted.toString();
  }
}
