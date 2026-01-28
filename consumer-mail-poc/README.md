

# Consumer Mail

Consumer kafka qui à pour rôle d'envoyer des mails pour les notifications qui sont destinées à être envoyées sous cette forme.

## Dépendances

Java :
- Spring Boot
- Model Kafka
- Spring Boot Kafka
- Jackson
- Spring Boot Data Ldap
- Jakarta Mail
- Lombok et Slf4j (communes à tous)

Infra :
- Kafka
- LDAP
- Mailpit

## Fonctionnement

- Le consumer lit sur un topic en entrée : `notifications.mail`.
- Il récupère chaque notification qu'il reçoit et envoie un mail grâce à la librairie jakarta.mail.
- Avant d'envoyer le mail il requête le LDAP pour savoir quelle est l'adresse mail de l'utilisateur en fonction de son uid.
- Si une notification ne peut pas être envoyée pour une quelconque raison, alors il dépose la notification dans un topic pour la rejouer plus tard : `notifications.replay.mail`.


## Exécution

`mvn spring-boot:run` ou `./scripts-bash/start-consumer.mail.sh`

## Configuration

| Propriété                                  | Signification                          |
|--------------------------------------------|----------------------------------------|
| soffit.jwt.signature-key                   | Clé de signature pour la soffit        |
| spring.kafka.bootstrap-servers             | Adresses des brokers kafka             |
| spring.kafka.consumer.group-id             | Groupe du consumer                     |
| spring.kafka.X.properties.sasl.jaas.config | Login/Mdp de l'utilisateur au kafka    |
| spring.ldap.X                              | Informations pour se connecter au LDAP |
| mail.smtp.X                                | Informations pour se connecter au SMTP |