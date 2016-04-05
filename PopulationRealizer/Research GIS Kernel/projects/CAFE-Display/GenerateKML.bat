@echo off
REM
REM Generates KMZ file from given CSV file.
REM

SET P=%~dp0
SET B=%~dpn1
SET N=%~n1

java -jar "%P%/software/csv2kml.jar" --crs EPSG:32610 --name %%%%{name} --bubble "%P%/software/AutoBubble.html" --style "%P%/software/CAFEStyle.html" "%B%.csv" Easting Northing --output "%B%.zip"

IF EXIST "%B%.zip" (
  IF EXIST "%B%.kmz" (
    del "%B%.kmz"
  )
  ren "%B%.zip" "%N%.kmz"
)

PAUSE
