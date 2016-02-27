@echo off
rem strip quotes
set file=%~1
echo %file%
wget --no-check-certificate -q -O "%file%" "http://digitalpopulations.pbworks.com/api_v2/op/GetFile/file/%file%/read_key/tXABcByWssKvDkT1amNP"
