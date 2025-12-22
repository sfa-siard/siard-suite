package ch.enterag.sqlparser.datatype;

import java.sql.Types;

import ch.enterag.utils.logging.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.datatype.enums.*;
import ch.enterag.sqlparser.ddl.enums.*;
import ch.enterag.sqlparser.generated.*;

public class PredefinedType 
  extends SqlBase
{
  /** logger */
  protected static IndentLogger _il = IndentLogger.getIndentLogger(PredefinedType.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class PdtVisitor extends EnhancedSqlBaseVisitor<PredefinedType>
  {
    @Override 
    public PredefinedType visitCharType(SqlParser.CharTypeContext ctx)
    {
      setType(PreType.CHAR);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitVarcharType(SqlParser.VarcharTypeContext ctx)
    {
      setType(PreType.VARCHAR);
      return visitChildren(ctx);
    }
    @Override 
    public PredefinedType visitClobType(SqlParser.ClobTypeContext ctx)
    {
      setType(PreType.CLOB);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitNcharType(SqlParser.NcharTypeContext ctx)
    {
      setType(PreType.NCHAR);
      return visitChildren(ctx);
    }
    @Override 
    public PredefinedType visitNvarcharType(SqlParser.NvarcharTypeContext ctx)
    {
      setType(PreType.NVARCHAR);
      return visitChildren(ctx);
    }
    @Override 
    public PredefinedType visitNclobType(SqlParser.NclobTypeContext ctx)
    {
      setType(PreType.NCLOB);
      return visitChildren(ctx);
    }
    @Override 
    public PredefinedType visitXmlType(SqlParser.XmlTypeContext ctx)
    {
      setType(PreType.XML);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitBinaryType(SqlParser.BinaryTypeContext ctx)
    {
      setType(PreType.BINARY);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitVarbinaryType(SqlParser.VarbinaryTypeContext ctx)
    {
      setType(PreType.VARBINARY);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitBlobType(SqlParser.BlobTypeContext ctx)
    {
      setType(PreType.BLOB);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitNumericType(SqlParser.NumericTypeContext ctx)
    {
      setType(PreType.NUMERIC);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitDecimalType(SqlParser.DecimalTypeContext ctx)
    {
      setType(PreType.DECIMAL);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitSmallintType(SqlParser.SmallintTypeContext ctx)
    {
      setType(PreType.SMALLINT);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitIntegerType(SqlParser.IntegerTypeContext ctx)
    {
      setType(PreType.INTEGER);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitBigintType(SqlParser.BigintTypeContext ctx)
    {
      setType(PreType.BIGINT);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitFloatType(SqlParser.FloatTypeContext ctx)
    {
      setType(PreType.FLOAT);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitRealType(SqlParser.RealTypeContext ctx)
    {
      setType(PreType.REAL);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitDoubleType(SqlParser.DoubleTypeContext ctx)
    {
      setType(PreType.DOUBLE);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitBooleanType(SqlParser.BooleanTypeContext ctx)
    {
      setType(PreType.BOOLEAN);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitDatalinkType(SqlParser.DatalinkTypeContext ctx)
    {
      setType(PreType.DATALINK);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitDateType(SqlParser.DateTypeContext ctx)
    {
      setType(PreType.DATE);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitTimeType(SqlParser.TimeTypeContext ctx)
    {
      setType(PreType.TIME);
      setSecondsDecimals(iTIME_DECIMALS_DEFAULT);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitTimestampType(SqlParser.TimestampTypeContext ctx)
    {
      setType(PreType.TIMESTAMP);
      setSecondsDecimals(iTIMESTAMP_DECIMALS_DEFAULT);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitIntervalType(SqlParser.IntervalTypeContext ctx)
    {
      setType(PreType.INTERVAL);
      return visitChildren(ctx);
    }
    @Override
    public PredefinedType visitLength(SqlParser.LengthContext ctx)
    {
      setLength(Integer.parseInt(ctx.getText()));
      return PredefinedType.this;
    }
    @Override
    public PredefinedType visitPrecision(SqlParser.PrecisionContext ctx)
    {
      setPrecision(Integer.parseInt(ctx.getText()));
      return PredefinedType.this;
    }
    @Override
    public PredefinedType visitScale(SqlParser.ScaleContext ctx)
    {
      setScale(Integer.parseInt(ctx.getText()));
      return PredefinedType.this;
    }
    @Override
    public PredefinedType visitSecondsDecimals(SqlParser.SecondsDecimalsContext ctx)
    {
      setSecondsDecimals(Integer.parseInt(ctx.getText()));
      return PredefinedType.this;
    }
    @Override
    public PredefinedType visitIntervalQualifier(SqlParser.IntervalQualifierContext ctx)
    {
      IntervalQualifier iq = getSqlFactory().newIntervalQualifier();
      iq.parse(ctx);
      setIntervalQualifier(iq);
      return PredefinedType.this;
    }
    @Override
    public PredefinedType visitMultiplier(SqlParser.MultiplierContext ctx)
    {
      setMultiplier(getMultiplier(ctx));
      return PredefinedType.this;
    }
    @Override
    public PredefinedType visitWithOrWithoutTimeZone(SqlParser.WithOrWithoutTimeZoneContext ctx)
    {
      setWithOrWithoutTimeZone(getWithOrWithoutTimeZone(ctx));
      return PredefinedType.this;
    }
  } /* class PdtVisitor */
  /*==================================================================*/
  public static final int iUNDEFINED = -1;
  public static final int iTIME_DECIMALS_DEFAULT = 0;
  public static final int iTIMESTAMP_DECIMALS_DEFAULT = 6;

  private PdtVisitor _visitor = new PdtVisitor();
  private PdtVisitor getVisitor() { return _visitor; }
  
  private PreType _type = null;
  public PreType getType() { return _type; }
  public void setType(PreType type) { _type = type; }

  private int _iLength = iUNDEFINED;
  public int getLength() { return _iLength; }
  public void setLength(int iLength) { if (iLength != 0) _iLength = iLength; }
  
  private int _iPrecision = iUNDEFINED;
  public int getPrecision() { return _iPrecision; }
  public void setPrecision(int iPrecision) { if (iPrecision != 0) _iPrecision = iPrecision; }
  
  private int _iScale = iUNDEFINED;
  public int getScale() { return _iScale; }
  public void setScale(int iScale) { if (iScale != 0) _iScale = iScale; }
  
  private int _iSecondsDecimals = iUNDEFINED;
  public int getSecondsDecimals() { return _iSecondsDecimals; }
  public void setSecondsDecimals(int iSecondsDecimals) { if (iSecondsDecimals != 0) _iSecondsDecimals = iSecondsDecimals; }
  
  private Multiplier _multiplier = null;
  public Multiplier getMultiplier() { return _multiplier; }
  public void setMultiplier(Multiplier multiplier) { _multiplier = multiplier; }
  
  private WithOrWithoutTimeZone _wowtz = null;
  public WithOrWithoutTimeZone getWithOrWithoutTimeZone() { return _wowtz; }
  public void setWithOrWithoutTimeZone(WithOrWithoutTimeZone wowtz) { _wowtz = wowtz; }

  private IntervalQualifier _iq = null;
  public IntervalQualifier getIntervalQualifier() { return _iq; }
  public void setIntervalQualifier(IntervalQualifier iq) { _iq = iq; }
  
  /*------------------------------------------------------------------*/
  /** length field in parentheses.
   * @return length in parentheses.
   */
  protected String formatLength()
  {
    String sLength = "";
    if (getLength() != iUNDEFINED)
      sLength = sLEFT_PAREN + String.valueOf(getLength()) + sRIGHT_PAREN;
    return sLength;
  } /* formatLength */
  
  /*------------------------------------------------------------------*/
  /** LOB length (possibly qualified by K, M, or G) in parentheses. 
   * @return lob length in parentheses.
   */
  protected String formatLobLength()
  {
    String sLength = "";
    if (getLength() != iUNDEFINED)
    {
      sLength = sLEFT_PAREN + String.valueOf(getLength());
      if (getMultiplier() != null)
        sLength = sLength + getMultiplier().getKeyword();
      sLength = sLength + sRIGHT_PAREN;
    }
    return sLength;
  } /* formatLobLength */
  
  /*------------------------------------------------------------------*/
  /** precision and scale in parentheses.
   * @return scale and precision in parentheses.
   */
  protected String formatPrecisionScale()
  {
    String sPrecisionScale = "";
    if (getPrecision() != iUNDEFINED)
    {
      sPrecisionScale = sLEFT_PAREN + String.valueOf(getPrecision());
      if (getScale() != iUNDEFINED)
        sPrecisionScale = sPrecisionScale + sCOMMA + sSP + String.valueOf(getScale());
      sPrecisionScale = sPrecisionScale + sRIGHT_PAREN;
    }
    return sPrecisionScale;
  } /* formatPrecisionScale */
  
  /*------------------------------------------------------------------*/
  /** decimals of seconds in parentheses.
   * @param iDefaultDecimals default decimals.
   * @return seconds decimals in parentheses.
   */
  protected String formatSecondsDecimals(int iDefaultDecimals)
  {
    String sSecondsDecimals = "";
    if (getSecondsDecimals() != iUNDEFINED)
    {
      if (getSecondsDecimals() != iDefaultDecimals)
        sSecondsDecimals = sSecondsDecimals + sLEFT_PAREN + String.valueOf(getSecondsDecimals()) + sRIGHT_PAREN;
    }
    return sSecondsDecimals;
  } /* formatSecondsDecimals */
  
  /*------------------------------------------------------------------*/
  /** Add TIME ZONE to type.
   * @return type with TIME ZONE.
   */
  protected String formatTimeZone()
  {
    String sTimeZone = "";
    if (getWithOrWithoutTimeZone() != null)
      sTimeZone = sSP + getWithOrWithoutTimeZone().getKeywords();
    return sTimeZone;
  } /* formatTimeZone */

  /*------------------------------------------------------------------*/
  /** format the predefined data type.
   * @return the SQL string corresponding to the fields of the data type.
   */
  @Override
  public String format()
  {
    String sType = null;
    if (getType() != null)
    {
      sType = getType().getKeyword();
      if ((getType() == PreType.CHAR) ||
          (getType() == PreType.VARCHAR) ||
          (getType() == PreType.NCHAR) ||
          (getType() == PreType.NVARCHAR) ||
          (getType() == PreType.BINARY) ||
          (getType() == PreType.VARBINARY))
        sType = sType + formatLength();
      else if ((getType() == PreType.CLOB) ||
          (getType() == PreType.NCLOB) ||
          (getType() == PreType.BLOB))
        sType = sType + formatLobLength();
      else if ((getType() == PreType.NUMERIC) ||
          (getType() == PreType.DECIMAL) ||
          (getType() == PreType.FLOAT))
        sType = sType + formatPrecisionScale();
      else if (getType() == PreType.TIME)
        sType = sType + formatSecondsDecimals(iTIME_DECIMALS_DEFAULT) + formatTimeZone();
      else if (getType() == PreType.TIMESTAMP)
        sType = sType + formatSecondsDecimals(iTIMESTAMP_DECIMALS_DEFAULT) + formatTimeZone();
      else if (getType() == PreType.INTERVAL)
        sType = sType + sSP + getIntervalQualifier().format();
   }
    return sType;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the predefined type from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.PredefinedTypeContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
  
  /*------------------------------------------------------------------*/
  /** parse the predefined type from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    SqlParser.PredefinedTypeContext ctx = null; 
    try { ctx = getParser().predefinedType(); }
    catch(Exception e)
    {
      setParser(newSqlParser2(sSql));
      ctx = getParser().predefinedType();
    }
    parse(ctx);
  } /* parse */

  /*------------------------------------------------------------------*/
  /** initialization for predefined type CHAR
   * @param iLength length.
   */
  public void initCharType(int iLength)
  {
    initialize(PreType.CHAR, iLength, null, iUNDEFINED, iUNDEFINED, iUNDEFINED, null, null);
  } /* initCharType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type VARCHAR
   * @param iLength length.
   */
  public void initVarCharType(int iLength)
  {
    initialize(PreType.VARCHAR, iLength, null, iUNDEFINED, iUNDEFINED, iUNDEFINED, null, null);
  } /* initVarCharType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type CLOB
   * @param iLength length.
   * @param multiplier multiplier for length (or null).
   */
  public void initClobType(int iLength, Multiplier multiplier)
  {
    initialize(PreType.CLOB, iLength, multiplier, iUNDEFINED, iUNDEFINED, iUNDEFINED, null, null);
  } /* initClobType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type NCHAR
   * @param iLength length.
   */
  public void initNCharType(int iLength)
  {
    initialize(PreType.NCHAR, iLength, null, iUNDEFINED, iUNDEFINED, iUNDEFINED, null, null);
  } /* initNCharType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type NVARCHAR.
   * @param iLength length.
   */
  public void initNVarCharType(int iLength)
  {
    initialize(PreType.NVARCHAR, iLength, null, iUNDEFINED, iUNDEFINED, iUNDEFINED, null, null);
  } /* initNVarCharType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type NCLOB.
   * @param iLength length.
   * @param multiplier multiplier for length (or null).
   */
  public void initNClobType(int iLength, Multiplier multiplier)
  {
    initialize(PreType.NCLOB, iLength, multiplier, iUNDEFINED, iUNDEFINED, iUNDEFINED, null, null);
  } /* initNClobType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type XML.
   */
  public void initXmlType()
  {
    initialize(PreType.XML, iUNDEFINED, null, iUNDEFINED, iUNDEFINED, iUNDEFINED, null, null);
  } /* initXmlType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type BINARY
   * @param iLength length.
   */
  public void initBinaryType(int iLength)
  {
    initialize(PreType.BINARY, iLength, null, iUNDEFINED, iUNDEFINED, iUNDEFINED, null, null);
  } /* initBinaryType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type CHAR
   * @param iLength length.
   */
  public void initVarbinaryType(int iLength)
  {
    initialize(PreType.VARBINARY, iLength, null, iUNDEFINED, iUNDEFINED, iUNDEFINED, null, null);
  } /* initVarbinaryType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type BLOB.
   * @param iLength length.
   * @param multiplier multiplier for length (or null).
   */
  public void initBlobType(int iLength, Multiplier multiplier)
  {
    initialize(PreType.BLOB, iLength, multiplier, iUNDEFINED, iUNDEFINED, iUNDEFINED, null, null);
  } /* initBlobType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type NUMERIC
   * @param iPrecision number of digits and sign.
   * @param iScale number of decimal digits to the right of the decimal point.
   */
  public void initNumericType(int iPrecision, int iScale)
  {
    initialize(PreType.NUMERIC, iUNDEFINED, null, iPrecision, iScale, iUNDEFINED, null, null);
  } /* initNumericType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type DECIMAL
   * @param iPrecision number of digits and sign.
   * @param iScale number of decimal digits to the right of the decimal point.
   */
  public void initDecimalType(int iPrecision, int iScale)
  {
    initialize(PreType.DECIMAL, iUNDEFINED, null, iPrecision, iScale, iUNDEFINED, null, null);
  } /* initDecimalType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type SMALLINT.
   */
  public void initSmallIntType()
  {
    initialize(PreType.SMALLINT, iUNDEFINED, null, iUNDEFINED, iUNDEFINED, iUNDEFINED, null, null);
  } /* initSmallIntType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type INTEGER.
   */
  public void initIntegerType()
  {
    initialize(PreType.INTEGER, iUNDEFINED, null, iUNDEFINED, iUNDEFINED, iUNDEFINED, null, null);
  } /* initIntegerType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type BIGINT.
   */
  public void initBigIntType()
  {
    initialize(PreType.BIGINT, iUNDEFINED, null, iUNDEFINED, iUNDEFINED, iUNDEFINED, null, null);
  } /* initBigIntType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type FLOAT
   * @param iPrecision minimum number of binary digits.
   */
  public void initFloatType(int iPrecision)
  {
    initialize(PreType.FLOAT, iUNDEFINED, null, iPrecision, iUNDEFINED, iUNDEFINED, null, null);
  } /* initFloatType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type REAL.
   */
  public void initRealType()
  {
    initialize(PreType.REAL, iUNDEFINED, null, iUNDEFINED, iUNDEFINED, iUNDEFINED, null, null);
  } /* initRealType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type DOUBLE PRECISION.
   */
  public void initDoubleType()
  {
    initialize(PreType.DOUBLE, iUNDEFINED, null, iUNDEFINED, iUNDEFINED, iUNDEFINED, null, null);
  } /* initDoubleType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type BOOLEAN.
   */
  public void initBooleanType()
  {
    initialize(PreType.BOOLEAN, iUNDEFINED, null, iUNDEFINED, iUNDEFINED, iUNDEFINED, null, null);
  } /* initBooleanType */

  /*------------------------------------------------------------------*/
  /** initialization for predefined type DATALINK.
   */
  public void initDatalinkType()
  {
    initialize(PreType.DATALINK, iUNDEFINED, null, iUNDEFINED, iUNDEFINED, iUNDEFINED, null, null);
  } /* initDatalinkType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type DATE.
   */
  public void initDateType()
  {
    initialize(PreType.DATE, iUNDEFINED, null, iUNDEFINED, iUNDEFINED, iUNDEFINED, null, null);
  } /* initDateType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type TIME.
   * @param iSecondsDecimals number decimals for seconds.
   * @param wowtz WITH/WITHOUT TIME ZONE.  
   */
  public void initTimeType(int iSecondsDecimals, WithOrWithoutTimeZone wowtz)
  {
    initialize(PreType.TIME, iUNDEFINED, null, iUNDEFINED, iUNDEFINED, iSecondsDecimals, wowtz, null);
  } /* initTimeType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type TIMESTAMP.
   * @param iSecondsDecimals number decimals for seconds.
   * @param wowtz WITH/WITHOUT TIME ZONE.  
   */
  public void initTimestampType(int iSecondsDecimals, WithOrWithoutTimeZone wowtz)
  {
    initialize(PreType.TIMESTAMP, iUNDEFINED, null, iUNDEFINED, iUNDEFINED, iSecondsDecimals, wowtz, null);
  } /* initTimestampType */
  
  /*------------------------------------------------------------------*/
  /** initialization for predefined type INTERVAL.
   * @param ifStart interval start field.
   * @param ifEnd interval end field.
   * @param iPrecision  maximum digits of start field.
   * @param iSecondsDecimals maximum number of decimals for seconds.
   */
  public void initIntervalType(IntervalField ifStart, IntervalField ifEnd, 
    int iPrecision, int iSecondsDecimals)
  {
    IntervalQualifier iq = getSqlFactory().newIntervalQualifier();
    iq.initialize(ifStart, ifEnd, iPrecision, iSecondsDecimals);
    initialize(PreType.INTERVAL, iUNDEFINED, null, iUNDEFINED, iUNDEFINED, iUNDEFINED, null, iq);
  } /* initIntervalType */
  
  /*------------------------------------------------------------------*/
  /** initialize a predefined type.
   * @param pretype predefined data type name.
   * @param iLength length of the data type.
   * @param multiplier length multiplier (K, M, G) if it is a LOB type.
   * @param iPrecision precision (number of digits) of numeric data type.
   * @param iScale scale (number of decimals) of numeric data type.
   * @param iSecondsDecimals number of decimals of seconds in intervale type.
   * @param wowtz with or without time zone indicator.
   * @param iq interval qualifier.
   */
  public void initialize(
    PreType pretype, int iLength, Multiplier multiplier,
    int iPrecision, int iScale, int iSecondsDecimals,
    WithOrWithoutTimeZone wowtz, IntervalQualifier iq)
  {
    _il.enter(pretype, String.valueOf(iLength), multiplier,
      String.valueOf(iPrecision), String.valueOf(iScale), String.valueOf(iSecondsDecimals),
      wowtz, iq);
    setType(pretype);
    setLength(iLength);
    setMultiplier(multiplier);
    setPrecision(iPrecision);
    setScale(iScale);
    setSecondsDecimals(iSecondsDecimals);
    setWithOrWithoutTimeZone(wowtz);
    setIntervalQualifier(iq);
    _il.exit();
  } /* initialize */

  /*------------------------------------------------------------------*/
  /** initialize datatype from java.sql.Types data type and length and scale.
   * @param iDataType java.sql.Types data type.
   * @param lPrecision length/precision.
   * @param iScale scale.
   */
  public void initialize(int iDataType, long lPrecision, int iScale)
  {
    long lGiga = (long)Integer.MAX_VALUE+1l;
    int iPrecision = iUNDEFINED;
    int iLength = iUNDEFINED;
    Multiplier multiplier = null;
    if (lPrecision < lGiga)
    {
      if (lPrecision > 0)
      {
        iPrecision = (int)lPrecision;
        iLength = iPrecision;
      }
    }
    else
    {
      long lLength = (lPrecision+lGiga-1)/lGiga;
      iLength= (int)lLength;
      multiplier = Multiplier.G;
    }
    if (iScale < 0)
      iScale = iUNDEFINED;
    int iSecondsDecimals = iScale;
    if (iDataType != Types.OTHER)
    {
      PreType pt = PreType.getBySqlType(iDataType);
      initialize(pt, iLength, multiplier, iPrecision, iScale, iSecondsDecimals, null, null);
    }
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public PredefinedType(SqlFactory sf)
  {
    super(sf);
  } /* constructor */

} /* class PredefinedType */
