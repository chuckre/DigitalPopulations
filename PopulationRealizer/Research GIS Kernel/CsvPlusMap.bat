@echo off
REM
REM CsvPlusMap:  Append values from a map as new column onto a CSV file.
REM

set P=%~dp0

set CLASSPATH=%P%\bin
set CLASSPATH=%CLASSPATH%;%P%\lib\jopt-simple-3.1.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\SuperCSV-1.52.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\commons\commons-collections-3.2.1.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\commons\commons-io-1.4.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\commons\commons-lang-2.5.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\commons\commons-primitives-1.0.jar

java -ea mil.army.usace.ehlschlaeger.digitalpopulations.csvplusmap.CsvPlusMap %*
