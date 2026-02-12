/*======================================================================
An ANTLR4 grammar for SQL value expressions. 
Application : SQL utilities
Description : An ANTLR4 grammar for SQL value expressions.
              based on http://savage.net.au/SQL/sql-2003-2.bnf
------------------------------------------------------------------------
Copyright  : 2016 Enter AG, Rüti ZH, Switzerland
Created    : 12.04.2016, Hartwig Thomas
======================================================================*/
grammar SqlExpression;
import SqlIdentifier, SqlEnums, SqlDataTypes;

querySpecification : SELECT setQuantifier? selectList 
  fromClause
  whereClause?
  groupByClause? 
  havingClause?
  windowClause?; 
 
selectList 
  : ASTERISK 
  | selectSublist (COMMA selectSublist)*
  ;
selectSublist 
  : valueExpression (AS columnName)? 
  | identifierChain PERIOD ASTERISK
  | valueExpression PERIOD ASTERISK (AS LEFT_PAREN columnName (COMMA columnName)* RIGHT_PAREN)?
  ;

fromClause : FROM tableReference (COMMA tableReference)*;
whereClause : WHERE booleanValueExpression;
groupByClause : GROUP BY setQuantifier? groupingElement (COMMA groupingElement)*;
havingClause : HAVING booleanValueExpression;
windowClause : WINDOW windowDefinition (COMMA windowDefinition)*;

tableReference
  : tablePrimary
  | tableReference CROSS JOIN tablePrimary // crossJoin
  | tableReference joinType JOIN tableReference joinSpecification // qualifiedJoin
  | tableReference NATURAL joinType JOIN tablePrimary // naturalJoin
  | tableReference UNION tablePrimary // unionJoin
  | TABLESAMPLE sampleMethod LEFT_PAREN numericValueExpression RIGHT_PAREN (REPEATABLE LEFT_PAREN numericValueExpression RIGHT_PAREN)?
  ;
joinSpecification
  : ON booleanValueExpression
  | USING LEFT_PAREN columnName (COMMA columnName)* RIGHT_PAREN
  ;

tablePrimary
  : tableName tableAlias?
  | LEFT_PAREN tableReference RIGHT_PAREN
  | LATERAL? LEFT_PAREN queryExpression RIGHT_PAREN tableAlias?
  | UNNEST LEFT_PAREN (arrayValueExpression | multisetValueExpression) RIGHT_PAREN (WITH ORDINALITY)? tableAlias?
  | TABLE LEFT_PAREN (arrayValueExpression | multisetValueExpression) RIGHT_PAREN tableAlias?
  | ONLY LEFT_PAREN tableName RIGHT_PAREN tableAlias?
  ;
tableAlias : AS? correlationName (LEFT_PAREN columnName (COMMA columnName)* RIGHT_PAREN)?;

groupingElement // equals groupingSet
  : ordinaryGroupingSet // ordinaryGroupingSet
  | ROLLUP LEFT_PAREN ordinaryGroupingSet RIGHT_PAREN //rollupList
  | CUBE LEFT_PAREN ordinaryGroupingSet RIGHT_PAREN // cubeList
  | GROUPING SETS LEFT_PAREN groupingElement (COMMA groupingElement)* RIGHT_PAREN // groupingSetsSpecification
  | LEFT_PAREN RIGHT_PAREN // emptyGroupingSet
  ;
ordinaryGroupingSet : identifierChain | LEFT_PAREN identifierChain (COMMA identifierChain)* RIGHT_PAREN;

queryExpression : withClause? queryExpressionBody;
queryExpressionBody
  : queryExpressionBody queryOperator setQuantifier? correspondingSpecification? queryExpressionBody
  | tableReference
  | LEFT_PAREN queryExpressionBody RIGHT_PAREN
  | querySpecification
  | TABLE tableName
  | VALUES LEFT_PAREN tableRowValueExpression (COMMA tableRowValueExpression)* RIGHT_PAREN
  ;
