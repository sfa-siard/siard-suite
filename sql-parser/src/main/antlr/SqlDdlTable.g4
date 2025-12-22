/*======================================================================
An ANTLR4 grammar for SQL statements. 
Application : SQL utilities
Description : An ANTLR4 grammar for SQL statements.
              based on http://savage.net.au/SQL/sql-2003-2.bnf
------------------------------------------------------------------------
Copyright  : 2016 Enter AG, Rüti ZH, Switzerland
Created    : 11.03.2016, Hartwig Thomas
======================================================================*/
grammar SqlDdlTable;
import SqlDataTypes, SqlExpression;

dropTableStatement : DROP TABLE tableName dropBehavior;
createTableStatement : CREATE tableScope? TABLE tableName tableContents 
  commitAction?;
alterTableStatement : ALTER TABLE tableName alterTableAction;
dropViewStatement : DROP VIEW tableName dropBehavior;
createViewStatement : CREATE RECURSIVE? VIEW tableName viewSpecification 
  AS queryExpression (WITH levels? CHECK OPTION)?;
alterTableAction
  : ADD COLUMN? columnDefinition # addColumnDefinition
  | DROP COLUMN? columnName dropBehavior # dropColumnDefinition
  | ALTER COLUMN? columnName alterColumnAction # alterColumnDefinition
  | ADD tableConstraintDefinition # addTableConstraintDefinition
  | DROP CONSTRAINT constraintName dropBehavior # dropTableConstraintDefinition
  ;
alterColumnAction
  : SET DEFAULT defaultOption #setColumnDefaultClause
  | DROP DEFAULT # dropColumnDefaultClause
  | ADD SCOPE tableName # addColumnScopeClause
  | DROP SCOPE dropBehavior # dropColumnScopeClause
  ;
  
tableContents
  : tableElementList
  | OF udtName subtableClause? tableElementList
  | (LEFT_PAREN columnName (COMMA columnName)* RIGHT_PAREN)? AS LEFT_PAREN queryExpression RIGHT_PAREN withOrWithoutData 
  ;
subtableClause: UNDER tableName;
tableElementList : LEFT_PAREN tableElement (COMMA tableElement)* RIGHT_PAREN;
tableElement
  : columnDefinition
  | tableConstraintDefinition
  | likeClause
  | selfrefColumnSpecification
  | columnOptions
  ;

viewSpecification 
  : LEFT_PAREN columnName (COMMA columnName)* RIGHT_PAREN
  | OF udtName subviewClause? viewElementList
  ;
subviewClause: UNDER tableName;
viewElementList : LEFT_PAREN viewElement (COMMA viewElement)* RIGHT_PAREN;
viewElement
  : selfrefColumnSpecification
  | columnOptions
  ;

/* tableElement */
columnDefinition : columnName dataType referenceScopeCheck? deleteAction? 
  (DEFAULT defaultOption)? 
  generationClause? 
  columnConstraintDefinition?;
tableConstraintDefinition :  (CONSTRAINT constraintName)? tableConstraint constraintCharacteristics?;
likeClause : LIKE tableName (identityOption | defaultsOption)?;
selfrefColumnSpecification : REF IS columnName referenceGeneration;
columnOptions : columnName WITH OPTIONS (scopeDefinition | DEFAULT defaultOption | columnConstraintDefinition);

/* columnDefinition */
defaultOption
  : literal 
  | CURRENT_DATE
  | CURRENT_TIME (LEFT_PAREN secondsDecimals RIGHT_PAREN)?
  | LOCALTIME (LEFT_PAREN secondsDecimals RIGHT_PAREN)?
  | CURRENT_TIMESTAMP (LEFT_PAREN secondsDecimals RIGHT_PAREN)?
  | LOCALTIMESTAMP (LEFT_PAREN secondsDecimals RIGHT_PAREN)?
  | USER
  | CURRENT_USER
  | CURRENT_ROLE
  | SESSION_USER
  | SYSTEM_USER
  | CURRENT_PATH
  | NULL 
  | ARRAY LEFT_BRACKET RIGHT_BRACKET 
  | MULTISET LEFT_BRACKET RIGHT_BRACKET
  ;
generationClause : GENERATED ALWAYS AS LEFT_PAREN valueExpression RIGHT_PAREN;
columnConstraintDefinition : (CONSTRAINT constraintName)? columnConstraint constraintCharacteristics?;

/* tableConstraintDefinition */
tableConstraint
  : UNIQUE LEFT_PAREN columnName (COMMA columnName)* RIGHT_PAREN
  | PRIMARY KEY LEFT_PAREN columnName (COMMA columnName)* RIGHT_PAREN
  | FOREIGN KEY LEFT_PAREN columnName (COMMA columnName)* RIGHT_PAREN referencesSpecification
  | CHECK LEFT_PAREN booleanValueExpression RIGHT_PAREN
  ; 
constraintCharacteristics 
  : constraintCheckTime deferrability?
  | deferrability constraintCheckTime?
  ;

/* columnConstraintDefinition */  
columnConstraint
  : NOT NULL
  | UNIQUE VALUE?
  | PRIMARY KEY
  | REFERENCES tableName LEFT_PAREN columnName RIGHT_PAREN (MATCH match)? referentialTriggeredAction? 
  | CHECK LEFT_PAREN booleanValueExpression RIGHT_PAREN
  ;

/* tableConstraint */  
referencesSpecification
  : REFERENCES tableName (LEFT_PAREN columnName (COMMA columnName)* RIGHT_PAREN)? 
    (MATCH match)? referentialTriggeredAction?;
  
/* referencesSpecification */  
referentialTriggeredAction
  : updateAction deleteAction? 
  | deleteAction updateAction?
  ;
  
updateAction : ON UPDATE referentialAction;
deleteAction : ON DELETE referentialAction;
