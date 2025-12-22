package ch.admin.bar.siard2.postgres;

import java.math.*;
import java.sql.*;
import java.sql.Date;
import java.text.*;
import java.util.*;
import javax.xml.datatype.*;
import ch.admin.bar.siard2.ColumnIdentifier;
import org.postgresql.jdbc.*;
import org.postgresql.util.*;
import org.postgresql.core.*;

import ch.enterag.utils.database.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.enums.PreType;
import ch.enterag.sqlparser.expression.enums.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.admin.bar.siard2.postgres.identifier.*;

public class PostgresObject
{
  private static char cCOMMA = ',';
  private String _sValue = null;
  private Object _o = null;
  public Object getObject() { return _o; }
  public void setObject(Object o) { _o = o; }
  public String getValue() { return _sValue; }
  private PostgresQualifiedId _qiType = null;
  private String _sBaseType = null;
  private PostgresConnection _pconn = null;
  private String _sIndent = null;
  
  private class TypeDescription
  {
    private int _iDataType;
    public int getDataType() { return _iDataType; }
    private String _sTypeName;
    public String getTypeName() { return _sTypeName; } 
    public TypeDescription(int iDataType, String sTypeName)
    {
      _iDataType = iDataType;
      _sTypeName = sTypeName;
    }
  } /* class AttributeDescription */
  
  public static String stripQuotes(String s)
    throws ParseException
  {
    if (s != null)
    {
      if (s.length() > 1)
      {
        int l = s.length();
        String sQuote = s.substring(l-1,l);
        if (sQuote.equals("\"") && sQuote.equals(s.substring(0,1)))
        {
          s = s.substring(1,s.length()-1).
            replace("\"\"","\""); // doubled double quotes
          s = s.
            replace("\\t","\t"). // TAB
            replace("\\b","\b"). // BACKSPACE
            replace("\\n","\n"). // NEW LINE
            replace("\\r","\r"). // CARRIAGE RETURN
            replace("\\f","\f"). // FORM FEED
            replace("\\\\","\\"); // escaped escape
        }
      }
    }
    return s;
  } /* stripQuotes */
  
  public static String addQuotes(String s)
  {
    String sQuote = "\"";
    if (s != null)
    {
      s = s.
        replace("\\","\\\\"). // espace escape
        replace("\t","\\t"). // TAB
        replace("\b","\\b"). // BACKSPACE
        replace("\n","\\n"). // NEW LINE
        replace("\r","\\r"). // CARRIAGE RETURN
        replace("\f","\\f"); // FORM FEED
      if (sQuote.equals("\""))
        s = s.replace("\"", "\\\""); // escape double quotes
      s = sQuote + s + sQuote;
    }
    return s;
  } /* addQuotes */
  
  private String getBaseType(String sTypeName)
  {
    int iArray = sTypeName.lastIndexOf("ARRAY");
    if (iArray >= 0)
      sTypeName = sTypeName.substring(0,iArray).trim();
    int iParen = sTypeName.indexOf("(");
    if (iParen >= 0)
      sTypeName = sTypeName.substring(0,iParen).trim();
    return sTypeName;
  }
  
  public PostgresObject(String sValue, int iDataType, String sTypeName, PostgresConnection pconn, String sIndent)
    throws ParseException, SQLException
  {
    _sValue = sValue; 
    _sIndent = sIndent;
    //System.out.println(sIndent + _sValue);
    if ((iDataType == Types.STRUCT) || (iDataType == Types.DISTINCT))
    {
      _qiType = new PostgresQualifiedId(sTypeName);
      if (_qiType.getSchema() == null)
        _qiType.setSchema("pg_catalog");
    }
    else if (iDataType == Types.ARRAY)
      _sBaseType = getBaseType(sTypeName);
    _pconn = pconn;
    _o = fromString(sValue,iDataType);
  } /* constructor */
  