tableRowValueExpression
  : rowValueExpression 
  | commonValueExpression
  | booleanValueExpression
  | valueExpressionPrimary
  ;
correspondingSpecification: CORRESPONDING (BY LEFT_PAREN columnName (COMMA columnName)* RIGHT_PAREN)?;
withClause : WITH RECURSIVE? withElement+;
withElement : queryName (LEFT_PAREN columnName (COMMA columnName)* RIGHT_PAREN)?
  (AS LEFT_PAREN queryExpression RIGHT_PAREN searchOrCycleClause?)?;
searchOrCycleClause
  : searchClause
  | cycleClause
  | searchClause cycleClause
  ;
 searchClause 
  : SEARCH DEPTH FIRST BY sortSpecificationList SET columnName
  | SEARCH BREADTH FIRST BY  sortSpecificationList SET columnName
  ;
cycleClause
  : CYCLE columnName (COMMA columnName)*
  | SET columnName TO valueExpression
  | DEFAULT valueExpression
  | USING columnName
  ;

valueExpression
  : commonValueExpression
  | booleanValueExpression
  | rowValueExpression
  ;

commonValueExpression
  : valueExpressionPrimary
  | stringValueExpression
  | numericValueExpression
  | datetimeValueExpression
  | intervalValueExpression
  | arrayValueExpression
  | multisetValueExpression
  ;

numericValueExpression
  : sign? numericValueFunction
  | numericValueExpression multiplicativeOperator numericValueExpression
  | numericValueExpression additiveOperator numericValueExpression
  | LEFT_PAREN numericValueExpression RIGHT_PAREN
  | sign? valueExpressionPrimary
  ;

numericValueFunction
  : (CHAR_LENGTH | CHARACTER_LENGTH | OCTET_LENGTH) LEFT_PAREN stringValueExpression RIGHT_PAREN # lengthExpression 
  | POSITION LEFT_PAREN stringValueExpression IN stringValueExpression RIGHT_PAREN # positionExpression
  | EXTRACT LEFT_PAREN extractField FROM (datetimeValueExpression | intervalValueExpression) RIGHT_PAREN # extractExpression
  | CARDINALITY LEFT_PAREN (arrayValueExpression | multisetValueExpression) RIGHT_PAREN # cardinalityExpression
  | ABS LEFT_PAREN numericValueExpression RIGHT_PAREN # absoluteValueExpression
  | MOD LEFT_PAREN numericValueExpression COMMA numericValueExpression RIGHT_PAREN # modulusExpression
  | LN  LEFT_PAREN numericValueExpression RIGHT_PAREN # naturalLogarithm 
  | EXP LEFT_PAREN numericValueExpression RIGHT_PAREN # exponentialFunction
  | POWER LEFT_PAREN numericValueExpression COMMA numericValueExpression RIGHT_PAREN # powerFunction
  | SQRT LEFT_PAREN numericValueExpression RIGHT_PAREN # squareRoot
  | FLOOR LEFT_PAREN numericValueExpression RIGHT_PAREN # floorFunction 
  | (CEIL | CEILING) LEFT_PAREN numericValueExpression RIGHT_PAREN # ceilingFunction 
  | WIDTH_BUCKET LEFT_PAREN widthBucketOperand COMMA widthBucketBound1 COMMA widthBucketBound2 COMMA widthBucketCount RIGHT_PAREN # widthBucketFunction     
  ;
extractField : primaryDatetimeField | timeZoneField;
widthBucketOperand : numericValueExpression;
widthBucketBound1 : numericValueExpression;
widthBucketBound2 : numericValueExpression;
widthBucketCount : numericValueExpression;

stringValueExpression
  : stringValueFunction
  | stringValueExpression CONCATENATION_OPERATOR stringValueExpression 
  | valueExpressionPrimary
  ;

