@ECHO OFF
REM
REM Generates XSD schema files for Digital Populations.
REM Allows DP to test for more errors in input files.
REM

schemagen -cp src "src\mil\army\usace\ehlschlaeger\digitalpopulations\censusgen\fittingcriteria\*.java"
ren schema1.xsd FittingCriteria.xsd
move FittingCriteria.xsd src\mil\army\usace\ehlschlaeger\digitalpopulations\censusgen\fittingcriteria

schemagen -cp src "src\mil\army\usace\ehlschlaeger\digitalpopulations\censusgen\filerelationship\*.java"
ren schema1.xsd FileRelationship.xsd
move FileRelationship.xsd src\mil\army\usace\ehlschlaeger\digitalpopulations\censusgen\filerelationship
