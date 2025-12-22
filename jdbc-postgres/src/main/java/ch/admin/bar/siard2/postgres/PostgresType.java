/*======================================================================
PostgresType enum lists all native predefined Postgres data types we 
expect to encounter as types of columns in database tables.
Application : SIARD2
Description : PostgresType enum lists all native predefined Postgres data types.
Platform    : Java 17
------------------------------------------------------------------------
Copyright  : 2019, Swiss Federal Archives, Berne, Switzerland
License    : CDDL 1.0
Created    : 29.07.2017, Hartwig Thomas, Enter AG, Rüti ZH, Switzerland
======================================================================*/
package ch.admin.bar.siard2.postgres;

import java.util.*;
import ch.enterag.sqlparser.datatype.enums.*;

/*====================================================================*/
/** PostgresType enum lists all native predefined Postgres data types.
 * See also: https://www.postgresql.org/docs/11/datatype.html
 * @author Hartwig Thomas
 */
public enum PostgresType
{
  INTEGER(PreType.INTEGER,"int4","integer", "int"),
  SMALLINT(PreType.SMALLINT,"int2","smallint"),
  BIGINT(PreType.BIGINT,"int8","bigint"),
  OID(PreType.BIGINT, "oid"),
  SERIAL(PreType.INTEGER,"serial4","serial"),
  SMALLSERIAL(PreType.SMALLINT,"serial2","smallserial"),
  BIGSERIAL(PreType.BIGINT,"serial8","bigserial"),
  MONEY(PreType.DECIMAL, "money"),
  NUMERIC(PreType.NUMERIC,"numeric","decimal"),
  DOUBLE(PreType.DOUBLE,"float8","double precision"),
  REAL(PreType.REAL,"float4","real"),
  BOOLEAN(PreType.BOOLEAN,"bool","boolean"),
  DATE(PreType.DATE, "date"),
  TIME(PreType.TIME,"time without time zone","time"),
  TIMETZ(PreType.TIME, "time with time zone", "timetz"),
  TIMESTAMP(PreType.TIMESTAMP, "timestamp without time zone", "timestamp"),
  TIMESTAMPTZ(PreType.TIMESTAMP, "timestamp with time zone", "timestamptz"),
  INTERVAL(PreType.INTERVAL, "interval"),
  CHAR(PreType.CHAR, "char","character","bpchar"),
  VARCHAR(PreType.VARCHAR,"varchar","character varying"),
  TEXT(PreType.VARCHAR, "text"),
  JSON(PreType.VARCHAR, "json"),
  JSONB(PreType.VARCHAR, "jsonb"),
  XML(PreType.XML, "xml"),
  TSVECTOR(PreType.VARCHAR, "tsvector"), // // https://www.postgresql.org/docs/11/datatype-textsearch.html
  TSQUERY(PreType.VARCHAR, "tsquery"), // https://www.postgresql.org/docs/11/datatype-textsearch.html
  BIT(PreType.BINARY, "bit"),
  VARBIT(PreType.VARBINARY, "varbit", "bit varying"),
  BYTEA(PreType.VARBINARY, "bytea"),
  UUID(PreType.BINARY, "uuid"), // length 16
  MACADDR(PreType.BINARY, "macaddr"), // length 6
  MACADDR8(PreType.BINARY, "macaddr8"), // length 8
  POINT(PreType.VARCHAR, "point"),
  LINE(PreType.VARCHAR, "line"),
  LSEG(PreType.VARCHAR, "lseg"),
  BOX(PreType.VARCHAR, "box"),
  PATH(PreType.VARCHAR, "path"),
  POLYGON(PreType.VARCHAR, "polygon"),
  CIRCLE(PreType.VARCHAR, "circle"),
  CIDR(PreType.VARCHAR, "cidr"), // https://www.postgresql.org/docs/11/datatype-net-types.html#DATATYPE-CIDR
  INET(PreType.VARBINARY, "inet"), // length 4 (IPv4) or 16 (IPv6)
  NAME(PreType.VARCHAR, "name"), // length 63
  TXID(PreType.VARCHAR, "txid_snapshot"), // https://www.postgresql.org/docs/11/functions-info.html#FUNCTIONS-TXID-SNAPSHOT-PARTS
  BLOB(PreType.BLOB, "blob"), // domain in schema public based on oid created by PostgresConnection
  CLOB(PreType.CLOB, "clob"); // domain in schema public based on oid created by PostgresConnection
  
  /* https://www.postgresql.org/docs/11/rangetypes.html */
  public static Set<String>  setBUILTIN_RANGES = new HashSet<String>(Arrays.asList(new String[] 
  {
    "int4range",
    "int8range",
    "numrange",
    "tsrange",
    "tstzrange",
    "daterange"
  }));
  
  private String _sKeyword = null;
  public String getKeyword() { return _sKeyword; }
  private Set<String> _setAliases = null;
  public Set<String> getAliases() { return _setAliases; }
  private PreType _pt = null;
  public PreType getPreType() { return _pt; }
  private PostgresType(PreType pt, String sKeyword, String... asAliases)
  {
    _sKeyword = sKeyword;
    _setAliases = new HashSet<String>(Arrays.asList(asAliases));
    _pt = pt;
  } /* constructor */
  public static PostgresType getByKeyword(String sKeyword)
  {
    PostgresType pgt  = null;
    for (int i = 0; (pgt == null) && (i < PostgresType.values().length); i++)
    {
      PostgresType dt = PostgresType.values()[i];
      if (sKeyword.equals(dt.getKeyword()) || dt.getAliases().contains(sKeyword))
        pgt = dt;
    }
    return pgt;
  } /* getByKeyword */
  public static PostgresType getByPreType(PreType pt)
  {
    PostgresType pgt = null;
    switch(pt)
    {
      case CHAR:
      case NCHAR:
        pgt = CHAR; break;
      case VARCHAR:
      case NVARCHAR:
        pgt = VARCHAR; break;
      case CLOB:
      case NCLOB:
        pgt = CLOB; break;
      case XML: pgt = XML; break;
      case BINARY:
      case VARBINARY:
        pgt = BYTEA; break;
      case BLOB:
      case DATALINK:
        pgt = BLOB; break;
      case NUMERIC:
      case DECIMAL:
        pgt = NUMERIC; break;
      case SMALLINT: pgt = SMALLINT; break;
      case INTEGER: pgt = INTEGER; break;
      case BIGINT: pgt = BIGINT; break;
      case FLOAT:
      case DOUBLE:
        pgt = DOUBLE; break;
      case REAL: pgt = REAL; break;
      case BOOLEAN: pgt = BOOLEAN; break;
      case DATE: pgt = DATE; break;
      case TIME: pgt = TIME; break;
      case TIMESTAMP: pgt = TIMESTAMP; break;
      case INTERVAL: pgt = INTERVAL; break;
    }
    return pgt;
  } /* getByPreType */
} /* enum PostgresType */
