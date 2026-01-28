#!/bin/bash

./scripts-bash/stop-all.sh
./scripts-bash/stop-infra.sh

./scripts-bash/restart-infra.sh
./scripts-bash/restart-minimal.sh

# attendre que le kafka soit complètement démarré et fonctionnel
sleep 30

cd e2e-tests
mvn test
cd ..

./scripts-bash/stop-all.sh
./scripts-bash/stop-infra.sh