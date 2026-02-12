/*======================================================================
MsSqlSqlFactory overrides the BaseSqlFactory for the MSSQL-specific SQL parser classes.
Version     : $Id: $
Application : SIARD2
Description : MsSqlSqlFactory overrides the BaseSqlFactory for the MSSQL-specific 
              SQL parser classes.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 19.05.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.mssql;

import java.sql.*;
import java.text.*;
import java.util.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.ddl.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.identifier.*;
import ch.admin.bar.siard2.mssql.datatype.*;
import ch.admin.bar.siard2.mssql.ddl.*;
import ch.admin.bar.siard2.mssql.expression.*;

/*====================================================================*/
/** MsSqlSqlFactory overrides the BaseSqlFactory for the MSSQL-specific
 * SQL parser classes. 
 * @author Hartwig Thomas
 */
public class MsSqlSqlFactory
  extends BaseSqlFactory
  implements SqlFactory
{
  private Map<String,Map<QualifiedId, Integer>> _mapColumnType = new HashMap<String,Map<QualifiedId, Integer>>();
  
  /*------------------------------------------------------------------*/
  /** Should be called by executeQuery of Statement and PreparedStatement.
   * Establishes the data types of the columns involved in the query.
   * @param qs parsed query specification.
   * @param sDefaultCatalog default catalog.
   * @param sDefaultSchema default schema.
   * @param dmd database metadata.
   * @throws SQLException
   */
  public void startQuery(QuerySpecification qs, 
    String sDefaultCatalog, String sDefaultSchema,
    BaseDatabaseMetaData dmd)
    throws SQLException
  {
    for (Iterator<TableReference> iterTableReference = qs.getTableReferences().iterator(); iterTableReference.hasNext(); )
    {
      TableReference tr = iterTableReference.next();
      TablePrimary tp = tr.getTablePrimary();
      String sCatalogName = tp.getTableName().getCatalog();
      if ((sCatalogName == null) || (sCatalogName.length() == 0))
        sCatalogName = sDefaultCatalog;
      String sSchemaName = tp.getTableName().getSchema();
      if ((sSchemaName == null) || (sSchemaName.length() == 0))
        sSchemaName = sDefaultSchema;
      String sTableName = tp.getTableName().getName();
      QualifiedId qiTable = new QualifiedId(sCatalogName, sSchemaName, sTableName);
      String sAliasName = tp.getCorrelationName().get();
      if (sAliasName != null)
        qiTable = new QualifiedId(null,null,sAliasName);
      /* get the column names of this table and their types */
      ResultSet rsColumns = dmd.getColumns(sCatalogName,
        dmd.toPattern(sSchemaName),
        dmd.toPattern(sTableName),
        "%");
      while(rsColumns.next())
      {
        int iDataType = Types.NULL;
        String sColumnName = rsColumns.getString("COLUMN_NAME");
        String sTypeName = rsColumns.getString("TYPE_NAME");
        try
        {
          QualifiedId qiType = new QualifiedId(sTypeName); 
          if (qiType.getCatalog() == null)
            qiType.setCatalog(sDefaultCatalog);
          if (qiType.getSchema() == null)
            qiType.setSchema(sDefaultSchema);
          ResultSet rsUdts = dmd.getUDTs(
            qiType.getCatalog(), 
            dmd.toPattern(qiType.getSchema()),
            dmd.toPattern(qiType.getName()),
            null);
          while(rsUdts.next())
            iDataType = rsUdts.getInt("DATA_TYPE");
          rsUdts.close(); 
        }
        catch(ParseException pe) {}
        Map<QualifiedId, Integer> mapTableType = _mapColumnType.get(sColumnName);
        if (mapTableType == null)
        {
          mapTableType = new HashMap<QualifiedId,Integer>();
          _mapColumnType.put(sColumnName, mapTableType);
        }
        mapTableType.put(qiTable, Integer.valueOf(iDataType));
      }
      rsColumns.close();
    }
  } /* startQuery */

  /*------------------------------------------------------------------*/
  /** This is called by MsSqlGeneralValueSpecification to decide, whether
   * ".ToString()" needs to be appended to the column name.
   * @param idColumn IdChain identifying the column.
   * @return Types.NULL, Types.DISTINCT, Types.STRUCT, or Types.JAVA_OBJECT.
   */
  public int getDataType(IdChain icColumn)
  {
    int iDataType = Types.NULL;
    if (icColumn.get().size() > 0)
    {
      String sColumnName = icColumn.get().get(icColumn.get().size()-1); 
      Map<QualifiedId, Integer> mapTableType = _mapColumnType.get(sColumnName);
      if (icColumn.get().size() > 1)
      {
        String sTableName = icColumn.get().get(icColumn.get().size()-2); 
        for (Iterator<QualifiedId> iterTable = mapTableType.keySet().iterator(); iterTable.hasNext(); )
        {
          QualifiedId qiTable = iterTable.next();
          if (!qiTable.getName().equals(sTableName))
            iterTable.remove();
        }
        if (icColumn.get().size() > 2)
        {
          String sSchemaName = icColumn.get().get(icColumn.get().size()-3); 
          for (Iterator<QualifiedId> iterTable = mapTableType.keySet().iterator(); iterTable.hasNext(); )
          {
            QualifiedId qiTable = iterTable.next();
            if (!qiTable.getSchema().equals(sSchemaName))
              iterTable.remove();
          }
        }
      }
      if (mapTableType.size() == 1)
      {
        QualifiedId qiTable = null;
        for (Iterator<QualifiedId> iterTable = mapTableType.keySet().iterator(); iterTable.hasNext(); )
          qiTable = iterTable.next();
        iDataType = mapTableType.get(qiTable).intValue();
      }
      else
        throw new IllegalArgumentException("Invalid column identifier "+icColumn.format()+"!");
    }
    else
      throw new IllegalArgumentException("Column identifier must not be empty!");
    return iDataType;
  } /* getDataType */
  
  @Override
  public CreateTypeStatement newCreateTypeStatement()
  {
    return new MsSqlCreateTypeStatement(this);
  } /* newDropSchemaStatement */
  
  @Override
  public DropSchemaStatement newDropSchemaStatement()
  {
    return new MsSqlDropSchemaStatement(this);
  } /* newDropSchemaStatement */
  
  @Override
  public DropTableStatement newDropTableStatement()
  {
    return new MsSqlDropTableStatement(this);
  } /* newDropTableStatement */
  
  @Override
  public DropViewStatement newDropViewStatement()
  {
    return new MsSqlDropViewStatement(this);
  } /* newDropViewStatement */
  
  @Override
  public DropTypeStatement newDropTypeStatement()
  {
    return new MsSqlDropTypeStatement(this);
  } /* newDropTypeStatement */
  
  @Override
  public DropProcedureStatement newDropProcedureStatement()
  {
    return new MsSqlDropProcedureStatement(this);
  } /* newDropTypeStatement */
  
  @Override 
  public DataType newDataType()
  {
    return new MsSqlDataType(this);
  } /* newDataType */
  
  @Override 
  public PredefinedType newPredefinedType()
  {
    return new MsSqlPredefinedType(this);
  } /* newPredefinedType */
  
  @Override 
  public Literal newLiteral()
  {
    return new MsSqlLiteral(this);
  } /* newLiteral */
  
  @Override 
  public ValueExpressionPrimary newValueExpressionPrimary()
  {
    return new MsSqlValueExpressionPrimary(this);
  } /* newValueExpressionPrimary */
  
  @Override 
  public UnsignedLiteral newUnsignedLiteral()
  {
    return new MsSqlUnsignedLiteral(this);
  } /* newUnsignedLiteral */

  @Override
  public NumericValueFunction newNumericValueFunction()
  {
    return new MsSqlNumericValueFunction(this);
  }
  
  @Override
  public GeneralValueSpecification newGeneralValueSpecification()
  {
    return new MsSqlGeneralValueSpecification(this);
  }

  @Override
  public SelectSublist newSelectSublist()
  {
    return new MsSqlSelectSublist(this);
  }
  
} /* MsSqlSqlFactory */
