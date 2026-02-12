package ch.admin.bar.siard2.oracle;

public enum OracleType 
{
  INTERVALDS("INTERVALDS",-104),
  INTERVALYM("INTERVALYM",-103),
  TIMESTAMP_WITH_LOCAL_TIME_ZONE("TIMESTAMP WITH LOCAL TIME ZONE",-102),
  TIMESTAMP_WITH_TIME_ZONE("TIMESTAMP WITH TIME ZONE",-101),
  BIT("NUMBER",-7),
  TINYINT("NUMBER",-6),
  BIGINT("NUMBER",-5),
  LONG_RAW("LONG RAW",-4),
  RAW("RAW",-3),
  LONG("LONG",-1),
  CHAR("CHAR",1),
  NUMERIC("NUMBER",2),
  NUMBER("NUMBER", 3),
  INTEGER("NUMBER",4),
  SMALLINT("NUMBER",5),
  FLOAT("FLOAT",6),
  REAL("REAL",7),
  VARCHAR2("VARCHAR2",12),
  TIME("DATE",92),
  DATE("DATE",93),
  TIMESTAMP("TIMESTAMP",93),
  BINARY_FLOAT("TIMESTAMP",100),
  BINARY_DOUBLE("TIMESTAMP",101),
  STRUCT("STRUCT",2002),
  ARRAY("ARRAY",2003),
  BLOB("BLOB",2004),
  CLOB("CLOB",2005),
  REF("REF",2006),
  NCHAR("NCHAR", -15),
  NVARCHAR2("NVARCHAR2", -9),
  NCLOB("NCLOB", 2011),
  XMLTYPE("XMLTYPE", 2009),
  BFILE("BFILE",13),
  ANYDATA("ANYDATA",2005);
  
  private String _sName = null;
  public String getName() { return _sName; }
  private int _iSystemTypeId = -1;
  public int getSystemTypeId() { return _iSystemTypeId; }
  private OracleType(String sName, int iSystemTypeId)
  { 
    _sName = sName;
    _iSystemTypeId = iSystemTypeId;
  }
  public static OracleType getBySystemTypeId(int iSystemTypeId)
  {
	  OracleType ostResult = null;
    for (int i = 0; (ostResult == null) && (i < OracleType.values().length); i++)
    {
    	OracleType ost = OracleType.values()[i];
      if (ost.getSystemTypeId() == iSystemTypeId)
        ostResult = ost;
    }
    return ostResult;
  } /* getBySystemTypeId */
} /* OracleType */
