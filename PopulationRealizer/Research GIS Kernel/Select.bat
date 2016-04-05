@echo off
REM
REM Generate a table containing only a subset of another table.
REM

set P=%~dp0

set CLASSPATH=%P%\bin
set CLASSPATH=%CLASSPATH%;%P%\lib\SuperCSV-1.52.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\jopt-simple-3.1.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\commons\commons-primitives-1.0.jar

java -ea mil.army.usace.ehlschlaeger.rgik.util.Select %*
