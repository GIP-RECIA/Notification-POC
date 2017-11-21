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
- Consume those events in **Web Application**.
- Produce those events in **Web Application** (on user input).
- Configure ElasticSearch to support those events.

Step 4
------

**Dispatch real world events as notifications**

- Write a minimalist **Event Registration Service** that simply dispatch events to notifications.
- Display real time notifications in **Web Application**.
- Persist produced notifications in **ElasticSearch**
- Display persisted notifications in **Web Application**.


Step 5
------

**Make notifications configurable by the user**

- Enhance **Event Registration Service** to register user to various event properties (type, level, ...) and 
media.
- Use user configuration to dispatch events to notifications on registered users and media only.
- Update **Web Application** to support login and events that match user only.
- Enhance **Event Emitter Service** to allow aggregation of UserEvent into a single Notification (digest).
