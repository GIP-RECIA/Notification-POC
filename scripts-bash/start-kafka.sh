#!/bin/bash

# Démarrage du kafka
cd kafka
docker compose up -d
sleep 10

# Création des topics
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic notifications.events.requested --partitions 3 --replication-factor 3 --config retention.ms=86400000
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic notifications.events.expanded --partitions 3 --replication-factor 3 --config retention.ms=86400000
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic notifications.user.preferences --partitions 3 --replication-factor 3 --config cleanup.policy=compact --config retention.ms=86400000
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic notifications.dlt --partitions 3 --replication-factor 3 --config retention.ms=86400000
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic notifications.push.tokens --partitions 3 --replication-factor 3 --config cleanup.policy=compact --config retention.ms=86400000
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic notifications.web --partitions 3 --replication-factor 3 --config retention.ms=86400000
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic notifications.mail --partitions 3 --replication-factor 3 --config retention.ms=86400000
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic notifications.push --partitions 3 --replication-factor 3 --config retention.ms=86400000
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic notifications.router --partitions 3 --replication-factor 3 --config retention.ms=86400000
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic notifications.replayer --partitions 3 --replication-factor 3 --config retention.ms=86400000

cd ..