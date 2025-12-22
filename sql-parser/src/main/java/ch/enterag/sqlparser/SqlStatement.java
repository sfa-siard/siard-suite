package ch.enterag.sqlparser;

import java.io.*;
import java.math.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import ch.enterag.utils.logging.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.dml.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;

public class SqlStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(SqlStatement.class.getName());
  private static final int _iMAX_USER_NAME_LENGTH = 64;
  private static final int _iBUFSIZ = 8192;

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class SsVisitor extends EnhancedSqlBaseVisitor<SqlStatement>
  {
    @Override
    public SqlStatement visitDdlStatement(SqlParser.DdlStatementContext ctx)
    {
      setDdlStatement(getSqlFactory().newDdlStatement());
      getDdlStatement().parse(ctx);
      return SqlStatement.this;
    }
    @Override
    public SqlStatement visitDmlStatement(SqlParser.DmlStatementContext ctx)
    {
      setDmlStatement(getSqlFactory().newDmlStatement());
      getDmlStatement().parse(ctx);
      return SqlStatement.this;
    }
    @Override
    public SqlStatement visitQuerySpecification(SqlParser.QuerySpecificationContext ctx)
    {
      setQuerySpecification(getSqlFactory().newQuerySpecification());
      getQuerySpecification().parse(ctx);
      return SqlStatement.this;
    }
  }
  /*==================================================================*/

  private SsVisitor _visitor = new SsVisitor();
  private SsVisitor getVisitor() { return _visitor; }

  private DdlStatement _ddls = null;
  public DdlStatement getDdlStatement() { return _ddls; }
  public void setDdlStatement(DdlStatement ddls) { _ddls = ddls; }
  
  private DmlStatement _dmls = null;
  public DmlStatement getDmlStatement() { return _dmls; }
  public void setDmlStatement(DmlStatement dmls) { _dmls = dmls; }
  
  private QuerySpecification _qs = null;
  public QuerySpecification getQuerySpecification() { return _qs; }
  public void setQuerySpecification(QuerySpecification qs) { _qs = qs; }
  
  /* evaluation */
  private String _sDefaultCatalog = null;
  public String getDefaultCatalog() { return _sDefaultCatalog; }
  private String _sDefaultSchema = null;
  public String getDefaultSchema() { return _sDefaultSchema; }
  private String _sUser = null;
  public String getUser() { return _sUser; }
  public void setEvaluationContext(String sUser, String sDefaultCatalog, String sDefaultSchema)
  {
    _sUser = sUser;
    _sDefaultCatalog = sDefaultCatalog;
    _sDefaultSchema = sDefaultSchema;
  } /* setEvaluationContext */

  private Map<GeneralValueSpecification, Object> _mapQuestionMarkValues = new HashMap<GeneralValueSpecification, Object>();
  private Map<GeneralValueSpecification, DataType> _mapQuestionMarkTypes = new HashMap<GeneralValueSpecification,DataType>();

  public void setQuestionMarks(List<GeneralValueSpecification> listQuestionMarks)
  {
    _mapQuestionMarkValues.clear();
    for (int iQuestionMark = 0; iQuestionMark < listQuestionMarks.size(); iQuestionMark++)
    {
      GeneralValueSpecification gvs = listQuestionMarks.get(iQuestionMark);
      _mapQuestionMarkValues.put(gvs,null);
      _mapQuestionMarkTypes.put(gvs,null);
    }
  } /* setQuestionMarks */

  public void setQuestionMarkType(GeneralValueSpecification gvs, DataType dt)
  {
    if (_mapQuestionMarkTypes.containsKey(gvs))
      _mapQuestionMarkValues.put(gvs,dt);
  } /* setQuestionMarkType */
  
  public void setQuestionMarkValue(GeneralValueSpecification gvs, Object oValue)
  {
    try
    {
      if (_mapQuestionMarkValues.containsKey(gvs))
      {
        if (oValue instanceof Boolean)
          oValue = (Boolean)oValue;
        else if (oValue instanceof Byte)
        {
          Byte byValue = (Byte)oValue;
          oValue = BigDecimal.valueOf(byValue.longValue());
        }
        else if (oValue instanceof Short)
        {
          Short wValue = (Short)oValue;
          oValue = BigDecimal.valueOf(wValue.longValue());
        }
        else if (oValue instanceof Integer)
        {
          Integer iValue = (Integer)oValue;
          oValue = BigDecimal.valueOf(iValue.longValue());
        }
        else if (oValue instanceof Long)
        {
          Long lValue = (Long)oValue;
          oValue = BigDecimal.valueOf(lValue);
        }
        else if (oValue instanceof BigDecimal)
          oValue = (BigDecimal)oValue;
        else if (oValue instanceof Float)
        {
          Float fValue = (Float)oValue;
          oValue = Double.valueOf(fValue.doubleValue());
        }
        else if (oValue instanceof Double)
          oValue = (Double)oValue;
        else if (oValue instanceof byte[])
          oValue = (byte[])oValue;
        else if (oValue instanceof Date)
          oValue = (Date)oValue;
        else if (oValue instanceof Time)
          oValue = (Time)oValue;
        else if (oValue instanceof Timestamp)
          oValue = (Timestamp)oValue;
        else if (oValue instanceof String)
          oValue = (String)oValue;
        else if (oValue instanceof Clob)
        {
          Clob clob = (Clob)oValue;
          oValue = clob.getSubString(1l, (int)clob.length());
        }
        else if (oValue instanceof Blob)
        {
          Blob blob = (Blob)oValue;
          oValue = blob.getBytes(1l, (int)blob.length());
        }
        else if (oValue instanceof SQLXML)
        {
          SQLXML sqlxml = (SQLXML)oValue;
          oValue = sqlxml.getString();
        }
        else if (oValue instanceof InputStream)
        {
          InputStream is = (InputStream)oValue;
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          byte[] buf = new byte[_iBUFSIZ];
          for (int iRead = is.read(buf); iRead != -1; iRead = is.read(buf))
            baos.write(buf,0,iRead);
          baos.close();
          oValue = baos.toByteArray();
          is.close();
        }
        else if (oValue instanceof Reader)
        {
          Reader rdr = (Reader)oValue;
          StringWriter sw = new StringWriter();
          char[] cbuf = new char[_iBUFSIZ];
          for (int iRead = rdr.read(cbuf); iRead != -1; iRead = rdr.read(cbuf))
            sw.write(cbuf,0,iRead);
          sw.close();
          oValue = sw.toString();
          rdr.close();
        }
        _mapQuestionMarkValues.put(gvs,oValue);
      }
      else
        throw new IllegalArgumentException("Invalid attempt at setting a question mark value!");
    }
    catch(SQLException se) { throw new IllegalArgumentException("Parameter value could not be set!",se); }
    catch(IOException ie) { throw new IllegalArgumentException("Parameter value could not be set!",ie); }
  } /* setQuestionMarkValue */
  
  public DataType getGeneralType(GeneralValueSpecification gvs)
  {
    DataType dt = null;
    switch (gvs.getGeneralValue())
    {
      case CURRENT_PATH:
      case CURRENT_ROLE:
      case VALUE:
        throw new IllegalArgumentException("General value "+gvs.getGeneralValue().getKeywords()+" not supported!");
      case CURRENT_USER:
      case SESSION_USER:
      case SYSTEM_USER:
      case USER:
        dt = getSqlFactory().newDataType();
        PredefinedType pt = getSqlFactory().newPredefinedType();
        dt.initPredefinedDataType(pt);
        pt.initVarCharType(_iMAX_USER_NAME_LENGTH);
        break;
      case QUESTION_MARK:
        dt = _mapQuestionMarkTypes.get(gvs);
        break;
    }
    return dt;
  } /* getGeneralType */
  
  public Object getGeneralValue(GeneralValueSpecification gvs)
  {
    Object oValue = null;
    switch (gvs.getGeneralValue())
    {
      case CURRENT_PATH:
      case CURRENT_ROLE:
      case VALUE:
        throw new IllegalArgumentException("General value "+gvs.getGeneralValue().getKeywords()+" not supported!");
      case CURRENT_USER:
      case SESSION_USER:
      case SYSTEM_USER:
      case USER:
        oValue = getUser();
        break;
      case QUESTION_MARK:
        oValue = _mapQuestionMarkValues.get(gvs);
        break;
    }
    return oValue;
  } /* getGeneralValue */

  /*------------------------------------------------------------------*/
  /** look up the registered data type of a query column.
   * @param idcColumn column.
   * @return data type of the query column.
   */
  public DataType getColumnType(IdChain idcColumn)
  {
    DataType dt = null;
    int iLength = idcColumn.get().size();
    if (iLength > 0)
    {
      QuerySpecification qs = getQuerySpecification();
      DmlStatement dstmt = getDmlStatement();
      String sColumnName = idcColumn.get().get(iLength-1);
      if (qs != null)
      {
        TablePrimary tp = qs.getTablePrimary(this, idcColumn);
        dt = tp.getColumnType(sColumnName);
      }
      else if (dstmt != null)
      {
        UpdateStatement us = dstmt.getUpdateStatement();
        DeleteStatement ds = dstmt.getDeleteStatement();
        if (us != null)
          dt = us.getColumnType(sColumnName);
        else if (ds != null)
          dt = ds.getColumnType(sColumnName);
        else
          throw new IllegalArgumentException("No column types for insert statement should be needed!");
      }
      else
        throw new IllegalArgumentException("No column types for DDL statements should be needed!");
    }
    else
      throw new IllegalArgumentException("Identifier chain is invalid for column!");
    return dt;
  } /* getColumnType */
  
  /*------------------------------------------------------------------*/
  /** look up the registered value of a query column.
   * @param idcColumn column.
   * @param bAggregated true, if the value occurs in the argument of an
   *        aggregating function.
   * @return value of the query column.
   */
  public Object getColumnValue(IdChain idcColumn, boolean bAggregated)
  {
    Object oValue = null;
    QuerySpecification qs = getQuerySpecification();
    DmlStatement dstmt = getDmlStatement();
    int iLength = idcColumn.get().size();
    if (iLength > 0)
    {
      String sColumnName = idcColumn.get().get(iLength-1);
      if (qs != null)
      {
        if (!bAggregated)
        {
          if (!qs.isGroupedOk(idcColumn))
            throw new IllegalArgumentException("Column "+idcColumn.format()+" not in GROUP BY!");
        }
        TablePrimary tp = qs.getTablePrimary(this, idcColumn);
        oValue = tp.getColumnValue(sColumnName);
      }
      else if (dstmt != null)
      {
        if (bAggregated)
          throw new IllegalArgumentException("Aggregate functions not allowed in DML expressions!");
        UpdateStatement us = dstmt.getUpdateStatement();
        DeleteStatement ds = dstmt.getDeleteStatement();
        if (us != null)
          oValue = us.getColumnValue(sColumnName);
        else if (ds != null)
          oValue = ds.getColumnValue(sColumnName);
        else
          throw new IllegalArgumentException("No column values for insert statement should be needed!");
      }
      else
        throw new IllegalArgumentException("No column values for DDL statements should be needed!");
    }
    else
      throw new IllegalArgumentException("Identifier chain is invalid for column!");
    return oValue;
  } /* getColumnValue */
  
  /*------------------------------------------------------------------*/
  /** format the SQL statement.
   * @return the SQL string corresponding to the fields of the SQL statement.
   */
  @Override
  public String format()
  {
    String sStatement = null;
    if (getDdlStatement() != null)
      sStatement = getDdlStatement().format();
    else if (getDmlStatement() != null)
      sStatement = getDmlStatement().format();
    else if (getQuerySpecification() != null)
      sStatement = getQuerySpecification().format();
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the SQL statement from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.SqlStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the SQL statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    SqlParser.SqlStatementContext ctx = null; 
    try { ctx = getParser().sqlStatement(); }
    catch(Exception e)
    {
      setParser(newSqlParser2(sSql));
      ctx = getParser().sqlStatement();
    }
    parse(ctx);
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize an SQL statement.
   * Only one parameter not null!
   * @param ddls DDL statement.
   * @param dmls DML statement.
   * @param qs query specification.
   */
  public void initialize(
    DdlStatement ddls,
    DmlStatement dmls,
    QuerySpecification qs
    )
  {
    _il.enter();
    setDdlStatement(ddls);
    setDmlStatement(dmls);
    setQuerySpecification(qs);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public SqlStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class SqlStatement */
