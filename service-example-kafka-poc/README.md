

# Service Exemple

Appli spring-boot java qui simule un service qui envoie une notification à l'aide de la librairie faite pour.

## Dépendances

Java :
- Spring Boot Webmvc
- Rest Client Kafka
- Lombok et Slf4j (communes à tous)

Infra :
- Aucune dépendance

## Fonctionnement

- Lorsque le service se lance il créé une instance de `HttpNotificationClient`
- Lorsqu'il doit envoyer une notification il utilise les méthodes en question `notificationClient.sendXXXPriorityToUser(title, message, id);`

## Exécution

- `mvn spring-boot:run` ou `./scripts-bash/start-service.sh`
- Ports 8077

## Configuration

TODO