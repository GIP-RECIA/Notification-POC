

# Router

Producer et consumer kafka qui à pour rôle de faire rejouer les notifications qui ont échouées à être émises.

## Dépendances

Java :
- Spring Boot
- Model Kafka
- Spring Boot Kafka
- Spring Boot LDAP
- Jackson
- Lombok et Slf4j (communes à tous)

Infra :
- Kafka

## Fonctionnement

- Le consumer écoute sur les topics `notification.replay.X`.
- A chaque fois qu'il reçoit une notification, il vérifie si elle combien de fois elle a été rejouée :
  - Si elle n'a pas été trop rejouée, alors il la remet à rejouer dans le topic `notifications.X` en comptant qu'elle a été rejouée une fois de plus (grâce à un Header Kafka) ;
  - Si elle a été trop rejouée, alors il la dépose dans un topic `notifications.dlt`.
- Ce module n'est lancé que pendant les heures autorisées par le droit à la déconnexion.

## Exécution

`mvn spring-boot:run` ou `./scripts-bash/start-replayer.sh`

## Configuration

| Propriété                                  | Signification                                                      |
|--------------------------------------------|--------------------------------------------------------------------|
| spring.kafka.bootstrap-servers             | Adresses des brokers kafka                                         |
| spring.kafka.producer.group-id             | Groupe du consumer                                                 |
| spring.kafka.X.properties.sasl.jaas.config | Login/Mdp de l'utilisateur au kafka                                |