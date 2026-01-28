

# Producer API

Producer kafka qui à pour rôle d'être le point d'entrée au kafka via une API exposée aux services de l'extérieur. Il assure la sécurité en ne poussant dans le kafka que les notifications de sources autorisées.

## Dépendances

Java :
- Spring Boot Web
- Model Kafka
- Spring Boot Kafka
- Spring Security
- Lombok et Slf4j (communes à tous)

Infra :
- Kafka
- Redis

## Fonctionnement

- Le producer expose une API publique `/events` dont se servent les services pour envoyer des events.
- Quand le producer reçoit un event, il vérifie d'abord que le service est autorisé à envoyer un event au kafka.
- Ensuite il produit l'event demandé dans le topic `events.requested` sans pré-traitement, l'idée étant de le mettre dans le kafka le plus vite possible.

## Exécution

- `mvn spring-boot:run` ou `./scripts-bash/start-producer.sh`
- Ports 8X79

## Configuration

| Propriété                                         | Signification                                                      |
|---------------------------------------------------|--------------------------------------------------------------------|
| spring.kafka.bootstrap-servers                    | Adresses des brokers kafka                                         |
| spring.kafka.producer.group-id                    | Groupe du consumer                                                 |
| spring.kafka.producer.properties.sasl.jaas.config | Login/Mdp de l'utilisateur au kafka                                |
| security.api-keys                                 | Liste de couples (service,clé) pour définir les services autorisés |
| security.allowed-ips                              | Liste des plages d'adresses IP autorisées pour tous les services   |
