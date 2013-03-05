#!/bin/sh
mvn clean package
IAM_MODE=DEV java -jar target/UserAdministration-1.0-SNAPSHOT.jar

