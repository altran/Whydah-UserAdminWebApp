UserAdministration
========================

Administration UI for Whydah Users and their mapping to Roles, Applications and Organizations.
Requires UserIdentityBackend, and if authorization is turned on; SSOLoginService and SecurityTokenService.
In order to use the Administration UI the User requires a UserAdmin-role defined in UserIdentityBackend.




Installation
============



* create a user for the service
* create start_service.sh

```
#!/bin/sh

export IAM_MODE=DEV
#export IAM_MODE=TEST

A=UserAdministration
V=1.0-SNAPSHOT
JARFILE=$A-$V.jar

pkill -f $A

wget --user=altran --password=l1nkSys -O $JARFILE "http://mvnrepo.cantara.no/service/local/artifact/maven/content?r=altran-snapshots&g=net.whydah.sso.service&a=$A&v=$V&p=jar"
nohup java -jar -DIAM_CONFIG=useradmin.TEST.properties $JARFILE &

tail -f nohup.out
```

* create useradmin.TEST.properties

```
# standalone=true
standalone=false

#
# Where am I installed and accessible?
#
myuri=http://localhost:9996/useradmin/
# myuri=http://myserver.net/useradmin/


#
#  Uses UserIdentiTyBackend to get the users
#
# useridentitybackend=http://myserver.net/uib/
useridentitybackend=http://localhost:9995/uib/

#
# uses SSOLogonservice to redirect non-authenticated users
#
logonserviceurl=http://localhost:9997/sso/
#logonserviceurl=http://myserver.net/sso/

#
# Logs on to SecurityTokenService to participate in the Whydah stack using AppCredentials
#
#tokenservice=http://myserverp.net/tokenservice/
tokenservice=http://localhost:9998/tokenservice/
```



TODO: 
Show proper error message when receiving connection errors. Connection Refused leaves a page without any data.
Change localization to English.
What does myJsonPersonCustomerStore.js do? It tries to get "members" from url: myHostJson+'?url=http://localhost:9999/members/'. Remove this?
Do a comprehensive test of the functionality in the UI. Does everything work as expected? If not, do we need it?