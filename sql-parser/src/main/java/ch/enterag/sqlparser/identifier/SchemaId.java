package ch.enterag.sqlparser.identifier;

import java.text.*;
import ch.enterag.sqlparser.*;

public class SchemaId
{
  private String _sCatalog;
  public String getCatalog() { return _sCatalog; }
  public void setCatalog(String sCatalog) { _sCatalog = sCatalog; }
  public void parseCatalog(String sCatalogDelimited) throws ParseException { setCatalog(SqlLiterals.parseId(sCatalogDelimited)); } 
  private String _sSchema;
  public String getSchema() { return _sSchema; }
  public void setSchema(String sSchema) { _sSchema = sSchema; }
  public void parseSchema(String sSchemaDelimited) throws ParseException { setSchema(SqlLiterals.parseId(sSchemaDelimited)); }
  public String quote() { return SqlLiterals.quoteQualifiedSchema(getCatalog(), getSchema()); }
  public String format() { return SqlLiterals.formatQualifiedSchema(getCatalog(), getSchema()); }
  public boolean isSet() { return (_sSchema != null); }
  public SchemaId() {}
  public SchemaId(String sCatalog, String sSchema)
  {
    setCatalog(sCatalog);
    setSchema(sSchema);
  }
}
