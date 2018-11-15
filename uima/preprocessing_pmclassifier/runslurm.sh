#!/bin/bash
#SBATCH --mem 10g
#SBATCH --cpus-per-task 1 
#SBATCH -J trecprocessing

java -jar -Xmx8g -Dlogback.configurationFile=/home/faessler/Coding/git/trec2018/uima/preprocessing_pmclassifier/config/logback.xml ~/bin/jcore-pipeline-runner-*.jar pipelinerunner.xml
