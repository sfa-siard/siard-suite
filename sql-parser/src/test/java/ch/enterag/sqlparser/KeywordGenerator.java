package ch.enterag.sqlparser;

import java.io.*;
import java.util.*;

import ch.enterag.utils.lang.Execute;

public class KeywordGenerator
{

  private static final String sENUM = "src/main/java/ch/enterag/sqlparser/K.java";
  private static final String sKEYWORD = "src/main/antlr/LexSql.g4";
  private static final String[] asReserved = new String[]
  {
    "A",
    "ABS",
    "ACTION",
    "ADD",
    "AFTER",
    "ALL",
    "ALLOCATE",
    "ALTER",
    "ALWAYS",
    "AND",
    "ANY",
    "ARE",
    "ARRAY",
    "AS",
    "ASC",
    "ASENSITIVE",
    "ASYMMETRIC",
    "AT",
    "ATOMIC",
    "AUTHORIZATION",
    "AVG",
    "BEFORE",
    "BEGIN",
    "BERNOULLI",
    "BETWEEN",
    "BIGINT",
    "BINARY",
    "BLOB",
    "BOOLEAN",
    "BOTH",
    "BY",
    "CALL",
    "CALLED",
    "CARDINALITY",
    "CASCADE",
    "CASCADED",
    "CASE",
    "CAST",
    "CEIL",
    "CEILING",
    "CHAR",
    "CHARACTER",
    "CHARACTER_LENGTH",
    "CHAR_LENGTH",
    "CHECK",
    "CHECKED",
    "CLOB",
    "CLOSE",
    "COALESCE",
    "COLLATE",
    "COLLECT",
    "COLUMN",
    "COMMIT",
    "CONNECT",
    "CONSTRAINT",
    "CONTINUE",
    "CORRESPONDING",
    "COVAR_POP",
    "COVAR_SAMP",
    "COUNT",
    "CREATE",
    "CROSS",
    "CUBE",
    "CUME_DIST",
    "CURRENT",
    "CURRENT_DATE",
    "CURRENT_DEFAULT_TRANSFORM_GROUP",
    "CURRENT_PATH",
    "CURRENT_ROLE",
    "CURRENT_TIME",
    "CURRENT_TIMESTAMP",
    "CURRENT_TRANSFORM_GROUP_FOR_TYPE",
    "CURRENT_USER",
    "CURSOR",
    "CYCLE",
    "DATA",
    "DATALINK",
    "DATE",
    "DAY",
    "DEALLOCATE",
    "DEC",
    "DECIMAL",
    "DECLARE",
    "DEFAULT",
    "DEFERRABLE",
    "DEFERRED",
    "DELETE",
    "DENSE_RANK",
    "DEREF",
    "DERIVED",
    "DESC",
    "DESCRIBE",
    "DETERMINISTIC",
    "DISCONNECT",
    "DISTINCT",
    "DOUBLE",
    "DROP",
    "DYNAMIC",
    "EACH",
    "ELEMENT",
    "ELSE",
    "END",
    "END-EXEC",
    "ESCAPE",
    "EVERY",
    "EXCEPT",
    "EXCLUDING",
    "EXEC",
    "EXECUTE",
    "EXISTS",
    "EXP",
    "EXTERNAL",
    "EXTRACT",
    "FALSE",
    "FETCH",
    "FILTER",
    "FINAL",
    "FLOAT",
    "FLOOR",
    "FOLLOWING",
    "FOR",
    "FOREIGN",
    "FREE",
    "FROM",
    "FULL",
    "FUNCTION",
    "FUSION",
    "G",
    "GENERATED",
    "GET",
    "GLOBAL",
    "GRANT",
    "GROUP",
    "GROUPING",
    "HAVING",
    "HOLD",
    "HOUR",
    "IDENTITY",
    "IMMEDIATE",
    "IN",
    "INCLUDING",
    "INDICATOR",
    "INITIALLY",
    "INNER",
    "INOUT",
    "INPUT",
    "INSENSITIVE",
    "INSERT",
    "INSTANCE",
    "INSTANTIABLE",
    "INT",
    "INTEGER",
    "INTERSECT",
    "INTERSECTION",
    "INTERVAL",
    "INTO",
    "IS",
    "ISOLATION",
    "JOIN",
    "K",
    "KEY",
    "LANGUAGE",
    "LARGE",
    "LATERAL",
    "LEADING",
    "LEFT",
    "LIKE",
    "LN",
    "LOCAL",
    "LOCALTIME",
    "LOCALTIMESTAMP",
    "LOWER",
    "M",
    "MATCH",
    "MAX",
    "MEMBER",
    "MERGE",
    "METHOD",
    "MIN",
    "MINUTE",
    "MOD",
    "MODIFIES",
    "MODULE",
    "MONTH",
    "MULTISET",
    "NATIONAL",
    "NATURAL",
    "NCHAR",
    "NCLOB",
    "NEW",
    "NEXT",
    "NO",
    "NONE",
    "NORMALIZE",
    "NORMALIZED",
    "NOT",
    "NULL",
    "NULLIF",
    "NUMERIC",
    "OBJECT",
    "OCTET_LENGTH",
    "OF",
    "OLD",
    "ON",
    "ONLY",
    "OPEN",
    "OPTION",
    "OPTIONS",
    "OR",
    "ORDER",
    "ORDINALITY",
    "OUT",
    "OUTER",
    "OUTPUT",
    "OVER",
    "OVERLAPS",
    "OVERRIDING",
    "PARAMETER",
    "PARTITION",
    "PERCENT_RANK",
    "PERCENTILE_CONT",
    "PERCENTILE_DISC",
    "POWER",
    "PRECEDING",
    "PRECISION",
    "PREPARE",
    "PRIMARY",
    "PROCEDURE",
    "RANGE",
    "RANK",
    "READS",
    "REAL",
    "RECURSIVE",
    "REF",
    "REFERENCES",
    "REFERENCING",
    "REGR_AVGX",
    "REGR_AVGY",
    "REGR_COUNT",
    "REGR_INTERCEPT",
    "REGR_R2",
    "REGR_SLOPE",
    "REGR_SXX",
    "REGR_SXY",
    "REGR_SYY",
    "RELEASE",
    "REPEATABLE",
    "RESTRICT",
    "RESULT",
    "RETURN",
    "RETURNS",
    "REVOKE",
    "RIGHT",
    "ROLLBACK",
    "ROLLUP",
    "ROW",
    "ROW_NUMBER",
    "ROWS",
    "SAVEPOINT",
    "SCHEMA",
    "SCOPE",
    "SCROLL",
    "SEARCH",
    "SECOND",
    "SELECT",
    "SENSITIVE",
    "SESSION_USER",
    "SET",
    "SIMILAR",
    "SIMPLE",
    "SMALLINT",
    "SOME",
    "SPECIFIC",
    "SPECIFICTYPE",
    "SQL",
    "SQLEXCEPTION",
    "SQLSTATE",
    "SQLWARNING",
    "SQRT",
    "START",
    "STATIC",
    "STDDEV_POP",
    "STDDEV_SAMP",
    "STYLE",
    "SUBMULTISET",
    "SUBSTRING",
    "SUM",
    "SYMMETRIC",
    "SYSTEM",
    "SYSTEM_USER",
    "TABLE",
    "TABLESAMPLE",
    "TEMPORARY",
    "THEN",
    "TIME",
    "TIMESTAMP",
    "TIMEZONE_HOUR",
    "TIMEZONE_MINUTE",
    "TO",
    "TRAILING",
    "TRANSLATION",
    "TREAT",
    "TRIGGER",
    "TRIM",
    "TRUE",
    "TYPE",
    "UESCAPE",
    "UNBOUNDED",
    "UNDER",
    "UNION",
    "UNIQUE",
    "UNKNOWN",
    "UNNEST",
    "UPDATE",
    "UPPER",
    "USER",
    "USING",
    "VALUE",
    "VALUES",
    "VAR_POP",
    "VAR_SAMP",
    "VARBINARY",
    "VARCHAR",
    "VARYING",
    "VIEW",
    "WHEN",
    "WHENEVER",
    "WHERE",
    "WIDTH_BUCKET",
    "WINDOW",
    "WITH",
    "WITHIN",
    "WITHOUT",
    "XML",
    "YEAR",
    "ZONE"
  };
  private static final String[] asNonReserved = new String[]
  {
    "ABSOLUTE",
    "ADA",
    "ADMIN",
    "ASSERTION",
    "ASSIGNMENT",
    "ATTRIBUTE",
    "ATTRIBUTES",
    "BREADTH",
    "C",
    "CATALOG",
    "CATALOG_NAME",
    "CHAIN",
    "CHARACTERISTICS",
    "CHARACTERS",
    "CHARACTER_SET_CATALOG",
    "CHARACTER_SET_NAME",
    "CHARACTER_SET_SCHEMA",
    "CLASS_ORIGIN",
    "COBOL",
    "CODE_UNITS",
    "COLLATION",
    "COLLATION_CATALOG",
    "COLLATION_NAME",
    "COLLATION_SCHEMA",
    "COLUMN_NAME",
    "COMMAND_FUNCTION",
    "COMMAND_FUNCTION_CODE",
    "COMMITTED",
    "CONDITION",
    "CONDITION_NUMBER",
    "CONNECTION_NAME",
    "CONSTRAINTS",
    "CONSTRAINT_CATALOG",
    "CONSTRAINT_NAME",
    "CONSTRAINT_SCHEMA",
    "CONSTRUCTOR",
    "CONSTRUCTORS",
    "CONTAINS",
    "CONVERT",
    "CORR",
    "CURRENT_COLLATION",
    "CURSOR_NAME",
    "DATETIME_INTERVAL_CODE",
    "DATETIME_INTERVAL_PRECISION",
    "DEFAULTS",
    "DEFINED",
    "DEFINER",
    "DEGREE",
    "DEPTH",
    "DESCRIPTOR",
    "DIAGNOSTICS",
    "DISPATCH",
    "DOMAIN",
    "DYNAMIC_FUNCTION",
    "DYNAMIC_FUNCTION_CODE",
    "EQUALS",
    "EXCEPTION",
    "EXCLUDE",
    "FIRST",
    "FORTRAN",
    "FOUND",
    "GENERAL",
    "GO",
    "GOTO",
    "GRANTED",
    "HIERARCHY",
    "IMPLEMENTATION",
    "INCREMENT",
    "INVOKER",
    "ISOLATION",
    "KEY_MEMBER",
    "KEY_TYPE",
    "LAST",
    "LENGTH",
    "LEVEL",
    "LOCATOR",
    "MAP",
    "MATCHED",
    "MAXVALUE",
    "MESSAGE_LENGTH",
    "MESSAGE_OCTET_LENGTH",
    "MESSAGE_TEXT",
    "MINVALUE",
    "MORE",
    "MUMPS",
    "NAME",
    "NAMES",
    "NESTING",
    "NULLABLE",
    "NULLS",
    "NUMBER",
    "OCTETS",
    "ORDERING",
    "OTHERS",
    "OVERLAY",
    "PAD",
    "PARAMETER_MODE",
    "PARAMETER_NAME",
    "PARAMETER_ORDINAL_POSITION",
    "PARAMETER_SPECIFIC_CATALOG",
    "PARAMETER_SPECIFIC_NAME",
    "PARAMETER_SPECIFIC_SCHEMA",
    "PARTIAL",
    "PASCAL",
    "PATH",
    "PLACING",
    "PLI",
    "POSITION",
    "PRESERVE",
    "PRIOR",
    "PRIVILEGES",
    "PUBLIC",
    "READ",
    "RELATIVE",
    "RESTART",
    "RETURNED_CARDINALITY",
    "RETURNED_LENGTH",
    "RETURNED_OCTET_LENGTH",
    "RETURNED_SQLSTATE",
    "ROLE",
    "ROUTINE",
    "ROUTINE_CATALOG",
    "ROUTINE_NAME",
    "ROUTINE_SCHEMA",
    "ROW_COUNT",
    "SCALE",
    "SCHEMA_NAME",
    "SCOPE_CATALOG",
    "SCOPE_NAME",
    "SCOPE_SCHEMA",
    "SECTION",
    "SECURITY",
    "SELF",
    "SEQUENCE",
    "SERIALIZABLE",
    "SERVER_NAME",
    "SESSION",
    "SETS",
    "SIZE",
    "SOURCE",
    "SPACE",
    "SPECIFIC_NAME",
    "STATE",
    "STATEMENT",
    "STRUCTURE",
    "SUBCLASS_ORIGIN",
    "TABLE_NAME",
    "TIES",
    "TOP_LEVEL_COUNT",
    "TRANSACTION",
    "TRANSACTIONS_COMMITTED",
    "TRANSACTIONS_ROLLED_BACK",
    "TRANSACTION_ACTIVE",
    "TRANSFORM",
    "TRANSFORMS",
    "TRANSLATE",
    "TRIGGER_CATALOG",
    "TRIGGER_NAME",
    "TRIGGER_SCHEMA",
    "UNCOMMITTED",
    "UNNAMED",
    "USAGE",
    "USER_DEFINED_TYPE_CATALOG",
    "USER_DEFINED_TYPE_CODE",
    "USER_DEFINED_TYPE_NAME",
    "USER_DEFINED_TYPE_SCHEMA",
    "WORK",
    "WRITE"
  };
  
