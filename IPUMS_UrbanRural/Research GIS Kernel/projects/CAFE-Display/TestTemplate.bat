@echo off
REM
REM Generates testtemplate.html to preview the description balloons.
REM

SET P=%~dp0
SET D=%~dp1
SET O=%D%testtemplate.html

echo Generating testtemplate.html
java -jar "%P%software/csv2kml.jar" --testtemplate --name %%{name} --bubble "%P%software/AutoBubble.html" %1 Easting Northing > "%O%"

IF EXIST "%O%" (
  start "" "%O%"
)

PAUSE
