  #!/bin/bash

cd consumer-mail
mvn spring-boot:run &
cd ..
cd consumer-push
mvn spring-boot:run &
cd ..
cd consumer-web
mvn spring-boot:run -Dspring-boot.run.profiles=node1 &
cd ..
cd producer-api
mvn spring-boot:run -Dspring-boot.run.profiles=node1 &
cd ..
cd preferences-api
mvn spring-boot:run &
cd ..
cd router
mvn spring-boot:run &
cd ..
cd expander
mvn spring-boot:run &
cd ..
cd service-example-kafka
mvn spring-boot:run &
cd ..
cd delayer
mvn spring-boot:run &
cd ..
cd smtp-proxy
mvn spring-boot:run &
cd ..