net stop UserAdministration
bin\wget -O UserAdministration-0.2-SNAPSHOT.jar "http://10.15.1.5:8080/nexus/service/local/artifact/maven/redirect?r=snapshots&g=net.whydah.identity&a=UserAdministration&v=0.2-SNAPSHOT&p=jar"
net start UserAdministration