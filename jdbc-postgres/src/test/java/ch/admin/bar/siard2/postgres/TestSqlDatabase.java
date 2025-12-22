package ch.admin.bar.siard2.postgres;

import java.io.*;
import java.math.*;
import java.nio.file.Files;
import java.sql.*;
import java.sql.Date;
import java.text.*;
import java.util.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.datatype.enums.PreType;
import ch.enterag.sqlparser.ddl.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.dml.*;
import ch.enterag.sqlparser.dml.enums.*;
import ch.enterag.sqlparser.expression.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.sqlparser.identifier.*;

public class TestSqlDatabase 
{
  public static final String _sTEST_SCHEMA = "TESTSQLSCHEMA";
  public static final String _sTEST_TABLE_SIMPLE = "TSQLSIMPLE";
  public static final String COLUMN_DATALINK = "COLUMN_DATALINK";

  public static QualifiedId getQualifiedSimpleTable() { return new QualifiedId(null,_sTEST_SCHEMA,_sTEST_TABLE_SIMPLE); }
  public static final String _sTEST_TABLE_COMPLEX = "TSQLCOMPLEX";
  public static QualifiedId getQualifiedComplexTable() { return new QualifiedId(null,_sTEST_SCHEMA,_sTEST_TABLE_COMPLEX); }
  private static final String _sTEST_VIEW_SIMPLE = "VSQLSIMPLE";
  public static QualifiedId getQualifiedSimpleView() { return new QualifiedId(null,_sTEST_SCHEMA,_sTEST_VIEW_SIMPLE); }
  private static final String _sTEST_TYPE_DISTINCT = "TYPSQLDISTINCT";
  public static QualifiedId getQualifiedDistinctType() { return new QualifiedId(null,_sTEST_SCHEMA,_sTEST_TYPE_DISTINCT); }
  private static final String _sTEST_TYPE_SIMPLE = "TYPSQLSIMPLE";
  public static QualifiedId getQualifiedSimpleType() { return new QualifiedId(null,_sTEST_SCHEMA,_sTEST_TYPE_SIMPLE); }
  private static final String _sTEST_TYPE_ALL = "TYPSQLALL";
  public static QualifiedId getQualifiedAllType() { return new QualifiedId(null,_sTEST_SCHEMA,_sTEST_TYPE_ALL); }
  private static final String _sTEST_TYPE_COMPLEX = "TYPSQLCOMPLEX";
  public static QualifiedId getQualifiedComplexType() { return new QualifiedId(null,_sTEST_SCHEMA,_sTEST_TYPE_COMPLEX); }

  public static int _iPrimarySimple = -1;

  public static byte[] getCircleJpgBytes() {
    try {
      return Files.readAllBytes(new File("testfiles/circle.jpg").toPath());
    } catch (IOException e) {
      return TestUtils.getBytes(1000000);
    }
  }

