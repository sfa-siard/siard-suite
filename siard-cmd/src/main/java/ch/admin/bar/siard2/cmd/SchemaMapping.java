package ch.admin.bar.siard2.cmd;

import java.io.*;
import java.util.*;
import ch.admin.bar.siard2.api.*;

public class SchemaMapping extends Mapping
{
  private String _sMappedSchemaName = null;
  public String getMappedSchemaName() { return _sMappedSchemaName; }
  public void setMappedSchemaName(String sMappedSchemaName) { _sMappedSchemaName = sMappedSchemaName; }
  private Map<String,TableMapping> _mapTables = new HashMap<String, TableMapping>();
  public TableMapping getTableMapping(String sTableName) { return _mapTables.get(sTableName); }
  String getMappedTableName(String sTableName) { return getTableMapping(sTableName).getMappedTableName(); }
  private Map<String,TypeMapping> _mapTypes = new HashMap<String, TypeMapping>();
  TypeMapping getTypeMapping(String sTypeName) { return _mapTypes.get(sTypeName); }
  String getMappedTypeName(String sTypeName) { return getTypeMapping(sTypeName).getMappedTypeName(); }
  
  private SchemaMapping(boolean bSupportsArrays, boolean bSupportsUdts,
    String sMappedSchemaName, MetaSchema ms, 
    int iMaxTableNameLength, int iMaxColumnNameLength)
    throws IOException
  {
    _sMappedSchemaName = sMappedSchemaName;
    List<String> listTypes = new ArrayList<String>();
    for (int iType = 0; iType < ms.getMetaTypes(); iType++)
      listTypes.add(ms.getMetaType(iType).getName());
    Map<String,String> mapTypes = getDisambiguated(listTypes, iMaxTableNameLength);
    for (int iType = 0; iType < ms.getMetaTypes(); iType++)
    {
      MetaType mt = ms.getMetaType(iType);
      _mapTypes.put(mt.getName(), 
        TypeMapping.newInstance(mapTypes.get(mt.getName()), mt, iMaxColumnNameLength));
    }
    List<String> listTables = new ArrayList<String>();
    for (int iTable = 0; iTable < ms.getMetaTables(); iTable++)
      listTables.add(ms.getMetaTable(iTable).getName());
    Map<String,String> mapTables = getDisambiguated(listTables, iMaxTableNameLength);
    for (int iTable = 0; iTable < ms.getMetaTables(); iTable++)
    {
      MetaTable mt = ms.getMetaTable(iTable);
      _mapTables.put(mt.getName(),
        TableMapping.newInstance(bSupportsArrays, bSupportsUdts, 
          mapTables.get(mt.getName()), mt, iMaxColumnNameLength));
    }
  } /* constructor */
  
  public static SchemaMapping newInstance(boolean bSupportsArrays, boolean bSupportsUdts,
    String sMappedSchemaName, MetaSchema ms,
    int iMaxTableNameLength, int iMaxColumnNameLength)
    throws IOException
  {
    return new SchemaMapping(bSupportsArrays, bSupportsUdts, 
      sMappedSchemaName, ms, iMaxTableNameLength, iMaxColumnNameLength);
  } /* newInstance */
  
} /* class SchemaMapping */
