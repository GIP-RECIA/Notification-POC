#!/bin/bash

cd kafka-poc
docker compose down
cd ..
./scripts-bash/start-kafka.sh