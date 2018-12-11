#!/bin/bash
#SBATCH --mem 7g
#SBATCH --cpus-per-task 1 
#SBATCH -J trecprocessing

java -jar -Xmx6g ~/bin/jcore-pipeline-runner-base*.jar pipelinerunner.xml
