/*======================================================================
An ANTLR4 grammar for SQL manipulation language. 
Application : SQL utilities
Description : An ANTLR4 grammar for SQL manipulation language.
              based on http://savage.net.au/SQL/sql-2003-2.bnf
------------------------------------------------------------------------
Copyright  : 2016 Enter AG, Rüti ZH, Switzerland
Created    : 24.03.2016, Hartwig Thomas
======================================================================*/
grammar SqlDml;
import SqlExpression;

insertStatement : INSERT INTO tableName (fromConstructor | fromDefault | fromSubquery);
fromConstructor : (LEFT_PAREN insertColumnList RIGHT_PAREN)? overrideClause? VALUES assignedRow (COMMA assignedRow)*;
fromSubquery : (LEFT_PAREN insertColumnList RIGHT_PAREN)? overrideClause? queryExpression;  
insertColumnList : columnName (COMMA columnName)*;
// tableValueConstructor : VALUES rowValueExpression (COMMA rowValueExpression)*;
// ctTableValueConstructor: VALUES assignedRow (COMMA assignedRow)*;
/***
ctRowValueExpression
  : ctRowValueConstructor
  | LEFT_PAREN ctRowValueConstructor COMMA  ctRowValueConstructor (COMMA ctRowValueConstructor)* RIGHT_PAREN
  | ROW LEFT_PAREN ctRowValueConstructor (COMMA ctRowValueConstructor)* RIGHT_PAREN
  ;
ctRowValueConstructor
  : valueExpression
  | specialValue
  ;
***/

fromDefault : DEFAULT VALUES;

deleteStatement : DELETE FROM tableName (WHERE booleanValueExpression)?;

updateStatement : UPDATE tableName SET setClause (COMMA setClause)* (WHERE booleanValueExpression)?;
setClause 
  : setTarget EQUALS_OPERATOR updateSource 
  | LEFT_PAREN setTarget (COMMA setTarget)* RIGHT_PAREN EQUALS_OPERATOR assignedRow
  ;
setTarget 
  : updateTarget (PERIOD updateTarget)* PERIOD methodName
  | updateTarget 
  ;
updateTarget : columnName (LEFT_BRACKET simpleValueSpecification RIGHT_BRACKET)?;
updateSource 
  : valueExpression 
  | specialValue
  ;
assignedRow 
  : LEFT_PAREN updateSource (COMMA updateSource)+ RIGHT_PAREN
  | ROW LEFT_PAREN updateSource (COMMA updateSource)* RIGHT_PAREN
  | updateSource
  ;
