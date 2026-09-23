#!/bin/bash

set -e

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="$ROOT_DIR/logs/ci"

mkdir -p "$LOG_DIR"

declare -A PIDS

echo "================================"
echo "Starting notification services"
echo "================================"


start_service() {
    local service="$1"
    shift

    echo "Starting $service..."

    cd "$ROOT_DIR/$service"

    "$@" > "$LOG_DIR/${service}.log" 2>&1 &
    PIDS["$service"]=$!

    cd "$ROOT_DIR"
}


wait_for_service() {
    local service="$1"
    local timeout="${2:-180}"
    local log_file="$LOG_DIR/${service}.log"

    echo "Waiting for $service..."

    for ((i=0; i<timeout/2; i++)); do

        # Le processus s'est arrêté -> erreur
        if ! kill -0 "${PIDS[$service]}" 2>/dev/null; then
            echo ""
            echo "ERROR: $service stopped during startup"
            echo "--------------------------------"
            tail -n 50 "$log_file"
            echo "--------------------------------"
            exit 1
        fi

        # Spring Boot indique que l'application est démarrée
        if grep -qE "Started .* in .* seconds" "$log_file" 2>/dev/null; then
            echo "$service is ready!"
            return 0
        fi

        sleep 2
    done

    echo ""
    echo "ERROR: Timeout while starting $service"
    echo "--------------------------------"
    tail -n 50 "$log_file"
    echo "--------------------------------"
    exit 1
}


# ========================================
# Start all services
# ========================================

start_service consumer-mail \
    mvn spring-boot:run

start_service consumer-push \
    mvn spring-boot:run

start_service consumer-web \
    mvn spring-boot:run \
    -Dspring-boot.run.profiles=node1

start_service producer-api \
    mvn spring-boot:run \
    -Dspring-boot.run.profiles=node1

start_service preferences-api \
    mvn spring-boot:run

start_service router \
    mvn spring-boot:run

start_service expander \
    mvn spring-boot:run

start_service service-example-kafka \
    mvn spring-boot:run

start_service delayer \
    mvn spring-boot:run \
    -DskipTests

start_service smtp-proxy \
    mvn spring-boot:run

start_service monitor \
    mvn spring-boot:run


# ========================================
# Wait for all services
# ========================================

wait_for_service consumer-mail
wait_for_service consumer-push
wait_for_service consumer-web
wait_for_service producer-api
wait_for_service preferences-api
wait_for_service router
wait_for_service expander
wait_for_service service-example-kafka
wait_for_service delayer
wait_for_service smtp-proxy
wait_for_service monitor


echo ""
echo "================================"
echo "All notification services are ready"
echo "================================"