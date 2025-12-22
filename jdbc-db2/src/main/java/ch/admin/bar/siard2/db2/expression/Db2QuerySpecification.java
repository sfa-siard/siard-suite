package ch.admin.bar.siard2.db2.expression;

import java.sql.*;
import java.text.*;
import java.util.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.identifier.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.admin.bar.siard2.db2.*;

public class Db2QuerySpecification
  extends QuerySpecification
{
  private Map<String,String> getStructColumns(QualifiedId qiTableName)
    throws SQLException
  {
    /* find all struct columns and their types */
    Map<String,String> mapStructColumns = new HashMap<String,String>();
    Connection conn = ((Db2SqlFactory)getSqlFactory()).getConnection();
    Db2DatabaseMetaData dmd = (Db2DatabaseMetaData)conn.getMetaData();
    ResultSet rs = dmd.getColumns(
      qiTableName.getCatalog(), 
      dmd.toPattern(qiTableName.getSchema()),
      dmd.toPattern(qiTableName.getName()),
      "%");
    while(rs.next())
    {
      String sColumnName = rs.getString("COLUMN_NAME");
      int iDataType = rs.getInt("DATA_TYPE");
      String sTypeName = rs.getString("TYPE_NAME");
      if (iDataType == Types.STRUCT)
        mapStructColumns.put(sColumnName,sTypeName);
    }
    rs.close();
    return mapStructColumns;
  } /* getStructColumns */
  
  private String formatStructColumn(QualifiedId qiType, String sColumnPrefix, String sColumnAlias)
    throws SQLException, ParseException
  {
    String sSpecification = "";
    Connection conn = ((Db2SqlFactory)getSqlFactory()).getConnection();
    Db2DatabaseMetaData dmd = (Db2DatabaseMetaData)conn.getMetaData();
    ResultSet rs = dmd.getAttributes(
      qiType.getCatalog(), 
      dmd.toPattern(qiType.getSchema()), 
      dmd.toPattern(qiType.getName()), 
      "%");
    boolean bFirst = true;
    while (rs.next())
    {
      if (bFirst)
        bFirst = false;
      else
        sSpecification = sSpecification + sCOMMA + sNEW_LINE + sINDENT;
      String sAttrName = rs.getString("ATTR_NAME");
      int iDataType = rs.getInt("DATA_TYPE");
      String sAttrTypeName = rs.getString("ATTR_TYPE_NAME");
      String sAliasName = sColumnAlias + "." + sAttrName;
      String sColumnName = sColumnPrefix + ".." + SqlLiterals.formatId(sAttrName);
      if (iDataType != Types.STRUCT)
        sSpecification = sSpecification + sColumnName + 
          " AS "+SqlLiterals.formatId(sAliasName);
      else
      {
        QualifiedId qiAttrType = new QualifiedId(sAttrTypeName);
        sSpecification = sSpecification + formatStructColumn(qiAttrType,sColumnName,sAliasName);
      }
    }
    rs.close();
    return sSpecification;
  } /* formatStructColumn */
  
  @Override
  public String format()
  {
    String sSpecification = null;
    if ((getTableReferences().size() == 1) &&
        (getWhereCondition() == null) &&
        (getGroupingElements().size() == 0) &&
        (getHavingCondition() == null) &&
        (getWindowNames().size() == 0))
    {
      TableReference tr = getTableReferences().get(0);
      if ((tr.getJoinOperator() == null) && (tr.getSampleMethod() == null))
      {
        TablePrimary tp = tr.getTablePrimary();
        if ((!tp.isOnly()) && (!tp.isTable()) && (!tp.isUnnest()) && 
          (tp.getQueryExpression() == null))
        {
          QualifiedId qiTableName = tp.getTableName();
          try
          {
            /* find all struct columns of this table */
            Map<String,String> mapStructColumns = getStructColumns(qiTableName);
            if (mapStructColumns.size() > 0)
            {
              sSpecification = K.SELECT.getKeyword();
              for (int iSelectColumn = 0; iSelectColumn < getSelectSublists().size(); iSelectColumn++)
              {
                if (iSelectColumn > 0)
                  sSpecification = sSpecification + sCOMMA;
                sSpecification = sSpecification +  sNEW_LINE + sINDENT;
                SelectSublist ssl = getSelectSublists().get(iSelectColumn);
                if (!ssl.isAsterisk())
                {
                  ValueExpression ve = ssl.getValueExpression();
                  CommonValueExpression cve = ve.getCommonValueExpression();
                  if (cve != null)
                  {
                    ValueExpressionPrimary vep = cve.getValueExpressionPrimary();
                    if (vep != null)
                    {
                      GeneralValueSpecification gvs = vep.getGeneralValueSpecification();
                      if (gvs != null)
                      {
                        IdChain idcColumnOrParameter = gvs.getColumnOrParameter();
                        if (idcColumnOrParameter.isSet())
                        {
                          List<String> listChain = idcColumnOrParameter.get(); 
                          String sColumnName = listChain.get(listChain.size()-1);
                          if (mapStructColumns.keySet().contains(sColumnName))
                          {
                            try
                            {
                              QualifiedId qiType = new QualifiedId(mapStructColumns.get(sColumnName));
                              sSpecification = sSpecification + formatStructColumn(qiType,SqlLiterals.formatId(sColumnName),sColumnName);
                            }
                            catch(ParseException pe){ throw new SQLException("Type name could not be parsed!",pe); }
                          }
                          else
                            sSpecification = sSpecification + ssl.format();
                        }
                        else
                          throw new SQLException("Not a column specification!");
                      }
                      else
                        throw new SQLException("Not a general value specification!");
                    }
                    else
                      throw new SQLException("Not a VEP!"); 
                  }
                  else
                    throw new SQLException("Not a common value expression!");
                }
                else
                  throw new SQLException("Asterisk found!");
              }
              /* FROM */
              sSpecification = sSpecification + sNEW_LINE + K.FROM.getKeyword() + 
                sSP + tr.format();;
            } /* if table has struct columns */
          }
          catch(SQLException se) { sSpecification = null; }
        }
      }
    }
    if (sSpecification == null)
      sSpecification = super.format();
    return sSpecification;
  }
  
  public Db2QuerySpecification(SqlFactory sf)
  {
    super(sf);
  }
}
