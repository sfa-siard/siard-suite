/*======================================================================
An ANTLR4 grammar for SQL identifiers. 
Application : SQL utilities
Description : An ANTLR4 grammar for SQL identifiers.
              based on http://savage.net.au/SQL/sql-2003-2.bnf
------------------------------------------------------------------------
Copyright  : 2016 Enter AG, Rüti ZH, Switzerland
Created    : 24.03.2016, Hartwig Thomas
======================================================================*/
grammar SqlIdentifier;
import LexSql;

userName : IDENTIFIER;
roleName : IDENTIFIER;
authorizationName : IDENTIFIER;
fieldName : IDENTIFIER;
columnName: IDENTIFIER;
attributeName : IDENTIFIER;
castIdentifier : IDENTIFIER;
parameterName : IDENTIFIER;
queryName : IDENTIFIER;
correlationName : IDENTIFIER;
windowName : IDENTIFIER;
catalogName : IDENTIFIER;
methodName : IDENTIFIER;
variableName : COLON IDENTIFIER;
schemaName 
  : IDENTIFIER PERIOD IDENTIFIER
  | IDENTIFIER 
  ;
qualifiedId 
  : IDENTIFIER PERIOD IDENTIFIER PERIOD IDENTIFIER
  | IDENTIFIER PERIOD IDENTIFIER
  | IDENTIFIER 
  ;
identifierChain : IDENTIFIER (PERIOD IDENTIFIER)*;
tableName : qualifiedId;
constraintName : qualifiedId;
udtName : qualifiedId;
specificMethodName : qualifiedId;
routineName : qualifiedId;
triggerName : qualifiedId;
sequenceName :  qualifiedId;
columnReference : identifierChain;
targetArrayReference : identifierChain;
arrayReference : identifierChain;
