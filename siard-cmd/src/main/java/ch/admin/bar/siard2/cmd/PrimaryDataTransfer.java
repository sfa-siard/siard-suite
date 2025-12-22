/*======================================================================
PrimaryDataTransfer opens a record set to a database for up- or download. 
Application : Siard2
Description : Opens a record set to a database for up- or download.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2008
Created    : 01.09.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.cmd;

import java.io.*;
import java.sql.*;
import java.util.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.identifier.*;
import ch.admin.bar.siard2.api.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/*====================================================================*/
/** Opens a record set to a database for up- or download.
 @author Hartwig Thomas
 */
@Slf4j
public class PrimaryDataTransfer
{
  protected Connection _conn = null;
  protected Archive _archive = null;
  protected ArchiveMapping _am = null;
  protected int _iQueryTimeoutSeconds = 30;
  public void setQueryTimeout(int iQueryTimeoutSeconds) { _iQueryTimeoutSeconds = iQueryTimeoutSeconds; }
  protected boolean _bSupportsArrays = false;
  public boolean supportsArrays() { return _bSupportsArrays; }
  protected boolean _bSupportsDistincts = false;
  public boolean supportsDistincts() { return _bSupportsDistincts; }
  protected boolean _bSupportsUdts = false;
  public boolean supportsUdts() { return _bSupportsUdts; }

  /*------------------------------------------------------------------*/
  /** issue a SELECT query for all fields of the table.
   * @param table table.
   * @param sm mapping of names in schema (null for read-only).
   * @throws IOException if an I/O error occurred.
   * @throws SQLException if a database error occurred.
   */
  protected ResultSet openTable(Table table, SchemaMapping sm)
    throws IOException, SQLException
  {
    MetaTable mt = table.getMetaTable();
    /* schema mapping is null on download */
    TableMapping tm = null;
    if (sm != null)
      tm = sm.getTableMapping(mt.getName());
    /* create query */
    StringBuilder sbSql = new StringBuilder("SELECT\r\n");
    List<List<String>> llColumnNames = mt.getColumnNames(supportsArrays(),supportsUdts());
    for (int iColumn = 0; iColumn < llColumnNames.size(); iColumn++)
    {
      if (iColumn > 0)
        sbSql.append(",\r\n");
      sbSql.append("  ");
      List<String> listColumnName = llColumnNames.get(iColumn);
      StringBuilder sbColumnName = new StringBuilder();
      for (int i = 0; i < listColumnName.size(); i++)
      {
        if (i > 0)
          sbColumnName.append(".");
        sbColumnName.append(listColumnName.get(i));
      }
      String sExtendedColumnName = sbColumnName.toString();
      if (tm != null)
        sExtendedColumnName = tm.getMappedExtendedColumnName(sExtendedColumnName);
      String formattedColumnName = SqlLiterals.formatId(sExtendedColumnName);
      if (!formattedColumnName.startsWith("\"") && formattedColumnName.equals(formattedColumnName.toUpperCase())) {
        formattedColumnName = "\"" + formattedColumnName.replace("\"", "\"\"") + "\"";
      }
      sbSql.append(formattedColumnName);
    }
    String sSchemaName = mt.getParentMetaSchema().getName();
    if (sm != null)
      sSchemaName = sm.getMappedSchemaName();
    String sTableName = mt.getName();
    if (tm != null)
      sTableName = tm.getMappedTableName();
    QualifiedId qiTable = new QualifiedId(null,sSchemaName,sTableName);
    sbSql.append("\r\n FROM "+qiTable.format());
    /* execute query */
    val sqlStatement = sbSql.toString();
    LOG.trace("SQL statement: '{}'", sqlStatement);

    int iHoldability = ResultSet.HOLD_CURSORS_OVER_COMMIT;
    if (sm == null)
    	iHoldability = ResultSet.HOLD_CURSORS_OVER_COMMIT;
    int iConcurrency = ResultSet.CONCUR_UPDATABLE;
    if (sm == null)
    	iConcurrency = ResultSet.CONCUR_READ_ONLY;
    int iType = ResultSet.TYPE_FORWARD_ONLY;
    if (sm == null)
    	iType = ResultSet.TYPE_FORWARD_ONLY;
    _conn.setHoldability(iHoldability);
    Statement stmt = _conn.createStatement(iType,iConcurrency,iHoldability);
    stmt.setQueryTimeout(_iQueryTimeoutSeconds);
    ResultSet rs = stmt.executeQuery(sbSql.toString());

    LOG.debug("Data from table '{}.{}' successfully loaded", qiTable.getSchema(), qiTable.getName());

    return rs;
  } /* openTable */

  /*------------------------------------------------------------------*/
  /** constructor
   * @param conn database connection.
   * @param archive SIARD archive.
   * @param am mapping of names in archive.
   * @param bSupportsArrays true, if database supports Arrays.
   * @param bSupportsDistincts true, if database supports DISTINCTs.
   * @param bSupportsUdts true, if database supports UDTs.
   */
  protected PrimaryDataTransfer(Connection conn, Archive archive, ArchiveMapping am, 
    boolean bSupportsArrays, boolean bSupportsDistincts, boolean bSupportsUdts)
  {
    _conn = conn;
    _archive = archive;
    _am = am;
    _bSupportsArrays = bSupportsArrays;
    _bSupportsDistincts = bSupportsDistincts;
    _bSupportsUdts = bSupportsUdts;
  } /* constructor PrimaryDataTransfer */

} /* class PrimaryDataTransfer */