  @SuppressWarnings("deprecation")
  private static List<TestColumnDefinition> getListCdSimple()
  {
    List<TestColumnDefinition> listCdSimple = new ArrayList<TestColumnDefinition>();
    listCdSimple.add(new TestColumnDefinition("CCHAR_5","CHAR(5)","Abcd"));
    listCdSimple.add(new TestColumnDefinition("CVARCHAR_255","VARCHAR(255)",TestUtils.getString(196)));
    listCdSimple.add(new TestColumnDefinition("CNULL","VARCHAR(255)",null));
    listCdSimple.add(new TestColumnDefinition("CCLOB_2M","CLOB(2M)",TestUtils.getString(2000000)));
    listCdSimple.add(new TestColumnDefinition("CNCHAR_5","NCHAR(5)","Niño"));
    listCdSimple.add(new TestColumnDefinition("CNVARCHAR_127","NCHAR VARYING(127)",TestUtils.getNString(100)));
    listCdSimple.add(new TestColumnDefinition("CNCLOB_1M","NCLOB(1M)",TestUtils.getNString(1000000)));
    listCdSimple.add(new TestColumnDefinition("CXML","XML","<a>foöäpwkfèégopàèwerkgvoperkv &lt; and &amp; ifjeifj</a>"));
    listCdSimple.add(new TestColumnDefinition("CBINARY_5","BINARY(5)",new byte[] {1,-2,3,-4, 0} ));
    listCdSimple.add(new TestColumnDefinition("CVARBINARY_255","VARBINARY(255)",TestUtils.getBytes(196) ));
    listCdSimple.add(new TestColumnDefinition("CBLOB","BLOB",TestUtils.getBytes(1000000)));
    listCdSimple.add(new TestColumnDefinition("CNUMERIC_31","NUMERIC(31)",new BigDecimal(BigInteger.valueOf(1234567890123456789l),0)));
    listCdSimple.add(new TestColumnDefinition("CDECIMAL_15_5","DECIMAL(15,5)",new BigDecimal(BigInteger.valueOf(3141592653210l),5)));
    listCdSimple.add(new TestColumnDefinition("CSMALLINT","SMALLINT",Short.valueOf((short)-32000)));
    listCdSimple.add(new TestColumnDefinition("CSMALLINT_BYTE","SMALLINT",Short.valueOf((short)-128)));
    _iPrimarySimple = listCdSimple.size(); // next column will be primary key column
    listCdSimple.add(new TestColumnDefinition("CINTEGER","INTEGER",Integer.valueOf(1234567890)));
    listCdSimple.add(new TestColumnDefinition("CBIGINT","BIGINT",Long.valueOf(-123456789012345678l)));
    DecimalFormat df = new DecimalFormat("#.######");
    df.setRoundingMode(RoundingMode.CEILING);
    listCdSimple.add(new TestColumnDefinition("CFLOAT_10","FLOAT(10)",Float.valueOf(df.format(Math.E))));
    listCdSimple.add(new TestColumnDefinition("CREAL","REAL",Float.valueOf(Double.valueOf(Math.PI).floatValue())));
    listCdSimple.add(new TestColumnDefinition("CDOUBLE","DOUBLE PRECISION",Double.valueOf(Math.E)));
    listCdSimple.add(new TestColumnDefinition("CBOOLEAN","BOOLEAN",Boolean.valueOf(true)));
    listCdSimple.add(new TestColumnDefinition("CDATE","DATE",new Date(2016-1900,11,28)));
    listCdSimple.add(new TestColumnDefinition("CTIME","TIME",new Time(13,45,28)));
    listCdSimple.add(new TestColumnDefinition("CTIMESTAMP","TIMESTAMP(9)",new Timestamp(2016-1900,11,28,13,45,28,123456789)));
    listCdSimple.add(new TestColumnDefinition("CINTERVAL_YEAR_3_MONTH","INTERVAL YEAR(3) TO MONTH",new Interval(1,123,3)));
    listCdSimple.add(new TestColumnDefinition("CINTERVAL_DAY_2_SECONDS_6","INTERVAL DAY(2) TO SECOND(6)",new Interval(1,4,17,54,23,123456000l)));
    listCdSimple.add(new TestColumnDefinition(COLUMN_DATALINK,"BLOB", getCircleJpgBytes()));
    return listCdSimple;
  }
  public static List<TestColumnDefinition> _listCdSimple = getListCdSimple();

  private static List<TestColumnDefinition> getListBaseDistinct()
  {
    List<TestColumnDefinition> listBaseDistinct = new ArrayList<TestColumnDefinition>();
    listBaseDistinct.add(new TestColumnDefinition(getQualifiedDistinctType().format(),"NCHAR VARYING(10)","Auwääh"));
    return listBaseDistinct;
  }
  public static List<TestColumnDefinition> _listBaseDistinct = getListBaseDistinct();
  
  private static List<TestColumnDefinition> getListAdSimple()
  {
    List<TestColumnDefinition> listAdSimple = new ArrayList<TestColumnDefinition>();
    listAdSimple.add(new TestColumnDefinition("TABLEID","INTEGER",Integer.valueOf(1)));
    listAdSimple.add(new TestColumnDefinition("TRANSCRIPTION","CLOB",TestUtils.getString(1000000)));
    listAdSimple.add(new TestColumnDefinition("SOUND","BLOB",TestUtils.getBytes(1000000)));
    return listAdSimple;
  }
  public static List<TestColumnDefinition> _listAdSimple = getListAdSimple();
  
  private static List<TestColumnDefinition> getListAdComplex()
  {
    List<TestColumnDefinition> listAdComplex = new ArrayList<TestColumnDefinition>();
    listAdComplex.add(new TestColumnDefinition("ID","INTEGER",Integer.valueOf(1)));
    listAdComplex.add(new TestColumnDefinition("NESTED_ROW",getQualifiedSimpleType().format(),getListAdSimple()));
    return listAdComplex;
  }
  public static List<TestColumnDefinition> _listAdComplex = getListAdComplex();

