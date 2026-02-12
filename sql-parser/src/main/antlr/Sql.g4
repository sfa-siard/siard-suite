/*======================================================================
An ANTLR4 grammar for SQL statements. 
Application : SQL utilities
Description : An ANTLR4 grammar for SQL statements.
              based on http://savage.net.au/SQL/sql-2003-2.bnf
------------------------------------------------------------------------
Copyright  : 2016 Enter AG, Rüti ZH, Switzerland
Created    : 11.03.2016, Hartwig Thomas
======================================================================*/
grammar Sql;
import SqlDdlSchema, SqlDdlTable, SqlDdlUdt, SqlDdlRoutine, SqlDml;

sqlStatement 
  : querySpecification
  | ddlStatement
  | dmlStatement
  ;
  
ddlStatement
 : createSchemaStatement
 | dropSchemaStatement
 | createTableStatement
 | dropTableStatement
 | alterTableStatement
 | createViewStatement
 | dropViewStatement
 | createTypeStatement
 | dropTypeStatement
 | alterTypeStatement
 | createMethodStatement
 | dropMethodStatement
 | createFunctionStatement
 | dropFunctionStatement
 | createProcedureStatement
 | dropProcedureStatement
 | createTriggerStatement
 | dropTriggerStatement
 ;
 
dmlStatement
  : insertStatement
  | deleteStatement
  | updateStatement
  ;
