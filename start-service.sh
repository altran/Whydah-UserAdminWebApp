#!/bin/sh

nohup /usr/bin/java -DIAM_MODE=PROD -DIAM_CONFIG=/home/UserAdminWebApp/useradminwebapp.PROD.properties -jar /home/UserAdminWebApp/UserAdminWebApp.jar &
