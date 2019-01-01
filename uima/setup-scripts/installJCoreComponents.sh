#!/bin/bash
# To be executed by gradle in the uima/ subdirectory of the repository!
components="jcore-pmclassifier-ae jcore-trecpm-extraabstracts-reader"
for i in $components; do
    echo "Calling python setup-scripts/createMetaDescriptors.py -c -i -r manual -v 1.0 -u false $i;"
	python setup-scripts/createMetaDescriptors.py -c -i -r manual -v 1.0 -u false $i;
done
mvn clean install -DskipTests=true
echo "Installing the parent pom in this directory"
