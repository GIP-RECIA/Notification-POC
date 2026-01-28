

# Preferences API

Producer et consumer kafka qui à pour rôle de permettre aux utilisateurs de gérer leurs préférences.

## Dépendances

Java :
- Spring Boot Web
- Model Kafka
- Spring Boot Kafka
- Kafka Streams
- Spring Security
- Soffit Java Client
- Lombok et Slf4j (communes à tous)

Infra :
- Kafka

## Fonctionnement

- Au démarrage on déclare une GlobalKTable qui va permettre d'accéder au topic des préférences utilisateur `user.preferences` qui est configuré de sorte à ne garder que la dernière valeur pour chaque clé.
- L'API expose un endpoint `/prefs` avec 2 méthodes (POST et GET) en fonction de si on veut récupérer ou modifier les préférences utilisateur.
  - On utilise un ReadOnlyKeyValueStore pour récupérer les valeurs depuis la GlobalKTable
  - On utilise un producer classique pour ajouter une nouvelle préférence utilsateur
- La gestion des préférences est simple :
  - Par défaut les utilisateurs n'ont pas de préférences. La première fois qu'ils veulent les modifier, on créé des préférences vides.
  - Si un utilisateur a déjà des préférences, alors on charge ses préférences existantes qu'il peut modifier à sa guise.
  - A noter que si un utilisateur a déjà des préférences mais qu'il lui manque un service (ex: nouveau service), alors on modifie ses préférences pour ajouter le nouveau service.

## Exécution

- `mvn spring-boot:run` ou `./scripts-bash/start-preferences.sh`
- Ports 8X78

## Configuration

| Propriété                                         | Signification                                               |
|---------------------------------------------------|-------------------------------------------------------------|
| soffit.jwt.signature-key                          | Clé de signature pour la soffit                             |
| spring.kafka.bootstrap-servers                    | Adresses des brokers kafka                                  |
| spring.kafka.producer.group-id                    | Groupe du consumer                                          |
| spring.kafka.producer.properties.sasl.jaas.config | Login/Mdp de l'utilisateur au kafka                         |
| prefs.notification-services                       | Liste des services dont les préférences sont configurables  |
| prefs.default-channels                            | Préférences par défaut lorsqu'on créé des préférences vides |
