

## Modèle

Librairie java à importer dans les différents modules qui centralise tous les objets java utiles aux notifications tout au long de leur cycle de vie.

## Mise en place

Ajouter la dépendance dans le projet. Exemple avec maven :

```xml
<dependency>
    <groupId>fr.recia</groupId>
    <artifactId>model-kafka-poc</artifactId>
    <version>1.0.20-SNAPSHOT</version>
</dependency>
```

## Cycle de vie des objets

L'idée globale est d'enrichir les objets au fil de leur cycle de vie :
- La librairie client rest émet un `ServiceEvent`
- L'expander transforme un `ServiceEvent` en plusieurs `Notification`
- La jointure entre les topics `events.expanded` et `user.preferences` donne une `PrefrencesNotification`
- Le router sort une `RoutedNotification`
- Le consumer web stocke des `StoredNotification`

## Description des objets

- L'objet central est la `Notification`. Celle-ci est composée d'un `NotificationHeader` et d'un `Content` ;
- Le `NotificationHeader` est un `EventHeader` enrichi d'un userId (à qui est destiné la notification) et d'un notificationId (l'ID unique de la notification) ;
- Un `ServiceEvent` associe un `EventHeader` avec un `Content` et une `Target` (pour qui l'event est émis, peut être un groupe ou un ensemble d'utilisateurs) ;
- Les `RoutedNotification`, `StoredNotification` et `PreferencesNotification` sont juste des `Notification` enrichies ;
- Le `Content` est la charge utile de la notification (pour l'instant juste un titre et un message) ;
- L'`EventHeader` contient des informations sur l'event à l'origine de la notification : id unique de l'event, priorité, service et date de création.
