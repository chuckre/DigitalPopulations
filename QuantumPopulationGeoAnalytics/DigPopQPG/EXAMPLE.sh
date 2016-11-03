#!/bin/bash

minRzn=1
maxRzn=3

#Parameters of spatial random field
D=250
E=1
F=100

fileDir='~/testData'

#Metric example
./metric.sh $fileDir'/HHTypeMetric' $minRzn $maxRzn $fileDir'/PuckaRatio' '1.0' '1.0' $fileDir'/SemiPuckaRatio' '0.51' '0.99' $fileDir'/KutchaRatio' '0.26' '0.5' $fileDir'/JhupriRatio' '0.126' '0.25'

#Indicator example
./indicator.sh $fileDir'/CIIndictor' $minRzn $maxRzn $D $E $F '0.1' '0.5' $fileDir'/HHTypeMetric' '0.3' '0.5' $fileDir'/HHUtilitiesMetric' '0.3' '0.5' 

