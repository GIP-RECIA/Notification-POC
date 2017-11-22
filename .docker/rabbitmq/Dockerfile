FROM rabbitmq:3.6.14-management

RUN rabbitmq-plugins enable --offline rabbitmq_web_stomp
COPY rabbitmq.config /etc/rabbitmq/rabbitmq.config

EXPOSE 15674
