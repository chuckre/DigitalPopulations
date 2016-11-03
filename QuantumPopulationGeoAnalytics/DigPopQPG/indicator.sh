#!/bin/bash

function randomNumber {
	range=`echo $2 - $1 | bc -l`
	rd=`shuf -i 0-1000 -n 1`
	echo `echo $rd / 1000 "*" $range + $1 | bc -l`
}

output=$1

alphabeta=(A B C D E F G H I J K L M N O P Q R S T U V W X Y Z)

if [ $(($#%3)) -ne 2 ] || [ $# -lt 9 ]
then
	echo "Incorrect parameters"
	exit
fi

D=$4

E=$5

F=$6

UR=$(randomNumber '0.0' '1.0')

nMap=$(($#/3-2))

#Log:
logFile=$1.log
now="$(date)"
echo "PACOM Indicator Generation starting at $now" > $logFile
echo "  Output metric: $1" >> $logFile
echo "  Staring rzn ID: $2, ending rzn ID: $3" >> $logFile
echo "  Inputs:" >> $logFile 
echo "   -A: randomField With D: $D E: $E F: $F" >> $logFile

allRzn=$(seq $2 $3)

allRzn=$(seq $2 $3)
for rzn in $allRzn
do
	uncertainty=$(randomNumber $7 $8)
	fomula="\"numpy.power(A,$uncertainty)"
	printf -v rznIDZero "%03d" $rzn
	command="gdal_calc.py -A randomfield_rzn$rznIDZero.tif"
	echo ./RandomField randomfield_rzn$rznIDZero.tif $9_rzn$rznIDZero.tif $D $E $F 
	for i in `seq 1 $nMap`
	do
		minID=$((3*i+7))
		maxID=$((3*i+8))
		value=$(randomNumber ${!minID} ${!maxID})
		fomula="$fomula*numpy.power(${alphabeta[i]},$value)"
		mapnameID=$((3*i+6))
		command="$command -${alphabeta[i]} ${!mapnameID}_rzn$rznIDZero.tif"
#Log:
		if [ $rzn -eq $2 ]
		then
			echo "   -${alphabeta[i]}: ${!mapnameID}" >> $logFile
		fi
	done
	fomula="$fomula\""
#Log:
	echo "**RZN $rzn" >> $logFile
	echo "  **Fomula $fomula" >> $logFile

	command="$command --outfile=$1_rzn$rznIDZero.tif --calc=$fomula --NoDataValue=-1"
	echo $command
	echo rm randomfield_rzn$rznIDZero.tif
done

./SARasterStat $1 $2 $3 $1

#Log:
now="$(date)"
echo "PACOM Indicator Generation ending at $now" >> $logFile 
