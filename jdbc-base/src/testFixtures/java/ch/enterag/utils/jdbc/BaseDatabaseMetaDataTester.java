package ch.enterag.utils.jdbc;

import java.util.*;
import java.sql.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.utils.*;
import ch.enterag.utils.database.*;

public abstract class BaseDatabaseMetaDataTester
{
  private DatabaseMetaData _dmd = null;
  public DatabaseMetaData getDatabaseMetaData() { return _dmd; }
  public BaseDatabaseMetaData getBaseDatabaseMetaData() { return (BaseDatabaseMetaData)_dmd; }
  /* setUp must create the database meta data and call this method */
  protected void setDatabaseMetaData(DatabaseMetaData dmd) { _dmd = dmd; }

  protected static void println(String s)
  {
    System.out.println("  "+s);
  } /* println */
  
  public static void print(ResultSet rs)
    throws SQLException
  {
    if ((rs != null) && (!rs.isClosed()))
    {
      ResultSetMetaData rsmd = rs.getMetaData();
      if (rsmd != null)
      {
        int iColumns = rsmd.getColumnCount();
        List<String> listColumns = new ArrayList<String>();
        StringBuilder sbLine = new StringBuilder();
        for (int iColumn = 0; iColumn < iColumns; iColumn++)
        {
          if (iColumn > 0)
            sbLine.append("\t");
          String sColumnName = rsmd.getColumnLabel(iColumn+1);
          sbLine.append(sColumnName);
          listColumns.add(sColumnName);
        }
        System.out.println(sbLine.toString());
        sbLine.setLength(0);
        while (rs.next())
        {
          for (int iColumn = 0; iColumn < iColumns; iColumn++)
          {
            if (iColumn > 0)
              sbLine.append("\t");
            String sColumnName = listColumns.get(iColumn);
            String sValue = String.valueOf(rs.getObject(iColumn+1));
            if (!rs.wasNull())
            {
              if (sColumnName.equals("DATA_TYPE"))
                sValue = sValue + " ("+SqlTypes.getTypeName(Integer.parseInt(sValue))+")";
            }
            else
              sValue = "(null)";
            sbLine.append(sValue);
          }
          System.out.println(sbLine.toString());
          sbLine.setLength(0);
        }
        rs.close();
      }
    }
    else if (rs.isClosed()) 
      throw new SQLException("Empty meta data result set!");
    else
      fail("Invalid meta data result set");
  } /* print */
  
  private String getCallingMethod(int iDepth)
  {
    String sCallingMethod = null;
    
    StackTraceElement[] asSte = Thread.currentThread().getStackTrace();
    sCallingMethod = asSte[iDepth].getMethodName();
    return sCallingMethod;
  } /* getCallingMethod */
  
  protected void enter()
  {
    System.out.println(getCallingMethod(3));
    System.out.flush();
  } /* enter */

