#!/bin/bash

cd replayer-kafka-poc
mvn spring-boot:run -Dspring-boot.run.profiles=node1 &
mvn spring-boot:run -Dspring-boot.run.profiles=node2 &
mvn spring-boot:run -Dspring-boot.run.profiles=node3 &
cd ..