#!/bin/bash

cd consumer-mail
mvn clean package &
cd ..
sleep 1
cd consumer-push
mvn clean package &
cd ..
sleep 1
cd consumer-web
mvn clean package &
cd ..
sleep 1
cd producer-api
mvn clean package &
cd ..
sleep 1
cd preferences-api
mvn clean package &
cd ..
sleep 1
cd router
mvn clean package &
cd ..
sleep 1
cd expander
mvn clean package &
cd ..
sleep 1
cd service-example-kafka
mvn clean package &
cd ..
sleep 1
cd delayer
mvn clean package -DskipTests &
cd ..
sleep 1
cd smtp-proxy
mvn clean package &
cd ..
sleep 1