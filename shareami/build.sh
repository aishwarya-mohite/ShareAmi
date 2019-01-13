#!/bin/sh

home=$(pwd) &&
mvn clean &&
mvn clean compile assembly:single &&
mv target/shareami-0.0.1-SNAPSHOT-jar-with-dependencies.jar $home/shareAmi-generator-0.0.1.jar
