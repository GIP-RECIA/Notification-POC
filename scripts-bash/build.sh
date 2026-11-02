#!/bin/bash

cd consumer-mail
mvn clean package -DskipTests &
cd ..
sleep 3
cd consumer-push
mvn clean package -DskipTests &
cd ..
sleep 3
cd consumer-web
mvn clean package -DskipTests &
cd ..
sleep 3
cd producer-api
mvn clean package -DskipTests &
cd ..
sleep 3
cd preferences-api
mvn clean package -DskipTests &
cd ..
sleep 3
cd router
mvn clean package -DskipTests &
cd ..
sleep 3
cd expander
mvn clean package -DskipTests &
cd ..
sleep 3
cd service-example-kafka
mvn clean package -DskipTests &
cd ..
sleep 3
cd delayer
mvn clean package -DskipTests &
cd ..
sleep 3
cd smtp-proxy
mvn clean package -DskipTests &
cd ..
sleep 3
cd monitor
mvn clean package -DskipTests &
cd ..