  @After
  public void tearDown()
  {
    try
    {
      Connection conn = _dmd.getConnection();
      if ((conn != null) && (!conn.isClosed()))
      {
        conn.commit();
        conn.close();
      }
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* tearDown */
  
  /*--------------------------------------------------------------------
  Base tests for all database statements extending BaseDatabaseMetaData.
  --------------------------------------------------------------------*/
  @Test
  public void testGetConnection()
  {
    enter();
    try { _dmd.getConnection(); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testAllProceduresAreCallable()
  {
    enter();
    try { println(String.valueOf(_dmd.allProceduresAreCallable())); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testAllTablesAreSelectable()
  {
    enter();
    try { println(String.valueOf(_dmd.allTablesAreSelectable())); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetURL()
  {
    enter();
    try { println(String.valueOf(_dmd.getURL())); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetUserName()
  {
    enter();
    try { println(String.valueOf(_dmd.getUserName())); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testIsReadOnly()
  {
    enter();
    try { println(String.valueOf(_dmd.isReadOnly())); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testNullsAreSortedHigh()
  {
    enter();
    try { println(String.valueOf(_dmd.nullsAreSortedHigh())); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testNullsAreSortedLow()
  {
    enter();
    try { println(String.valueOf(_dmd.nullsAreSortedLow())); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testNullsAreSortedAtStart()
  {
    enter();
    try { println(String.valueOf(_dmd.nullsAreSortedAtStart())); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testNullsAreSortedAtEnd()
  {
    enter();
    try { println(String.valueOf(_dmd.nullsAreSortedAtEnd())); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetDatabaseProductName()
  {
    enter();
    try { println(String.valueOf(_dmd.getDatabaseProductName())); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetDatabaseProductVersion()
  {
    enter();
    try { println(String.valueOf(_dmd.getDatabaseProductVersion())); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetDriverName()
  {
    enter();
    try { println("Wrapped "+String.valueOf(_dmd.getDriverName())); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetDriverVersion()
  {
    enter();
    try { println(String.valueOf(_dmd.getDriverVersion())); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetDriverMajorVersion()
  {
    enter();
    println(String.valueOf(_dmd.getDriverMajorVersion())); 
  }
  
  @Test
  public void testGetDriverMinorVersion()
  {
    enter();
    println(String.valueOf(_dmd.getDriverMinorVersion())); 
  }
  
  @Test
  public void testUsesLocalFiles()
  {
    enter();
    try { println(String.valueOf(_dmd.usesLocalFiles())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsMixedCaseIdentifiers()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsMixedCaseIdentifiers())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testStoresUpperCaseIdentifiers()
  {
    enter();
    try { println(String.valueOf(_dmd.storesUpperCaseIdentifiers())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testStoresLowerCaseIdentifiers()
  {
    enter();
    try { println(String.valueOf(_dmd.storesLowerCaseIdentifiers())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testStoresMixedCaseIdentifiers()
  {
    enter();
    try { println(String.valueOf(_dmd.storesMixedCaseIdentifiers())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsMixedCaseQuotedIdentifiers()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsMixedCaseQuotedIdentifiers())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testStoresUpperCaseQuotedIdentifiers()
  {
    enter();
    try { println(String.valueOf(_dmd.storesUpperCaseQuotedIdentifiers())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testStoresLowerCaseQuotedIdentifiers()
  {
    enter();
    try { println(String.valueOf(_dmd.storesLowerCaseQuotedIdentifiers())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testStoresMixedCaseQuotedIdentifiers()
  {
    enter();
    try { println(String.valueOf(_dmd.storesMixedCaseQuotedIdentifiers())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetIdentifierQuoteString()
  {
    enter();
    try { println(String.valueOf(_dmd.getIdentifierQuoteString())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetSQLKeywords()
  {
    enter();
    try { println(String.valueOf(_dmd.getSQLKeywords())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetNumericFunctions()
  {
    enter();
    try { println(String.valueOf(_dmd.getNumericFunctions())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetStringFunctions()
  {
    enter();
    try { println(String.valueOf(_dmd.getStringFunctions())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetSystemFunctions()
  {
    enter();
    try { println(String.valueOf(_dmd.getSystemFunctions())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetTimeDateFunctions()
  {
    enter();
    try { println(String.valueOf(_dmd.getTimeDateFunctions())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetSearchStringEscape()
  {
    enter();
    try { println(String.valueOf(_dmd.getSearchStringEscape())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetExtraNameCharacters()
  {
    enter();
    try { println(String.valueOf(_dmd.getExtraNameCharacters())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsAlterTableWithAddColumn()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsAlterTableWithAddColumn())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsAlterTableWithDropColumn()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsAlterTableWithDropColumn())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsColumnAliasing()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsColumnAliasing())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testNullPlusNonNullIsNull()
  {
    enter();
    try { println(String.valueOf(_dmd.nullPlusNonNullIsNull())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsConvert()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsConvert())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsConvert_Int_Int()
  {
    enter();
    try 
    {
      List<Integer> listTypes = SqlTypes.getAllTypes();
      for (int i = 0; i < listTypes.size(); i++)
      {
        int iFromType = listTypes.get(i).intValue();
        for (int k = 0; k < listTypes.size(); k++)
        {
          int iToType = listTypes.get(k).intValue();
          println(SqlTypes.getTypeName(iFromType)+" to "+ 
                  SqlTypes.getTypeName(iToType)+": "+
                  String.valueOf(_dmd.supportsConvert(iFromType,iToType)));
        }
      }
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsTableCorrelationNames()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsTableCorrelationNames())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsDifferentTableCorrelationNames()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsDifferentTableCorrelationNames())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsExpressionsInOrderBy()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsExpressionsInOrderBy())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsOrderByUnrelated()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsOrderByUnrelated())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsGroupBy()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsGroupBy())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsGroupByUnrelated()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsGroupByUnrelated())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsGroupByBeyondSelect()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsGroupByBeyondSelect())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsLikeEscapeClause()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsLikeEscapeClause())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsMultipleResultSets()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsMultipleResultSets())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsMultipleTransactions()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsMultipleTransactions())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsNonNullableColumns()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsNonNullableColumns())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsMinimumSQLGrammar()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsMinimumSQLGrammar())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsCoreSQLGrammar()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsCoreSQLGrammar())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsExtendedSQLGrammar()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsExtendedSQLGrammar())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsANSI92EntryLevelSQL()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsANSI92EntryLevelSQL())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsANSI92IntermediateSQL()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsANSI92IntermediateSQL())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsANSI92FullSQL()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsANSI92FullSQL())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsIntegrityEnhancementFacility()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsIntegrityEnhancementFacility())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsOuterJoins()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsOuterJoins())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsFullOuterJoins()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsFullOuterJoins())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsLimitedOuterJoins()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsLimitedOuterJoins())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetSchemaTerm()
  {
    enter();
    try { println(String.valueOf(_dmd.getSchemaTerm())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetProcedureTerm()
  {
    enter();
    try { println(String.valueOf(_dmd.getProcedureTerm())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetCatalogTerm()
  {
    enter();
    try { println(String.valueOf(_dmd.getCatalogTerm())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testIsCatalogAtStart()
  {
    enter();
    try { println(String.valueOf(_dmd.isCatalogAtStart())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetCatalogSeparator()
  {
    enter();
    try { println(String.valueOf(_dmd.getCatalogSeparator())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsSchemasInDataManipulation()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsSchemasInDataManipulation())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsSchemasInProcedureCalls()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsSchemasInProcedureCalls())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsSchemasInTableDefinitions()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsSchemasInTableDefinitions())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsSchemasInIndexDefinitions()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsSchemasInIndexDefinitions())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsSchemasInPrivilegeDefinitions()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsSchemasInPrivilegeDefinitions())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsCatalogsInDataManipulation()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsCatalogsInDataManipulation())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsCatalogsInProcedureCalls()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsCatalogsInProcedureCalls())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsCatalogsInTableDefinitions()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsCatalogsInTableDefinitions())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsCatalogsInIndexDefinitions()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsCatalogsInIndexDefinitions())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsCatalogsInPrivilegeDefinitions()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsCatalogsInPrivilegeDefinitions())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsPositionedDelete()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsPositionedDelete())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsPositionedUpdate()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsPositionedUpdate())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsSelectForUpdate()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsSelectForUpdate())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsStoredProcedures()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsStoredProcedures())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsSubqueriesInComparisons()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsSubqueriesInComparisons())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsSubqueriesInExists()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsSubqueriesInExists())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsSubqueriesInIns()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsSubqueriesInIns())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsSubqueriesInQuantifieds()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsSubqueriesInQuantifieds())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsCorrelatedSubqueries()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsCorrelatedSubqueries())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsUnion()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsUnion())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsUnionAll()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsUnionAll())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsOpenCursorsAcrossCommit()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsOpenCursorsAcrossCommit())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsOpenCursorsAcrossRollback()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsOpenCursorsAcrossRollback())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsOpenStatementsAcrossCommit()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsOpenStatementsAcrossCommit())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsOpenStatementsAcrossRollback()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsOpenStatementsAcrossRollback())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxBinaryLiteralLength()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxBinaryLiteralLength())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxCharLiteralLength()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxCharLiteralLength())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxColumnNameLength()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxColumnNameLength())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxColumnsInGroupBy()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxColumnsInGroupBy())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxColumnsInIndex()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxColumnsInIndex())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxColumnsInOrderBy()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxColumnsInOrderBy())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxColumnsInSelect()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxColumnsInSelect())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxColumnsInTable()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxColumnsInTable())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxConnections()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxConnections())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxCursorNameLength()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxCursorNameLength())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxIndexLength()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxIndexLength())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxSchemaNameLength()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxSchemaNameLength())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxProcedureNameLength()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxProcedureNameLength())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxCatalogNameLength()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxCatalogNameLength())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxRowSize()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxRowSize())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testDoesMaxRowSizeIncludeBlobs()
  {
    enter();
    try { println(String.valueOf(_dmd.doesMaxRowSizeIncludeBlobs())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxStatementLength()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxStatementLength())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxStatements()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxStatements())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxTableNameLength()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxTableNameLength())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxTablesInSelect()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxTablesInSelect())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetMaxUserNameLength()
  {
    enter();
    try { println(String.valueOf(_dmd.getMaxUserNameLength())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetDefaultTransactionIsolation()
  {
    enter();
    try { println(String.valueOf(_dmd.getDefaultTransactionIsolation())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsTransactions()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsTransactions())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsTransactionIsolationLevel()
  {
    enter();
    try 
    { 
      println("NONE: "+
        String.valueOf(_dmd.supportsTransactionIsolationLevel(Connection.TRANSACTION_NONE))); 
      println("READ_COMMITTED: "+
        String.valueOf(_dmd.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED))); 
      println("READ_UNCOMMITTED: "+
        String.valueOf(_dmd.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_UNCOMMITTED))); 
      println("REPEATABLE_READ: "+
        String.valueOf(_dmd.supportsTransactionIsolationLevel(Connection.TRANSACTION_REPEATABLE_READ))); 
      println("REPEATABLE_SERIALIZABLE: "+
        String.valueOf(_dmd.supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE))); 
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsDataDefinitionAndDataManipulationTransactions()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsDataDefinitionAndDataManipulationTransactions())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testSupportsDataManipulationTransactionsOnly()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsDataManipulationTransactionsOnly())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testDataDefinitionCausesTransactionCommit()
  {
    enter();
    try { println(String.valueOf(_dmd.dataDefinitionCausesTransactionCommit())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testDataDefinitionIgnoredInTransactions()
  {
    enter();
    try { println(String.valueOf(_dmd.dataDefinitionIgnoredInTransactions())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetProcedures()
  {
    enter();
    try { print(_dmd.getProcedures(null,null,"%")); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetProcedureColumns()
  {
    enter();
    try { print(_dmd.getProcedureColumns(null,null,"%","%")); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetTables()
  {
    enter();
    try { print(_dmd.getTables(null,null,"%",null)); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetSchemas()
  {
    enter();
    try { print(_dmd.getSchemas()); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetCatalogs()
  {
    enter();
    try { print(_dmd.getCatalogs()); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetTableTypes()
  {
    enter();
    try { print(_dmd.getTableTypes()); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetColumns()
  {
    enter();
    try { print(_dmd.getColumns(null,null,"%","%")); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetColumnPrivileges()
  {
    enter();
    try { print(_dmd.getColumnPrivileges(null,null,"%","%")); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetTablePrivileges()
  {
    enter();
    try { print(_dmd.getTablePrivileges(null,null,"%")); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetBestRowIdentifier()
  {
    enter();
    try { print(_dmd.getBestRowIdentifier(null,null,"%",DatabaseMetaData.bestRowUnknown,true)); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetVersionColumns()
  {
    enter();
    try { print(_dmd.getVersionColumns(null,null,"%")); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetPrimaryKeys()
  {
    enter();
    try { print(_dmd.getPrimaryKeys(null,null,"%")); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetImportedKeys()
  {
    enter();
    try { print(_dmd.getImportedKeys(null,null,"%")); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetExportedKeys()
  {
    enter();
    try { print(_dmd.getExportedKeys(null,null,"%")); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetCrossReference()
  {
    enter();
    try { print(_dmd.getCrossReference(null,null,"%",null,null,"%")); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetTypeInfo()
  {
    enter();
    try { print(_dmd.getTypeInfo()); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testGetIndexInfo()
  {
    enter();
    try { print(_dmd.getIndexInfo(null,null,"%",false,false)); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testSupportsResultSetType()
  {
    enter();
    try 
    {
      List<Integer> listTypes = new ArrayList<Integer>();
      listTypes.add(Integer.valueOf(ResultSet.TYPE_FORWARD_ONLY));
      listTypes.add(Integer.valueOf(ResultSet.TYPE_SCROLL_INSENSITIVE));
      listTypes.add(Integer.valueOf(ResultSet.TYPE_SCROLL_SENSITIVE));
      for (int i = 0; i < listTypes.size(); i++)
      {
        int iType = listTypes.get(i).intValue();
        println(SqlTypes.getTypeName(iType)+": "+String.valueOf(_dmd.supportsResultSetType(iType))); 
      }
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testSupportsResultSetConcurrency()
  {
    enter();
    try 
    {
      
      List<Integer> listTypes = new ArrayList<Integer>();
      listTypes.add(Integer.valueOf(ResultSet.TYPE_FORWARD_ONLY));
      listTypes.add(Integer.valueOf(ResultSet.TYPE_SCROLL_INSENSITIVE));
      listTypes.add(Integer.valueOf(ResultSet.TYPE_SCROLL_SENSITIVE));
      for (int i = 0; i < listTypes.size(); i++)
      {
        int iType = listTypes.get(i).intValue();
        println(SqlTypes.getTypeName(iType)+": "+String.valueOf(_dmd.supportsResultSetConcurrency(iType,ResultSet.CONCUR_READ_ONLY))+", "+String.valueOf(_dmd.supportsResultSetConcurrency(iType,ResultSet.CONCUR_UPDATABLE))); 
      }
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Test
  public void testOwnUpdatesAreVisible()
  {
    enter();
    try 
    {
      List<Integer> listTypes = SqlTypes.getAllTypes();
      for (int i = 0; i < listTypes.size(); i++)
      {
        int iType = listTypes.get(i).intValue();
        println(SqlTypes.getTypeName(iType)+": "+String.valueOf(_dmd.ownUpdatesAreVisible(iType))); 
      }
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testOwnDeletesAreVisible()
  {
    enter();
    try 
    {
      List<Integer> listTypes = SqlTypes.getAllTypes();
      for (int i = 0; i < listTypes.size(); i++)
      {
        int iType = listTypes.get(i).intValue();
        println(SqlTypes.getTypeName(iType)+": "+String.valueOf(_dmd.ownDeletesAreVisible(iType))); 
      }
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testOwnInsertsAreVisible()
  {
    enter();
    try 
    {
      List<Integer> listTypes = SqlTypes.getAllTypes();
      for (int i = 0; i < listTypes.size(); i++)
      {
        int iType = listTypes.get(i).intValue();
        println(SqlTypes.getTypeName(iType)+": "+String.valueOf(_dmd.ownInsertsAreVisible(iType))); 
      }
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testOthersUpdatesAreVisible()
  {
    enter();
    try 
    {
      List<Integer> listTypes = SqlTypes.getAllTypes();
      for (int i = 0; i < listTypes.size(); i++)
      {
        int iType = listTypes.get(i).intValue();
        println(SqlTypes.getTypeName(iType)+": "+String.valueOf(_dmd.othersUpdatesAreVisible(iType))); 
      }
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testOthersDeletesAreVisible()
  {
    enter();
    try 
    {
      List<Integer> listTypes = SqlTypes.getAllTypes();
      for (int i = 0; i < listTypes.size(); i++)
      {
        int iType = listTypes.get(i).intValue();
        println(SqlTypes.getTypeName(iType)+": "+String.valueOf(_dmd.othersDeletesAreVisible(iType))); 
      }
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testOthersInsertsAreVisible()
  {
    enter();
    try 
    {
      List<Integer> listTypes = SqlTypes.getAllTypes();
      for (int i = 0; i < listTypes.size(); i++)
      {
        int iType = listTypes.get(i).intValue();
        println(SqlTypes.getTypeName(iType)+": "+String.valueOf(_dmd.othersInsertsAreVisible(iType))); 
      }
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testUpdatesAreDetected()
  {
    enter();
    try 
    {
      List<Integer> listTypes = SqlTypes.getAllTypes();
      for (int i = 0; i < listTypes.size(); i++)
      {
        int iType = listTypes.get(i).intValue();
        println(SqlTypes.getTypeName(iType)+": "+String.valueOf(_dmd.updatesAreDetected(iType))); 
      }
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testDeletesAreDetected()
  {
    enter();
    try 
    {
      List<Integer> listTypes = SqlTypes.getAllTypes();
      for (int i = 0; i < listTypes.size(); i++)
      {
        int iType = listTypes.get(i).intValue();
        println(SqlTypes.getTypeName(iType)+": "+String.valueOf(_dmd.deletesAreDetected(iType))); 
      }
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testInsertsAreDetected()
  {
    enter();
    try 
    {
      List<Integer> listTypes = SqlTypes.getAllTypes();
      for (int i = 0; i < listTypes.size(); i++)
      {
        int iType = listTypes.get(i).intValue();
        println(SqlTypes.getTypeName(iType)+": "+String.valueOf(_dmd.insertsAreDetected(iType))); 
      }
    } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testSupportsBatchUpdates()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsBatchUpdates())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetUDTs()
  {
    enter();
    try { print(_dmd.getUDTs(null,null,"%",null)); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testSupportsSavepoints()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsSavepoints())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testSupportsNamedParameters()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsNamedParameters())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testSupportsMultipleOpenResults()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsMultipleOpenResults())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testSupportsGetGeneratedKeys()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsGetGeneratedKeys())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetSuperTypes()
  {
    enter();
    try { print(_dmd.getSuperTypes(null,null,"%")); } 
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetSuperTables()
  {
    enter();
    try { print(_dmd.getSuperTables(null,null,"%")); } 
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetAttributes()
  {
    enter();
    try { print(_dmd.getAttributes(null,null,"%","%")); } 
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testSupportsResultSetHoldability()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsResultSetHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT))+", "+String.valueOf(_dmd.supportsResultSetHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT))); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetDatabaseMajorVersion()
  {
    enter();
    try { println(String.valueOf(_dmd.getDatabaseMajorVersion())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetDatabaseMinorVersion()
  {
    enter();
    try { println(String.valueOf(_dmd.getDatabaseMinorVersion())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetSQLStateType()
  {
    enter();
    try { println(String.valueOf(_dmd.getSQLStateType())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testLocatorsUpdateCopy()
  {
    enter();
    try { println(String.valueOf(_dmd.locatorsUpdateCopy())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testSupportsStatementPooling()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsStatementPooling())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetRowIdLifetime()
  {
    enter();
    try { println(String.valueOf(_dmd.getRowIdLifetime())); } 
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetSchemas_String_String()
  {
    enter();
    try { print(_dmd.getSchemas(null,"%")); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testSupportsStoredFunctionsUsingCallSyntax()
  {
    enter();
    try { println(String.valueOf(_dmd.supportsStoredFunctionsUsingCallSyntax())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testAutoCommitFailureClosesAllResultSets()
  {
    enter();
    try { println(String.valueOf(_dmd.autoCommitFailureClosesAllResultSets())); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetClientInfoProperties()
  {
    enter();
    try { print(_dmd.getClientInfoProperties()); } 
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetFunctions()
  {
    enter();
    try { print(_dmd.getFunctions(null,null,"%")); } 
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetFunctionColumns()
  {
    enter();
    try { print(_dmd.getFunctionColumns(null,null,"%","%")); } 
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGetPseudoColumns()
  {
    enter();
    try { print(_dmd.getPseudoColumns(null,null,"%","%")); } 
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

  @Test
  public void testGeneratedKeyAlwaysReturned()
  {
    enter();
    try { println(String.valueOf(_dmd.generatedKeyAlwaysReturned())); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }

} /* class BaseDatabaseMetaDataTester */
