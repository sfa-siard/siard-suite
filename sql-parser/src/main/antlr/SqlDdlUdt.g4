/*======================================================================
An ANTLR4 grammar for SQL statements. 
Application : SQL utilities
Description : An ANTLR4 grammar for SQL statements.
              based on http://savage.net.au/SQL/sql-2003-2.bnf
------------------------------------------------------------------------
Copyright  : 2016 Enter AG, Rüti ZH, Switzerland
Created    : 11.03.2016, Hartwig Thomas
======================================================================*/
grammar SqlDdlUdt;
import SqlDataTypes, SqlDdlRoutine, SqlDdlTable;

dropTypeStatement : DROP TYPE udtName dropBehavior;
createTypeStatement : CREATE TYPE udtName subTypeClause? 
  (AS (predefinedType | LEFT_PAREN attributeDefinitions RIGHT_PAREN))? 
  udtOptions? methodSpecifications?;
alterTypeStatement : ALTER TYPE udtName alterTypeAction;
alterTypeAction
  : ADD ATTRIBUTE attributeDefinition # addAttributeDefinition
  | DROP ATTRIBUTE attributeName RESTRICT # dropAttributeDefinition
  | ADD methodSpecification # addMethodSpecification
  | DROP methodDesignator RESTRICT # dropMethodSpecification
  ;
methodDesignator : methodType? METHOD IDENTIFIER LEFT_PAREN (dataType (COMMA dataType)*)? RIGHT_PAREN;      
subTypeClause : UNDER udtName;
attributeDefinitions : attributeDefinition (COMMA attributeDefinition)*;
attributeDefinition : attributeName dataType referenceScopeCheck? deleteAction? (DEFAULT defaultOption)?;
udtOptions : udtOption+;
udtOption
  : instantiability 
  | finality
  | referenceTypeSpecification
  | refCastOption
  | castOption
  ;
referenceTypeSpecification : userDefinedRepresentation | derivedRepresentation | systemGeneratedRepresentation;
userDefinedRepresentation : REF USING predefinedType;
derivedRepresentation : REF FROM LEFT_PAREN attributeName (COMMA attributeName)* RIGHT_PAREN;
systemGeneratedRepresentation : REF IS SYSTEM GENERATED;
refCastOption : castToRef | castToType;
castToRef :  CAST LEFT_PAREN SOURCE AS REF RIGHT_PAREN WITH castIdentifier;
castToType : CAST LEFT_PAREN REF AS SOURCE RIGHT_PAREN WITH castIdentifier;
castOption : castToDistinct | castToSource;
castToDistinct :  CAST LEFT_PAREN SOURCE AS DISTINCT RIGHT_PAREN WITH castIdentifier;
castToSource : CAST LEFT_PAREN DISTINCT AS SOURCE RIGHT_PAREN WITH castIdentifier;
