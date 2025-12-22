package ch.enterag.sqlparser;

import ch.enterag.utils.logging.*;
import ch.enterag.sqlparser.antlr4.*;
import ch.enterag.sqlparser.ddl.*;
import ch.enterag.sqlparser.generated.*;

public class DdlStatement
  extends SqlBase
{
  /** logger */
  private static IndentLogger _il = IndentLogger.getIndentLogger(DdlStatement.class.getName());

  /*==================================================================*/
  /** visitor initializes fields from parse tree.
   */
  private class DsVisitor extends EnhancedSqlBaseVisitor<DdlStatement>
  {
    @Override
    public DdlStatement visitCreateSchemaStatement(SqlParser.CreateSchemaStatementContext ctx)
    {
      setCreateSchemaStatement(getSqlFactory().newCreateSchemaStatement());
      getCreateSchemaStatement().parse(ctx);
      return DdlStatement.this;
    }
    @Override
    public DdlStatement visitDropSchemaStatement(SqlParser.DropSchemaStatementContext ctx)
    {
      setDropSchemaStatement(getSqlFactory().newDropSchemaStatement());
      getDropSchemaStatement().parse(ctx);
      return DdlStatement.this;
    }
    @Override
    public DdlStatement visitCreateTableStatement(SqlParser.CreateTableStatementContext ctx)
    {
      setCreateTableStatement(getSqlFactory().newCreateTableStatement());
      getCreateTableStatement().parse(ctx);
      return DdlStatement.this;
    }
    @Override
    public DdlStatement visitAlterTableStatement(SqlParser.AlterTableStatementContext ctx)
    {
      setAlterTableStatement(getSqlFactory().newAlterTableStatement());
      getAlterTableStatement().parse(ctx);
      return DdlStatement.this;
    }
    @Override
    public DdlStatement visitDropTableStatement(SqlParser.DropTableStatementContext ctx)
    {
      setDropTableStatement(getSqlFactory().newDropTableStatement());
      getDropTableStatement().parse(ctx);
      return DdlStatement.this;
    }
    @Override
    public DdlStatement visitCreateViewStatement(SqlParser.CreateViewStatementContext ctx)
    {
      setCreateViewStatement(getSqlFactory().newCreateViewStatement());
      getCreateViewStatement().parse(ctx);
      return DdlStatement.this;
    }
    @Override
    public DdlStatement visitDropViewStatement(SqlParser.DropViewStatementContext ctx)
    {
      setDropViewStatement(getSqlFactory().newDropViewStatement());
      getDropViewStatement().parse(ctx);
      return DdlStatement.this;
    }
    @Override
    public DdlStatement visitCreateTypeStatement(SqlParser.CreateTypeStatementContext ctx)
    {
      setCreateTypeStatement(getSqlFactory().newCreateTypeStatement());
      getCreateTypeStatement().parse(ctx);
      return DdlStatement.this;
    }
    @Override
    public DdlStatement visitAlterTypeStatement(SqlParser.AlterTypeStatementContext ctx)
    {
      setAlterTypeStatement(getSqlFactory().newAlterTypeStatement());
      getAlterTypeStatement().parse(ctx);
      return DdlStatement.this;
    }
    @Override
    public DdlStatement visitDropTypeStatement(SqlParser.DropTypeStatementContext ctx)
    {
      setDropTypeStatement(getSqlFactory().newDropTypeStatement());
      getDropTypeStatement().parse(ctx);
      return DdlStatement.this;
    }
    @Override
    public DdlStatement visitCreateMethodStatement(SqlParser.CreateMethodStatementContext ctx)
    {
      setCreateMethodStatement(getSqlFactory().newCreateMethodStatement());
      getCreateMethodStatement().parse(ctx);
      return DdlStatement.this;
    }
    @Override
    public DdlStatement visitDropMethodStatement(SqlParser.DropMethodStatementContext ctx)
    {
      setDropMethodStatement(getSqlFactory().newDropMethodStatement());
      getDropMethodStatement().parse(ctx);
      return DdlStatement.this;
    }
    @Override
    public DdlStatement visitCreateFunctionStatement(SqlParser.CreateFunctionStatementContext ctx)
    {
      setCreateFunctionStatement(getSqlFactory().newCreateFunctionStatement());
      getCreateFunctionStatement().parse(ctx);
      return DdlStatement.this;
    }
    @Override
    public DdlStatement visitDropFunctionStatement(SqlParser.DropFunctionStatementContext ctx)
    {
      setDropFunctionStatement(getSqlFactory().newDropFunctionStatement());
      getDropFunctionStatement().parse(ctx);
      return DdlStatement.this;
    }
    @Override
    public DdlStatement visitCreateProcedureStatement(SqlParser.CreateProcedureStatementContext ctx)
    {
      setCreateProcedureStatement(getSqlFactory().newCreateProcedureStatement());
      getCreateProcedureStatement().parse(ctx);
      return DdlStatement.this;
    }
    @Override
    public DdlStatement visitDropProcedureStatement(SqlParser.DropProcedureStatementContext ctx)
    {
      setDropProcedureStatement(getSqlFactory().newDropProcedureStatement());
      getDropProcedureStatement().parse(ctx);
      return DdlStatement.this;
    }
    @Override
    public DdlStatement visitCreateTriggerStatement(SqlParser.CreateTriggerStatementContext ctx)
    {
      setCreateTriggerStatement(getSqlFactory().newCreateTriggerStatement());
      getCreateTriggerStatement().parse(ctx);
      return DdlStatement.this;
    }
    @Override
    public DdlStatement visitDropTriggerStatement(SqlParser.DropTriggerStatementContext ctx)
    {
      setDropTriggerStatement(getSqlFactory().newDropTriggerStatement());
      getDropTriggerStatement().parse(ctx);
      return DdlStatement.this;
    }
  }
  /*==================================================================*/

  private DsVisitor _visitor = new DsVisitor();
  private DsVisitor getVisitor() { return _visitor; }

  private CreateSchemaStatement _css = null;
  public CreateSchemaStatement getCreateSchemaStatement() { return _css; }
  public void setCreateSchemaStatement(CreateSchemaStatement css) { _css = css; }
  
  private DropSchemaStatement _dss = null;
  public DropSchemaStatement getDropSchemaStatement() { return _dss; }
  public void setDropSchemaStatement(DropSchemaStatement dss) { _dss = dss; }
  
  private CreateTableStatement _cts = null;
  public CreateTableStatement getCreateTableStatement() { return _cts; }
  public void setCreateTableStatement(CreateTableStatement cts) { _cts = cts; }
  
  private AlterTableStatement _ats = null;
  public AlterTableStatement getAlterTableStatement() { return _ats; }
  public void setAlterTableStatement(AlterTableStatement ats) { _ats = ats; }
  
  private DropTableStatement _dts = null;
  public DropTableStatement getDropTableStatement() { return _dts; }
  public void setDropTableStatement(DropTableStatement dts) { _dts = dts; }
  
  private CreateViewStatement _cvs = null;
  public CreateViewStatement getCreateViewStatement() { return _cvs; }
  public void setCreateViewStatement(CreateViewStatement cvs) { _cvs = cvs; }
  
  private DropViewStatement _dvs = null;
  public DropViewStatement getDropViewStatement() { return _dvs; }
  public void setDropViewStatement(DropViewStatement dvs) { _dvs = dvs; }
  
  private CreateTypeStatement _ctys = null;
  public CreateTypeStatement getCreateTypeStatement() { return _ctys; }
  public void setCreateTypeStatement(CreateTypeStatement ctys) { _ctys = ctys; }
  
  private AlterTypeStatement _atys = null;
  public AlterTypeStatement getAlterTypeStatement() { return _atys; }
  public void setAlterTypeStatement(AlterTypeStatement atys) { _atys = atys; }
  
  private DropTypeStatement _dtys = null;
  public DropTypeStatement getDropTypeStatement() { return _dtys; }
  public void setDropTypeStatement(DropTypeStatement dtys) { _dtys = dtys; }
  
  private CreateMethodStatement _cms = null;
  public CreateMethodStatement getCreateMethodStatement() { return _cms; }
  public void setCreateMethodStatement(CreateMethodStatement cms) { _cms = cms; }
  
  private DropMethodStatement _dms = null;
  public DropMethodStatement getDropMethodStatement() { return _dms; }
  public void setDropMethodStatement(DropMethodStatement dms) { _dms = dms; }
  
  private CreateFunctionStatement _cfs = null;
  public CreateFunctionStatement getCreateFunctionStatement() { return _cfs; }
  public void setCreateFunctionStatement(CreateFunctionStatement cfs) { _cfs = cfs; }
  
  private DropFunctionStatement _dfs = null;
  public DropFunctionStatement getDropFunctionStatement() { return _dfs; }
  public void setDropFunctionStatement(DropFunctionStatement dfs) { _dfs = dfs; }
  
  private CreateProcedureStatement _cps = null;
  public CreateProcedureStatement getCreateProcedureStatement() { return _cps; }
  public void setCreateProcedureStatement(CreateProcedureStatement cps) { _cps = cps; }
  
  private DropProcedureStatement _dps = null;
  public DropProcedureStatement getDropProcedureStatement() { return _dps; }
  public void setDropProcedureStatement(DropProcedureStatement dps) { _dps = dps; }
  
  private CreateTriggerStatement _ctrs = null;
  public CreateTriggerStatement getCreateTriggerStatement() { return _ctrs; }
  public void setCreateTriggerStatement(CreateTriggerStatement ctrs) { _ctrs = ctrs; }
  
  private DropTriggerStatement _dtrs = null;
  public DropTriggerStatement getDropTriggerStatement() { return _dtrs; }
  public void setDropTriggerStatement(DropTriggerStatement dtrs) { _dtrs = dtrs; }
  
  /*------------------------------------------------------------------*/
  /** format the DDL statement.
   * @return the SQL string corresponding to the fields of the DDL statement.
   */
  @Override
  public String format()
  {
    String sStatement = null;
    if (getCreateSchemaStatement() != null)
      sStatement = getCreateSchemaStatement().format();
    else if (getDropSchemaStatement() != null)
      sStatement = getDropSchemaStatement().format();
    else if (getCreateTableStatement() != null)
      sStatement = getCreateTableStatement().format();
    else if (getAlterTableStatement() != null)
      sStatement = getAlterTableStatement().format();
    else if (getDropTableStatement() != null)
      sStatement = getDropTableStatement().format();
    else if (getCreateViewStatement() != null)
      sStatement = getCreateViewStatement().format();
    else if (getDropViewStatement() != null)
      sStatement = getDropViewStatement().format();
    else if (getCreateTypeStatement() != null)
      sStatement = getCreateTypeStatement().format();
    else if (getAlterTypeStatement() != null)
      sStatement = getCreateTypeStatement().format();
    else  if (getDropTypeStatement() != null)
      sStatement = getDropTypeStatement().format();
    else if (getCreateMethodStatement() != null)
      sStatement = getCreateMethodStatement().format();
    else if (getDropMethodStatement() != null)
      sStatement = getDropMethodStatement().format();
    else if (getCreateFunctionStatement() != null)
      sStatement = getCreateFunctionStatement().format();
    else if (getDropFunctionStatement() != null)
      sStatement = getDropFunctionStatement().format();
    else if (getCreateProcedureStatement() != null)
      sStatement = getCreateProcedureStatement().format();
    else if (getDropProcedureStatement() != null)
      sStatement = getDropProcedureStatement().format();
    else if (getCreateTriggerStatement() != null)
      sStatement = getCreateTriggerStatement().format();
    else if (getDropTriggerStatement() != null)
      sStatement = getDropTriggerStatement().format();
    return sStatement;
  } /* format */

  /*------------------------------------------------------------------*/
  /** parse the DDL statement from the parsing tree context.
   * @param ctx parsing context (tree).
   * @throws NullPointerException if no parsing tree is available. 
   */
  public void parse(SqlParser.DdlStatementContext ctx)
  {
    setContext(ctx);
    getVisitor().visit(getContext());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** parse the DDL statement from SQL.
   * @param sSql SQL.
   */
  @Override
  public void parse(String sSql)
  {
    setParser(newSqlParser(sSql));
    parse(getParser().ddlStatement());
  } /* parse */
   
  /*------------------------------------------------------------------*/
  /** initialize a DDL statement.
   * Only one parameter not null!
   * @param css create schema statement.
   * @param dss drop schema statement.
   * @param cts create table statement.
   * @param ats alter table statement.
   * @param dts drop table statement.
   * @param cvs create view statement.
   * @param dvs drop view statement.
   * @param ctys create type statement.
   * @param atys alter type statement.
   * @param dtys drop type statement.
   * @param cms create method statement.
   * @param dms drop method statement.
   * @param cfs create function statement.
   * @param dfs drop function statement.
   * @param cps create procedure statement.
   * @param dps drop procedure statement.
   * @param ctrs create trigger statement.
   * @param dtrs drop trigger statement. 
   */
  public void initialize(
    CreateSchemaStatement css,
    DropSchemaStatement dss,
    CreateTableStatement cts,
    AlterTableStatement ats,
    DropTableStatement dts,
    CreateViewStatement cvs,
    DropViewStatement dvs,
    CreateTypeStatement ctys,
    AlterTypeStatement atys,
    DropTypeStatement dtys,
    CreateMethodStatement cms,
    DropMethodStatement dms,
    CreateFunctionStatement cfs,
    DropFunctionStatement dfs,
    CreateProcedureStatement cps,
    DropProcedureStatement dps,
    CreateTriggerStatement ctrs,
    DropTriggerStatement dtrs
    )
  {
    _il.enter();
    setCreateSchemaStatement(css);
    setDropSchemaStatement(dss);
    setCreateTableStatement(cts);
    setAlterTableStatement(ats);
    setDropTableStatement(dts);
    setCreateViewStatement(cvs);
    setDropViewStatement(dvs);
    setCreateTypeStatement(ctys);
    setAlterTypeStatement(atys);
    setDropTypeStatement(dtys);
    setCreateMethodStatement(cms);
    setDropMethodStatement(dms);
    setCreateFunctionStatement(cfs);
    setDropFunctionStatement(dfs);
    setCreateProcedureStatement(cps);
    setDropProcedureStatement(dps);
    setCreateTriggerStatement(ctrs);
    setDropTriggerStatement(dtrs);
    _il.exit();
  } /* initialize */
  
  /*------------------------------------------------------------------*/
  /** constructor with factory only to be called by factory.
   * @param sf factory.
   */
  public DdlStatement(SqlFactory sf)
  {
    super(sf);
  } /* constructor */
  
} /* class DdlStatement */
