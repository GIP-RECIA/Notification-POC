#!/bin/bash

set -e

echo "================================"
echo "Starting test infrastructure"
echo "================================"

./scripts-bash/ci-start-kafka.sh
./scripts-bash/ci-start-redis.sh
./scripts-bash/ci-start-ldap.sh

echo "================================"
echo "Test infrastructure is ready"
echo "================================"