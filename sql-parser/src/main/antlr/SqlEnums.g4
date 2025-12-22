/*======================================================================
An ANTLR4 grammar for SQL statements. 
Application : SQL utilities
Description : An ANTLR4 grammar for SQL statements.
              based on http://savage.net.au/SQL/sql-2003-2.bnf
------------------------------------------------------------------------
Copyright  : 2016 Enter AG, Rüti ZH, Switzerland
Created    : 11.03.2016, Hartwig Thomas
======================================================================*/
grammar SqlEnums;
import LexSql;

dropBehavior : CASCADE | RESTRICT;
commitAction : ON COMMIT (PRESERVE | DELETE) ROWS;
defaultsOption : (INCLUDING | EXCLUDING) DEFAULTS;
identityOption : (INCLUDING | EXCLUDING) IDENTITY;
match: FULL | PARTIAL | SIMPLE;
referenceGeneration : SYSTEM GENERATED | USER GENERATED | DERIVED;
referenceScopeCheck : REFERENCES ARE NOT? CHECKED;
referentialAction : CASCADE | SET NULL | SET DEFAULT | RESTRICT | NO ACTION;
tableScope : (GLOBAL | LOCAL) TEMPORARY;
instantiability : NOT? INSTANTIABLE;
finality : NOT? FINAL;
parameterMode : IN | OUT | INOUT;
languageName :  ADA | C | COBOL | FORTRAN | MUMPS | PASCAL | PLI | SQL;
parameterStyle : SQL | GENERAL;
nullCallClause : RETURNS NULL ON NULL INPUT | CALLED ON NULL INPUT;
deterministic : NOT? DETERMINISTIC;
dataAccess : NO SQL | CONTAINS SQL | READS SQL DATA | MODIFIES SQL DATA;
methodType : INSTANCE | STATIC | CONSTRUCTOR;
symmetricOption : ASYMMETRIC | SYMMETRIC;
quantifier : ALL | SOME | ANY;
setQuantifier : DISTINCT | ALL;
sampleMethod : BERNOULLI | SYSTEM;
joinType : INNER | (LEFT | RIGHT | FULL) OUTER?;
orderingSpecification : ASC | DESC;
nullOrdering : NULLS FIRST | NULLS LAST;
rankFunction : RANK | DENSE_RANK | PERCENT_RANK | CUME_DIST;  
setFunction 
  : AVG 
  | MAX 
  | MIN 
  | SUM 
  | EVERY 
  | ANY 
  | SOME 
  | COUNT 
  | STDDEV_POP 
  | STDDEV_SAMP 
  | VAR_POP 
  | VAR_SAMP 
  | COLLECT 
  | FUSION 
  | INTERSECTION
  ;
binarySetFunction 
  : COVAR_POP 
  | COVAR_SAMP 
  | CORR 
  | REGR_SLOPE 
  | REGR_INTERCEPT 
  | REGR_COUNT 
  | REGR_RSQUARED 
  | REGR_AVGX 
  | REGR_AVGY 
  | REGR_SXX 
  | REGR_SYY 
  | REGR_SXY
  ;
inverseDistributionFunction : PERCENTILE_CONT | PERCENTILE_DISC;
windowFrameUnits : ROWS | RANGE;
windowFrameExclusion 
  : EXCLUDE CURRENT ROW 
  | EXCLUDE GROUP 
  | EXCLUDE TIES 
  | EXCLUDE NO OTHERS
  ;
multiplicativeOperator : ASTERISK | SOLIDUS;
additiveOperator : PLUS_SIGN | MINUS_SIGN;
booleanOperator : AND | OR;
timeZoneField : TIMEZONE_HOUR | TIMEZONE_MINUTE;
multiplier: K | M | G;
withOrWithoutTimeZone : (WITH | WITHOUT) TIME ZONE;
multisetOperator : MULTISET (INTERSECT | UNION | EXCEPT);
queryOperator : INTERSECT | UNION | EXCEPT;
primaryDatetimeField : YEAR | MONTH | DAY | HOUR | MINUTE | SECOND;
compOp
 : EQUALS_OPERATOR
 | NOT_EQUALS_OPERATOR
 | LESS_THAN_OPERATOR
 | GREATER_THAN_OPERATOR
 | LESS_THAN_OR_EQUALS_OPERATOR
 | GREATER_THAN_OR_EQUALS_OPERATOR
 ; 
sign : PLUS_SIGN | MINUS_SIGN;
withOrWithoutData : WITH NO? DATA;
constraintCheckTime : INITIALLY DEFERRED | INITIALLY IMMEDIATE;
deferrability : NOT? DEFERRABLE;
levels : CASCADED | LOCAL;
triggerActionTime : BEFORE | AFTER;
overrideClause : OVERRIDING USER VALUE | OVERRIDING SYSTEM VALUE;
specialValue 
  : NULL 
  | ARRAY LEFT_BRACKET RIGHT_BRACKET 
  | MULTISET LEFT_BRACKET RIGHT_BRACKET
  | DEFAULT
  ;