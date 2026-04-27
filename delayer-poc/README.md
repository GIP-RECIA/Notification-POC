# Delayer

Un Stateful Stream Processor kafka qui a pour rôle de delayer ou rejouer des notifications pour respecter le droit à la déconnexion (delai) et traiter les erreurs (replay).

## Dépendances

Java :
- Spring Boot
- Model Kafka
- Spring Boot Kafka
- Kafka Streams
- Jackson
- Spring Boot Data Ldap
- Lombok et Slf4j (communes à tous)

Infra :
- Kafka
- LDAP


## Fonctionnement

- Le delayer écoute sur deux topics différents : `notifications.router` et `notifications.replayer`
- Il crée un stream kafka configuré avec notre propre topology pour créer un store et savoir où écouter et où envoyer
- Quand il reçoit la notification, il fait un check pour savoir si elle peut être envoyée ou non. Si elle peut, alors elle est envoyée directement vers le topic correspondant à la notification, <br>
sinon, il est envoyé dans le store avec un délai qui a été calculé, avec un système de calcul de délai qui prend en compte le fuseau horaire, grâce à une requête ldap qui permet de récupérer un attribut du ldap avec le userID <br>
et que nous convertissons en string region.
  Enfin, il est envoyé vers le store
- Le punctuator scan le store toutes les minutes mais en utilisant la méthode range pour savoir si une notification peut être envoyée. <br>
On crée une clé avec le deliveryTime et l’id de la notification. Ensuite, on utilise range(from, to) avec to = maintenant qui va récupérer seulement les notifications qui ont un deliveryTime qui va de 0 à maintenant, donc celles qui peuvent être envoyées.
  Si la notification peut être envoyée, il l’envoie et la supprime du store, sinon, il n’y touche pas.

## Exécution

`mvn spring-boot:run` ou `./scripts-bash/start-delayer.sh`

## Configuration

| Propriété                      | Signification                                                                 |
|--------------------------------|-------------------------------------------------------------------------------|
| spring.kafka.bootstrap-servers | Adresses des brokers kafka                                                    |
| streams                        | Configuration des streams                                                     |
| streams.jaas.config            | Login/Mdp de l'utilisateur au kafka                                           |
| spring.ldap.X                  | Informations pour se connecter au LDAP                                        |
| vacances                       | Déclaration des vacances et jours fériés de la région centre et de la réunion |
| request                        | Requête ldap pour récupérer le domaines selon l'id de l'utilisateur           |
| domaines                       | Déclaration des domaines par région                                           |