stringValueFunction
  : SUBSTRING LEFT_PAREN stringValueExpression FROM startPosition (FOR stringLength)? RIGHT_PAREN
  | SUBSTRING LEFT_PAREN stringValueExpression SIMILAR stringValueExpression ESCAPE CHARACTER_STRING_LITERAL RIGHT_PAREN
  | (UPPER | LOWER) LEFT_PAREN stringValueExpression RIGHT_PAREN
  | TRIM LEFT_PAREN stringValueExpression RIGHT_PAREN
  | NORMALIZE LEFT_PAREN stringValueExpression RIGHT_PAREN
  | udtValueExpression PERIOD SPECIFICTYPE
  ;

startPosition : numericValueExpression;
stringLength : numericValueExpression;

datetimeValueExpression
  : datetimeValueFunction timeZone?
  | datetimeValueExpression MINUS_SIGN intervalValueExpression
  | datetimeValueExpression PLUS_SIGN intervalValueExpression
  | valueExpressionPrimary timeZone? 
  ;
timeZone : AT (LOCAL | TIME ZONE intervalPrimary);
intervalPrimary 
  : valueExpressionPrimary intervalQualifier?
  | ABS LEFT_PAREN intervalValueExpression RIGHT_PAREN
  ;

datetimeValueFunction
  : CURRENT_DATE
  | CURRENT_TIME (LEFT_PAREN secondsDecimals RIGHT_PAREN)?
  | CURRENT_TIMESTAMP (LEFT_PAREN secondsDecimals RIGHT_PAREN)?
  | LOCALTIME (LEFT_PAREN secondsDecimals RIGHT_PAREN)?
  | LOCALTIMESTAMP (LEFT_PAREN secondsDecimals RIGHT_PAREN)?
  ;

intervalValueExpression
  : sign? ABS LEFT_PAREN intervalValueExpression RIGHT_PAREN // intervalFunction
  | numericValueExpression ASTERISK intervalValueExpression
  | intervalValueExpression multiplicativeOperator numericValueExpression
  | intervalValueExpression additiveOperator intervalValueExpression
  | LEFT_PAREN datetimeValueExpression MINUS_SIGN datetimeValueExpression RIGHT_PAREN intervalQualifier
  | sign? valueExpressionPrimary intervalQualifier? // intervalPrimary
  ;
  
udtValueExpression : valueExpressionPrimary;

referenceValueExpression : valueExpressionPrimary;

arrayValueExpression 
  : valueExpressionPrimary
  | arrayValueExpression CONCATENATION_OPERATOR arrayValueExpression 
  ;

multisetValueExpression 
  : SET LEFT_PAREN multisetValueExpression RIGHT_PAREN 
  | multisetValueExpression multisetOperator setQuantifier? valueExpressionPrimary
  | multisetValueExpression multisetOperator setQuantifier? SET LEFT_PAREN multisetValueExpression RIGHT_PAREN
  | valueExpressionPrimary
  ;
  
booleanValueExpression
  : NOT booleanValueExpression
  | booleanValueExpression booleanOperator booleanValueExpression
  | booleanPrimary (IS (NOT)? BOOLEAN_LITERAL)?
  ;
  
booleanPrimary 
  : rowValuePredicand comparisonCondition // comparisonPredicate
  | rowValuePredicand betweenCondition // betweenPredicate
  | rowValuePredicand inCondition // inPredicate
  | rowValuePredicand likeCondition // likePredicate
  | rowValuePredicand similarCondition // similarPredicate
  | rowValuePredicand nullCondition // nullPredicate
  | rowValuePredicand quantifiedComparisonCondition // quantifiedComparisonPredicate
  | EXISTS LEFT_PAREN queryExpression RIGHT_PAREN // existsPredicate
  | UNIQUE LEFT_PAREN queryExpression RIGHT_PAREN // uniquePredicate
  | stringValueExpression IS NOT? NORMALIZED // normalizedPredicate
  | rowValuePredicand matchCondition // matchPredicate
  | rowValuePredicand overlapsCondition // overlapsPredicate
  | rowValuePredicand distinctCondition // distinctPredicate
  | rowValuePredicand memberCondition //memberPredicate
  | rowValuePredicand submultisetCondition // submultisetPredicate
  | rowValuePredicand setCondition // setPredicate
  | rowValuePredicand typeCondition // typePredicate
  | LEFT_PAREN booleanValueExpression RIGHT_PAREN // booleanPredicand
  | valueExpressionPrimary // booleanPrimary
  ;
