#!/bin/bash

experimentFile=$1
logPath=$2
fromIndiv=$3
toIndiv=$4

./scripts/compile.sh

/usr/bin/java -cp "./platform/src/:./multiscalemodel/src/:./bin/:./deps/*:./deps/j3dport/*" edu.usf.experiment.PreExperiment $experimentFile $logPath 
idMessage=`sbatch -a $fromIndiv-$toIndiv ./scripts/execOne.sh $logPath`
slurmId=`echo $idMessage | cut -d " " -f 4`
sbatch --dependency=afterok:$slurmId scripts/postProcess.sh $experimentFile $logPath

