#!/bin/bash
#SBATCH --mem 10g
#SBATCH --cpus-per-task 1
#SBATCH -J trecprocessing

java -jar -Xmx2g ~/bin/jcore-pipeline-runner-base*.jar pipelinerunner.xml
