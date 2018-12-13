#!/bin/bash
#SBATCH --mem 7g
#SBATCH --cpus-per-task 1 
#SBATCH -J trecprocessing

# This library causes a conflict
if [ -f lib/commons-cli-1.2.jar ]; then
	rm lib/commons-cli-1.2.jar
fi
java -jar -Xmx6g ~/bin/jcore-pipeline-runner-base*.jar pipelinerunner.xml
