#!/bin/bash

# Contributors: Yizhao Gao (yizhaotsccsj@gmail.com)

function randomNumber {
	range=`echo $2 - $1 | bc -l`
	rd=`shuf -i 0-1000 -n 1`
	echo `echo $rd / 1000 "*" $range + $1 | bc -l`
}

output=$1

alphabeta=(A B C D E F G H I J K L M N O P Q R S T U V W X Y Z)

if [ $(($#%3)) -ne 0 ] || [ $# -lt 4 ]
then
	echo "Incorrect parameters"
	exit
fi

nMap=$(($#/3-1))
#echo $nMap

# A file to store commands
echo "#!/bin/bash" > temp.sh

#Log:
logFile=$1.log
now="$(date)"
echo "PACOM Metric Generation starting at $now" > $logFile
echo "  Output metric: $1" >> $logFile
echo "  Staring rzn ID: $2, ending rzn ID: $3" >> $logFile
echo "  Inputs:" >> $logFile 

allRzn=$(seq $2 $3)
for rzn in $allRzn
do
	printf -v rznIDZero "%03d" $rzn
	command="gdal_calc.py"
	for i in `seq 0 $((nMap-1))`
	do
		minID=$((3*i+5))
		maxID=$((3*i+6))
		value=$(randomNumber ${!minID} ${!maxID})
		if [ $i -eq 0 ]
		then
			fomula="\"A*$value"
		else
			fomula="$fomula+$value*${alphabeta[i]}"
		fi
		mapnameID=$((3*i+4))
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
	echo $command >> temp.sh
#	$command
done

echo ./SARasterStat $1 $2 $3 $1 >> temp.sh

bash temp.sh

#Log:
now="$(date)"
echo "PACOM Metric Generation ending at $now" >> $logFile 
