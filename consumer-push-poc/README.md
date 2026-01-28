

# Consumer Push

Consumer kafka qui à pour rôle d'envoyer des notifications push sur mobile pour les notifications qui sont destinées à être envoyées sous cette forme.

## Dépendances

Java :
- Spring Boot
- Model Kafka
- Spring Boot Kafka
- Jackson
- Lombok et Slf4j (communes à tous)
- ?

Infra :
- Kafka
- ?

## Fonctionnement

- Le consumer lit sur un topic en entrée : `notifications.push`.
- TODO
- Si une notification ne peut pas être envoyée pour une quelconque raison, alors il dépose la notification dans un topic pour la rejouer plus tard : `notifications.replay.push`.


## Exécution

`mvn spring-boot:run` ou `./scripts-bash/start-consumer.push.sh`

## Configuration

| Propriété                                  | Signification                          |
|--------------------------------------------|----------------------------------------|
| soffit.jwt.signature-key                   | Clé de signature pour la soffit        |
| spring.kafka.bootstrap-servers             | Adresses des brokers kafka             |
| spring.kafka.consumer.group-id             | Groupe du consumer                     |
| spring.kafka.X.properties.sasl.jaas.config | Login/Mdp de l'utilisateur au kafka    |