  public PostgresObject(Object o, int iDataType, String sTypeName, PostgresConnection pconn, String sIndent)
    throws ParseException, SQLException
  {
    _o = o;
    _sIndent = sIndent;
    if ((iDataType == Types.STRUCT) || (iDataType == Types.DISTINCT))
    {
      _qiType = new PostgresQualifiedId(sTypeName);
      if (_qiType.getSchema() == null)
        _qiType.setSchema("pg_catalog");
    }
    else if (iDataType == Types.ARRAY)
      _sBaseType = getBaseType(sTypeName);
    _pconn = pconn;
    _sValue = fromObject(o,iDataType);
    //System.out.println(sIndent + _sValue);
  }
  
  private List<TypeDescription> getAttributeDescriptions()
    throws SQLException, ParseException
  {
    PostgresDatabaseMetaData pdmd = (PostgresDatabaseMetaData)_pconn.getMetaData();
    List<TypeDescription> listAttributeDescription = new ArrayList<TypeDescription>();
    ResultSet rsAttributes = pdmd.getAttributes(
      pdmd.toPattern(_qiType.getCatalog()),
      pdmd.toPattern(_qiType.getSchema()),
      pdmd.toPattern(_qiType.getName()),
      "%");
    while (rsAttributes.next())
    {
      int iDataType = rsAttributes.getInt("DATA_TYPE");
      String sAttributeTypeName = rsAttributes.getString("ATTR_TYPE_NAME");
      if ((iDataType == Types.OTHER) && (PostgresType.INTERVAL.getKeyword().equals(sAttributeTypeName)))
      {
        ColumnIdentifier columnIdentifier = new ColumnIdentifier(rsAttributes.getString("TYPE_SCHEM"), rsAttributes.getString("TYPE_NAME"), rsAttributes.getString("ATTR_NAME"));
        sAttributeTypeName = PostgresMetaColumns.getIntervalTypeName(_pconn, columnIdentifier, sAttributeTypeName);
      }
      TypeDescription ad = new TypeDescription(
        iDataType,
        sAttributeTypeName);
      // String sAttributeName = rsAttributes.getString("ATTR_NAME");
      // System.out.println(sAttributeName+": "+String.valueOf(ad.getDataType())+" "+ad.getTypeName());
      listAttributeDescription.add(ad);
    }
    rsAttributes.close();
    return listAttributeDescription;
  } /* getAttributeDescriptions */
  
  private TypeDescription getBaseTypeDescription()
    throws SQLException
  {
    TypeDescription tdBase = null; 
    PostgresDatabaseMetaData pdmd = (PostgresDatabaseMetaData)_pconn.getMetaData();
    ResultSet rsUdt = pdmd.getUDTs(
      pdmd.toPattern(_qiType.getCatalog()),
      pdmd.toPattern(_qiType.getSchema()),
      pdmd.toPattern(_qiType.getName()),
      new int[] {Types.DISTINCT});
    if (rsUdt.next())
    {
      int iBaseType = rsUdt.getInt("BASE_TYPE");
      String sBaseType = null;
      PreType pt = PreType.getBySqlType(iBaseType);
      if (pt != null)
        sBaseType = pt.getKeyword();
      tdBase = new TypeDescription(iBaseType,sBaseType);
    }
    return tdBase;
  }

  private String formatStruct(Struct struct)
    throws SQLException, ParseException
  {
    StringBuilder sb = new StringBuilder();
    List<TypeDescription> listAttributeDescriptions = getAttributeDescriptions();
    for (int iAttribute = 0; iAttribute < struct.getAttributes().length; iAttribute++)
    {
      if (iAttribute > 0)
        sb.append(",");
      Object o = struct.getAttributes()[iAttribute];
      if (o != null)
      {
        TypeDescription ad = listAttributeDescriptions.get(iAttribute);
        PostgresObject po = new PostgresObject(o,ad.getDataType(),ad.getTypeName(),_pconn, _sIndent+"  ");
        String s = po.getValue();
        if (ad.getDataType() == Types.STRUCT)
          s = addQuotes(s);
        sb.append(s);
      }
    }
    // add parentheses
    sb.insert(0, "(");
    sb.append(")");
    return sb.toString();
  } /* formatStruct */
  
