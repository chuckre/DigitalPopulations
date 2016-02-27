@echo off
REM
REM Digital Populations Census Synthesizer
REM

set P2=%~dp0
set P=%P2%\..\Research GIS Kernel

set CLASSPATH=%P2%\bin;%P%\bin;%P%\lib\SuperCSV-1.52.jar;%P%\lib\jopt-simple-3.1.jar;%P%\lib\commons\commons-lang-2.5.jar;%P%\lib\commons\commons-primitives-1.0.jar

REM -Xrunhprof:help
REM -Xrunhprof:cpu=samples,depth=6,interval=20 
REM -Xrunhprof:cpu=times

java -ea -Xmx1000m mil.army.usace.censusgen.CensusGenCS %*
