#!/bin/bash

cd consumer-mail-poc
mvn spring-boot:run &
cd ..
cd consumer-push-poc
mvn spring-boot:run &
cd ..
cd consumer-kafka-poc
mvn spring-boot:run -Dspring-boot.run.profiles=node1 &
cd ..
cd producer-api-poc
mvn spring-boot:run -Dspring-boot.run.profiles=node1 &
cd ..
cd preferences-api-poc
mvn spring-boot:run &
cd ..
cd routing-kafka-poc
mvn spring-boot:run &
cd ..
cd replayer-kafka-poc
mvn spring-boot:run &
cd ..
cd expand-kafka-poc
mvn spring-boot:run &
cd ..
cd service-example-kafka-poc
mvn spring-boot:run &
cd ..