comparisonCondition : compOp rowValuePredicand;
betweenCondition : NOT? BETWEEN symmetricOption? rowValuePredicand AND rowValuePredicand;
inCondition : NOT? IN (rowValueExpression (COMMA rowValueExpression)* | LEFT_PAREN queryExpression RIGHT_PAREN);
likeCondition : NOT? LIKE stringValueExpression (ESCAPE CHARACTER_STRING_LITERAL)?;
similarCondition : NOT? SIMILAR TO stringValueExpression (ESCAPE CHARACTER_STRING_LITERAL)?;
nullCondition : IS NOT? NULL;
quantifiedComparisonCondition : compOp quantifier LEFT_PAREN queryExpression RIGHT_PAREN;
matchCondition : MATCH UNIQUE? match LEFT_PAREN queryExpression RIGHT_PAREN;
overlapsCondition : OVERLAPS rowValuePredicand;
distinctCondition : IS DISTINCT FROM rowValuePredicand;
memberCondition : NOT? MEMBER OF? multisetValueExpression;
submultisetCondition : NOT? SUBMULTISET OF multisetValueExpression;
setCondition : IS NOT? A SET;
typeCondition : IS NOT? OF LEFT_PAREN udtSpecification (COMMA udtSpecification)* RIGHT_PAREN; 
udtSpecification : ONLY? udtName;
rowValuePredicand 
  : commonValueExpression
  | rowValueExpression 
  ;
  
rowValueExpression
  : valueExpressionPrimary
  | LEFT_PAREN valueExpression (COMMA valueExpression)+ RIGHT_PAREN
  | ROW LEFT_PAREN valueExpression (COMMA valueExpression)* RIGHT_PAREN
  | LEFT_PAREN queryExpression RIGHT_PAREN
  ;

valueExpressionPrimary
  : generalValueSpecification # generalValueSpec
  | unsignedLiteral # unsignedLit
  | aggregateFunction # aggregateFunc
  | arrayValueConstructor # arrayValueConstruct
  | multisetValueConstructor # multisetValueConstruct
  | caseExpression # caseExp
  | castSpecification # castSpec
  | groupingOperation # groupingOp
  | windowFunction # windowFunc
  | scalarSubquery # scalarSubq
  | subtypeTreatment # subtypeTreat
  | valueExpressionPrimary PERIOD methodName sqlArgumentList # methodInvoc
  | generalizedInvocation # generalizedMethodInvoc
  | staticMethodInvocation # staticMethodInvoc
  | newSpecification # newSpec
  | valueExpressionPrimary DEREFERENCE_OPERATOR IDENTIFIER sqlArgumentList? # attributeOrMethodRef
  | referenceResolution # referenceRes
  | LEFT_PAREN arrayValueExpression CONCATENATION_OPERATOR arrayValueExpression RIGHT_PAREN LEFT_BRACKET numericValueExpression RIGHT_BRACKET # arrayElementRefConcat
  | valueExpressionPrimary LEFT_BRACKET numericValueExpression RIGHT_BRACKET # arrayElementRef
  | multisetElementReference # multisetElementRef
  | routineInvocation # routineInvoc
  | nextValueExpression # nextValueExp
  | LEFT_PAREN valueExpressionPrimary RIGHT_PAREN # valueExpressionPrimaryParen
  ;