  private String formatArray(Array array)
    throws SQLException, ParseException
  {
    StringBuilder sb = new StringBuilder();
    PreType pt = PreType.getByKeyword(_sBaseType);
    int iDataType = Types.NULL;
    if (pt != null)
      iDataType = pt.getSqlType();
    Object[] aoElement = (Object[])array.getArray();
    for (int iElement = 0; iElement < aoElement.length; iElement++)
    {
      if (iElement > 0)
        sb.append(",");
      Object oElement = aoElement[iElement];
      if (oElement != null)
      {
        PostgresObject po = new PostgresObject(oElement, iDataType, _sBaseType, _pconn, _sIndent+"  ");
        sb.append(po.getValue());
      }
    }
    // add parentheses
    sb.insert(0, "{");
    sb.append("}");
    return sb.toString();
  }
  
  private String fromObject(Object o, int iDataType)
    throws SQLException, ParseException
  {
    String sValue = null;
    switch(iDataType)
    {
      case Types.CHAR:
      case Types.VARCHAR:
      case Types.NCHAR:
      case Types.NVARCHAR:
        String s = (String)o;
        sValue = addQuotes(s);
        break;
      case Types.CLOB:
        PostgresClob clob = (PostgresClob)o;
        sValue = String.valueOf(clob.getOid());
        break;
      case Types.NCLOB:
        PostgresNClob nclob = (PostgresNClob)o;
        sValue = String.valueOf(nclob.getOid());
        break;
      case Types.SQLXML:
        PgSQLXML sqlxml = (PgSQLXML)o;
        sValue = addQuotes(sqlxml.getString());
        break;
      case Types.BINARY:
      case Types.VARBINARY:
        byte[] buf = (byte[])o;
        sValue = PostgresLiterals.formatBytesLiteral(buf);
        sValue = addQuotes(sValue.substring(1,sValue.length()-1));
        break;
      case Types.BLOB:
      case Types.DATALINK:
        PostgresBlob blob = (PostgresBlob)o;
        sValue = String.valueOf(blob.getOid());
        break;
      case Types.NUMERIC:
      case Types.DECIMAL:
        BigDecimal bd = (BigDecimal)o;
        sValue = PostgresLiterals.formatExactLiteral(bd);
        break;
      case Types.SMALLINT:
      case Types.INTEGER:
      case Types.BIGINT:
        bd = null;
        if (o instanceof Short)
        {
          Short sh = (Short)o;
          bd = BigDecimal.valueOf(sh);
        }
        else if (o instanceof Integer)
        {
          Integer i = (Integer)o;
          bd = BigDecimal.valueOf(i);
        }
        else if (o instanceof Long)
        {
          Long l = (Long)o;
          bd = BigDecimal.valueOf(l);
        }
        else if (o instanceof BigInteger)
        {
          BigInteger bi = (BigInteger)o;
          bd = new BigDecimal(bi);
        }
        else if (o instanceof BigDecimal)
          bd = (BigDecimal)o;
        if (o != null)
          sValue = PostgresLiterals.formatExactLiteral(bd);
        break;
      case Types.DOUBLE:
        if (o instanceof Float)
        {
          Float f = (Float)o;
          sValue = String.valueOf(f.doubleValue());
        }
        else
        {
          Double d = (Double)o;
          sValue = String.valueOf(d.doubleValue());
        }
        break;
      case Types.REAL:
        Float f = (Float)o;
        sValue = String.valueOf(f.floatValue());
        break;
      case Types.BOOLEAN:
        Boolean b = (Boolean)o;
        sValue = b.booleanValue()?"t":"f";
        break;
      case Types.DATE:
        Date date = (Date)o;
        sValue = stripQuotes(PostgresLiterals.formatDateLiteral(date).substring(PostgresLiterals.sDATE_LITERAL_PREFIX.length()));
        break;
      case Types.TIME:
        Time time = (Time)o;
        sValue = stripQuotes(PostgresLiterals.formatTimeLiteral(time).substring(PostgresLiterals.sTIME_LITERAL_PREFIX.length()));
        break;
      case Types.TIMESTAMP:
        Timestamp ts = (Timestamp)o;
        sValue = PostgresLiterals.formatTimestampLiteral(ts).substring(PostgresLiterals.sTIMESTAMP_LITERAL_PREFIX.length()).replace("'","\"");
        break;
      case Types.OTHER:
        Interval iv = null;
        if (o instanceof Interval)
          iv = (Interval)o;
        else
        {
          Duration duration = (Duration)o;
          iv = Interval.fromDuration(duration);
        }
        StringBuilder sb = new StringBuilder();
        if (iv.getYears() > 0)
        {
          sb.append(String.valueOf(iv.getYears()));
          sb.append(" year");
          if (iv.getYears() > 1)
            sb.append("s");
        }
        if (iv.getMonths() > 0)
        {
          if (sb.length() > 0)
            sb.append(" ");
          sb.append(String.valueOf(iv.getMonths()));
          sb.append(" mon");
          if (iv.getMonths() > 1)
            sb.append("s");
        }
        if (iv.getDays() > 0)
        {
          if (sb.length() > 0)
            sb.append(" ");
          sb.append(String.valueOf(iv.getDays()));
          sb.append(" day");
          if (iv.getDays() > 1)
            sb.append("s");
        }
        if ((iv.getHours() > 0) || (iv.getMinutes() > 0) || (iv.getSeconds() > 0) || (iv.getNanoSeconds() > 0))
        {
          if (sb.length() > 0)
            sb.append(" ");
          s = String.valueOf(iv.getHours());
          if (s.length() == 1)
            sb.append(0);
          sb.append(s);
          sb.append(":");
          s = String.valueOf(iv.getMinutes());
          if (s.length() == 1)
            sb.append("0");
          sb.append(s);
          sb.append(":");
          s = String.valueOf(iv.getSeconds());
          if (s.length() == 1)
            sb.append("0");
          sb.append(s);
          sb.append(".");
          s = String.valueOf(iv.getNanoSeconds()/1000);
          for (int i = s.length(); i < 6; i++)
            sb.append("0");
          sb.append(s);
        }
        sValue = addQuotes(sb.toString());
        break;
      case Types.STRUCT:
        Struct struct = (Struct)o;
        sValue = formatStruct(struct);
        break;
      case Types.DISTINCT:
        TypeDescription tdBase = getBaseTypeDescription();
        PostgresObject poBase = new PostgresObject(o,tdBase.getDataType(), tdBase.getTypeName(),_pconn, _sIndent + "  ");
        sValue = stripQuotes(poBase.getValue());
        break;
      case Types.ARRAY:
        Array array = (Array)o;
        sValue = formatArray(array);
        break;
    }
    return sValue;
  }
  