  private static String sLINE_END = (Execute.isOsWindows()? "\r\n": "\n");  
  private static String sKEYWORD_GENERATED_STARTS = "/** >> Generated by KeywordGenerator */"+sLINE_END;
  private static final String sKEYWORD_GENERATED_ENDS = "/** << Generated by KeywordGenerator */"+sLINE_END;
  private static final String sENUM_GENERATED_STARTS = "  // >> Generated by KeywordGenerator"+sLINE_END;
  private static final String sENUM_GENERATED_ENDS = "  // << Generated by KeywordGenerator"+sLINE_END;
  private String _sKeywordHeader = null;
  private String _sKeywordFooter = null;
  private String _sEnumHeader = null;
  private String _sEnumFooter = null;

  private String readLine(Reader rdr)
    throws IOException
  {
    String sLine = null;
    StringBuilder sb = new StringBuilder();
    int i;
    for (i = rdr.read(); (i != -1) && ((char)i != '\n'); i = rdr.read())
      sb.append((char)i);
    if (i != -1)
    {
      sb.append((char)i);
      sLine = sb.toString();
    }
    else
    {
      if (sb.length() > 0)
        sLine = sb.toString();
    }
    return sLine;
  } /* readLine */
  
  private void readKeywordHeaderAndFooter(Reader rdr)
    throws IOException
  {
    StringBuilder sbHeader = new StringBuilder();
    StringBuilder sbFooter = new StringBuilder();
    for (String sLine = readLine(rdr); 
         (sLine != null) && (!sLine.equals(sKEYWORD_GENERATED_STARTS));
         sLine=readLine(rdr))
      sbHeader.append(sLine);
    for (String sLine = readLine(rdr); 
        (sLine != null) && (!sLine.equals(sKEYWORD_GENERATED_ENDS));
        sLine=readLine(rdr));
    for (String sLine = readLine(rdr); 
        sLine != null;
        sLine=readLine(rdr))
      sbFooter.append(sLine);
    _sKeywordHeader = sbHeader.toString();
    _sKeywordFooter = sbFooter.toString();
  } /* readKeywordHeaderAndFooter */

