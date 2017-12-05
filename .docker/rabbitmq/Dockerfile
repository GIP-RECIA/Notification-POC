FROM rabbitmq:3.6.14-management

RUN rabbitmq-plugins enable --offline rabbitmq_web_stomp
RUN rabbitmq-plugins enable --offline rabbitmq_stomp
COPY rabbitmq.config /etc/rabbitmq/rabbitmq.config

EXPOSE 61613
EXPOSE 15674