aggregateFunction
  : COUNT LEFT_PAREN ASTERISK RIGHT_PAREN filterClause?
  | setFunction LEFT_PAREN setQuantifier? valueExpression RIGHT_PAREN filterClause?
  | binarySetFunction LEFT_PAREN dependentVariableExpression COMMA independentVariableExpression RIGHT_PAREN filterClause?
  | rankFunction LEFT_PAREN rankFunctionArgumentList RIGHT_PAREN withinGroupSpecification  filterClause?
  | inverseDistributionFunction LEFT_PAREN numericValueExpression RIGHT_PAREN withinGroupSpecification filterClause?
  ;
rankFunctionArgumentList : valueExpression (COMMA valueExpression)*;
dependentVariableExpression : numericValueExpression;
independentVariableExpression : numericValueExpression;
withinGroupSpecification : WITHIN GROUP LEFT_PAREN ORDER BY sortSpecificationList RIGHT_PAREN;
sortSpecificationList : sortSpecification (COMMA sortSpecification)*;
sortSpecification : valueExpression orderingSpecification? nullOrdering?;
filterClause : FILTER LEFT_PAREN WHERE booleanValueExpression RIGHT_PAREN;
groupingOperation : GROUPING LEFT_PAREN columnReference (COMMA columnReference)* RIGHT_PAREN;

windowFunction : windowFunctionType OVER (windowName | LEFT_PAREN windowSpecification RIGHT_PAREN);
windowFunctionType
  : rankFunction LEFT_PAREN RIGHT_PAREN
  | ROW_NUMBER LEFT_PAREN RIGHT_PAREN
  | aggregateFunction
  ;
windowDefinition : windowName AS windowSpecification;
windowSpecification : windowName? windowPartitionClause? windowOrderClause? windowFrameClause?;
windowPartitionClause : PARTITION BY columnReference (COMMA columnReference)*;
windowOrderClause : ORDER BY sortSpecificationList;
windowFrameClause : windowFrameUnits (windowFrameStart | windowFrameBetween) windowFrameExclusion?;
windowFrameStart
  : UNBOUNDED PRECEDING
  | unsignedValueSpecification PRECEDING 
  | CURRENT ROW
  ;
windowFrameBetween :  BETWEEN windowFrameBound1 AND windowFrameBound2;
windowFrameBound1 : windowFrameBound;
windowFrameBound2 : windowFrameBound;
windowFrameBound 
  : windowFrameStart
  | UNBOUNDED FOLLOWING
  | unsignedValueSpecification FOLLOWING
  ;

scalarSubquery : LEFT_PAREN queryExpression RIGHT_PAREN;

caseExpression
  : NULLIF LEFT_PAREN valueExpression COMMA valueExpression RIGHT_PAREN
  | COALESCE LEFT_PAREN valueExpression (COMMA valueExpression)* RIGHT_PAREN
  | CASE OVERLAPS? rowValuePredicand simpleWhenClause+ (ELSE result)? END
  | CASE searchedWhenClause+ (ELSE result)? END
  ;
simpleWhenClause : WHEN whenOperand THEN result;
searchedWhenClause : WHEN booleanValueExpression THEN result;
result: valueExpression | NULL;
whenOperand
  : rowValuePredicand
  | comparisonCondition
  | betweenCondition
  | inCondition
  | likeCondition
  | similarCondition
  | nullCondition
  | quantifiedComparisonCondition
  | matchCondition
  | overlapsCondition
  | distinctCondition
  | memberCondition
  | submultisetCondition
  | setCondition
  | typeCondition
  ;

castSpecification : CAST LEFT_PAREN castOperand AS dataType RIGHT_PAREN;
castOperand 
  : valueExpression
  | NULL
  | ARRAY LEFT_BRACKET RIGHT_BRACKET // empty array
  | MULTISET LEFT_BRACKET RIGHT_BRACKET // empty multiset
  ;

subtypeTreatment : TREAT LEFT_PAREN valueExpression AS (udtName | referenceType) RIGHT_PAREN;

