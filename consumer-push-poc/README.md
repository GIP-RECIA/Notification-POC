

# Consumer Push

Consumer kafka qui à pour rôle d'envoyer des notifications push sur mobile pour les notifications qui sont destinées à être envoyées sous cette forme.

## Dépendances

Java :
- Spring Boot Web
- Model Kafka
- Spring Boot Kafka
- Google Auth Library OAuth2
- Kafka Streams
- Jackson
- Lombok et Slf4j (communes à tous)

Infra :
- Kafka
- Firebase API (externe)

## Fonctionnement

- Le consumer lit sur un topic en entrée : `notifications.push`.
- Il récupère les devices token associés à l'utilisateur stockés dans kafka
- Il s'authentifie auprès de l'API firebase en récupérant un Access Token qui sera utilisé dans les requêtes suivantes si ce n'est pas déjà fait
- Pour chaque device token, il envoie une notification à l'API firebase
  - L'enregistrement des devices se fait de manière séparé par des appels directs à une API exposée sur la route `/register`
  - Lorsqu'on reçoit une réponse en erreur avec l'info `UNREGISTERED` depuis firebase pour un device token donné, alors on le supprime de la liste 
- Si une notification ne peut pas être envoyée pour une quelconque raison, alors il dépose la notification dans un topic pour la rejouer plus tard : `notifications.replay.push`.

## Exécution

- `mvn spring-boot:run` ou `./scripts-bash/start-consumer.push.sh`
- Ports 8X76

## Configuration

| Propriété                                  | Signification                                                                                   |
|--------------------------------------------|-------------------------------------------------------------------------------------------------|
| soffit.jwt.signature-key                   | Clé de signature pour la soffit                                                                 |
| spring.kafka.bootstrap-servers             | Adresses des brokers kafka                                                                      |
| spring.kafka.consumer.group-id             | Groupe du consumer                                                                              |
| spring.kafka.X.properties.sasl.jaas.config | Login/Mdp de l'utilisateur au kafka                                                             |
| streams.bootstrap-servers                  | Adresses des brokers kafka                                                                      |
| streams.application-id                     | Id unique de l'application pour le stream                                                       |
| streams.replication-factor                 | Nombre d'instances du stream                                                                    |
| streams.sasl.jaas.config                   | Login/Mdp de l'utilisateur des streams au kafka                                                 |
| fcm.service-accont-path                    | Chemin vers le fichier de clé privée pour s'authentifier pour obtenir des AT auprès de firebase |
| fcm.fcm-url                                | Endpoint complet de l'API firebase pour envoyer des notifications                               |
| fcm.firebase-auth-url                      | Endpoint complet de l'API firebase pour s'authentifier                                          |