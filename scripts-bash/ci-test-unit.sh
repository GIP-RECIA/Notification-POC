#!/bin/bash

set -e

echo "================================"
echo "Running unit tests"
echo "================================"

echo "Running router tests..."
cd router
mvn test
cd ..

echo "Running delayer tests..."
cd delayer
mvn test
cd ..

echo "================================"
echo "All unit tests passed"
echo "================================"