  private void readEnumHeaderAndFooter(Reader rdr)
      throws IOException
    {
      StringBuilder sbHeader = new StringBuilder();
      StringBuilder sbFooter = new StringBuilder();
      for (String sLine = readLine(rdr); 
           (sLine != null) && (!sLine.equals(sENUM_GENERATED_STARTS));
           sLine=readLine(rdr))
        sbHeader.append(sLine);
      for (String sLine = readLine(rdr); 
          (sLine != null) && (!sLine.equals(sENUM_GENERATED_ENDS));
          sLine=readLine(rdr));
      for (String sLine = readLine(rdr); 
          sLine != null;
          sLine=readLine(rdr))
        sbFooter.append(sLine);
      _sEnumHeader = sbHeader.toString();
      _sEnumFooter = sbFooter.toString();
    } /* readKeywordHeaderAndFooter */
  
  private void writeKeywordHeader(Writer wr)
      throws IOException
  {
    wr.write(_sKeywordHeader);
    wr.write(sKEYWORD_GENERATED_STARTS);
  }
    
  private void writeKeywordFooter(Writer wr)
    throws IOException
  {
    wr.write(sKEYWORD_GENERATED_ENDS);
    wr.write(_sKeywordFooter);
  }

  private void writeEnumHeader(Writer wr)
      throws IOException
    {
    wr.write(_sEnumHeader);
      wr.write(sENUM_GENERATED_STARTS);
    } /* writeEnumHeader */
    
