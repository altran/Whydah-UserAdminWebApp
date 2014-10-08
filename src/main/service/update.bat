net stop UserAdminWebApp
bin\wget -O UserAdminWebApp-2.12-SNAPSHOT.jar "http://10.15.1.5:8080/nexus/service/local/artifact/maven/redirect?r=snapshots&g=net.whydah.identity&a=UserAdminWebApp&v=2.12-SNAPSHOT&p=jar"
net start UserAdminWebApp