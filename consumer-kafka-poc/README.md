

# Consumer Web

Consumer kafka qui à pour rôle de récupérer et de transmettre les notifications destinées à être lues via la méthode d'envoi par web.

## Dépendances

Java :
- Spring Boot Web
- Model Kafka
- Soffit Java Client
- Spring Boot Kafka
- Jackson
- Spring Boot Data Redis
- Spring Security
- Lombok et Slf4j (communes à tous)

Infra :
- Kafka
- Redis

## Fonctionnement

- Le consumer lit sur un topic en entrée : `notifications.web`. 
- Il récupère chaque notification qu'il reçoit et la dépose dans un redis.
- Dans le redis les notifications sont stockées de deux manières :
  - A plat sous la forme (clé, notif) ;
  - Indéxées à l'utilisateur via un ensemble associé à l'id de l'utilisateur pour pouvoir les retrouver plus facilement.
- Si une notification ne peut pas être déposée dans le redis pour une quelconque raison, alors il dépose la notification dans un topic pour la rejouer plus tard : `notifications.replay.web`.
- Il expose aussi une API protégée par soffit que peut requêter la partie web : récupération des notifications, suppression d'une notification ou marquer comme lu.
- Il contient également une tâche de nettoyage du redis pour les notifications expirées.

## Exécution

- `mvn spring-boot:run` ou `./scripts-bash/start-consumer.web.sh`
- Ports 8X76

## Configuration

| Propriété                                  | Signification                           |
|--------------------------------------------|-----------------------------------------|
| soffit.jwt.signature-key                   | Clé de signature pour la soffit         |
| spring.kafka.bootstrap-servers             | Adresses des brokers kafka              |
| spring.kafka.consumer.group-id             | Groupe du consumer                      |
| spring.kafka.X.properties.sasl.jaas.config | Login/Mdp de l'utilisateur au kafka     |
| spring.data.redis.X                        | Informations pour se connecter au redis |