    private void writeEnumFooter(Writer wr)
      throws IOException
    {
      wr.write(sENUM_GENERATED_ENDS);
      wr.write(_sEnumFooter);
    } /* writeEnumFooter */
  
  private String getRule(String sKeyword)
  {
    String sRule = sKeyword.replace("-", "_").replace("2", "SQUARED");
    /* ANTLR4 reserved word */
    if (sRule.equalsIgnoreCase("more"))
      sRule = sKeyword + "_";
    return sRule;
  } /* getRule */
  
  private String formatKeywordLine(String sKeyword)
  {
    StringBuilder sbKeywordLine = new StringBuilder();
    String sRule = getRule(sKeyword);
    /***
    if (sRule.length() == 1)
      sbKeywordLine.append("//");
    ***/
    sbKeywordLine.append(sRule);
    sbKeywordLine.append(" :");
    if (sRule.length() > 1)
    {
      for (int j = 0; j < sKeyword.length(); j++)
      {
        sbKeywordLine.append(" ");
        char c = sKeyword.charAt(j);
        if (c == '-')
          sbKeywordLine.append("MINUS_SIGN");
        else if (c == '_')
          sbKeywordLine.append("UNDERSCORE");
        else if (c == '2')
          sbKeywordLine.append("TWO");
        else if (Character.isUpperCase(c))
          sbKeywordLine.append(c);
        else
          throw new IllegalArgumentException("Unexpected keyword character!");
      }
    }
    else
      sbKeywordLine.append(" '"+sRule.toUpperCase()+"' | '"+sRule.toLowerCase()+"'");
    sbKeywordLine.append(";");
    return sbKeywordLine.toString();
  } /* formatKeywordLine */
  
