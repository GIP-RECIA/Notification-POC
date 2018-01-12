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

```json
{
  header: {
    type: "...", // Type of the event
    priority: "...", // Priority of the event (HIGH or NORMAL)
    groupUuids: [...], // Groups UUIDs to notify
    userUuids: [...], // User UUIDs to notify
    medias: [...], // Media to use (web, mail, ...)
  },
  content: {
    title: "...",
    message: "...",
    date: "...",
    properties: {
       ... // Custom additional key/value properties
    }
  }
}
```

Event content is transmit through the processing chain and it's structure will be kept in other objects. It contains
data that should be displayed to the user at the end of the processing.

#### UserEvent

A **UserEvent** is an **Event** that has been dispatched to a specific user.

It's related to a single user, but NOT related to which media will be used.

There are commonly many **UserEvent** instances generated for each **Event** instance.

```json
{
  header: {
    event: {
      ... // Event.header content
    },
    userUuid: "..." // User uuid to dispatch event to.
  },
  content: {
    ... // Event.content content
  }
}
```

#### Notification

A **Notification** is one **UserEvent** instance that should be sent through a specific media type 
(Mail, SMS, Web Portal) to notify the user.

Notification instance may be build and sent in real-time when **UserEvent** are processed (High priority alerts, One 
Notification per UserEvent), but may also be delayed to digest many **UserEvent** instances into a single **Notification** 
(Daily or Weekly mail).

There are commonly one or more **Notification** instance generated for each **UserEvent** instance.

```json
{
  header: {
    userEvent: {
      ... // UserEvent.header content
    },
    media: "..." // Media name to dispatch event to.
  },
  content: {
    ... // Event.content content
  }
}
```

#### Emission

An **Emission** is an attempt to send one Notification over it's specified media.

There is commonly one **Emission** instance per Notification instance, but there may be more in case of errors (one instance
per retry attempt)

```json
{
  header: {
    notification: {
      ... // Notification.header content
    },
    media: "..." // Media uuid to dispatch event to.
  },
  content: {
    date: "...",
    failed: false/true,
    message: "..."
  }
}
```

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

- **CAS**

**Role**: Act as an OpenID server to authenticate users.