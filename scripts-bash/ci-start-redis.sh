#!/bin/bash

set -e

cd redis

echo "Starting Redis..."
docker compose up -d

echo "Waiting for Redis..."

until docker exec redis redis-cli ping | grep -q "PONG"
do
    sleep 2
done

echo "Redis is ready!"

cd ..