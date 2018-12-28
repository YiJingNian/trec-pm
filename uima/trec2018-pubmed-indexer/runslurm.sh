#!/bin/bash
#SBATCH --mem 40g
#SBATCH --cpus-per-task 14
#SBATCH -J trecprocessing

java -jar -Xmx2g ~/bin/jcore-pipeline-runner-base*.jar pipelinerunner.xml