  private String formatEnumLine(String sKeyword, boolean bReserved, boolean bLast)
  {
    StringBuilder sbKeywordLine = new StringBuilder();
    sbKeywordLine.append("  ");
    sbKeywordLine.append(sKeyword.replace('-', '_'));
    sbKeywordLine.append("(\"");
    sbKeywordLine.append(sKeyword);
    sbKeywordLine.append("\", ");
    sbKeywordLine.append(String.valueOf(bReserved));
    sbKeywordLine.append(")");
    if (!bLast)
      sbKeywordLine.append(",");
    else
      sbKeywordLine.append(";");
    return sbKeywordLine.toString();
  } /* formatEnumLine */
  
  private void writeKeywordIdentifier(Writer wr)
    throws IOException
  {
    wr.write(sLINE_END+"IDENTIFIER: REGULAR_IDENTIFIER |  NON_RESERVED_KEYWORD | DELIMITED_IDENTIFIER;"+sLINE_END+sLINE_END);
  } /* writeKeywordIdentifier */
  
  private void writeKeywords(Writer wrEnum, Writer wrKeyword, String[] asKeyword, Set<String> setKeywords, boolean bReserved, boolean bLast)
    throws IOException
  {
    Set<String> setReserved = null; 
    if (bReserved)
    {
      wrKeyword.write("/** Reserved Keywords: */"+sLINE_END);
      wrEnum.write("  // Reserved Keywords:"+sLINE_END);
    }
    else
    {
      setReserved = new HashSet<String>(setKeywords); 
      wrKeyword.write("/** Non-Reserved Keywords: */"+sLINE_END);
      wrEnum.write("  // Non-Reserved Keywords:"+sLINE_END);
    }
    for (int i = 0; i < asKeyword.length; i++)
    {
      String sKeyword = asKeyword[i];
      if (!setKeywords.contains(sKeyword)) // throw out duplicates
      {
        setKeywords.add(sKeyword);
        wrKeyword.write(formatKeywordLine(sKeyword)+sLINE_END);
        boolean bLastLine = bLast && (i == (asKeyword.length-1)); 
        wrEnum.write(formatEnumLine(sKeyword, bReserved, bLastLine)+sLINE_END);
      }
    }
    if (!bReserved)
    {
      wrKeyword.write(sLINE_END+"NON_RESERVED_KEYWORD"+sLINE_END);
      for (int i = 0; i < asKeyword.length; i++)
      {
        String sKeyword = asKeyword[i];
        if (!setReserved.contains(sKeyword)) // throw out duplicates
        {
          setReserved.add(sKeyword);
          String sRule = getRule(sKeyword);
          /***
          if (sRule.length() == 1)
            wrKeyword.write("//");
          else
          ***/
            wrKeyword.write("  ");
          if (i == 0)
            wrKeyword.write(": "+sRule+sLINE_END);
          else
            wrKeyword.write("| "+sRule+sLINE_END);
        }
      }
      wrKeyword.write("  ;"+sLINE_END);
    }
  } /* writeKeywords */
  
