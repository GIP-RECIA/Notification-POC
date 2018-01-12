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

* Build CAS Overlay from `cas-esco-overlay` directory (see [GIP-RECIA/cas-esco-overlay/README.md](https://github.com/GIP-RECIA/cas-esco-overlay/blob/master/README.md))

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

## Showcase

- Login with an existing account from on [localhost:8088/notification](http://localhost/notification)

- It should then display **Connected** green badge on navbar (It means websocket is up and ready to receive notifications)

- HTTP **POST** to event service to emit an event

POST [localhost:8081/event/emit](localhost:8081/event/emit)
Content-Type: application/json
Body (Adjust `userUuids` field to the uuid of connected user):

```json
{
  "header": {
    "type": "test",
    "userUuids": ["F1000ugr"],
    "medias": ["web"]
  },
  "content": {
    "title": "Hello World",
    "message": "First notification"
  }
}
```

- This should display a real time notification on the webapp through established WebSocket.

- When reloading the webapp, the existing notification are loaded through ElasticSearch storage.

## Services

- Notification Web Application: [http://localhost:8088/notification](http://localhost/notification)

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