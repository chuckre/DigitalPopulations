@echo off

set P=%~dp0
set J=%P%\jars

set CLASSPATH=%J%\DPGUI.jar;%J%\SuperCSV-1.52.jar;%J%\jopt-simple-3.1.jar

java -ea mil.army.usace.ehlschlaeger.rgik.util.Select %*