  private Struct parseStruct(String sToken)
    throws ParseException, SQLException
  {
    Struct struct = null;
    // strip quotes
    sToken = stripQuotes(sToken);
    // strip parentheses
    String sStartMark = sToken.substring(0,1);
    String sEndMark = sToken.substring(sToken.length()-1,sToken.length());
    
    if ((sStartMark.equals("(") || sStartMark.equals("[")) && 
        (sEndMark.equals(")") || sEndMark.equals("]")))
    {
      Object[] ao = null;
      List<TypeDescription> listAttributeDescriptions = getAttributeDescriptions();
      sToken = sToken.substring(1,sToken.length()-1);
      PGtokenizer ptAttributes = new PGtokenizer(sToken, cCOMMA);
      if (ptAttributes.getSize() == listAttributeDescriptions.size())
      {
        ao = new Object[ptAttributes.getSize()];
        for (int iAttribute = 0; iAttribute < ptAttributes.getSize(); iAttribute++)
        {
          String sAttribute = ptAttributes.getToken(iAttribute);
          TypeDescription ad = listAttributeDescriptions.get(iAttribute);
          if (sAttribute.length() > 0) // zero length strings cannot be tokenized - they represent NULL
          {
            PostgresObject po = new PostgresObject(sAttribute,ad.getDataType(),ad.getTypeName(),_pconn, _sIndent + "  ");
            ao[iAttribute] = po.getObject();
          }
          else
            ao[iAttribute] = null;
        }
      }
      else if ((ptAttributes.getSize() == 2) && (listAttributeDescriptions.size() == 3))
      {
        ao = new Object[3];
        
        String sAttributeStart = ptAttributes.getToken(0);
        TypeDescription adStart = listAttributeDescriptions.get(0);
        PostgresObject poStart = new PostgresObject(sAttributeStart,adStart.getDataType(),adStart.getTypeName(),_pconn, _sIndent + "  ");
        ao[0] = poStart.getObject();
        
        String sAttributeEnd = ptAttributes.getToken(1);
        TypeDescription adEnd = listAttributeDescriptions.get(1);
        PostgresObject poEnd = new PostgresObject(sAttributeEnd,adEnd.getDataType(),adEnd.getTypeName(),_pconn, _sIndent + "  ");
        ao[1] = poEnd.getObject();

        ao[2] = sStartMark+sEndMark;
      }
      else
        throw new ParseException("Number of attributes does not match number of tokens when parsing "+sToken+"!",0);
      struct = new PostgresStruct(_qiType.format(), ao); 
    }
    else
      throw new ParseException("STRUCT must be in parentheses!",0);
    return struct;
  } /* parseStruct */
  
