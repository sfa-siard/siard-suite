/*======================================================================
An ANTLR4 grammar for SQL statements. 
Application : SQL utilities
Description : An ANTLR4 grammar for SQL statements.
              based on http://savage.net.au/SQL/sql-2003-2.bnf
------------------------------------------------------------------------
Copyright  : 2016 Enter AG, Rüti ZH, Switzerland
Created    : 11.03.2016, Hartwig Thomas
======================================================================*/
grammar SqlDdlSchema;
import SqlIdentifier, SqlEnums;

dropSchemaStatement : DROP SCHEMA schemaName dropBehavior;
createSchemaStatement : CREATE SCHEMA schemaName (AUTHORIZATION authorizationName)?;