  private static List<TestColumnDefinition> getListCdArray()
  {
    List<TestColumnDefinition> listCd = new ArrayList<TestColumnDefinition>();
    listCd.add(new TestColumnDefinition("CARRAY[1]","VARCHAR(255)",TestUtils.getString(196)));
    listCd.add(new TestColumnDefinition("CARRAY[2]","VARCHAR(255)",""));
    listCd.add(new TestColumnDefinition("CARRAY[3]","VARCHAR(255)",TestUtils.getString(5)));
    listCd.add(new TestColumnDefinition("CARRAY[4]","VARCHAR(255)",TestUtils.getString(64)));
    return listCd;
  }
  public static List<TestColumnDefinition> _listCdArray = getListCdArray();

  public static int _iPrimaryComplex = -1;
  private static List<TestColumnDefinition> getListCdComplex()
  {
    List<TestColumnDefinition> listCdComplex = new ArrayList<TestColumnDefinition>();
    _iPrimaryComplex = listCdComplex.size(); // next column will be primary key column
    listCdComplex.add(new TestColumnDefinition("CID","INT",Integer.valueOf(1234567890)));
    listCdComplex.add(new TestColumnDefinition("COMPLETE",getQualifiedAllType().format(),_listCdSimple));
    listCdComplex.add(new TestColumnDefinition("CUDT",getQualifiedComplexType().format(),_listAdComplex));
    listCdComplex.add(new TestColumnDefinition("CDISTINCT",getQualifiedDistinctType().format(),_listBaseDistinct));
    listCdComplex.add(new TestColumnDefinition("CARRAY","VARCHAR(255) ARRAY[1000]",_listCdArray));
    return listCdComplex;
  }
  public static List<TestColumnDefinition> _listCdComplex = getListCdComplex();
  
  private SqlFactory _sf = new BaseSqlFactory();
  private Connection _conn = null;
  private String _sDbUser = null;

	/*------------------------------------------------------------------*/
	public TestSqlDatabase(Connection conn, String sDbUser) 
		throws SQLException
	{
		_conn = conn;
		_sDbUser = sDbUser;
    try { _conn.setAutoCommit(false); }
    catch (SQLException se) { rollback("Autocommit", se); }
		drop();
		create();
	} /* constructor */

  /*------------------------------------------------------------------*/
  private void rollback(String sMessage, SQLException se)
  {
    System.out.println(sMessage+": "+EU.getExceptionMessage(se));
      try { _conn.rollback(); }
      catch(SQLException seRollback) 
      {
        System.err.println("Rollback failed: " +
          EU.getExceptionMessage(seRollback)); 
      }
  } /* rollback */

	/*------------------------------------------------------------------*/
	private void drop()
	{
    deleteTables();
    dropTables();
    dropTypes();
    dropSchema();
	} /* drop */

  /*------------------------------------------------------------------*/
  private void deleteTables()
  {
    deleteTable(getQualifiedSimpleTable());
    deleteTable(getQualifiedComplexTable());
  } /* deleteTables */
  
  /*------------------------------------------------------------------*/
  private void dropTables()
  {
    dropView(getQualifiedSimpleView()); // before referenced tables ...
    dropTable(getQualifiedComplexTable()); // first, because of foreign key ...
    dropTable(getQualifiedSimpleTable());
  } /* dropTables */

  /*------------------------------------------------------------------*/
  private void dropTypes()
  {
    dropType(getQualifiedDistinctType());
    dropType(getQualifiedComplexType());
    dropType(getQualifiedSimpleType());
    dropType(getQualifiedAllType());
  } /* dropTypes */
  
  /*------------------------------------------------------------------*/
  private void dropSchema() 
  {
    DropSchemaStatement dss = _sf.newDropSchemaStatement();
    dss.initialize(new SchemaId(null,_sTEST_SCHEMA),DropBehavior.RESTRICT);
    executeDrop(dss.format());
  } /* dropSchema */
  
  /*------------------------------------------------------------------*/
  private void deleteTable(QualifiedId qiTable)
  {
    DeleteStatement ds = _sf.newDeleteStatement();
    ds.initialize(qiTable, null);
    executeDrop(ds.format());
  } /* deleteTable */
  
  /*------------------------------------------------------------------*/
  private void dropTable(QualifiedId qiTable)
  {
    DropTableStatement dts = _sf.newDropTableStatement();
    dts.initialize(qiTable,DropBehavior.CASCADE);
    executeDrop(dts.format());
  } /* dropTable */
  
