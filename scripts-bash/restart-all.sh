#!/bin/bash

./scripts-bash/restart-producer.sh
./scripts-bash/restart-consumer-web.sh
./scripts-bash/restart-consumer-mail.sh
./scripts-bash/restart-consumer-push.sh
./scripts-bash/restart-preferences.sh
./scripts-bash/restart-router.sh
./scripts-bash/restart-expand.sh
./scripts-bash/restart-replayer.sh
./scripts-bash/restart-service.sh