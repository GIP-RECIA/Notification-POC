

# e2e-tests

Ensemble des tests d'intégrations du POC notification. Ils peuvent se lancer à la demande sans besoin de composant externe.

## Lancement

Pour lancer les tests au complet, faire un `./scripts-bash/run-tests.sh`
Pour lancer un test en particliuer, lancer l'infra et les services, puis éxécuter le test depuis l'IDE ou avec `mvn test`

## Fonctionnement

- On utilise le `HttpNotificationClient` en entrée pour émettre des events.
- On utilise l'API des préférences avec des requêtes POST pour ajouter des nouvelles préférences
- On vérifie qu'on récupère bien les notifications en sortie au bon endroit et avec la bonne forme en faisant des requêtes sur les différents points de sortie des consumers.