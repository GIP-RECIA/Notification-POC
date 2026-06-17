#!/bin/bash

cd consumer-mail
mvn clean package &
cd ..
cd consumer-push
mvn clean package &
cd ..
cd consumer-web
mvn clean package &
cd ..
cd producer-api
mvn clean package &
cd ..
cd preferences-api
mvn clean package &
cd ..
cd router
mvn clean package &
cd ..
cd expander
mvn clean package &
cd ..
cd service-example-kafka
mvn clean package &
cd ..
cd delayer
mvn clean package -DskipTests &
cd ..
cd smtp-proxy
mvn clean package &
cd ..