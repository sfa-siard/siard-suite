package ch.enterag.utils.base;

import java.math.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import ch.enterag.sqlparser.*;

public class TestColumnDefinition
{
  protected String _sName = null;
  public String getName() { return _sName; }
  protected String _sType = null;
  public String getType() { return _sType; }
  protected Object _oValue = null;
  public Object getValue() { return _oValue; }
  public void setValue(Object oValue) { _oValue = oValue; }
  public String getValueLiteral()
  {
    String sValueLiteral = "NULL";
    if (_oValue != null)
    {
      if (_oValue instanceof String)
        sValueLiteral = SqlLiterals.formatStringLiteral((String)_oValue);
      else if (_oValue instanceof Byte)
        sValueLiteral = SqlLiterals.formatBytesLiteral(new byte[] {((Byte)_oValue).byteValue()});
      else if (_oValue instanceof byte[])
        sValueLiteral = SqlLiterals.formatBytesLiteral((byte[])_oValue);
      else if (_oValue instanceof Short)
        sValueLiteral = String.valueOf((Short)_oValue);
      else if (_oValue instanceof Integer)
        sValueLiteral = String.valueOf((Integer)_oValue);
      else if (_oValue instanceof Long)
        sValueLiteral = String.valueOf((Long)_oValue);
      else if (_oValue instanceof BigInteger)
        sValueLiteral = String.valueOf((BigInteger)_oValue);
      else if (_oValue instanceof BigDecimal)
        sValueLiteral = ((BigDecimal)_oValue).toPlainString();
      else if (_oValue instanceof Float)
        sValueLiteral = ((Float)_oValue).toString();
      else if (_oValue instanceof Double)
        sValueLiteral = ((Double)_oValue).toString();
      else if (_oValue instanceof Boolean)
        sValueLiteral = String.valueOf((Boolean)_oValue);
      else if (_oValue instanceof Date)
        sValueLiteral = SqlLiterals.formatDateLiteral((Date)_oValue);
      else if (_oValue instanceof Time)
        sValueLiteral = SqlLiterals.formatTimeLiteral((Time)_oValue);
      else if (_oValue instanceof Timestamp)
        sValueLiteral = SqlLiterals.formatTimestampLiteral((Timestamp)_oValue);
      else if (_oValue instanceof Interval)
        sValueLiteral = SqlLiterals.formatIntervalLiteral((Interval)_oValue); 
      else if (_oValue instanceof List)
      {
        @SuppressWarnings("unchecked")
        List<TestColumnDefinition> listCd = (List<TestColumnDefinition>)_oValue;
        StringBuilder sbUdtValue = new StringBuilder("new ");
        sbUdtValue.append(getType());
        sbUdtValue.append("(");
        for (int iAttribute = 0; iAttribute < listCd.size(); iAttribute++)
        {
          if (iAttribute > 0)
            sbUdtValue.append(", ");
          TestColumnDefinition tcd = listCd.get(iAttribute);
          sbUdtValue.append(tcd.getValueLiteral());
        }
        sbUdtValue.append(")");
      }
    }
    return sValueLiteral;
  }
  public TestColumnDefinition(String sName, String sType, Object oValue)
  {
    _sName = sName;
    _sType = sType;
    _oValue = oValue;
  }
} /* TestColumnDefinition */
