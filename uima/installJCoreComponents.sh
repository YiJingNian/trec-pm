#!/bin/bash
for i in \
jcore-pmclassifier-ae \
jcore-trecpm-extraabstracts-reader; do
	python createMetaDescriptors.py -c -i -r manual -v 1.0 -u false $i;
	mvn clean install -f $i/pom.xml;
done
