#!/usr/bin/env bash
echo "Waiting for $1 to be available "
until $(curl --output /dev/null --silent --head --fail $1); do
    printf '.'
    sleep 1
done
printf " READY !"