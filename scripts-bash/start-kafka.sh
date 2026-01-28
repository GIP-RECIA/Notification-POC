#!/bin/bash

# Démarrage du kafka
cd kafka-poc
docker compose up -d
sleep 10

# Création des topics
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic events.requested --partitions 3 --replication-factor 3 --config retention.ms=86400000
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic events.expanded --partitions 3 --replication-factor 3 --config retention.ms=86400000
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic user.preferences --partitions 3 --replication-factor 3 --config cleanup.policy=compact --config retention.ms=86400000
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic notifications.web --partitions 3 --replication-factor 3 --config retention.ms=86400000
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic notifications.mail --partitions 3 --replication-factor 3 --config retention.ms=86400000
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic notifications.push --partitions 3 --replication-factor 3 --config retention.ms=86400000
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic notifications.replay.web --partitions 3 --replication-factor 3 --config retention.ms=86400000
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic notifications.replay.mail --partitions 3 --replication-factor 3 --config retention.ms=86400000
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic notifications.replay.push --partitions 3 --replication-factor 3 --config retention.ms=86400000
docker exec -it broker-1 /opt/kafka/bin/kafka-topics.sh --command-config /etc/kafka/kraft/client-admin-config.properties --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic notifications.dlt --partitions 3 --replication-factor 3 --config retention.ms=86400000

cd ..