generalizedInvocation : LEFT_PAREN valueExpressionPrimary AS dataType RIGHT_PAREN PERIOD methodName sqlArgumentList?;  

sqlArgumentList : LEFT_PAREN (sqlArgument (COMMA sqlArgument)*)? RIGHT_PAREN;
sqlArgument 
  : valueExpression 
  | generalizedExpression 
  | targetSpecification
  | DEFAULT
  | NULL
  | ARRAY LEFT_BRACKET RIGHT_BRACKET // empty array
  | MULTISET LEFT_BRACKET RIGHT_BRACKET // empty multiset
  ;
generalizedExpression : valueExpression AS udtName;
targetSpecification
  : variableName indicatorVariable? // host parameter specification or embeddedVariableSpecification
  | identifierChain // sqlParameterReference or columnReference
  | targetArrayReference LEFT_BRACKET simpleValueSpecification RIGHT_BRACKET // targetArrayElementSpecification
  | QUESTION_MARK // dynamicParameterSpecification
  ;
simpleTargetSpecification
  : variableName indicatorVariable? // host parameter specification or embeddedVariableSpecification
  | identifierChain // sqlParameterReference or columnReference
  ;

staticMethodInvocation : udtName DOUBLE_COLON methodName sqlArgumentList?;

newSpecification : NEW routineInvocation;
routineInvocation : routineName sqlArgumentList;

referenceResolution : DEREF LEFT_PAREN valueExpressionPrimary RIGHT_PAREN; 

arrayValueConstructor
   : ARRAY LEFT_BRACKET valueExpression (COMMA valueExpression)* RIGHT_BRACKET
   | ARRAY LEFT_PAREN queryExpression (ORDER BY sortSpecificationList)? RIGHT_PAREN
   ;

multisetValueConstructor
  : MULTISET LEFT_BRACKET valueExpression (COMMA valueExpression)* RIGHT_BRACKET
  | MULTISET LEFT_PAREN queryExpression RIGHT_PAREN
  | TABLE LEFT_PAREN queryExpression RIGHT_PAREN
  ; 

multisetElementReference : ELEMENT LEFT_PAREN multisetValueExpression RIGHT_PAREN;

nextValueExpression : NEXT VALUE FOR sequenceName;
  
unsignedValueSpecification : unsignedLiteral | generalValueSpecification;
generalValueSpecification
  : reference // Column or SQL Parameter or method or field Reference
  | QUESTION_MARK // dynamicParameterSpecification
  | CURRENT_PATH
  | CURRENT_ROLE
  | CURRENT_USER
  | SESSION_USER
  | SYSTEM_USER
  | USER
  | VALUE
  | variableName indicatorVariable? // host parameter specification or embeddedVariableSpecification
  ;
reference : identifierChain;  
indicatorVariable : INDICATOR variableName;

simpleValueSpecification
  : literal
  | variableName indicatorVariable? // host parameter specification or embeddedVariableSpecification
  | identifierChain // sqlParameterSpecification
  ;

literal: sign? unsignedNumericLiteral | generalLiteral;
unsignedLiteral: unsignedNumericLiteral | generalLiteral;
unsignedNumericLiteral : UNSIGNED_APPROXIMATE | exactNumericLiteral;
exactNumericLiteral : UNSIGNED_INTEGER (PERIOD UNSIGNED_INTEGER)? | PERIOD UNSIGNED_INTEGER;
generalLiteral 
  : CHARACTER_STRING_LITERAL
  | NATIONAL_CHARACTER_STRING_LITERAL
  | BIT_STRING_LITERAL
  | BYTE_STRING_LITERAL
  | DATE_LITERAL
  | TIME_LITERAL
  | TIMESTAMP_LITERAL
  | intervalLiteral
  | BOOLEAN_LITERAL
  ; 
intervalLiteral : INTERVAL sign? CHARACTER_STRING_LITERAL intervalQualifier?;
