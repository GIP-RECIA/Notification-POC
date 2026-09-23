#!/bin/bash

set -e

echo "Starting LDAP..."

docker compose down -v

docker compose build

docker compose up -d

echo "Waiting for LDAP..."

until docker exec openldap-notifications ldapsearch \
    -x \
    -H ldap://localhost:389 \
    -D "cn=admin,ou=administrateurs,dc=esco-centre,dc=fr" \
    -w admin \
    -b "dc=esco-centre,dc=fr" \
    -s base \
    "(objectClass=*)" > /dev/null 2>&1
do
    sleep 2
done

echo "LDAP is ready!"