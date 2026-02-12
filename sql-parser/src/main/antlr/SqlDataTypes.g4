/*======================================================================
An ANTLR4 grammar for SQL data types. 
Application : SQL utilities
Description : An ANTLR4 grammar for SQL data types.
              based on http://savage.net.au/SQL/sql-2003-2.bnf
------------------------------------------------------------------------
Copyright  : 2016 Enter AG, Rüti ZH, Switzerland
Created    : 24.03.2016, Hartwig Thomas
======================================================================*/
grammar SqlDataTypes;
import SqlIdentifier, SqlEnums;

dataType
  : predefinedType # preType
  | udtName # structType
  | ROW LEFT_PAREN fieldDefinition (COMMA fieldDefinition)* RIGHT_PAREN # rowType
  | REF LEFT_PAREN udtName RIGHT_PAREN scopeDefinition? # refType
  | dataType ARRAY (LEFT_BRACKET length RIGHT_BRACKET)? # arrayType 
  | dataType MULTISET # multisetType
  ;

referenceType : REF LEFT_PAREN udtName RIGHT_PAREN scopeDefinition?;
scopeDefinition : SCOPE tableName;
fieldDefinition : fieldName dataType referenceScopeCheck? (ON DELETE referentialAction)?;
  
predefinedType
  : charType
  | varcharType
  | clobType
  | ncharType
  | nvarcharType
  | nclobType
  | xmlType
  | varbinaryType
  | binaryType
  | blobType
  | numericType
  | decimalType
  | smallintType
  | integerType
  | bigintType
  | floatType
  | realType
  | doubleType
  | booleanType
  | dateType
  | timeType
  | timestampType
  | intervalType
  | datalinkType
  ;

charType : (CHARACTER | CHAR) (LEFT_PAREN length RIGHT_PAREN)?;
varcharType : ((CHARACTER | CHAR) VARYING | VARCHAR) (LEFT_PAREN length RIGHT_PAREN)?;
clobType : ((CHARACTER | CHAR) LARGE OBJECT | CLOB) (LEFT_PAREN lobLength RIGHT_PAREN)?;
ncharType : (NATIONAL (CHARACTER | CHAR) | NCHAR) (LEFT_PAREN length RIGHT_PAREN)?;
nvarcharType : (NATIONAL (CHARACTER | CHAR) VARYING | NCHAR VARYING) (LEFT_PAREN length RIGHT_PAREN)?;
nclobType : (NATIONAL (CHARACTER | CHAR) LARGE OBJECT | NCHAR LARGE OBJECT | NCLOB) (LEFT_PAREN lobLength RIGHT_PAREN)?;
xmlType : XML;
binaryType : BINARY (LEFT_PAREN length RIGHT_PAREN)?;
varbinaryType : (BINARY VARYING | VARBINARY) (LEFT_PAREN length RIGHT_PAREN)?;
blobType: (BINARY LARGE OBJECT | BLOB)  (LEFT_PAREN lobLength RIGHT_PAREN)?;
numericType: NUMERIC (LEFT_PAREN precision (COMMA scale)? RIGHT_PAREN)?;
decimalType : (DECIMAL | DEC) (LEFT_PAREN precision (COMMA scale)? RIGHT_PAREN)?;
smallintType : SMALLINT;
integerType : (INTEGER | INT);
bigintType : BIGINT;
floatType : FLOAT (LEFT_PAREN precision RIGHT_PAREN)?;
realType: REAL;
doubleType : DOUBLE PRECISION;
booleanType : BOOLEAN;
dateType : DATE;
timeType : TIME (LEFT_PAREN secondsDecimals RIGHT_PAREN)? withOrWithoutTimeZone?;
timestampType : TIMESTAMP (LEFT_PAREN secondsDecimals RIGHT_PAREN)? withOrWithoutTimeZone?;
intervalType : INTERVAL intervalQualifier;
datalinkType : DATALINK;
intervalQualifier
  : YEAR (LEFT_PAREN precision RIGHT_PAREN)? (TO MONTH)?
  | MONTH (LEFT_PAREN precision RIGHT_PAREN)?
  | DAY (LEFT_PAREN precision RIGHT_PAREN)? (TO HOUR | TO MINUTE | TO SECOND (LEFT_PAREN secondsDecimals RIGHT_PAREN)?)?
  | HOUR (LEFT_PAREN precision RIGHT_PAREN)? (TO MINUTE | TO SECOND (LEFT_PAREN secondsDecimals RIGHT_PAREN)?)?
  | MINUTE (LEFT_PAREN precision RIGHT_PAREN)? (TO SECOND (LEFT_PAREN secondsDecimals RIGHT_PAREN)?)?
  | SECOND (LEFT_PAREN precision (COMMA secondsDecimals) RIGHT_PAREN)?
  ;

lobLength: length multiplier?;
secondsDecimals: UNSIGNED_INTEGER;
precision: UNSIGNED_INTEGER;
scale: UNSIGNED_INTEGER; 
length: UNSIGNED_INTEGER;
