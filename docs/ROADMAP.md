Roadmap
=======

Step 1
------

**Set up RabbitMQ and sample applications**

- Setup a **RabbitMQ** instance with WebStomp support.
- Setup a **Apache** instance as a WebSocket Reverse Proxy to provide access to RabbitMQ WebStomp plugin.
- Write a **CLI Application** generating/consuming random events.
- Write a **Web Application** consuming those events.
- Showcase Direct Exchange configuration.
- Showcase Fanout Exchange configuration.
- Showcase Routing Exchange configuration.
- Showcase Topics Exchange configuration.

*RabbitMQ exchange configuration are introduced in [RabbitMQ tutorials](https://www.rabbitmq.com/tutorials/)*

Step 2
------

**Set up ElasticSearch**

- Setup an **ElasticSearch** instance. 
- Configure RabbitMQ Logstash plugin.
- Configure ElasticSearch to search through emitted random events.

Step 3
------

**Emit real world events**

- Create real world classes to emit events from (Alert, Document, News, ...).
- Consume those events in **CLI Application**. 
- Consume those events in **Web Application**.
- Produce those events in **Web Application** (on user input).
- Configure ElasticSearch to support those events.

Step 4
------

**Dispatch real world events to user notifications**

- Write a **User Event Registration Service** to register user to various event properties (type, level, ...) and 
media.
- Dispatch incoming events to media based on user registrations in **Notification Service**.
- Persist produced notifications in **ElasticSearch**
- Update **Web Application** to support login and events that match registration only.
- Display persisted notifications in **CLI Application**.
- Display persisted notifications in **Web Application**.
