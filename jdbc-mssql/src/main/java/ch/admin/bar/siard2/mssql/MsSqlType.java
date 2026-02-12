package ch.admin.bar.siard2.mssql;

public enum MsSqlType
{
  IMAGE("image",34),
  TEXT("text",35),
  UUID("uniqueidentifier",36),
  DATE("date",40),
  TIME("time",41),
  SMALLDATETIME("smalldatetime",58),
  DATETIME("datetime",61),
  DATETIME2("datetime2",42),
  DATETIMEOFFSET("datetimeoffset",43),
  TINYINT("tinyint",48),
  SMALLINT("smallint",52),
  INTEGER("int",56),
  REAL("real",59),
  FLOAT("float",62),
  DECIMAL("decimal",106),
  NUMERIC("decimal",108),
  SMALLMONEY("smallmoney",122),
  MONEY("money",60),
  SQL_VARIANT("sql_variant",98),
  NTEXT("ntext",99),
  BIT("bit",104),
  BIGINT("bigint",127),
  VARBINARY("varbinary",165),
  VARCHAR("varchar",167),
  BINARY("binary",173),
  CHAR("char",175),
  TIMESTAMP("timestamp",189),
  NVARCHAR("nvarchar",231),
  NCHAR("nchar",239),
  CLRUDT("clrudt",240),
  XML("xml",241),
  TABLEUDT("tableudt",243);
  
  private String _sName = null;
  public String getName() { return _sName; }
  private int _iSystemTypeId = -1;
  public int getSystemTypeId() { return _iSystemTypeId; }
  private MsSqlType(String sName, int iSystemTypeId)
  { 
    _sName = sName;
    _iSystemTypeId = iSystemTypeId;
  }
  public static MsSqlType getBySystemTypeId(int iSystemTypeId)
  {
    MsSqlType mstResult = null;
    for (int i = 0; (mstResult == null) && (i < MsSqlType.values().length); i++)
    {
      MsSqlType mst = MsSqlType.values()[i];
      if (mst.getSystemTypeId() == iSystemTypeId)
        mstResult = mst;
    }
    return mstResult;
  } /* getBySystemTypeId */
} /* MsSqlType */
