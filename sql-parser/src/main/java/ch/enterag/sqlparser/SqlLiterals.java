package ch.enterag.sqlparser;

import java.io.*;
import java.math.*;
import java.text.*;
import java.util.*;
import ch.enterag.utils.*;
import ch.enterag.sqlparser.datatype.enums.*;
import ch.enterag.sqlparser.expression.enums.*;
import ch.enterag.utils.logging.*;

public abstract class SqlLiterals
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(SqlLiterals.class.getName());
  protected static final String sMINUS = "-";
  protected static final String sLEFT_PAREN = "(";
  protected static final String sRIGHT_PAREN = ")";
  protected static final String sHYPHEN = "-";
  protected static final String sSP = " ";
  protected static final String sCOLON = ":";
  protected static final String sPERIOD = ".";
  protected static final String sCOMMA = ",";
  protected static final String sZERO = "0";
  protected static final String sAPOSTROPHE = "'";
  protected static final String sDOUBLE_APOSTROPHE = "''";
  protected static final String sQUOTE = "\"";
  protected static final String sDOUBLE_QUOTE = sQUOTE + sQUOTE;
  protected static final int iMIN_IDENTIFIER_LENGTH = 1;
  protected static final int iMAX_IDENTIFIER_LENGTH = 128;
  public static final String sDOT = ".";
  public static final String sNULL = K.NULL.getKeyword();
  public static final String sNATIONAL_LITERAL_PREFIX = "N";
  public static final String sBIT_LITERAL_PREFIX = "B";
  public static final String sBYTE_LITERAL_PREFIX = "X";
  public static final String sDATE_LITERAL_PREFIX = "DATE";
  public static final String sTIME_LITERAL_PREFIX = "TIME";
  public static final String sTIMESTAMP_LITERAL_PREFIX = "TIMESTAMP";
  public static final String sINTERVAL_LITERAL_PREFIX = "INTERVAL";
  public static final SimpleDateFormat sdfDATE = new SimpleDateFormat("yyyy-MM-dd");
  public static final SimpleDateFormat sdfTIME = new SimpleDateFormat("HH:mm:ss");
  public static final SimpleDateFormat sdfTIMESTAMP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public static final DecimalFormatSymbols dfsSYMBOLS = new DecimalFormatSymbols();
  public static DecimalFormat dfAPPROXIMATE = null;
  static
  {
    dfsSYMBOLS.setDecimalSeparator('.');
    dfAPPROXIMATE = new DecimalFormat("0.###############E0",dfsSYMBOLS);
  }

  /*------------------------------------------------------------------*/
  /** serialize an object to a byte array.
   * @param o object.
   * @return byte array.
   */
  public static byte[] serializeObject(Object o)
  {
    /* serialize the object as bytes */
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try
    {
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(o);
      oos.close();
    }
    catch (IOException ie) { _il.exception(ie); }
    return baos.toByteArray();
  } /* serializeOpbject */
  
  /*------------------------------------------------------------------*/
  /** deserialize an object from a byte array.
   * @param buf byte array.
   * @return deserialized object. 
   */
  public static Object deserializeObject(byte[] buf)
  {
    Object o = null; // default value: null
    ByteArrayInputStream bais = new ByteArrayInputStream(buf);
    try
    {
      ObjectInputStream ois = new ObjectInputStream(bais);
      o = ois.readObject();
      ois.close();
    }
    catch (IOException ie) { _il.exception(ie); }
    catch (ClassNotFoundException cnfe) { _il.exception(cnfe); }
    return o;
  } /* deserialize */
  
  /*------------------------------------------------------------------*/
  /** serialize an object to a byte buffer.
   * @param <T> type of object.
   * @param o object
   * @return byte buffer.
   */
  public static <T> byte[] serialize(T o)
  {
    return serializeObject(o);
  } /* serialize */
  
  /*------------------------------------------------------------------*/
  /** deserialize an object from a byte buffer.
   * @param <T> type of object.
   * @param buf byte buffer.
   * @param type type of object.
   * @return object.
   */
  public static <T> T deserialize(byte[] buf, Class<T> type)
  {
    return type.cast(deserializeObject(buf));
  } /* deserialize */
  
  /*------------------------------------------------------------------*/
  /** check, whether the given string is a reserved keyword.
   * @param s string.
   * @return true, if it is a reserved key word.
   */
  public static boolean isReservedKeyword(String s)
  {
    boolean bReserved = false;
    K k = K.getByKeyword(s);
    if (k != null)
      bReserved = k.isReserved();
    return bReserved;
  } /* isKeyword */
  
  public static boolean isAlNum(char c)
  {
    return Character.isLetter(c) || (c == '_') || ((c >= '0') && (c <= '9')); 
  } /* isAlNum */
  
  /*------------------------------------------------------------------*/
  /** check, whether an identifier is a regular identifier.
   * @param sIdentifier identifier.
   * @return true, if it is not a key word, has a length between 1 and 128,
   * starts with a letter continuing with letters, digits, or underscores.
   */
  public static boolean isRegular(String sIdentifier)
  {
    boolean bRegular = false;
    if (!isReservedKeyword(sIdentifier.toUpperCase()))
    {
      if ((sIdentifier != null) && 
          (sIdentifier.length() >= iMIN_IDENTIFIER_LENGTH) && 
          (sIdentifier.length() <= iMAX_IDENTIFIER_LENGTH))
      {
        char c = sIdentifier.charAt(0);
        bRegular = Character.isLetter(c);
        for (int i = 1; bRegular && (i < sIdentifier.length()); i++)
        {
          c = sIdentifier.charAt(i);
          bRegular = isAlNum(c); 
        }
      }
    }
    return bRegular;
  } /* isRegular */
  
  /*------------------------------------------------------------------*/
  /** check whether an identifier is delimited.
   * @param sDelimited
   * @return true, if identifier is delimited, false otherwise.
   */
  protected static boolean isDelimited(String sDelimited)
  {
    return (sDelimited != null) && (sDelimited.length() >= 2) && 
      sDelimited.startsWith(sQUOTE) && sDelimited.endsWith(sQUOTE);
  } /* isDelimited */

  /*------------------------------------------------------------------*/
  /** return the identifier starting at 0 as a delimited
   * identifier (with quotes and not converted to uppercase).
   * @param s string containing identifier, possibly delimited.
   * @return "normalized" identifier
   * @throws ParseException if the identifier is invalid.
   */
  public static String parseIdPrefix(String s)
    throws ParseException
  {
    String sIdentifier = null;
    StringBuilder sbIdentifier = new StringBuilder();
    if ((s != null) && (s.length() > 0)) 
    {
      int i = 0;
      if (s.charAt(0) == sQUOTE.charAt(0))
      {
        sbIdentifier.append(sQUOTE);
        boolean bInQuote = true;
        for(i++; bInQuote && (i < s.length()); i++)
        {
          if (s.charAt(i) != sQUOTE.charAt(0))
            sbIdentifier.append(s.charAt(i));
          else
          {
            if ((i < s.length()-1) && 
                (s.charAt(i+1) == sQUOTE.charAt(0)))
            {
              sbIdentifier.append(sQUOTE);
              i++;
            }
            else
              bInQuote = false;
          }
        }
        sbIdentifier.append(sQUOTE);
      }
      else // not delimited
      {
        for(; isAlNum(s.charAt(i)) && (i < s.length()); i++)
          sbIdentifier.append(s.charAt(i));
      }
      if (sbIdentifier.length() != 0)
        sIdentifier = sbIdentifier.toString();
    }
    else
      throw new IllegalArgumentException("String with identifier must have length greater than 0!");
    return sIdentifier;
  } /* parseIdPrefix */
  
  /*------------------------------------------------------------------*/
  /** turn a regular (mixed-case) or delimited identifier into its
   * normal form (uppercase for regular, undelimited for delimited
   * identifiers).
   * @param sDelimited identifier, possibly delimited.
   * @return "normalized" identifier
   * @throws ParseException if the identifier is invalid.
   */
  public static String parseId(String sDelimited)
    throws ParseException
  {
    String sIdentifier = null;
    if (sDelimited != null)
    {
      if (isDelimited(sDelimited))
      {
        /* make sure, all quotes inside are double quotes */
        sDelimited = sDelimited.substring(1,sDelimited.length()-1);
        StringBuilder sbIdentifier = new StringBuilder();
        for (int i = 0; i < sDelimited.length(); i++)
        {
          sbIdentifier.append(sDelimited.charAt(i));
          if (sDelimited.charAt(i) == sQUOTE.charAt(0))
          {
            if ((i < sDelimited.length()-1) && 
                (sDelimited.charAt(i+1) == sQUOTE.charAt(0)))
              i++; // skip next quote
            else
              throw new ParseException("Delimited identifier ("+sDelimited+") contains single quote!",i);
          }
        }
        sIdentifier = sbIdentifier.toString();
      }
      else
      {
        sIdentifier = sDelimited.toUpperCase();
        if (!isRegular(sIdentifier))
          throw new ParseException("Identifier ("+sIdentifier+") "+
            "must be regular (start with letter, continue with letter, underscore or digit, " +
            "and not equal a reserved keyword)!",0);
      }
      if ((sIdentifier.length() < iMIN_IDENTIFIER_LENGTH) || 
          (sIdentifier.length() > iMAX_IDENTIFIER_LENGTH))
        throw new ParseException("Identifier ("+sIdentifier+") "+
          "length must be at least " + String.valueOf(iMIN_IDENTIFIER_LENGTH) + " " + 
          "and at most " + String.valueOf(iMAX_IDENTIFIER_LENGTH) + "!",0);
    }
    else
      throw new NullPointerException("Identifier must not be null!");
    return sIdentifier;
  } /* parseId */

  /*------------------------------------------------------------------*/
  /** quote an identifier unconditionally.
   * @param sIdentifier identifier to be quoted.
   * @return identifier in quotes with doubled internal quotes.
   */
  public static String quoteId(String sIdentifier)
  {
    return  sQUOTE + sIdentifier.replace(sQUOTE, sDOUBLE_QUOTE) + sQUOTE;
  } /* quoteId */
  
  /*------------------------------------------------------------------*/
  /** quote an identifier in normal form (upper case, regular).
   * @param sIdentifier identifier in normal form.
   * @return identifier suitably delimited.
   */
  public static String formatId(String sIdentifier)
  {
    String sDelimited = null;
    if (sIdentifier != null)
    {
      if ((sIdentifier.length() >= iMIN_IDENTIFIER_LENGTH) && 
          (sIdentifier.length() <= iMAX_IDENTIFIER_LENGTH))
      {
        if ((!sIdentifier.equals(sIdentifier.toUpperCase())) || (!isRegular(sIdentifier)))
          sDelimited = sQUOTE + sIdentifier.replace(sQUOTE, sDOUBLE_QUOTE) + sQUOTE;
        else
          sDelimited = sIdentifier.toUpperCase();
      }
      else
        throw new IllegalArgumentException("Identifier length ("+sIdentifier+") " +
          "must be at least " + String.valueOf(iMIN_IDENTIFIER_LENGTH) + " " + 
          "and at most " + String.valueOf(iMAX_IDENTIFIER_LENGTH) + "!");
    }
    else
      throw new NullPointerException("Identifier must not be null!");
    return sDelimited;
  } /* formatId */
  
  /*------------------------------------------------------------------*/
  /** scan input for end of identifier.
   * @param sQualified qualified formatted identifier.
   * @param iStart start of formatted identifier.
   * @return end of formatted identifier.
   * @throws ParseException if identifier is null or unquoted and does 
   *         not start with a letter. 
   */
  public static int getIdentifierEnd(String sQualified, int iStart)
    throws ParseException
  {
    int iEnd = -1;
    if (sQualified != null)
    {
      iEnd = sQualified.length();
      char c = sQualified.charAt(iStart);
      if (c != sQUOTE.charAt(0))
      {
        if (Character.isLetter(c))
        {
          for (int i = iStart+1; (iEnd == sQualified.length()) && (i < sQualified.length()); i++)
          {
            c = sQualified.charAt(i);
            if (!Character.isLetter(c) && c != '_' && ((c < '0') || (c > '9')))
              iEnd = i;
          }
        }
        else
          throw new ParseException("Unquoted identifier must start with a letter!",0);
      }
      else
      {
        boolean bQuote = false;
        for (int i = iStart+1; (iEnd == sQualified.length()) && (i < sQualified.length()); i++)
        {
          c = sQualified.charAt(i);
          if (c != sQUOTE.charAt(0))
          {
            if (bQuote)
              iEnd = i;
          }
          else
          {
            if (!bQuote)
              bQuote = true;
            else
              bQuote = false;
          }
        }
      }
    }
    else
      throw new NullPointerException("Qualified identifier must not be null!");
    return iEnd;
  } /* getIdentifierEnd */

  /*------------------------------------------------------------------*/
  /** unconditionally quote  a catalog-qualified schema name for use in 
   * an SQL statement.
   * @param sCatalogName catalog name or null.
   * @param sUnqualifiedSchemaName schema name (must not be null).
   * @return formatted schema name - possibly qualified by catalog name.
   */
  public static String quoteQualifiedSchema(String sCatalogName, String sUnqualifiedSchemaName)
  {
    String sQuotedSchemaName = quoteId(sUnqualifiedSchemaName);
    if (sCatalogName != null)
      sQuotedSchemaName = quoteId(sCatalogName)+sDOT+sQuotedSchemaName;
    return sQuotedSchemaName;
  } /* quoteQualifiedSchema */
  
  /*------------------------------------------------------------------*/
  /** format a catalog-qualified schema name for use in an SQL statement.
   * @param sCatalogName catalog name or null.
   * @param sUnqualifiedSchemaName schema name (must not be null).
   * @return formatted schema name - possibly qualified by catalog name.
   */
  public static String formatQualifiedSchema(String sCatalogName, String sUnqualifiedSchemaName)
  {
    String sFormattedSchemaName = formatId(sUnqualifiedSchemaName);
    if (sCatalogName != null)
      sFormattedSchemaName = formatId(sCatalogName)+sDOT+sFormattedSchemaName;
    return sFormattedSchemaName;
  } /* formatQualifiedSchema */
  
  /*------------------------------------------------------------------*/
  /** unconditionally quote a schema-qualified name for use in an SQL statement.
   * @param sCatalogName catalog name or null.
   * @param sSchemaName schema name or null.
   * @param sUnqualifiedName schema-qualified name (must not be null).
   * @return quoted name - possibly qualified by schema name.
   */
  public static String quoteQualifiedName(String sCatalogName, String sSchemaName, String sUnqualifiedName)
  {
    String sQuotedQualifiedName = quoteId(sUnqualifiedName);
    if (sSchemaName != null)
      sQuotedQualifiedName = quoteQualifiedSchema(sCatalogName, sSchemaName)+sDOT+sQuotedQualifiedName;
    return sQuotedQualifiedName;
  } /* quoteQualifiedName */
  
  /*------------------------------------------------------------------*/
  /** format a schema-qualified name for use in an SQL statement.
   * @param sCatalogName catalog name or null.
   * @param sSchemaName schema name or null.
   * @param sUnqualifiedName schema-qualified name (must not be null).
   * @return formatted name - possibly qualified by schema name.
   */
  public static String formatQualifiedName(String sCatalogName, String sSchemaName, String sUnqualifiedName)
  {
    String sFormattedQualifiedName = formatId(sUnqualifiedName);
    if (sSchemaName != null)
      sFormattedQualifiedName = formatQualifiedSchema(sCatalogName, sSchemaName)+sDOT+sFormattedQualifiedName;
    return sFormattedQualifiedName;
  } /* formatQualifiedName */
  
  /*------------------------------------------------------------------*/
  /** unconditionally quote a list of identifiers as a comma-separated list.
   * @param list list of identifiers.
   * @return comma-separated list of quoted identifiers.
   */
  public static String quoteIdentifierCommaList(List<String> list)
  {
    StringBuilder sbCommaList = new StringBuilder();
    for (int i = 0; i < list.size(); i++)
    {
      if (i != 0)
        sbCommaList.append(", ");
      sbCommaList.append(quoteId(list.get(i)));
    }
    return sbCommaList.toString();
  } /* quoteIdentifierCommaList */
  
  /*------------------------------------------------------------------*/
  /** format a list of identifiers as a comma-separated list.
   * @param list list of identifiers.
   * @return comma-separated list of formatted identifiers.
   */
  public static String formatIdentifierCommaList(List<String> list)
  {
    StringBuilder sbCommaList = new StringBuilder();
    for (int i = 0; i < list.size(); i++)
    {
      if (i != 0)
        sbCommaList.append(", ");
      sbCommaList.append(formatId(list.get(i)));
    }
    return sbCommaList.toString();
  } /* formatIdentifierCommaList */
  
  /*------------------------------------------------------------------*/
  /** test a prefix and detach it.
   * @param s string to be tested.
   * @param sPrefix prefix to be detached.
   * @return string without prefix.
   */
  protected static String cutPrefix(String s, String sPrefix)
  {
    String sResult = null;
    if (s.toUpperCase().startsWith(sPrefix))
      sResult = s.substring(sPrefix.length()).trim();
    return sResult;
  } /* cutPrefix */
  
  /*------------------------------------------------------------------*/
  /** parse a quoted character string literal.
   * @param sQuoted quoted character string literal.
   * @return parsed string value.
   * @throws ParseException if the character string is not quoted properly.
   */
  public static String parseStringLiteral(String sQuoted)
    throws ParseException
  {
    String sParsed = null;
    if ((sQuoted.length() >= 2) && sQuoted.startsWith("'") && sQuoted.endsWith("'"))
    {
      sQuoted = sQuoted.substring(1,sQuoted.length() - 1);
      StringBuilder sbParsed = new StringBuilder();
      for (int i = 0; i < sQuoted.length(); i++)
      {
        sbParsed.append(sQuoted.charAt(i));
        if (sQuoted.substring(i,i+1).equals(sAPOSTROPHE))
        {
          if ((i < sQuoted.length()-1) && (sQuoted.substring(i+1,i+2).equals(sAPOSTROPHE)))
            i++; // skip next quote
          else
            throw new ParseException("Quoted string ("+sQuoted+") contains single apostrophe!",i);
        }
      }
      sParsed = sbParsed.toString();
    }
    else
      throw new ParseException("Quoted string must be quoted!",0);
    return sParsed;
  } /* parseStringLiteral */

  /*------------------------------------------------------------------*/
  /** format (quote) string value.
   * @param sValue string to be quoted.
   * @return character string literal.
   */
  public static String formatStringLiteral(String sValue)
  {
    String sQuoted = sNULL;
    if (sValue != null)
      sQuoted = sAPOSTROPHE + sValue.replaceAll(sAPOSTROPHE, sDOUBLE_APOSTROPHE) + sAPOSTROPHE;
      
    return sQuoted;
  } /* formatStringLiteral */

  /*------------------------------------------------------------------*/
  /** parse national character string
   * @param sNationalString national character string
   * @return string value.
   * @throws ParseException if the input is not a valid national character string literal.
   */
  public static String parseNationalStringLiteral(String sNationalString)
    throws ParseException
  {
    String sParsed = cutPrefix(sNationalString,sNATIONAL_LITERAL_PREFIX);
    if (sParsed != null)
      sParsed = parseStringLiteral(sParsed);
    else
      throw new ParseException("National character string literal must start with "+sNATIONAL_LITERAL_PREFIX+"!",0);
    return sParsed;
  } /* parseNationalStringLiteral */
  
  /*------------------------------------------------------------------*/
  /** format national character string value.
   * @param sValue string to be formatted.
   * @return national character string literal.
   */
  public static String formatNationalStringLiteral(String sValue)
  {
    String sFormatted = sNULL;
    if (sValue != null)
      sFormatted = sNATIONAL_LITERAL_PREFIX + formatStringLiteral(sValue);
    return sFormatted;
  } /* formatNationalStringLiteral */
  
  /*------------------------------------------------------------------*/
  /** parse bit string
   * @param sBitString bit string
   * @return bit string value.
   * @throws ParseException if the input is not a valid bit string literal.
   */
  public static String parseBitStringLiteral(String sBitString)
    throws ParseException
  {
    String sParsed = cutPrefix(sBitString, sBIT_LITERAL_PREFIX);
    if (sParsed != null)
    {
      sParsed = parseStringLiteral(sParsed);
      for (int i = 0; i < sParsed.length(); i++)
      {
        if ("01".indexOf(sParsed.charAt(i)) < 0)
          throw new ParseException("Bit string must consist of 0s and 1!",i);
      }
    }
    else
      throw new ParseException("Bit character string literal must start with "+sBIT_LITERAL_PREFIX+"!",0);
    return sParsed;
  } /* parseBitStringLiteral */
  
  /*------------------------------------------------------------------*/
  /** format bit string value.
   * @param sValue bit string to be formatted.
   * @return bit string literal.
   */
  public static String formatBitStringLiteral(String sValue)
  {
    String sFormatted = sNULL;
    if (sValue != null)
      sFormatted = sBIT_LITERAL_PREFIX + formatStringLiteral(sValue);
    return sFormatted;
  } /* formatBitStringLiteral */
  
  /*------------------------------------------------------------------*/
  /** parse a byte string literal.
   * @param sByteString byte string literal.
   * @return byte buffer value.
   * @throws ParseException if the input is not a valid byte string literal.
   */
  public static byte[] parseBytesLiteral(String sByteString)
    throws ParseException
  {
    byte[] bufParsed = null;
    String sParsed = cutPrefix(sByteString,sBYTE_LITERAL_PREFIX);
    if (sParsed != null)
    {
      sParsed = parseStringLiteral(sParsed);
      bufParsed = BU.fromHex(sParsed);
    }
    else
      throw new ParseException("Byte character string literal must start with "+sBYTE_LITERAL_PREFIX+"!",0);
    return bufParsed;
  } /* parseBytesLiteral */
  
  /*------------------------------------------------------------------*/
  /** format byte buffer value.
   * @param bufValue byte buffer value to be formatted.
   * @return byte string literal.
   */
  public static String formatBytesLiteral(byte[] bufValue)
  {
    String sFormatted = sNULL;
    if (bufValue != null)
      sFormatted = sBYTE_LITERAL_PREFIX + formatStringLiteral(BU.toHex(bufValue));
    return sFormatted;
  } /* formatBytesLiteral */
  
  /*------------------------------------------------------------------*/
  /** parse a date literal.
   * @param sDateLiteral date literal.
   * @return date value.
   * @throws ParseException if the input is not a valid date literal.
   */
  public static java.sql.Date parseDateLiteral(String sDateLiteral)
    throws ParseException
  {
    java.sql.Date dateParsed = null;
    String sParsed = cutPrefix(sDateLiteral,sDATE_LITERAL_PREFIX);
    if (sParsed != null)
    {
      sParsed = parseStringLiteral(sParsed);
      /* must have structure yyyy-MM-dd */
      try { dateParsed = new java.sql.Date(sdfDATE.parse(sParsed).getTime()); }
      catch (ParseException pe) { throw new ParseException("Date format must conform to yyyy-mm-dd!",pe.getErrorOffset()); }
    }
    else
      throw new ParseException("Date character string literal must start with "+sDATE_LITERAL_PREFIX+"!",0);
    return dateParsed;
  } /* parseDateLiteral */

  /*------------------------------------------------------------------*/
  /** format a date value
   * @param dateValue date value to be formatted.
   * @return date literal.
   */
  public static String formatDateLiteral(java.sql.Date dateValue)
  {
    String sFormatted = sNULL;
    if (dateValue != null)
      sFormatted = sDATE_LITERAL_PREFIX + formatStringLiteral(sdfDATE.format(dateValue));
    return sFormatted;
  } /* formatDateLiteral */
  
  /*------------------------------------------------------------------*/
  /** parse a time literal.
   * @param sTimeLiteral time literal.
   * @return time value.
   * @throws ParseException if the input is not a valid time literal.
   */
  public static java.sql.Time parseTimeLiteral(String sTimeLiteral)
    throws ParseException
  {
    java.sql.Time timeParsed = null;
    String sParsed = cutPrefix(sTimeLiteral,sTIME_LITERAL_PREFIX);
    if (sParsed != null)
    {
      sParsed = parseStringLiteral(sParsed);
      String sMillis = null;
      int iDecimal = sParsed.lastIndexOf('.');
      if (iDecimal >= 0)
      {
        sMillis = sParsed.substring(iDecimal+1);
        if (sMillis.length() > 3)
          sMillis = sMillis.substring(0,3);
        while (sMillis.length() < 3)
          sMillis = sMillis + "0";
        sParsed = sParsed.substring(0,iDecimal);
      }
      /* must have structure hh:mm:ss */
      try 
      { 
        timeParsed = new java.sql.Time(sdfTIME.parse(sParsed).getTime());
        if (sMillis != null)
          timeParsed.setTime(timeParsed.getTime()+Integer.parseInt(sMillis));
      }
      catch (ParseException pe) { throw new ParseException("Time format must conform to hh:mm:ss.fff!",pe.getErrorOffset()); }
    }
    else
      throw new ParseException("Time character string literal must start with "+sTIME_LITERAL_PREFIX+"!",0);
    return timeParsed;
  } /* parseTimeLiteral */

  /*------------------------------------------------------------------*/
  /** format a time value
   * @param timeValue time value to be formatted.
   * @return time literal.
   */
  public static String formatTimeLiteral(java.sql.Time timeValue)
  {
    String sFormatted = sNULL;
    if (timeValue != null)
    {
      sFormatted = sdfTIME.format(timeValue);
      long lMillis = timeValue.getTime() % 1000;
      if (lMillis != 0)
      {
        String sMillis = String.valueOf(lMillis);
        while (sMillis.length() < 3)
          sMillis = "0"+sMillis;
        while (sMillis.endsWith("0"))
          sMillis = sMillis.substring(0,sMillis.length()-1);
        sFormatted = sFormatted + sDOT + sMillis;
      }
      sFormatted = sTIME_LITERAL_PREFIX + formatStringLiteral(sFormatted);
    }
    return sFormatted;
  } /* formatTimeLiteral */
  
  /*------------------------------------------------------------------*/
  /** parse a timestamp literal.
   * @param sTimestampLiteral timestamp literal.
   * @return timestamp value.
   * @throws ParseException if the input is not a valid timestamp literal.
   */
  public static java.sql.Timestamp parseTimestampLiteral(String sTimestampLiteral)
    throws ParseException
  {
    java.sql.Timestamp tsParsed = null;
    String sParsed = cutPrefix(sTimestampLiteral,sTIMESTAMP_LITERAL_PREFIX);
    if (sParsed != null)
    {
      sParsed = parseStringLiteral(sParsed);
      String sNanos = null;
      int iDecimal = sParsed.lastIndexOf('.');
      if (iDecimal >= 0)
      {
        sNanos = sParsed.substring(iDecimal+1);
        if (sNanos.length() > 9)
          sNanos = sNanos.substring(0,9);
        while (sNanos.length() < 9)
          sNanos = sNanos + "0";
        sParsed = sParsed.substring(0,iDecimal);
      }
      /* must have structure hh:mm:dd */
      try 
      {
        tsParsed = new java.sql.Timestamp(sdfTIMESTAMP.parse(sParsed).getTime());
        if (sNanos != null)
          tsParsed.setNanos(Integer.parseInt(sNanos));
      }
      catch (ParseException pe) { throw new ParseException("Timestamp format must conform to yyyy-mm-dd hh-mm-ss.fffffffff!",pe.getErrorOffset()); }
    }
    else
      throw new ParseException("Timestamp character string literal must start with "+sTIMESTAMP_LITERAL_PREFIX+"!",0);
    return tsParsed;
  } /* parseTimestampLiteral */

  /*------------------------------------------------------------------*/
  /** format a timestamp value
   * @param tsValue timestamp value to be formatted.
   * @return timestamp literal.
   */
  public static String formatTimestampLiteral(java.sql.Timestamp tsValue)
  {
    String sFormatted = sNULL;
    if (tsValue != null)
    {
      sFormatted = sdfTIMESTAMP.format(tsValue);
      int iNanos = tsValue.getNanos();
      if (iNanos > 0)
      {
        String sNanos = String.valueOf(iNanos);
        while (sNanos.length() < 9)
          sNanos = "0" + sNanos;
        while (sNanos.endsWith("0"))
          sNanos = sNanos.substring(0,sNanos.length()-1);
        sFormatted = sFormatted + sDOT + sNanos;
      }
      sFormatted = sTIMESTAMP_LITERAL_PREFIX + formatStringLiteral(sFormatted);
    }
    return sFormatted;
  } /* formatTimestampLiteral */

  /*------------------------------------------------------------------*/
  /** parse an interval literal.
   * @param sIntervalLiteral interval literal.
   * @return interval value.
   * @throws ParseException if the input is not a valid interval literal.
   */
  public static Interval parseInterval(String sIntervalLiteral)
      throws ParseException
  {
    Interval interval = null;
    String sParsed = cutPrefix(sIntervalLiteral,sINTERVAL_LITERAL_PREFIX);
    if (sParsed != null)
    {
      int iSign = 1;
      /* permitted by standard but not supported by Postgres */
      if (sParsed.startsWith(sMINUS))
      {
        iSign = -1;
        sParsed = sParsed.substring(1).trim();
      }
      int iEndQuote = sParsed.lastIndexOf(sAPOSTROPHE);
      if (sParsed.startsWith(sAPOSTROPHE) && (iEndQuote >= 0))
      {
        String sIntervalQualifier = sParsed.substring(iEndQuote+1).trim().toUpperCase();
        sParsed = sParsed.substring(1,iEndQuote);
        if (sParsed.startsWith(sMINUS))
        {
          iSign = -iSign;
          sParsed = sParsed.substring(1).trim();
        }
        /* analyze interval qualifier <start> TO <end> or <start> */
        String sStart = sIntervalQualifier;
        String sEnd = null;
        int iTo = sIntervalQualifier.indexOf(K.TO.getKeyword());
        if (iTo >= 0)
        {
          sStart = sIntervalQualifier.substring(0,iTo).trim();
          sEnd = sIntervalQualifier.substring(iTo+K.TO.getKeyword().length()).trim();
        }
        /* detach field widths */
        int iLeftParen = sStart.indexOf(sLEFT_PAREN);
        if (iLeftParen >= 0)
          sStart = sStart.substring(0,iLeftParen);
        IntervalField ifStart = IntervalField.getByKeywords(sStart);
        IntervalField ifEnd = null;
        if (sEnd != null)
        {
          iLeftParen = sEnd.indexOf(sLEFT_PAREN);
          if (iLeftParen >= 0)
            sEnd = sEnd.substring(0,iLeftParen);
          ifEnd = IntervalField.getByKeywords(sEnd);
        }
        if (ifStart != null)
        {
          if ((ifStart == IntervalField.YEAR) || (ifStart == IntervalField.MONTH))
          {
            /* YEAR-MONTH interval */
            String sYears = null;
            String sMonths = null;
            if (ifStart == IntervalField.YEAR)
            {
              sYears = sParsed;
              if (ifEnd != null)
              {
                if (ifEnd == IntervalField.MONTH)
                {
                  int iHyphen = sParsed.indexOf(sHYPHEN);
                  if (iHyphen >= 0)
                  {
                    sYears = sParsed.substring(0,iHyphen).trim();
                    sMonths = sParsed.substring(iHyphen+1);
                  }
                  else
                    throw new ParseException("YEAR-MONTH interval value must contain a hyphen!",sINTERVAL_LITERAL_PREFIX.length());
                }
                else
                  throw new ParseException("Invalid YEAR-MONTH interval qualifier "+sIntervalQualifier+"!",sIntervalLiteral.length());
              }
            }
            else if (ifStart == IntervalField.MONTH)
            {
              sMonths = sParsed;
              if (ifEnd != null)
                throw new ParseException("Invalid MONTH interval qualifier "+sIntervalQualifier+"!",sIntervalLiteral.length());
            }
            int iYears = 0;
            if (sYears != null)
              iYears = Integer.parseInt(sYears);
            int iMonths = 0;
            if (sMonths != null)
              iMonths = Integer.parseInt(sMonths);
            interval = new Interval(iSign, iYears, iMonths);
          }
          else
          {
            /* DAY TIME interval */
            String sDays = null;
            String sHours = null;
            String sMinutes = null;
            String sSeconds = null;
            String sDecimals = null;
            if (ifStart == IntervalField.DAY)
            {
              sDays = sParsed;
              if (ifEnd != null)
              {
                int iSpace = sParsed.indexOf(sSP);
                if (iSpace >= 0)
                {
                  sDays = sParsed.substring(0,iSpace);
                  sParsed = sParsed.substring(iSpace+1);
                  if (ifEnd == IntervalField.HOUR)
                    sHours = sParsed;
                  else
                  {
                    int iColon = sParsed.indexOf(sCOLON);
                    if (iColon >= 0)
                    {
                      sHours = sParsed.substring(0,iColon);
                      sParsed = sParsed.substring(iColon+1);
                      if (ifEnd == IntervalField.MINUTE)
                        sMinutes = sParsed;
                      else
                      {
                        iColon = sParsed.indexOf(sCOLON);
                        if (iColon >= 0)
                        {
                          sMinutes = sParsed.substring(0,iColon);
                          sParsed = sParsed.substring(iColon+1);
                          if (ifEnd == IntervalField.SECOND)
                          {
                            sSeconds = sParsed;
                            int iPeriod = sParsed.indexOf(sPERIOD);
                            if (iPeriod >= 0)
                            {
                              sDecimals = sSeconds.substring(iPeriod+1);
                              sSeconds = sSeconds.substring(0,iPeriod);
                            }
                          }
                          else
                            throw new ParseException("Invalid DAY-TIME interval qualifier "+sIntervalQualifier+"!",sIntervalLiteral.length());
                        }
                        else
                          throw new ParseException("DAY-TIME interval value must contain a colon to separate the minutes from the seconds!",sINTERVAL_LITERAL_PREFIX.length());
                      }
                    }
                    else
                      throw new ParseException("DAY-TIME interval value must contain a colon to separate the hours from the minutes!",sINTERVAL_LITERAL_PREFIX.length());
                  }
                }
                else
                  throw new ParseException("DAY-TIME interval value have contain a space to separate the days from the hours!",sINTERVAL_LITERAL_PREFIX.length());
              }
            }
            else if (ifStart == IntervalField.HOUR)
            {
              sHours = sParsed;
              if (ifEnd != null)
              {
                int iColon = sParsed.indexOf(sCOLON);
                if (iColon >= 0)
                {
                  sHours = sParsed.substring(0,iColon);
                  sParsed = sParsed.substring(iColon+1);
                  if (ifEnd == IntervalField.MINUTE)
                    sMinutes = sParsed;
                  else
                  {
                    iColon = sParsed.indexOf(sCOLON);
                    if (iColon >= 0)
                    {
                      sMinutes = sParsed.substring(0,iColon);
                      sParsed = sParsed.substring(iColon+1);
                      if (ifEnd == IntervalField.SECOND)
                      {
                        sSeconds = sParsed;
                        int iPeriod = sParsed.indexOf(sPERIOD);
                        if (iPeriod >= 0)
                        {
                          sDecimals = sSeconds.substring(iPeriod+1);
                          sSeconds = sSeconds.substring(0,iPeriod);
                        }
                      }
                      else
                        throw new ParseException("Invalid DAY-TIME interval qualifier "+sIntervalQualifier+"!",sIntervalLiteral.length());
                    }
                    else
                      throw new ParseException("DAY-TIME interval value must contain a colon to separate the minutes from the seconds!",sINTERVAL_LITERAL_PREFIX.length());
                  }
                }
                else
                  throw new ParseException("DAY-TIME interval value must contain a colon to separate the hours from the minutes!",sINTERVAL_LITERAL_PREFIX.length());
              }
            }
            else if (ifStart == IntervalField.MINUTE)
            {
              sMinutes = sParsed;
              if (ifEnd != null)
              {
                int iColon = sParsed.indexOf(sCOLON);
                if (iColon >= 0)
                {
                  sMinutes = sParsed.substring(0,iColon);
                  sParsed = sParsed.substring(iColon+1);
                  if (ifEnd == IntervalField.SECOND)
                  {
                    sSeconds = sParsed;
                    int iPeriod = sParsed.indexOf(sPERIOD);
                    if (iPeriod >= 0)
                    {
                      sDecimals = sSeconds.substring(iPeriod+1);
                      sSeconds = sSeconds.substring(0,iPeriod);
                    }
                  }
                  else
                    throw new ParseException("Invalid DAY-TIME interval qualifier "+sIntervalQualifier+"!",sIntervalLiteral.length());
                }
                else
                  throw new ParseException("DAY-TIME interval value must contain a colon to separate the minutes from the seconds!",sINTERVAL_LITERAL_PREFIX.length());
              }
            }
            else if (ifStart == IntervalField.SECOND)
            {
              sSeconds = sParsed;
              int iPeriod = sParsed.indexOf(sPERIOD);
              if (iPeriod >= 0)
              {
                sDecimals = sSeconds.substring(iPeriod+1);
                sSeconds = sSeconds.substring(0,iPeriod);
              }
            }
            int iDays = 0;
            if (sDays != null)
              iDays = Integer.parseInt(sDays);
            int iHours = 0;
            if (sHours != null)
              iHours = Integer.parseInt(sHours);
            int iMinutes = 0;
            if (sMinutes != null)
              iMinutes = Integer.parseInt(sMinutes);
            int iSeconds = 0;
            if (sSeconds != null)
              iSeconds = Integer.parseInt(sSeconds);
            long lNanos = 0;
            if (sDecimals != null)
            {
              if (sDecimals.length() > 9)
                sDecimals = sDecimals.substring(0,9);
              while (sDecimals.length() < 9)
                sDecimals = sDecimals + sZERO;
              lNanos = Long.parseLong(sDecimals);
            }
            interval = new Interval(iSign, iDays, iHours, iMinutes, iSeconds, lNanos);
          }
        }
        else
          throw new ParseException("Interval qualifier must start with a valid start field!",sINTERVAL_LITERAL_PREFIX.length());
      }
      else
        throw new ParseException("Interval literal must contain quoted value",sINTERVAL_LITERAL_PREFIX.length());
    }
    else
      throw new ParseException("Interval literal must start with "+sINTERVAL_LITERAL_PREFIX+"!",0);
    return interval;
  } /* parseInterval */
  
  /*------------------------------------------------------------------*/
  /** format an interval value
   * @param ivValue interval value to be formatted.
   * @return interval literal.
   */
  public static String formatIntervalLiteral(Interval ivValue)
  {
    String sFormatted = sNULL;
    if (ivValue != null)
    {
      IntervalField ifStart = null;
      IntervalField ifEnd = null;
      String sValue = null;
      int iPrecision = -1;
      int iSecondsPrecision = -1;
      if (ivValue.getYears() != 0)
      {
        ifStart = IntervalField.YEAR;
        sValue = String.valueOf(ivValue.getYears());
        if (sValue.length() > 2)
          iPrecision = sValue.length();
      }
      if (ivValue.getMonths() != 0)
      {
        if (ifStart == null)
        {
          ifStart = IntervalField.MONTH;
          sValue = String.valueOf(ivValue.getMonths());
          if (sValue.length() > 2)
            iPrecision = sValue.length();
        }
        else
        {
          ifEnd = IntervalField.MONTH;
          sValue = sValue + sHYPHEN + String.valueOf(ivValue.getMonths()); 
        }
      }
      if (ivValue.getDays() != 0)
      {
        ifStart = IntervalField.DAY;
        sValue = String.valueOf(ivValue.getDays());
        if (sValue.length() > 2)
          iPrecision = sValue.length();
      }
      if (ivValue.getHours() != 0)
      {
        if (ifStart == null)
        {
          ifStart = IntervalField.HOUR;
          sValue = String.valueOf(ivValue.getHours());
          if (sValue.length() > 2)
            iPrecision = sValue.length();
        }
        else
        {
          ifEnd = IntervalField.HOUR;
          sValue = sValue + sSP + String.valueOf(ivValue.getHours());
        }
      }
      if (ivValue.getMinutes() != 0)
      {
        if (ifStart == null)
        {
          ifStart = IntervalField.MINUTE;
          sValue = String.valueOf(ivValue.getMinutes());
          if (sValue.length() > 2)
            iPrecision = sValue.length();
        }
        else
        {
          ifEnd = IntervalField.MINUTE;
          sValue = sValue + sCOLON + String.valueOf(ivValue.getMinutes());
        }
      }
      if ((ivValue.getSeconds() != 0) || (ivValue.getNanoSeconds() != 0))
      {
        if (ifStart == null)
        {
          ifStart = IntervalField.SECOND;
          sValue = String.valueOf(ivValue.getSeconds());
          if (sValue.length() > 2)
            iPrecision = sValue.length();
        }
        else
        {
          ifEnd = IntervalField.SECOND;
          sValue = sValue + sCOLON + String.valueOf(ivValue.getSeconds());
        }
        if (ivValue.getNanoSeconds() != 0)
        {
          String sNanos = String.valueOf(ivValue.getNanoSeconds());
          while (sNanos.length() < 9)
            sNanos = sZERO + sNanos;
          while (sNanos.endsWith(sZERO))
            sNanos = sNanos.substring(0,sNanos.length()-1);
          sValue = sValue + sPERIOD + sNanos;
          if (sNanos.length() > 6)
            iSecondsPrecision = sNanos.length();
        }
      }
      sFormatted = sINTERVAL_LITERAL_PREFIX + sSP;
      /*** is allowed by standard but not liked by Postgres
      if (ivValue.getSign() < 0)
        sFormatted = sFormatted + sMINUS + sSP;
      ***/
      if (ivValue.getSign() < 0)
        sValue = sMINUS + sSP + sValue;
      sFormatted = sFormatted + formatStringLiteral(sValue) + 
        sSP + ifStart.getKeywords();
      if ((iPrecision >= 0) || ((ifStart == IntervalField.SECOND) && (iSecondsPrecision >= 0)))
      {
        if (iPrecision < 0)
          iPrecision = 2;
        sFormatted = sFormatted + sLEFT_PAREN+String.valueOf(iPrecision);
        if ((ifStart == IntervalField.SECOND) && (iSecondsPrecision >= 0))
          sFormatted = sFormatted + sCOMMA + sSP + String.valueOf(iSecondsPrecision);
        sFormatted = sFormatted + sRIGHT_PAREN;
      }
      if (ifEnd != null)
        sFormatted = sFormatted + sSP + K.TO.getKeyword() + sSP + ifEnd.getKeywords();
      if ((ifEnd == IntervalField.SECOND) && (iSecondsPrecision >= 0))
        sFormatted = sFormatted + sLEFT_PAREN + String.valueOf(iSecondsPrecision) + sRIGHT_PAREN;
    }
    return sFormatted;
  } /* formatIntervalLiteral */
  
  /*------------------------------------------------------------------*/
  /** parse a boolean literal.
   * @param sBooleanLiteral boolean literal.
   * @return parsed boolean value.
   * @throws ParseException if the input is not a valid boolean literal.
   */
  public static BooleanLiteral parseBooleanLiteral(String sBooleanLiteral)
    throws ParseException
  {
    BooleanLiteral blParsed = null;
    sBooleanLiteral = sBooleanLiteral.toUpperCase();
    if (sBooleanLiteral.equals(BooleanLiteral.TRUE.getKeywords()))
      blParsed = BooleanLiteral.TRUE;
    else if (sBooleanLiteral.equals(BooleanLiteral.FALSE.getKeywords()))
      blParsed = BooleanLiteral.FALSE;
    else if (sBooleanLiteral.equals(BooleanLiteral.UNKNOWN.getKeywords()))
      blParsed = BooleanLiteral.UNKNOWN;
    else
      throw new ParseException("Boolean literals must be " + 
        BooleanLiteral.TRUE.getKeywords()+", "+
        BooleanLiteral.FALSE.getKeywords() +", or " + 
        BooleanLiteral.UNKNOWN.getKeywords()+"!",0);
    return blParsed;
  } /* parseBooleanLiteral */

  /*------------------------------------------------------------------*/
  /** format a boolean literal value.
   * N.B.: the tree-valued SQL booleans are also often rendered as JAVA 
   * Boolean values with JAVA null for SQL UNKNOWN.
   * @param blValue boolean value to be formatted.
   * @return boolean literal.
   */
  public static String formatBooleanLiteral(BooleanLiteral blValue)
  {
    String sFormatted = blValue.getKeywords();
    return sFormatted;
  } /* formatBooleanLiteral */
  
  /*------------------------------------------------------------------*/
  /** parse an approximate numeric literal.
   * @param sApproximateLiteral approximate numeric literal.
   * @return parsed double.
   * @throws ParseException if input cannot be parsed.
   */
  public static Double parseApproximateLiteral(String sApproximateLiteral)
    throws ParseException
  {
    Double dParsed = dfAPPROXIMATE.parse(sApproximateLiteral).doubleValue();
    return dParsed;
  } /* parseApproximateLiteral */
  
  /*------------------------------------------------------------------*/
  /** format a double value as an approximate numeric literal.
   * @param dValue double value to be formatted.
   * @return approximate numeric literal.
   */
  public static String formatApproximateLiteral(Double dValue)
  {
    String sFormatted = dfAPPROXIMATE.format(dValue.doubleValue());
    return sFormatted;
  } /* formatApproximateLiteral */

  /*------------------------------------------------------------------*/
  /** parse an exact numeric literal.
   * @param sExactLiteral exact numeric literal.
   * @return parsed BigDecimal.
   * @throws ParseException if input cannot be parsed.
   */
  public static BigDecimal parseExactLiteral(String sExactLiteral)
    throws ParseException
  {
    BigDecimal bd = null;
    try { bd = new BigDecimal(sExactLiteral); }
    catch(NumberFormatException nfe) { throw new ParseException("Invalid exact literal ("+nfe.getMessage()+")!",0); }
    return bd;
  } /* parseExact */
  
  /*------------------------------------------------------------------*/
  /** format a decimal value as an exact numeric literal.
   * @param bd decimal value to be formatted.
   * @return exact numeric literal.
   */
  public static String formatExactLiteral(BigDecimal bd)
  {
    String sFormatted = bd.toPlainString();
    return sFormatted;
  } /* formatExactLiteral */

} /* class SqlLiterals */
