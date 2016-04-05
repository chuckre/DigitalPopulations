@echo off
REM
REM Start DPGUI from command line.
REM

REM Fetch and canonicalize .bat file's dir.
REM % ~ is doc'd under "FOR /?"
set P=%~dp0
set J=%P%\jars

set CLASSPATH=%J%\DPGUI.jar;%J%\appframework-1.0.3.jar;%J%\beansbinding-1.2.1.jar;%J%\swing-worker-1.1.jar;%J%\SuperCSV-1.52.jar

java -ea dpgui.FCEditorApp %*
