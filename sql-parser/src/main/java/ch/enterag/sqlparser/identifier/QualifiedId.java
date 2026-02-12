package ch.enterag.sqlparser.identifier;

import java.text.*;
import java.util.*;
import ch.enterag.sqlparser.*;

public class QualifiedId
{
  private String _sCatalog = null;
  public String getCatalog() { return _sCatalog; }
  public void setCatalog(String sCatalog) { _sCatalog = sCatalog; }
  public void parseCatalog(String sCatalogDelimited) throws ParseException { setCatalog(SqlLiterals.parseId(sCatalogDelimited)); } 
  private String _sSchema = null;
  public String getSchema() { return _sSchema; }
  public void setSchema(String sSchema) { _sSchema = sSchema; }
  public void parseSchema(String sSchemaDelimited) throws ParseException { setSchema(SqlLiterals.parseId(sSchemaDelimited)); }
  private String _sName = null;
  public String getName() { return _sName; }
  public void setName(String sName) { _sName = sName; }
  public void parseName(String sNameDelimited) throws ParseException { setName(SqlLiterals.parseId(sNameDelimited)); }
  public String quote() { return SqlLiterals.quoteQualifiedName(getCatalog(), getSchema(), getName()); }
  public String format() { return SqlLiterals.formatQualifiedName(getCatalog(), getSchema(), getName()); }
  public boolean isSet() { return (_sName != null); }
  /** equals helps identifying QualifiedIds in collections.
   */
  @Override
  public boolean equals(Object o) 
  {
    boolean bEqual = false;
    if (o instanceof QualifiedId)
    {
      QualifiedId qi = (QualifiedId)o;
      if (qi != null)
      {
        bEqual = Objects.equals(getName(), qi.getName()) &&
                 Objects.equals(getSchema(), qi.getSchema()) &&
                 Objects.equals(getCatalog(), qi.getCatalog());
      }
    }
    return bEqual;
  }
  /** hashCode must match equals.
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(getName(),getSchema(),getCatalog());
  }
  public QualifiedId() {}
  public QualifiedId(String sCatalog, String sSchema, String sName)
  {
    setCatalog(sCatalog);
    setSchema(sSchema);
    setName(sName);
  }
  public QualifiedId(String sFormatted)
    throws ParseException
  {
    if (sFormatted != null)
    {
      List<String> listIds = new ArrayList<String>();
      for (int iStart = 0; iStart < sFormatted.length(); )
      {
        int iEnd = SqlLiterals.getIdentifierEnd(sFormatted, iStart);
        listIds.add(SqlLiterals.parseId(sFormatted.substring(iStart,iEnd)));
        if (iEnd < sFormatted.length())
        {
          if (!sFormatted.substring(iEnd).startsWith(SqlLiterals.sDOT))
            throw new ParseException("Qualified identifier must be separated by periods!",0);
        }
        iStart = iEnd+1;
      }
      if (listIds.size() > 0)
        setName(listIds.get(listIds.size()-1));
      if (listIds.size() > 1)
        setSchema(listIds.get(listIds.size()-2));
      if (listIds.size() > 2)
        setCatalog(listIds.get(listIds.size()-3));
    }
  } /* parse */
}
