@echo off
rem strip quotes
set page=%~1
set file=%page::=_%
echo %page%
wget -q -O "%file%.json" "http://digitalpopulations.pbworks.com/api_v2/op/GetPage/page/%page%/read_key/tXABcByWssKvDkT1amNP"
