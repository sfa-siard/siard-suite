package ch.admin.bar.siard2.cmd;

import java.io.*;
import java.util.*;
import ch.admin.bar.siard2.api.*;

public class ArchiveMapping {
  private Map<String,SchemaMapping> _mapSchemas = new HashMap<String,SchemaMapping>();
  public SchemaMapping getSchemaMapping(String sSchemaName) { return _mapSchemas.get(sSchemaName); }
  public String getMappedSchemaName(String sSchemaName) { return getSchemaMapping(sSchemaName).getMappedSchemaName(); }
  
  private ArchiveMapping(boolean bSupportsArrays, boolean bSupportsUdts,
    Map<String,String> mapSchemas, MetaData md,
    int iMaxTableNameLength, int iMaxColumnNameLength)
    throws IOException
  {
    for (int iSchema = 0; iSchema < md.getMetaSchemas(); iSchema++)
    {
      MetaSchema ms = md.getMetaSchema(iSchema);
      String sMappedSchemaName = mapSchemas.get(ms.getName());
      if (sMappedSchemaName == null)
        sMappedSchemaName = ms.getName();
      _mapSchemas.put(ms.getName(), 
        SchemaMapping.newInstance(bSupportsArrays, bSupportsUdts, 
          sMappedSchemaName, ms, iMaxTableNameLength, iMaxColumnNameLength));
    }
  } /* constructor */
  
  public static ArchiveMapping newInstance(boolean bSupportsArrays, boolean bSupportsUdts,
    Map<String,String> mapSchemas, 
    MetaData md, int iMaxTableNameLength, int iMaxColumnNameLength)
    throws IOException
  {
    return new ArchiveMapping(bSupportsArrays,bSupportsUdts,
      mapSchemas, md, iMaxTableNameLength, iMaxColumnNameLength);
  } /* newInstance */
} /* class ArchiveMapping */
