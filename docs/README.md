Docs
====

Docs images are built with [draw.io](https://www.draw.io/), and sources are available as xml files in `docs` directory.

Roadmap
-------

[Roadmap is available here](https://github.com/GIP-RECIA/Notification-POC/blob/master/docs/ROADMAP.md)


Architecture
------------
![Event-UserEvent-Notification.png](./Event-UserEvent-Notification.png)

![Sequence_Diagram.png](./Sequence_Diagram.png)


### Data Structure Definitions

#### Event

An **Event** is the raw source of data that is emitted from any application, at any time.

It's NOT related to which user will receive a notification nor which media will be used.

| Property  | Type | Description |
| ------------- | ------------- | --- |
| priority | Enum\<EventPriority\> | Priority of this event |
| type | Enum\<EventType\> | Type of this event |
| title | String | Title of the event |
| message | String | Message content of the event |
| date | Date | Date of the event |
| scheduleDate | Date | Date when notifications should occur |
| expiryDate | Date | Date after when notifications should not occur |
| properties | Map\<String, String\> | Other custom properties

#### UserEvent

A **UserEvent** is an **Event** that has been dispatched to a specific user.

It's related to a single user, but NOT related to which media will be used.

There are commonly many **UserEvent** instances generated for each **Event** instance.


| Property  | Type | Description |
| ------------- | ------------- | ------------- |
| event  | Event | Source event
| userUid  | String | User unique identifier

#### Notification

A **Notification** is one or many **UserEvent** instances that should be sent through a specific media type 
(Mail, SMS, Web Portal) to notify the user.

Notification instance may be build and sent in real-time when **UserEvent** are processed (High priority alerts, One 
Notification per UserEvent), but may also be delayed to digest many **UserEvent** instances into a single **Notification** 
(Daily or Weekly mail).

There are commonly one or more **Notification** instance generated for each **UserEvent** instance.

| Property  | Type | Description |
| ------------- | ------------- | ------------- |
| userEvent  | UserEvents | Source user event to notify (Not a digest)
| digestUserEvents  | List\<UserEvents\>  | Digest of user events to notify (Is a digest) |
| mediaType  | String | Which type of media will the notification be emitted with |

#### Emission

An **Emission** is an attempt to send one Notification over it's specified media.

There is commonly one **Emission** instance per Notification instance, but there may be more in case of errors (one instance
per retry attempt)

| Property  | Type |
| ------------- | ------------- |
| notification  | Notification  |
| error  | boolean |
| errorMessage | String |

### Software components

* **RabbitMQ**

**Role**: Handle reception and dispatching of events produced by applications.

* **Apache**

**Role**: WebSocket Reverse Proxy in front of RabbitMQ WebStomp plugin WebSocket service, to allow web applications 
to be notified through the same URL.

* **ElasticSearch**

**Role**: Consume all events handled by RabbitMQ to ensure storage, search, tracking and statistics. Store events and 
generated notifications.

* **CLI Application**

**Tech**: Java/Vanilla  
**RabbitMQ Protocol**: AMQP  
**Type**: Command Line Interface application  
**Role**: Generate/consume random events (Step 1) and real world events (Step 3).

- **Web Application**

**Tech**: HTML5/VueJS  
**RabbitMQ Protocol**: STOMP over WebSocket  
**Type**: Web application  
**Role**: Display generated random events (Step 1) and real world events (Step 3). Emit real world events (Step 3).

- **Event Registration Service**

**Tech**: Java/Spring Boot  
**RabbitMQ Protocol**: AMQP + STOMP over WebSocket  
**Type**: Service  
**Role**: Register users to various events type, priority and media, and dispatch incoming events to notifications based
on registrations.

- **Notification Emitter Service**

**Tech**: Java/Spring Boot  
**RabbitMQ Protocol**: AMQP + STOMP over WebSocket  
**Type**: Service  
**Role**: Effectively sends notifications to various media.