  private Object fromString(String sValue, int iDataType)
    throws SQLException, ParseException
  {
    Object o;
    String sToken = sValue; 
    BaseConnection bc = (BaseConnection)_pconn.unwrap(Connection.class);
    long lOid;
    BigDecimal bd;
    switch(iDataType)
    {
      case Types.CHAR:
      case Types.VARCHAR:
      case Types.NCHAR:
      case Types.NVARCHAR:
        o = stripQuotes(sToken); 
        break;
      case Types.CLOB:
      case Types.NCLOB: 
        lOid = Long.parseLong(sToken);
        o = new PostgresClob(_pconn,lOid);
        break;
      case Types.SQLXML:
        o = new PgSQLXML(bc,stripQuotes(sToken)); 
        break;
      case Types.BINARY:
      case Types.VARBINARY:
        o = PostgresLiterals.parseBytesLiteral(stripQuotes(sToken));
        break;
      case Types.BLOB:
      case Types.DATALINK:
        lOid = Long.parseLong(sToken);
        o = new PostgresBlob(_pconn,lOid);
        break;
      case Types.NUMERIC:
      case Types.DECIMAL:
        o = PostgresLiterals.parseExactLiteral(sToken);
        break;
      case Types.SMALLINT:
        bd = PostgresLiterals.parseExactLiteral(sToken);
        o = Short.valueOf(bd.shortValue());
        break;
      case Types.INTEGER: 
        bd = PostgresLiterals.parseExactLiteral(sToken);
        o = Integer.valueOf(bd.intValue());
        break;
      case Types.BIGINT:
        bd = PostgresLiterals.parseExactLiteral(sToken);
        o = Long.valueOf(bd.longValue());
        break;
      case Types.DOUBLE:
        o = Double.valueOf(sToken);
        break;
      case Types.REAL:
        bd = PostgresLiterals.parseExactLiteral(sToken);
        o = Float.valueOf(sToken);
        break;
      case Types.BOOLEAN:
        BooleanLiteral bl = PostgresLiterals.parseBooleanLiteral(sToken);
        Boolean b = null;
        if (bl != BooleanLiteral.UNKNOWN)
          b = (bl == BooleanLiteral.TRUE)?Boolean.TRUE:Boolean.FALSE;
        o = b;
        break;
      case Types.DATE:
        o = PostgresLiterals.parseDateLiteral(sToken);
        break;
      case Types.TIME:
        o = PostgresLiterals.parseTimeLiteral(sToken);
        break;
      case Types.TIMESTAMP:
        o = PostgresLiterals.parseTimestampLiteral(stripQuotes(sToken));
        break;
      case Types.OTHER: 
        o = PostgresLiterals.parseInterval(stripQuotes(sToken)).toDuration();
        break;
      case Types.STRUCT:
          o = parseStruct(sToken);
        break;
      case Types.DISTINCT:
        sToken = stripQuotes(sToken);
        TypeDescription tdBase = getBaseTypeDescription();
        PostgresObject poBase = new PostgresObject(sToken,tdBase.getDataType(), tdBase.getTypeName(),_pconn, _sIndent + "  ");
        o = poBase.getObject();
        break;
      default:
        throw new SQLException("Invalid data type found: "+String.valueOf(iDataType)+" ("+SqlTypes.getTypeName(iDataType)+")!");
    }
    return o;
  } /* fromString */

} /* class PostgresObject */
