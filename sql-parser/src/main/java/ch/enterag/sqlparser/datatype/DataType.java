package ch.enterag.sqlparser.datatype;

import java.util.*;
import ch.enterag.utils.logging.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.generated.*;
import ch.enterag.sqlparser.identifier.*;

public class DataType
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(DataType.class.getName());
  
  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  protected class DtVisitor extends EnhancedSqlBaseVisitor<DataType>
  {
    @Override 
    public DataType visitDataType(SqlParser.DataTypeContext ctx)
    {
      visitChildren(ctx);
      return DataType.this;
    }
    @Override
    public DataType visitPreType(SqlParser.PreTypeContext ctx)
    {
      setType(Type.PRE);
      setPredefinedType(getSqlFactory().newPredefinedType());
      getPredefinedType().parse(ctx.predefinedType());
      return DataType.this;
    }
    @Override
    public DataType visitStructType(SqlParser.StructTypeContext ctx)
    {
      setType(Type.STRUCT);
      setUdtName(ctx.udtName(),getUdtName());
      return DataType.this;
    }
    @Override
    public DataType visitRowType(SqlParser.RowTypeContext ctx)
    {
      setType(Type.ROW);
      return visitChildren(ctx);
    }
    @Override
    public DataType visitRefType(SqlParser.RefTypeContext ctx)
    {
      setType(Type.REF);
      setUdtName(ctx.udtName(),getUdtName());
      if (ctx.scopeDefinition() != null)
        setTableName(ctx.scopeDefinition().tableName(),getScopeTable());
      return DataType.this;
    }
    @Override
    public DataType visitArrayType(SqlParser.ArrayTypeContext ctx)
    {
      setType(Type.ARRAY);
      setDataType(getSqlFactory().newDataType());
      getDataType().parse(ctx.dataType());
      if (ctx.length() != null)
        setLength(Integer.parseInt(ctx.length().getText()));
      return DataType.this;
    }
    @Override
    public DataType visitMultisetType(SqlParser.MultisetTypeContext ctx)
    {
      setType(Type.MULTISET);
      setDataType(getSqlFactory().newDataType());
      getDataType().parse(ctx.dataType());
      return DataType.this;
    }
    @Override
    public DataType visitFieldDefinition(SqlParser.FieldDefinitionContext ctx)
    {
      FieldDefinition fd = getSqlFactory().newFieldDefinition();
      fd.parse(ctx);
      addFieldDefinition(fd);
      return DataType.this;
    }
  } /* class DtVisitor */
  /*==================================================================*/
  public static enum Type { PRE, STRUCT, ROW, REF, ARRAY, MULTISET }
  /*==================================================================*/
  public static final int iUNDEFINED = -1;

  protected DtVisitor _visitor = new DtVisitor();
  protected DtVisitor getVisitor() { return _visitor; }
  
  private Type _type = null;
  public Type getType() { return _type; }
  public void setType(Type type) { _type = type; }
  
  private PredefinedType _pt = null;
  public PredefinedType getPredefinedType() { return _pt; }
  public void setPredefinedType(PredefinedType pt) { _pt = pt; }
  
  /* UDT name of structured type or REF type */
  private QualifiedId _qUdtName = new QualifiedId();
  public QualifiedId getUdtName() { return _qUdtName; }
  private void setUdtName(QualifiedId qUdtName) { _qUdtName = qUdtName; }
  
  /* field definitions of ROW type */
  private List<FieldDefinition> _listFd = null;
  public List<FieldDefinition> getFieldDefinitionList() { return _listFd; }
  public void setFieldDefinitionList(List<FieldDefinition> listFd) { _listFd = listFd; }
  public void addFieldDefinition(FieldDefinition fd) 
  {
    if (_listFd == null)
      _listFd = new ArrayList<FieldDefinition>();
    _listFd.add(fd);
  }
  
  /* SCOPE table of REF type */
  private QualifiedId _qScopeTable = new QualifiedId();
  public QualifiedId getScopeTable() { return _qScopeTable; }
  private void setScopeTable(QualifiedId qScopeTable) { _qScopeTable = qScopeTable; }
  
  /* DataType for ARRAY type or MULTISET type */
  private DataType _dt = null;
  public DataType getDataType() { return _dt; }
  public void setDataType(DataType dt) { _dt = dt; }
  
  /* length for ARRAY type */
  private int _iLength = iUNDEFINED;
  public int getLength() { return _iLength; }
  public void setLength(int iLength) { _iLength = iLength; }

  /*------------------------------------------------------------------*/
  /** format a STRUCT type.
   * @return SQL for STRUCT type.
   */
  protected String formatStructType()
  {
    String sDataType = getUdtName().format();
    return sDataType;
  } /* formatStructType */
  
  /*------------------------------------------------------------------*/
  /** format a ROW type
   * @return SQL for ROW type.
   */
  protected String formatRowType()
  {
    String sDataType = K.ROW.getKeyword() + sLEFT_PAREN;
    for (int iField = 0; iField < getFieldDefinitionList().size(); iField++)
    {
      if (iField > 0)
        sDataType = sDataType + sCOMMA + sSP;
      sDataType = sDataType + getFieldDefinitionList().get(iField).format();
    }
    sDataType = sDataType + sRIGHT_PAREN;
    return sDataType;
  } /* formatRowType */
  
  /*------------------------------------------------------------------*/
  /** format a REF type.
   * @return SQL for REF type.
   */
  protected String formatRefType()
  {
    String sDataType = K.REF.getKeyword() + 
      sLEFT_PAREN + getUdtName().format() + sRIGHT_PAREN;
    if (getScopeTable() != null)
      sDataType = sDataType + sSP + K.SCOPE.getKeyword() + sSP + getScopeTable().format();
    return sDataType;
  } /* formatRefType */
  
  /*------------------------------------------------------------------*/
  /** format an ARRAY type.
   * @return SQL for ARRAY type.
   */
  protected String formatArrayType()
  {
    String sDataType = getDataType().format()+ sSP + K.ARRAY.getKeyword();
    if (getLength() != iUNDEFINED)
      sDataType = sDataType + sLEFT_BRACKET + String.valueOf(getLength()) + sRIGHT_BRACKET;
    return sDataType;
  } /* formatArrayType */
  
  /*------------------------------------------------------------------*/
  /** format a MULTISET type.
   * @return SQL for MULTISET type.
   */
  protected String formatMultisetType()
  {
    String sDataType = getDataType().format() + sSP + K.MULTISET.getKeyword();
    return sDataType;
  } /* formatMultisetType */
  
  /*------------------------------------------------------------------*/
  /** format the data type.
   * @return the SQL string corresponding to the fields of the data type.
   */
  @Override
  public String format()
  {
    String sDataType = null;
    switch (getType())
    {
      case PRE: sDataType = getPredefinedType().format(); break;
      case STRUCT: sDataType = formatStructType(); break;
      case ROW: sDataType = formatRowType(); break;
      case REF: sDataType = formatRefType(); break;
      case ARRAY: sDataType = formatArrayType(); break;
      case MULTISET: sDataType = formatMultisetType(); break;
    }
    return sDataType;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the data type from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.DataTypeContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the data type from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    SqlParser.DataTypeContext ctx = null; 
    try { ctx = getParser().dataType(); }
    catch(Exception e)
    {
      setParser(newSqlParser2(sSql));
      ctx = getParser().dataType();
    }
    parse(ctx);
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialization of a predefined data type.
   * @param ptType predefined data type.
   */
  public void initPredefinedDataType(PredefinedType ptType)
  {
    initialize(Type.PRE,ptType,new QualifiedId(),new ArrayList<FieldDefinition>(), new QualifiedId(),null,iUNDEFINED);
  } /* initPredefinedDataType */
  
  /*------------------------------------------------------------------*/
  /** initialization for ARRAY type.
   * @param ptBase base type of array.
   * @param iLength length of array (or iUNDEFINED).
   */
  public void initArrayType(PredefinedType ptBase, int iLength)
  {
    DataType dtBase = getSqlFactory().newDataType();
    dtBase.initPredefinedDataType(ptBase);
    initialize(Type.ARRAY, null, new QualifiedId(),new ArrayList<FieldDefinition>(), new QualifiedId(),dtBase,iLength);
  } /* initArrayType */
  
  /*------------------------------------------------------------------*/
  /** initialization for STRUCT (DISTINCT or UDT) type.
   * N.B.: Structure of type (attributes etc.) can be retrieved from type
   * definition. Therefore only the name of the type represents it
   * here.
   * @param qidTypeName name of STRUCT type. 
   */
  public void initStructType(QualifiedId qidTypeName)
  {
    initialize(Type.STRUCT, null, qidTypeName,new ArrayList<FieldDefinition>(), new QualifiedId(),null,iUNDEFINED);
  } /* initDistinctType */
  
  /*------------------------------------------------------------------*/
  /** initialize a data type.
   * @param type type of data type.
   * @param pt predefined data type (or null).
   * @param qUdtName name of structured data type (not null!).
   * @param listFieldDefinitions list of field definitions of ROW type (or null).
   * @param qScopeTable name of SCOPE table of REF type (not null!).
   * @param dtCollection base type of ARRAY or MULTISET type (or null).
   * @param iLength (maximum) length of array type (or -1 for not set). 
   */
  public void initialize(Type type,
    PredefinedType pt,
    QualifiedId qUdtName,
    List<FieldDefinition> listFieldDefinitions,
    QualifiedId qScopeTable,
    DataType dtCollection,
    int iLength)
  {
    _il.enter(type, pt, qUdtName, listFieldDefinitions, qScopeTable, dtCollection, String.valueOf(iLength));
    setType(type);
    setPredefinedType(pt);
    setUdtName(qUdtName);
    setFieldDefinitionList(listFieldDefinitions);
    setScopeTable(qScopeTable);
    setDataType(dtCollection);
    setLength(iLength);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public DataType(SqlFactory sf)
  {
    super(sf);
  } /* constructor */

} /* class DataType */