  /*------------------------------------------------------------------*/
  private void dropView(QualifiedId qiView)
  {
    DropViewStatement dvs = _sf.newDropViewStatement();
    dvs.initialize(qiView,DropBehavior.RESTRICT);
    executeDrop(dvs.format());
  } /* dropView */
  
  /*------------------------------------------------------------------*/
  private void dropType(QualifiedId qiType)
  {
    DropTypeStatement dts = _sf.newDropTypeStatement();
    dts.initialize(qiType, DropBehavior.RESTRICT);
    executeDrop(dts.format());
  } /* dropType */
  
  /*------------------------------------------------------------------*/
  private void executeDrop(String sSql)
  {
    try
    {
      Statement stmt = _conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
      stmt.executeUpdate(sSql);
      stmt.close();
      _conn.commit();
    }
    catch(SQLException se) 
    { 
      System.out.println(EU.getExceptionMessage(se));
      /* terminate transaction */
      try { _conn.rollback(); }
      catch(SQLException seRollback) { System.out.println("Rollback failed with "+EU.getExceptionMessage(seRollback)); }
    }
  } /* executeDrop */

	/*------------------------------------------------------------------*/
	private void create() 
		throws SQLException
	{
    createSchema();
    createTypes();
    createTables();
    insertTables();
	} /* create */

  /*------------------------------------------------------------------*/
  private void createSchema()
    throws SQLException
  {
      CreateSchemaStatement css = _sf.newCreateSchemaStatement();
      css.initialize(new SchemaId(null,_sTEST_SCHEMA), new Identifier(_sDbUser));
      Statement stmt = _conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
      int iResult = stmt.executeUpdate(css.format());
      if (iResult != 0)
        throw new SQLException("Create schema "+_sTEST_SCHEMA+" failed!");
      stmt.close();
      _conn.commit();
  } /* createSchema */

  /*------------------------------------------------------------------*/
  private void createTypes()
    throws SQLException
  {
    createType(getQualifiedDistinctType(),_listBaseDistinct);
    createType(getQualifiedSimpleType(),_listAdSimple);
    createType(getQualifiedAllType(),_listCdSimple);
    createType(getQualifiedComplexType(),_listAdComplex);
  } /* createTypes */

  /*------------------------------------------------------------------*/
  private void createTables()
    throws SQLException
  {
    createTable(getQualifiedSimpleTable(),_listCdSimple,
      Arrays.asList(new String[] {_listCdSimple.get(_iPrimarySimple).getName()}),
      null,null,null);
    createTable(getQualifiedComplexTable(),_listCdComplex,
      Arrays.asList(new String[] {_listCdComplex.get(_iPrimaryComplex).getName()}),
      Arrays.asList(new String[] {_listCdComplex.get(_iPrimaryComplex).getName()}),
      getQualifiedSimpleTable(), 
      Arrays.asList(new String[] {_listCdSimple.get(_iPrimarySimple).getName()}));
    createView(getQualifiedSimpleView(),_listCdSimple,getQualifiedSimpleTable());
  } /* createTables */

  /*------------------------------------------------------------------*/
  private void insertTables()
    throws SQLException
  {
    insertTable(getQualifiedSimpleTable(),_listCdSimple);
    insertTable(getQualifiedComplexTable(),_listCdComplex);
  } /* insertTables */
  
  /*------------------------------------------------------------------*/
  private AttributeDefinition getAttributeDefinition(TestColumnDefinition tcd)
    throws SQLException
  {
    DataType dt = _sf.newDataType();
    if (tcd.getValue() instanceof List<?>)
    {
      try 
      { 
        QualifiedId qiType = new QualifiedId(tcd.getType());
        dt.initStructType(qiType);
      }
      catch(ParseException pe) { throw new SQLException("Type name "+tcd.getType()+" could not be parsed!",pe); }
    }
    else
    {
      PredefinedType pt = _sf.newPredefinedType();
      pt.parse(tcd.getType());
      dt.initPredefinedDataType(pt);
    }
    AttributeDefinition ad = _sf.newAttributeDefinition();
    ad.initNameType(new Identifier(tcd.getName()), dt);
    return ad;
  } /* getAttributeDefinition */
  
