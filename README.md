# Notifications POC for the ESCO-Portail

This is a proof of concept for emitting and receiving notifications from/to ESCO-Portail.

It leverages [RabbitMQ](http://www.rabbitmq.com/) and [Elastic.io](https://www.elastic.co) products to emit, route, 
store and dispatch notifications.

## Requirements

[Docker](https://www.docker.com/) and [docker-compose](https://docs.docker.com/compose/) are used to setup the 
technical environment containing all requirements.

#### Optional

You may also install [smartcd](https://github.com/cxreg/smartcd) to automatically register some handy shell aliases 
when entering into the project directory. This will bring commands for most common operations that goes through docker 
containers.

## Setting up the environment

* Clone the github repository

```bash
git clone https://github.com/GIP-RECIA/Notification-POC.git
```

* Start the containers with docker-compose

```bash
docker-compose up -d
```
