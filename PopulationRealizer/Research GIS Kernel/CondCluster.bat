@echo off
REM
REM Digital Populations: ConditionalPointClusterDetector
REM

set P=%~dp0

set CLASSPATH=%P%\bin
set CLASSPATH=%CLASSPATH%;%P%\lib\SuperCSV-1.52.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\jopt-simple-3.1.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\commons\commons-lang-2.5.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\commons\commons-primitives-1.0.jar

REM -Xrunhprof:help
REM -Xrunhprof:cpu=samples,depth=6,interval=20 
REM -Xrunhprof:cpu=times

java -ea -Xmx1000m mil.army.usace.ehlschlaeger.digitalpopulations.ConditionalPointClusterDetector %*
