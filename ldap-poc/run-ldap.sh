#!/bin/bash

echo "Starting LDAP docker image..."

docker compose down
docker volume rm ldap-poc_notifications-ldap-data
docker volume rm ldap-poc_notifications-ldap-config
docker compose build --no-cache
docker compose up &

sleep 10

docker ps | grep "openldap-notifications"
retVal=$?
if [ $retVal == 0 ]; then
    echo "LDAP docker container is running."
else
    echo "LDAP docker container failed to start."
    exit $retVal
fi
