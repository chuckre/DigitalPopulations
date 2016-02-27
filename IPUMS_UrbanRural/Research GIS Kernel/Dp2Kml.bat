@echo off
REM
REM Like Csv2Kml, but specific to Digital Populations.
REM Can handle much larger numbers of households.
REM

set P=%~dp0

set CLASSPATH=%P%\bin
set CLASSPATH=%CLASSPATH%;%P%\lib\h2-1.2.140.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\jopt-simple-3.1.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\junit-4.7.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\SuperCSV-1.52.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\truezip-6.6.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\commons\commons-collections-3.2.1.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\commons\commons-io-1.4.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\commons\commons-lang-2.5.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\commons\commons-primitives-1.0.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\geotools\geoapi-2.3-M1.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\geotools\geoapi-pending-2.3-M1.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\geotools\gt-api-2.7-M1.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\geotools\gt-epsg-hsql-2.7-M1.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\geotools\gt-metadata-2.7-M1.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\geotools\gt-referencing-2.7-M1.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\geotools\hsqldb-1.8.0.7.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\geotools\jsr-275-1.0-beta-2.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\geotools\jts-1.11.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\geotools\vecmath-1.3.2.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\jak\JavaAPIforKml.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\jaxb\activation.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\jaxb\jaxb-api.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\jaxb\jaxb-impl.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\jaxb\jsr173_1.0_api.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\velocity\velocity-1.7.jar
set CLASSPATH=%CLASSPATH%;%P%\lib\velocity\velocity-tools-generic-2.0.jar

java -ea mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml.Dp2Kml %*
