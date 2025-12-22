package ch.enterag.sqlparser.identifier;

import java.text.*;
import ch.enterag.sqlparser.*;

public class ColonId extends Identifier
{
  private static final String sCOLON = ":"; 
  public ColonId() { super(); }
  public ColonId(String sIdentifier) { super(sIdentifier); }
  @Override
  public void parse(String sDelimited) throws ParseException 
  {
    if (sDelimited.startsWith(sCOLON))
      set(SqlLiterals.parseId(sDelimited.substring(1).trim()));
    else
      throw new ParseException("Colon expected!",0);
  }
  @Override public String format() { return sCOLON + super.format(); }
  @Override public String quote() { return sCOLON + super.quote(); }
}