  /*------------------------------------------------------------------*/
  private void createType(QualifiedId qiType, List<TestColumnDefinition> _listAd)
    throws SQLException
  {
    CreateTypeStatement cts = _sf.newCreateTypeStatement();
    if (_listAd.size() == 1)
    {
      TestColumnDefinition tcd = _listAd.get(0);
      PredefinedType ptBase = _sf.newPredefinedType();
      ptBase.parse(tcd.getType());
      cts.initDistinct(qiType, ptBase);
    }
    else
    {
      List<AttributeDefinition> listAttributeDefinitions = new ArrayList<AttributeDefinition>();
      for (int iAttribute = 0; iAttribute < _listAd.size(); iAttribute++)
      {
        TestColumnDefinition tcd = _listAd.get(iAttribute);
        listAttributeDefinitions.add(getAttributeDefinition(tcd));
      }
      cts.initAttributes(qiType, listAttributeDefinitions);
    }
    executeCreate(cts.format());
  } /* createType */
  
  /*------------------------------------------------------------------*/
  private void createTable(QualifiedId qiTable, List<TestColumnDefinition> listCd,
    List<String> listPrimary, List<String> listForeign,
    QualifiedId qiTableReferenced, List<String> listReferenced)
    throws SQLException
  {
    CreateTableStatement cts = _sf.newCreateTableStatement();
    List<TableElement> listTableElements = new ArrayList<TableElement>();
    for (int iColumn = 0; iColumn < listCd.size(); iColumn++)
    {
      TestColumnDefinition tcd = listCd.get(iColumn);
      listTableElements.add(getTableElement(tcd, (tcd.getName().equals("CCHAR_5")) || listPrimary.contains(tcd.getName())));
    }
    if (listPrimary != null)
      listTableElements.add(getPrimaryTableElement("PK"+qiTable.getName(), listPrimary));
    if (listForeign != null)
      listTableElements.add(getForeignTableElement("FK"+qiTable.getName(),
        listForeign,qiTableReferenced,listReferenced));
    cts.initTableElements(null,
      qiTable,
      listTableElements,null);
    executeCreate(cts.format());
  } /* createTable */
  
  /*------------------------------------------------------------------*/
  private TableElement getTableElement(TestColumnDefinition tcd, boolean bNotNull)
    throws SQLException
  {
    DataType dt = _sf.newDataType();
    dt.parse(tcd.getType());
    ColumnDefinition cd = _sf.newColumnDefinition();
    if (bNotNull)
    {
      ColumnConstraintDefinition ccd = cd.getColumnConstraintDefinition();
      if (ccd == null)
      {
        ccd = _sf.newColumnConstraintDefinition();
        cd.setColumnConstraintDefinition(ccd);
      }
      ccd.setType(ColumnConstraintType.NOT_NULL);
    }
    cd.initNameType(new Identifier(tcd.getName()), dt);
    TableElement te = _sf.newTableElement();
    te.initColumnDefinition(cd);
    return te;
  } /* getTableElement */
  
  /*------------------------------------------------------------------*/
  private TableElement getPrimaryTableElement(String sPkName, List<String> listPrimary)
  {
    IdList ilPrimary = new IdList();
    for (int i = 0; i < listPrimary.size(); i++)
      ilPrimary.get().add(listPrimary.get(i));
    TableConstraintDefinition tcd = _sf.newTableConstraintDefinition();
    tcd.initPrimaryKey(new QualifiedId(null,null,sPkName), ilPrimary);
    TableElement te = _sf.newTableElement();
    te.initTableConstraintDefinition(tcd);
    return te;
  } /* getPrimaryTableElement */

  /*------------------------------------------------------------------*/
  private TableElement getForeignTableElement(String sFkName, List<String> listForeign,
    QualifiedId qiTableReferenced, List<String> listReferenced)
  {
    IdList ilForeign = new IdList();
    for (int i = 0; i < listForeign.size();i++)
      ilForeign.get().add(listForeign.get(i));
    IdList ilReferenced = new IdList();
    for (int i = 0; i < listReferenced.size();i++)
      ilReferenced.get().add(listReferenced.get(i));
    TableConstraintDefinition tcd = _sf.newTableConstraintDefinition();
    tcd.initialize(new QualifiedId(null,null,sFkName), TableConstraintType.FOREIGN_KEY,
      ilForeign, qiTableReferenced, ilReferenced, null, ReferentialAction.CASCADE, 
      null, null, null, null);
    TableElement te = _sf.newTableElement();
    te.initTableConstraintDefinition(tcd);
    return te;
  } /* getForeignTableElement */
  
