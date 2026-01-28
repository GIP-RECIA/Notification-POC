# Event Rest Client Kafka

Librairie qui permet aux services d'envoyer des notifications en passant par le producer kafka.

## Mise en place

1- Ajouter la dépendance dans le projet. Exemple avec maven :

```xml
<dependency>
    <groupId>fr.recia</groupId>
    <artifactId>event-rest-client-kafka-poc</artifactId>
    <version>1.0.6-SNAPSHOT</version>
</dependency>
```

2- Déclarer le bean nécéssaire au fonctionnement de la librairie :

```java
    @Bean
public HttpNotificationClient notificationClient() {
    return new HttpNotificationClient(producer_url, service_name, api_key);
}
```

3- Envoyer des notifications grâce aux méthodes disponibles grâce à l'instance de `HttpNotificationClient`:
```java
public void sendLowPriorityToUser(String title, String message, String userId);
public void sendNormalPriorityToUser(String title, String message, String userId);
public void sendHighPriorityToUser(String title, String message, String userId);
public void sendLowPriorityToGroup(String title, String message, String userId);
public void sendNormalPriorityToGroup(String title, String message, String userId);
public void sendHighPriorityToGroup(String title, String message, String userId);
```