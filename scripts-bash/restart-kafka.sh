#!/bin/bash

cd kafka
docker compose down
cd ..
./scripts-bash/start-kafka.sh