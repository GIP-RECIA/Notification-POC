

# Expander

Consumer et producer kafka qui à pour rôle de transformer les events émis par les services et communs à un ensemble d'utilisateurs en notifications à l'utilisateur.

## Dépendances

Java :
- Spring Boot
- Model Kafka
- Spring Boot Kafka
- Jackson
- Spring Boot Data Ldap
- Lombok et Slf4j (communes à tous)

Infra :
- Kafka
- LDAP

## Fonctionnement

- Le consumer lit sur un topic en entrée : `events.requested`.
- Il regarde la target (liste user ou liste groupe) de l'event :
  - Si c'est un user alors il créé une notification par user qu'il dépose dans le topic de sortie `events.expanded`
  - Si c'est un groupe alors il requête le LDAP pour récupérer tous les users des groupes concernés. Il créé ensuite une notification par user qu'il dépose dans le topic de sortie `events.expanded`

## Exécution

`mvn spring-boot:run` ou `./scripts-bash/start-expand.sh`

## Configuration

| Propriété                                  | Signification                          |
|--------------------------------------------|----------------------------------------|
| spring.kafka.bootstrap-servers             | Adresses des brokers kafka             |
| spring.kafka.consumer.group-id             | Groupe du consumer                     |
| spring.kafka.X.properties.sasl.jaas.config | Login/Mdp de l'utilisateur au kafka    |
| spring.ldap.X                              | Informations pour se connecter au LDAP |