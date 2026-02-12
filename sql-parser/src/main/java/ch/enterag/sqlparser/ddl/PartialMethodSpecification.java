package ch.enterag.sqlparser.ddl;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class PartialMethodSpecification
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(PartialMethodSpecification.class.getName());
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class PmsVisitor extends EnhancedSqlBaseVisitor<PartialMethodSpecification>
  {
    @Override 
    public PartialMethodSpecification visitMethodType(SqlParser.MethodTypeContext ctx)
    {
      setMethodType(getMethodType(ctx));
      return PartialMethodSpecification.this;
    }
    @Override
    public PartialMethodSpecification visitPartialMethodSpecification(SqlParser.PartialMethodSpecificationContext ctx)
    {
      setIdentifier(ctx.IDENTIFIER(),getMethodName());
      if (ctx.SPECIFIC() != null)
        setSpecificMethodName(ctx.specificMethodName(),getSpecificMethodName());
      return visitChildren(ctx);
    }
    @Override
    public PartialMethodSpecification visitSqlParameterDeclaration(SqlParser.SqlParameterDeclarationContext ctx)
    {
      SqlParameterDeclaration spd = getSqlFactory().newSqlParameterDeclaration();
      spd.parse(ctx);
      getSqlParameterDeclarations().add(spd);
      return PartialMethodSpecification.this;
    }
    @Override
    public PartialMethodSpecification visitReturnsDataType(SqlParser.ReturnsDataTypeContext ctx)
    {
      setReturnsType(true);
      if (ctx.dataType() != null)
      {
        setReturnsType(getSqlFactory().newDataType());
        getReturnsType().parse(ctx.dataType());
      }
      return PartialMethodSpecification.this;
    }
    @Override
    public PartialMethodSpecification visitResultCast(SqlParser.ResultCastContext ctx)
    {
      setResultCast(true);
      if (ctx.dataType() != null)
      {
        setResultCast(getSqlFactory().newDataType());
        getResultCast().parse(ctx.dataType());
      }
      return PartialMethodSpecification.this;
    }
    @Override
    public PartialMethodSpecification visitTableColumn(SqlParser.TableColumnContext ctx)
    {
      TableColumn tc = getSqlFactory().newTableColumn();
      tc.parse(ctx);
      getTableColumns().add(tc);
      return PartialMethodSpecification.this;
    }
  }
  /*==================================================================*/

  private PmsVisitor _visitor = new PmsVisitor();
  private PmsVisitor getVisitor() { return _visitor; }
  
  private MethodType _mt = null;
  public MethodType getMethodType() { return _mt; }
  public void setMethodType(MethodType mt) { _mt = mt; }
  
  private Identifier _idMethodName = new Identifier();
  public Identifier getMethodName() { return _idMethodName; }
  private void setMethodName(Identifier idMethod) { _idMethodName = idMethod; }
  
  private QualifiedId _qSpecificMethodName = new QualifiedId();
  public QualifiedId getSpecificMethodName() { return _qSpecificMethodName; }
  private void setSpecificMethodName(QualifiedId qSpecificMethodName) { _qSpecificMethodName = qSpecificMethodName; }

  private List<SqlParameterDeclaration> _listSqlParameterDeclarations = new ArrayList<SqlParameterDeclaration>();
  public List<SqlParameterDeclaration> getSqlParameterDeclarations() { return _listSqlParameterDeclarations; }
  private void setSqlParameterDeclarations(List<SqlParameterDeclaration> listSqlParameterDeclarations) { _listSqlParameterDeclarations = listSqlParameterDeclarations; }

  private boolean _bReturnsType = false;
  public boolean hasReturnsType() { return _bReturnsType; }
  public void setReturnsType(boolean bReturnsType) { _bReturnsType = bReturnsType; }
  
  private DataType _dtReturnsType = null;
  public DataType getReturnsType() { return _dtReturnsType; }
  public void setReturnsType(DataType dtReturnsType) { _dtReturnsType = dtReturnsType; }
  
  private boolean _bResultCast = false;
  public boolean hasResultCast() { return _bResultCast; }
  public void setResultCast(boolean bResultCast) { _bResultCast = bResultCast; }
  
  private DataType _dtResultCast = null;
  public DataType getResultCast() { return _dtResultCast; }
  public void setResultCast(DataType dtResultCast) { _dtResultCast = dtResultCast; }
  
  private List<TableColumn> _listTableColumns = new ArrayList<TableColumn>();
  public List<TableColumn> getTableColumns() { return _listTableColumns; }
  private void setTableColumns(List<TableColumn> listTableColumns) { _listTableColumns = listTableColumns; }
  
  /*------------------------------------------------------------------*/
  /** format the parameter declarations list.
   * @return formatted list.
   */
  private String formatSqlParameterDeclarations()
  {
    String sList = sLEFT_PAREN;
    for (int iParameter = 0; iParameter < getSqlParameterDeclarations().size(); iParameter++)
    {
      if (iParameter > 0)
        sList = sList + sCOMMA;
      sList = sList + sNEW_LINE + sINDENT + getSqlParameterDeclarations().get(iParameter).format();
    }
    if (getSqlParameterDeclarations().size() > 0)
      sList = sList + sNEW_LINE;
    sList = sList + sRIGHT_PAREN;
    return sList;
  } /* formatSqlParameterDeclarations */
  
  /*------------------------------------------------------------------*/
  /** format the table columns list.
   * @return formatted list.
   */
  private String formatTableColumns()
  {
    String sList = sLEFT_PAREN;
    for (int iColumn = 0; iColumn < getTableColumns().size(); iColumn++)
    {
      if (iColumn > 0)
        sList = sList + sCOMMA;
      sList = sList + sNEW_LINE + sINDENT + getTableColumns().get(iColumn).format();
    }
    sList = sList + sNEW_LINE + sRIGHT_PAREN;
    return sList;
  } /* formatTableColumns */
  
  /*------------------------------------------------------------------*/
  /** format the partial method specification.
   * @return the SQL string corresponding to the fields of the partial 
   *   method specification.
   */
  @Override
  public String format()
  {
    String sSpecification = "";
    if (getMethodType() != null)
      sSpecification = sSpecification + getMethodType().getKeywords() + sSP;
    sSpecification = sSpecification + K.METHOD.getKeyword() + sSP + getMethodName().format();
    sSpecification = sSpecification + formatSqlParameterDeclarations();
    if (hasReturnsType())
    {
      sSpecification = sSpecification + sSP + K.RETURNS.getKeyword(); 
      if (getReturnsType() != null)
        sSpecification = sSpecification + sSP + getReturnsType().format();
      else
        sSpecification = sSpecification + sSP + K.AS.getKeyword() + sSP + K.LOCATOR.getKeyword();
      if (hasResultCast())
      {
        sSpecification = sSpecification + sSP + K.CAST.getKeyword() + sSP + K.FROM.getKeyword();
        if (getResultCast() != null)
          sSpecification = sSpecification + sSP + getResultCast().format();
        else
          sSpecification = sSpecification + sSP + K.AS.getKeyword() + sSP + K.LOCATOR.getKeyword();
      }
    }
    else if (getTableColumns().size() > 0)
    {
      sSpecification = sSpecification + sSP + K.RETURNS.getKeyword() + sSP + 
        K.TABLE.getKeyword() + formatTableColumns();
    }
    if (getSpecificMethodName().isSet())
      sSpecification = sSpecification + sSP + K.SPECIFIC.getKeyword() + sSP +
        getSpecificMethodName().format();
    return sSpecification;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the partial method specification from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.PartialMethodSpecificationContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the partial method specification from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().partialMethodSpecification());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a method specification.
   * @param mt method type (or null).
   * @param idMethodName method name (not null!).
   * @param listSqlParameterDeclarations parameter declarations of method.
   * @param bReturnsType true, if a data type is returned.
   * @param dtReturnsType returns data type or null for AS LOCATOR.
   * @param bResultCast true, if the result is cast to another type.
   * @param dtResultCast data type to which the result is to be cast.
   * @param listTableColumns list of columns of returns table type. 
   * @param qSpecificMethodName SPECIFIC method name (not null!).
   */
  public void initialize(
    MethodType mt,
    Identifier idMethodName,
    List<SqlParameterDeclaration> listSqlParameterDeclarations,
    boolean bReturnsType,
    DataType dtReturnsType,
    boolean bResultCast,
    DataType dtResultCast,
    List<TableColumn> listTableColumns,
    QualifiedId qSpecificMethodName)    
  {
    _il.enter(mt,idMethodName, listSqlParameterDeclarations,
      String.valueOf(bReturnsType), dtReturnsType,
      String.valueOf(bResultCast), dtResultCast,
      listTableColumns,
      qSpecificMethodName);
    setMethodType(mt);
    setMethodName(idMethodName);
    setSqlParameterDeclarations(listSqlParameterDeclarations);
    setReturnsType(bReturnsType);
    setReturnsType(dtReturnsType);
    setResultCast(bResultCast);
    setResultCast(dtResultCast);
    setTableColumns(listTableColumns);
    setSpecificMethodName(qSpecificMethodName);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public PartialMethodSpecification(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
}/* class PartialMethodSpecification */