  /*------------------------------------------------------------------*/
  private void createView(QualifiedId qiView, 
    List<TestColumnDefinition> listCd, QualifiedId qiTable)
    throws SQLException
  {
    TablePrimary tp = _sf.newTablePrimary();
    tp.initialize(qiTable, new Identifier(), new ArrayList<Identifier>(), 
      null, false, null, false, false, null, null, false, false);
    TableReference tr = _sf.newTableReference();
    tr.initialize(tp, null, null, null, null, null, new ArrayList<Identifier>(), null, null);
    List<TableReference> listTableReferences = new ArrayList<TableReference>();
    listTableReferences.add(tr);
    QuerySpecification qs = _sf.newQuerySpecification();
    qs.initialize(true, new ArrayList<SelectSublist>(), listTableReferences, null);
    QueryExpressionBody qeb = _sf.newQueryExpressionBody();
    qeb.initialize(null, null, null, null, false, new ArrayList<Identifier>(), null,
      qs, new QualifiedId(), new ArrayList<TableRowValueExpression>());
    QueryExpression qe = _sf.newQueryExpression();
    qe.initialize(false, new ArrayList<WithElement>(), qeb);
    IdList ilColumnNames = new IdList();
    for (int iColumn = 0; iColumn < listCd.size(); iColumn++)
    {
      TestColumnDefinition tcd = listCd.get(iColumn);
      ilColumnNames.get().add(tcd.getName());
    }
    CreateViewStatement cvs = _sf.newCreateViewStatement();
    cvs.initialize(false, qiView, 
      ilColumnNames, new QualifiedId(), new ArrayList<ViewElement>(), 
      new QualifiedId(), qe, false, null);
    executeCreate(cvs.format());
  } /* createView */

  /*------------------------------------------------------------------*/
  private void executeCreate(String sSql)
    throws SQLException
  {
    Statement stmt = _conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
    int iResult = stmt.executeUpdate(sSql);
    stmt.close();
    if (iResult == 0)
      _conn.commit();
    else
    {
      _conn.rollback();
      throw new SQLException(sSql + " failed!");
    }
  } /* executeCreate */

  /*------------------------------------------------------------------*/
  private void insertTable(QualifiedId qiTable, List<TestColumnDefinition> listCd)
    throws SQLException
  {
    IdList ilColumns = new IdList();
    List<AssignedRow> listValues = new ArrayList<AssignedRow>();
    AssignedRow ar = _sf.newAssignedRow();
    listValues.add(ar);
    List<TestColumnDefinition> listLobs = new ArrayList<TestColumnDefinition>();
    for (int iColumn = 0; iColumn < listCd.size(); iColumn++)
    {
      TestColumnDefinition tcd = listCd.get(iColumn);
      ilColumns.get().add(tcd.getName());
      ar.getUpdateSources().add(getUpdateSource(tcd,listLobs));
    }
    InsertStatement is = _sf.newInsertStatement();
    is.initialize(qiTable, ilColumns, listValues, 
      null, null, false);
    PreparedStatement pstmt = _conn.prepareStatement(is.format());
    for (int iLob = 0; iLob < listLobs.size();iLob++)
    {
      TestColumnDefinition tcd = listLobs.get(iLob);
      if (tcd.getValue() instanceof String)
      {
        Reader rdrClob = new StringReader((String)tcd.getValue());
        pstmt.setCharacterStream(iLob+1, rdrClob);
      }
      else if (tcd.getValue() instanceof byte[])
      {
        InputStream isBlob = new ByteArrayInputStream((byte[])tcd.getValue());
        pstmt.setBinaryStream(iLob+1, isBlob);
      }
      else
        throw new SQLException("Invalid LOB!");
    }
    int iResult = pstmt.executeUpdate();
    pstmt.close();
    if (iResult == 1)
      _conn.commit();
    else
    {
      _conn.rollback();
      throw new SQLException("Insert into table "+qiTable.format()+" failed!");
    }
  } /* insertTable */

  /*------------------------------------------------------------------*/
  private CommonValueExpression getCommonValueExpression()
  {
    ValueExpressionPrimary vep = _sf.newValueExpressionPrimary();
    CommonValueExpression cve = _sf.newCommonValueExpression();
    cve.initialize(null, null, null, null, null, null, vep);
    return cve;
  } /* getCommonValueExpressionArray */

  /*------------------------------------------------------------------*/
  private CommonValueExpression getCommonValueExpressionLiteral()
  {
    UnsignedLiteral ul = _sf.newUnsignedLiteral();
    ValueExpressionPrimary vep = _sf.newValueExpressionPrimary();
    vep.initUnsignedLiteral(ul);
    CommonValueExpression cve = _sf.newCommonValueExpression();
    cve.initialize(null, null, null, null, null, null, vep);
    return cve;
  } /* getCommonValueExpression */
  
