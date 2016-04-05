@echo off
REM
REM Adds the directory containing this file onto your PATH variable.
REM Makes it easy to run the other programs in this dir.
REM

set P=%~dp0
path %PATH%;%P%
