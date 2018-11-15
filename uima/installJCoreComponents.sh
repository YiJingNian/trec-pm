#!/bin/bash
# Must be executed in the uima/ subdirectory of the repository!
for i in \
jcore-pmclassifier-ae \
jcore-trecpm-extraabstracts-reader; do
	python createMetaDescriptors.py -c -i -r manual -v 1.0 -u false $i;
	mvn clean install -f $i/pom.xml;
done
echo "Installing the parent pom in this directory"
mvn clean install