  /*------------------------------------------------------------------*/
  private CommonValueExpression getCommonValueExpressionDynamic()
  {
    GeneralValueSpecification gvs = _sf.newGeneralValueSpecification();
    gvs.initialize(new ColonId(), new ColonId(), new IdChain(), GeneralValue.QUESTION_MARK);
    CommonValueExpression cve = getCommonValueExpression();
    cve.getValueExpressionPrimary().setGeneralValueSpecification(gvs);
    return cve;
  } /* getCommonValueExpressionDynamic */
  
  /*------------------------------------------------------------------*/
  private CommonValueExpression getCommonValueExpressionNumeric()
  {
    UnsignedLiteral ul = _sf.newUnsignedLiteral();
    ValueExpressionPrimary vep = _sf.newValueExpressionPrimary();
    vep.initUnsignedLiteral(ul);
    NumericValueExpression nve = _sf.newNumericValueExpression();
    nve.initialize(null, null, null, null, null, null, vep, null);
    CommonValueExpression cve = _sf.newCommonValueExpression();
    cve.initialize(nve, null, null, null, null, null, null);
    return cve;
  } /* getCommonValueExpressionNumeric */
  
  /*------------------------------------------------------------------*/
  private ValueExpression getValueExpression(TestColumnDefinition tcd, 
    List<TestColumnDefinition> listLobs)
    throws SQLException
  {
    ValueExpression ve = _sf.newValueExpression();
    CommonValueExpression cve = null;
    Object o = tcd.getValue();
    if ((o == null) ||
      (o instanceof String) ||
      (o instanceof byte[]) ||
      (o instanceof Boolean) ||
      (o instanceof Date) ||
      (o instanceof Time) ||
      (o instanceof Timestamp) ||
      (o instanceof Interval))
      cve = getCommonValueExpressionLiteral();
    else if ((o instanceof BigDecimal) ||
             (o instanceof BigInteger) ||
             (o instanceof Short) ||
             (o instanceof Integer) ||
             (o instanceof Long) ||
             (o instanceof Float) ||
             (o instanceof Double))
      cve = getCommonValueExpressionNumeric();
    else if (o instanceof List<?>)
      cve = getCommonValueExpression();

    if (o != null)
    {
      if (o instanceof String)
      {
        String s = (String)o;
        if ((s != null) && 
          (!tcd.getType().startsWith(PreType.CLOB.getKeyword())) && 
          (!tcd.getType().startsWith(PreType.NCLOB.getKeyword())) && 
          (s.length() < 1000))
          cve.getValueExpressionPrimary().getUnsignedLit().
            initCharacterString(s);
        else
        {
          cve = getCommonValueExpressionDynamic();
          listLobs.add(tcd);
        }
      }
      else if (o instanceof byte[])
      {
        byte[] buf = (byte[])o;
        if ((buf != null) && 
          (!tcd.getType().startsWith(PreType.BLOB.getKeyword())) && 
          (buf.length < 1000))
          cve.getValueExpressionPrimary().getUnsignedLit().
            initBytes(buf);
        else
        {
          cve = getCommonValueExpressionDynamic();
          listLobs.add(tcd);
        }
      }
      else if (o instanceof BigDecimal)
      {
        BigDecimal bd = (BigDecimal)o;
        cve.getNumericValueExpression().getValueExpressionPrimary().getUnsignedLit().
          initBigDecimal(bd.abs());
      if (bd.signum() < 0)
        cve.getNumericValueExpression().
          setSign(Sign.MINUS_SIGN);
      }
      else if (o instanceof BigInteger)
      {
        BigInteger bi = (BigInteger)o;
        BigDecimal bd = new BigDecimal(bi);
        cve.getNumericValueExpression().getValueExpressionPrimary().getUnsignedLit().
          initBigDecimal(bd.abs());
      if (bd.signum() < 0)
        cve.getNumericValueExpression().
          setSign(Sign.MINUS_SIGN);
      }
      else if (o instanceof Short)
      {
        short sh = ((Short)o).shortValue();
        cve.getNumericValueExpression().getValueExpressionPrimary().getUnsignedLit().
          initInteger(Math.abs(sh));
        if (sh < 0)
          cve.getNumericValueExpression().
            setSign(Sign.MINUS_SIGN);
      }
      else if (o instanceof Integer)
      {
        int i = ((Integer)o).intValue();
        cve.getNumericValueExpression().getValueExpressionPrimary().getUnsignedLit().
          initInteger(Math.abs(i));
        if (i < 0)
          cve.getNumericValueExpression().
            setSign(Sign.MINUS_SIGN);
      }
      else if (o instanceof Long)
      {
        long l = ((Long)o).longValue();
        cve.getNumericValueExpression().getValueExpressionPrimary().getUnsignedLit().
          initLong(Math.abs(l));
        if (l < 0)
          cve.getNumericValueExpression().
            setSign(Sign.MINUS_SIGN);
      }
      else if (o instanceof Float)
      {
        double d = ((Float)o).doubleValue();
        cve.getNumericValueExpression().getValueExpressionPrimary().getUnsignedLit().
          initDouble(Math.abs(d));
        if (d < 0)
          cve.getNumericValueExpression().
            setSign(Sign.MINUS_SIGN);
      }
      else if (o instanceof Double)
      {
        double d = ((Double)o).doubleValue();
        cve.getNumericValueExpression().getValueExpressionPrimary().getUnsignedLit().
          initDouble(Math.abs(d));
        if (d < 0)
          cve.getNumericValueExpression().
            setSign(Sign.MINUS_SIGN);
      }
      else if (o instanceof Boolean)
      {
        boolean b = ((Boolean)o).booleanValue();
        cve.getValueExpressionPrimary().getUnsignedLit().
          initBoolean(b? BooleanLiteral.TRUE: BooleanLiteral.FALSE);
      }
      else if (o instanceof Date)
      {
        cve.getValueExpressionPrimary().getUnsignedLit().
          initDate((Date)o);
      }
      else if (o instanceof Time)
      {
        cve.getValueExpressionPrimary().getUnsignedLit().
          initTime((Time)o);
      }
      else if (o instanceof Timestamp)
      {
        cve.getValueExpressionPrimary().getUnsignedLit().
          initTimestamp((Timestamp)o);
      }
      else if (o instanceof Interval)
      {
        Interval iv = (Interval)o;
        cve.getValueExpressionPrimary().getUnsignedLit().initInterval(iv);
      }
      else if (o instanceof List<?>)
      {
        try
        {
          @SuppressWarnings("unchecked")
          List<TestColumnDefinition> listCd = (List<TestColumnDefinition>)o;
          if (listCd.size() == 4)
          {
            // ARRAY
            List<ValueExpression> listArrayValues = new ArrayList<ValueExpression>();
            for (int iElement = 0; iElement < listCd.size(); iElement++)
            {
              TestColumnDefinition tcdElement = listCd.get(iElement);
              ValueExpression veElement = getValueExpression(tcdElement,listLobs);
              listArrayValues.add(veElement);
            }
            cve.getValueExpressionPrimary().
              initArrayValueConstructor(listArrayValues);
          }
          else
          {
            QualifiedId qiType = new QualifiedId(tcd.getType()); 
            // UDT or DISTINCT
            List<SqlArgument> listAttributeValues = new ArrayList<SqlArgument>();
            for (int iAttribute = 0; iAttribute < listCd.size(); iAttribute++)
            {
              TestColumnDefinition tcdAttribute = listCd.get(iAttribute);
              listAttributeValues.add(getAttributeValue(tcdAttribute,listLobs));
            }
            cve.getValueExpressionPrimary().
              initUdtValueConstructor(qiType,listAttributeValues);
          }
        }
        catch(ParseException pe) { throw new SQLException("Type name "+tcd.getType()+" could not be parsed!",pe); }
      }
      else
        throw new SQLException("Unexpected value type "+o.getClass().getName()+"!");
    }
    ve.initialize(cve, null, null);
    return ve;
  } /* getValueExpression */
  
  /*------------------------------------------------------------------*/
  private SqlArgument getAttributeValue(TestColumnDefinition tcd, 
    List<TestColumnDefinition> listLobs)
    throws SQLException
  {
    SqlArgument sa = _sf.newSqlArgument();
    ValueExpression ve = getValueExpression(tcd, listLobs);
    sa.initialize(ve, new QualifiedId(), null);
    return sa;
  } /* getAttributeValue */
  
  /*------------------------------------------------------------------*/
  private UpdateSource getUpdateSource(TestColumnDefinition tcd, 
    List<TestColumnDefinition> listLobs)
    throws SQLException
  {
    UpdateSource us = _sf.newUpdateSource();
    if (tcd.getValue() != null)
    {
      ValueExpression ve = getValueExpression(tcd,listLobs);
      us.initialize(ve, null);
    }
    else
      us.initialize(null, SpecialValue.NULL);
    return us;
  } /* getUpdateSource */
  
} /* TestSqlDatabase */