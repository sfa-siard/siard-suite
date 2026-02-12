/*======================================================================
An ANTLR4 grammar for SQL statements. 
Application : SQL utilities
Description : An ANTLR4 grammar for SQL statements.
              based on http://savage.net.au/SQL/sql-2003-2.bnf
------------------------------------------------------------------------
Copyright  : 2016 Enter AG, Rüti ZH, Switzerland
Created    : 11.03.2016, Hartwig Thomas
======================================================================*/
grammar SqlDdlRoutine;
import SqlIdentifier, SqlEnums, SqlExpression;

dropProcedureStatement : DROP SPECIFIC? PROCEDURE routineName (LEFT_PAREN sqlParameterDeclarations? RIGHT_PAREN)? dropBehavior;
dropFunctionStatement : DROP SPECIFIC? FUNCTION routineName (LEFT_PAREN sqlParameterDeclarations? RIGHT_PAREN)? dropBehavior;
dropMethodStatement : DROP SPECIFIC? METHOD routineName (LEFT_PAREN sqlParameterDeclarations? RIGHT_PAREN)? FOR udtName dropBehavior;
dropTriggerStatement : DROP TRIGGER triggerName;
createProcedureStatement : CREATE PROCEDURE routineName LEFT_PAREN sqlParameterDeclarations? RIGHT_PAREN 
  routineCharacteristics? routineBody;
createFunctionStatement : CREATE FUNCTION routineName LEFT_PAREN sqlParameterDeclarations? RIGHT_PAREN
  returnsClause routineCharacteristics? (STATIC DISPATCH)? routineBody;
createMethodStatement : CREATE methodType METHOD routineName LEFT_PAREN sqlParameterDeclarations? RIGHT_PAREN 
  returnsClause? FOR udtName routineCharacteristics? routineBody; 
createTriggerStatement : CREATE TRIGGER triggerName triggerActionTime triggerEvent 
  ON tableName (REFERENCING oldOrNewValue (oldOrNewValue)*)? triggeredAction;

methodSpecifications : methodSpecification (COMMA methodSpecification)*;
methodSpecification : originalMethodSpecification | overridingMethodSpecification;
originalMethodSpecification: partialMethodSpecification (SELF AS RESULT)? (SELF AS LOCATOR)? routineCharacteristics?;
overridingMethodSpecification : OVERRIDING partialMethodSpecification;
partialMethodSpecification : methodType? METHOD IDENTIFIER 
  LEFT_PAREN sqlParameterDeclarations? RIGHT_PAREN returnsClause (SPECIFIC specificMethodName)?;

triggerEvent : INSERT | DELETE | UPDATE (OF columnName (COMMA columnName)*)?;
oldOrNewValue 
  : OLD ROW? AS? correlationName
  | NEW ROW? AS? correlationName
  | OLD TABLE AS? correlationName
  | NEW TABLE AS? correlationName
  ;
triggeredAction : (FOR EACH (ROW | STATEMENT))? 
  (WHEN LEFT_PAREN booleanValueExpression RIGHT_PAREN)?
  (BEGIN ATOMIC routineBody END | routineBody);

sqlParameterDeclarations : sqlParameterDeclaration (COMMA sqlParameterDeclaration)*;
sqlParameterDeclaration : parameterMode? parameterName? parameterType RESULT?;
parameterType : dataType (AS LOCATOR)?;

returnsClause : RETURNS returnsType;
returnsType : returnsDataType resultCast? | returnsTableType;
returnsDataType : dataType (AS LOCATOR)?;
resultCast : CAST FROM (dataType (AS LOCATOR)?);
returnsTableType : TABLE tableColumns;
tableColumns : LEFT_PAREN tableColumn (COMMA tableColumn)* RIGHT_PAREN;
tableColumn : columnName dataType;

routineCharacteristics : routineCharacteristic+;
routineCharacteristic
  : LANGUAGE languageName
  | PARAMETER STYLE parameterStyle
  | deterministic
  | dataAccess
  | nullCallClause
  ;
routineBody : .*? END?;