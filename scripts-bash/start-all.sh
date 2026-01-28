#!/bin/bash

./scripts-bash/start-producer.sh
./scripts-bash/start-consumer-web.sh
./scripts-bash/start-consumer-mail.sh
./scripts-bash/start-consumer-push.sh
./scripts-bash/start-preferences.sh
./scripts-bash/start-router.sh
./scripts-bash/start-expand.sh
./scripts-bash/start-replayer.sh
./scripts-bash/start-service.sh