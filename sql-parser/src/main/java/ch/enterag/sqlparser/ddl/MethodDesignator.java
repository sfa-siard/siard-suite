package ch.enterag.sqlparser.ddl;

import java.util.*;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;
import ch.enterag.utils.logging.*;

public class MethodDesignator
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(MethodDesignator.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class MdVisitor extends EnhancedSqlBaseVisitor<MethodDesignator>
  {
    @Override
    public MethodDesignator visitMethodDesignator(SqlParser.MethodDesignatorContext ctx)
    {
      setIdentifier(ctx.IDENTIFIER(),getMethodName());
      return visitChildren(ctx);
    }
    @Override 
    public MethodDesignator visitMethodType(SqlParser.MethodTypeContext ctx)
    {
      setMethodType(getMethodType(ctx));
      return MethodDesignator.this;
    }
    @Override
    public MethodDesignator visitDataType(SqlParser.DataTypeContext ctx)
    {
      DataType dt = getSqlFactory().newDataType();
      dt.parse(ctx);
      getDataTypes().add(dt);
      return MethodDesignator.this;
    }
  }
  /*==================================================================*/
  
  private MdVisitor _visitor = new MdVisitor();
  private MdVisitor getVisitor() { return _visitor; }
  
  private MethodType _mt = null;
  public MethodType getMethodType() { return _mt; }
  public void setMethodType(MethodType mt) { _mt = mt; }
  
  private Identifier _idMethodName = new Identifier();
  public Identifier getMethodName() { return _idMethodName; }
  private void setMethodName(Identifier idMethod) { _idMethodName = idMethod; }

  private List<DataType> _listDataTypes = new ArrayList<DataType>();
  public List<DataType> getDataTypes() { return _listDataTypes; }
  private void setDataTypes(List<DataType> listDataTypes) { _listDataTypes = listDataTypes; }
  
  /*------------------------------------------------------------------*/
  /** format the of for data types.
   * @return formatted list.
   */
  private String formatDataTypes()
  {
    String sDataTypes = sLEFT_PAREN;
    for (int iDataType = 0; iDataType < getDataTypes().size(); iDataType++)
    {
      if (iDataType > 0)
        sDataTypes = sDataTypes + sCOMMA;
      sDataTypes = sDataTypes + getDataTypes().get(iDataType).format();
    }
    sDataTypes = sDataTypes + sRIGHT_PAREN;
    return sDataTypes;
  } /* formatDataTypes */
  
  /*------------------------------------------------------------------*/
  /** format the method designator.
   * @return the SQL string corresponding to the fields of the method 
   *   designator.
   */
  @Override
  public String format()
  {
    String sDesignator = "";
    if (getMethodType() != null)
      sDesignator = sDesignator + getMethodType().getKeywords() + sSP;
    sDesignator = sDesignator + getMethodName().format() + formatDataTypes();
    return sDesignator;
  } /* format */
  
  /*------------------------------------------------------------------*/
  /** parse the method designator from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.MethodDesignatorContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the method designator from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().methodDesignator());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a method designator.
   * @param mt method type (or null).
   * @param idMethod method name (not null!).
   * @param listDataTypes data types of parameters of method.
   */
  public void initialize(
    MethodType mt,
    Identifier idMethod,
    List<DataType> listDataTypes)    
  {
    _il.enter(mt,idMethod, listDataTypes);
    setMethodType(mt);
    setMethodName(idMethod);
    setDataTypes(listDataTypes);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public MethodDesignator(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class MethodDesignator */
