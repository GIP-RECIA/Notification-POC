

# Router

Stream kafka qui à pour rôle de déposer les notifications dans les bons topics de sortie en fonction du moyen d'émission choisi par l'utilisateur dans ses préférences.

## Dépendances

Java :
- Spring Boot
- Model Kafka
- Spring Boot Kafka
- Kafka Streams
- Jackson
- Lombok et Slf4j (communes à tous)

Infra :
- Kafka

## Fonctionnement

- Le stream fait une jointure sur 2 topics en entrée : `events.expanded` et `user-preferences` (qui est une KTable)
- A partir de ce nouveau Stream il fait une flatMap qui va pemettre de crééer 1 notification par moyen d'emission choisi par l'utilisateur. Cette notification est dite "enrichie" car elle contient en plus dans ses valeurs le topic de sortie.
- La logique de choix des channels de sortie est dans la méthode `resolveChannels()`. Elle peut se résumer de la manière suivante :
  - Priorité aux préférences de service si elles sont définies et activées ;
  - Sinon on utilise les préférences globales ;
  - Si pas de préférences du tout, on utilise des préférences par défaut.
- On fait un `.to` pour router les notifications une par une dans le bon topic (on peut savoir où les déposer car elles contiennent le topic dans leurs valeurs)

## Exécution

`mvn spring-boot:run` ou `./scripts-bash/start-router.sh`

## Configuration

TODO