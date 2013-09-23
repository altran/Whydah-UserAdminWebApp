#!/bin/sh

export IAM_MODE=DEV
#export IAM_MODE=TEST

A=UserAdministration
V=1.0-SNAPSHOT
JARFILE=$A-$V.jar

pkill -f $A

wget --user=altran --password=l1nkSys -O $JARFILE "http://mvnrepo.cantara.no/service/local/artifact/maven/content?r=altran-snapshots&g=net.whydah.sso.service&a=$A&v=$V&p=jar"
java -jar -DIAM_CONFIG=useradmin.TEST.properties $JARFILE &
