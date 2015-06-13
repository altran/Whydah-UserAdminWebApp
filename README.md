UserAdministration
========================

Administration UI for Whydah Users and their mapping to Roles, Applications and Organizations.
Requires UserAdminService, and if authorization is turned on; SSOLoginService and SecurityTokenService.
In order to use the Administration UI the User requires a UserAdmin-role defined in UserIdentityBackend.


![Architectural Overview](https://raw2.github.com/altran/Whydah-SSOLoginWebApp/master/Whydah%20infrastructure.png)

Installation
============



* create a user for the service
* run start_service.sh
* ..or create the files from info below:

```
#!/bin/sh

#export IAM_MODE=DEV
export IAM_MODE=TEST
#export IAM_MODE=PROD

A=UserAdminWebApp
V=LATEST
JARFILE=$A-$V.jar

pkill -f $A

wget --user=altran --password=l1nkSys -O $JARFILE "http://mvnrepo.cantara.no/service/local/artifact/maven/content?r=snapshots&g=net.whydah.sso.service&a=$A&v=$V&p=jar"
nohup java -jar -DIAM_CONFIG=useradminwebapp.TEST.properties $JARFILE &

tail -f nohup.out
```

* create useradminwebapp.TEST.properties

```
# standalone=true
standalone=false

#
# Where am I installed and accessible?
#
myuri=http://localhost:9996/useradmin/
# myuri=http://myserver.net/useradmin/


#
#  Uses UserAdminService to get the users
#
useradminservice=http://localhost:9992/useradminservice/

#
# uses SSOLogonservice to redirect non-authenticated users
#
logonservice=http://localhost:9997/sso/
#logonservice=http://sso.myserver.net/sso/

#
# Logs on to SecurityTokenService to participate in the Whydah stack using AppCredentials
#
#tokenservice=http://myserverp.net/tokenservice/
tokenservice=http://localhost:9998/tokenservice/
```

Typical apache setup
====================

```
<VirtualHost *:80>
        ServerName myserver.net
        ServerAlias myserver
        ProxyRequests Off
        <Proxy *>
                Order deny,allow
                Allow from all
        </Proxy>
        ProxyPreserveHost on
                ProxyPass /sso http://localhost:9997/sso
                ProxyPass /uib http://localhost:9995/uib
                ProxyPass /tokenservice http://localhost:9998/tokenservice
                ProxyPass /useradmin http://localhost:9996/useradmin
                ProxyPass /test http://localhost:9990/test/
</VirtualHost>
```




Developer info
==============

* https://wiki.cantara.no/display/iam/Architecture+Overview
* https://wiki.cantara.no/display/iam/Key+Whydah+Data+Structures
* https://wiki.cantara.no/display/iam/Modules