  private KeywordGenerator()
    throws IOException
  {
    FileReader frEnum = new FileReader(sENUM);
    readEnumHeaderAndFooter(frEnum);
    frEnum.close();
    
    FileReader frKeyword = new FileReader(sKEYWORD);
    readKeywordHeaderAndFooter(frKeyword);
    frKeyword.close();
    
    FileWriter fwrEnum = new FileWriter(sENUM);
    FileWriter fwrKeyword = new FileWriter(sKEYWORD);

    writeEnumHeader(fwrEnum);
    writeKeywordHeader(fwrKeyword);

    Set<String> setKeywords = new HashSet<String>();
    writeKeywords(fwrEnum, fwrKeyword,asReserved,setKeywords,true,false);
    writeKeywordIdentifier(fwrKeyword);
    writeKeywords(fwrEnum, fwrKeyword,asNonReserved,setKeywords,false,true);
    
    writeKeywordFooter(fwrKeyword);
    writeEnumFooter(fwrEnum);
    fwrKeyword.close();
    fwrEnum.close();
  }
  /**
   * @param args
   */
  public static void main(String[] args)
  {
    try
    {
      new KeywordGenerator();
      System.exit(0);
      
    }
    catch (Exception e)
    {
      System.err.println(e.getClass().getName()+": "+e.getMessage());
      System.exit(8);
    }
  }

} /* KeywordGenerator */
