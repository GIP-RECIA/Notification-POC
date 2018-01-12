# Notifications POC for the ESCO-Portail

This is a proof of concept for emitting and receiving notifications from/to ESCO-Portail.

It leverages [RabbitMQ](http://www.rabbitmq.com/) and [Elastic.io](https://www.elastic.co) products to emit, route, 
store and dispatch notifications.

## Requirements

[Docker](https://www.docker.com/) and [docker-compose](https://docs.docker.com/compose/) are used to setup the 
technical environment containing all requirements.

## Setting up the environment

* Clone the github repository including submodules

```bash
git clone --recursive https://github.com/GIP-RECIA/Notification-POC.git
```

* Source enviromnent file

```bash
. ./env.sh
```

* Build CAS Overlay from `cas-esco-overlay` directory (see [cas-esco-overlay/README.md](./cas-esco-overlay/README.md))

* Download required docker images

```bash
dc pull
```

* Build custom docker images

```bash
dc build
```

* Start containers

```bash
dc up -d
```

## Services

- Notification Web Application: [http://localhost:8088/notification](http://localhost/notification)

- Random Beans Web Application: [http://localhost:8088/random-beans](http://localhost/random-beans)

- RabbitMQ Management Interface: [http://localhost:8088/rabbitmq/admin](http://localhost/rabbitmq/admin)

- Kibana: [http://localhost:5601](http://localhost:5061)

## Shutdown the environment

```bash
dc down
```

#### Clear data from the environment

```bash
dc down -v  # All data will be lost !
```

## Build webapp modules

To build a webapp (random-beans-webapp / notification-webapp), get inside the webapp directory and run `yarn build`.
