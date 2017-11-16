FROM rabbitmq:3.6.14-management
MAINTAINER Rémi Alvergnat <remi.alvergnat@gfi.fr>

RUN rabbitmq-plugins enable --offline rabbitmq_web_stomp
COPY rabbitmq.config /etc/rabbitmq/rabbitmq.config

EXPOSE 15674
