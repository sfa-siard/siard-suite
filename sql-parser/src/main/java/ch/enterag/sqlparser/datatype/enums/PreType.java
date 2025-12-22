package ch.enterag.sqlparser.datatype.enums;
import java.sql.*;
import java.util.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.antlr4.*;
public enum PreType
{
  CHAR(Types.CHAR, K.CHAR.getKeyword(),
    K.CHARACTER.getKeyword()),
  VARCHAR(Types.VARCHAR, K.VARCHAR.getKeyword(), 
    K.CHAR.getKeyword()+SqlBase.sSP+K.VARYING.getKeyword(), 
    K.CHARACTER.getKeyword()+SqlBase.sSP+K.VARYING.getKeyword()),
  CLOB(Types.CLOB, K.CLOB.getKeyword(),
    K.CHARACTER.getKeyword()+SqlBase.sSP+K.LARGE.getKeyword()+SqlBase.sSP+K.OBJECT.getKeyword()),
  NCHAR(Types.NCHAR, K.NCHAR.getKeyword(),
    K.NATIONAL.getKeyword()+SqlBase.sSP+K.CHAR.getKeyword(),
    K.NATIONAL.getKeyword()+SqlBase.sSP+K.CHARACTER.getKeyword()),
  NVARCHAR(Types.NVARCHAR, K.NCHAR.getKeyword()+SqlBase.sSP+K.VARYING.getKeyword(),
    K.NATIONAL.getKeyword()+SqlBase.sSP+K.CHAR.getKeyword()+SqlBase.sSP+K.VARYING.getKeyword(),
    K.NATIONAL.getKeyword()+SqlBase.sSP+K.CHARACTER.getKeyword()+SqlBase.sSP+K.VARYING.getKeyword()),
  NCLOB(Types.NCLOB, K.NCLOB.getKeyword(),
    K.NCHAR.getKeyword()+SqlBase.sSP+K.LARGE.getKeyword()+SqlBase.sSP+K.OBJECT.getKeyword(),
    K.NATIONAL.getKeyword()+SqlBase.sSP+K.CHARACTER.getKeyword()+SqlBase.sSP+K.LARGE.getKeyword()+SqlBase.sSP+K.OBJECT.getKeyword()),
  BINARY(Types.BINARY, K.BINARY.getKeyword()),
  VARBINARY(Types.VARBINARY, K.VARBINARY.getKeyword(),
    K.BINARY.getKeyword()+SqlBase.sSP+K.VARYING.getKeyword()),
  BLOB(Types.BLOB, K.BLOB.getKeyword(),
    K.BINARY.getKeyword()+SqlBase.sSP+K.LARGE.getKeyword()+SqlBase.sSP+K.OBJECT.getKeyword()),
  NUMERIC(Types.NUMERIC, K.NUMERIC.getKeyword()),
  DECIMAL(Types.DECIMAL, K.DEC.getKeyword(),
    K.DECIMAL.getKeyword()),
  SMALLINT(Types.SMALLINT, K.SMALLINT.getKeyword()),
  INTEGER(Types.INTEGER, K.INT.getKeyword(),
    K.INTEGER.getKeyword()),
  BIGINT(Types.BIGINT, K.BIGINT.getKeyword()),
  FLOAT(Types.FLOAT, K.FLOAT.getKeyword()),
  REAL(Types.REAL, K.REAL.getKeyword()),
  DOUBLE(Types.DOUBLE, K.DOUBLE.getKeyword()+" "+K.PRECISION.getKeyword()),
  BOOLEAN(Types.BOOLEAN, K.BOOLEAN.getKeyword()),
  DATE(Types.DATE, K.DATE.getKeyword()),
  TIME(Types.TIME, K.TIME.getKeyword(),
    K.TIME.getKeyword()+SqlBase.sSP+K.WITH.getKeyword()+SqlBase.sSP+K.TIME.getKeyword()+SqlBase.sSP+K.ZONE.getKeyword()),
  TIMESTAMP(Types.TIMESTAMP, K.TIMESTAMP.getKeyword(),
    K.TIMESTAMP.getKeyword()+SqlBase.sSP+K.WITH.getKeyword()+SqlBase.sSP+K.TIME.getKeyword()+SqlBase.sSP+K.ZONE.getKeyword()),
  XML(Types.SQLXML, K.XML.getKeyword()),
  INTERVAL(Types.OTHER, K.INTERVAL.getKeyword()), // INTERVAL not supported by JDBC!
  DATALINK(Types.DATALINK, K.DATALINK.getKeyword());

  private String _sKeyword = null;
  public String getKeyword() { return _sKeyword; }
  private Set<String> _setAliases = null;
  public Set<String> getAliases() { return _setAliases; }
  private int _iSqlType = Types.NULL;
  public int getSqlType() { return _iSqlType; }
  private PreType(int iSqlType, String sKeyword, String... asAliases)
  {
    _sKeyword = sKeyword;
    _setAliases = new HashSet<String>(Arrays.asList(asAliases));
    _iSqlType = iSqlType;
  } /* constructor */
  public static PreType getBySqlType(int iSqlType)
  {
    PreType datatype  = null;
    for (int i = 0; (datatype == null) && (i < PreType.values().length); i++)
    {
      PreType dt = PreType.values()[i];
      if (dt.getSqlType() == iSqlType)
        datatype = dt;
    }
    return datatype;
  } /* getBySqlType */
  public static PreType getByKeyword(String sKeyword)
  {
    PreType datatype  = null;
    for (int i = 0; (datatype == null) && (i < PreType.values().length); i++)
    {
      PreType dt = PreType.values()[i];
      if (dt.getKeyword().equals(sKeyword) || dt.getAliases().contains(sKeyword))
        datatype = dt;
    }
    return datatype;
  } /* getByKeyword */
}
