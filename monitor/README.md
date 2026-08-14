
# Monitor

Application spring boot qui à pour rôle de monitorer les différents modules de la plateforme de notifications en exposant un heath-check et des exports prometheus.

## Dépendances

Java :
- Spring Boot Web
- Spring Boot Actuator
- Micrometer Registry Prometheus
- Oshi Core
- Lombok

## Fonctionnement

Le monitor scanne régulièrement tous les process de l'OS et repère les modules de la plateforme de notifications grâce à leur nom. Il expose :
- actuator/health pour le health-check
- actuator/prometheus pour l'export prometheus

Les métriques exposées dans prometheus sont :
- `process_up{service="..."}` : 1 si le processus existe, 0 sinon
- `process_cpu_percent{service="..."}` : CPU consommé
- `process_memory_bytes{service="..."}` : mémoire consommée
- `processes_up` : nombre global de process up
- `processes_down` : nombre global de process down

## Exécution

- `mvn spring-boot:run` ou `./scripts-bash/start-monitor.sh`
- Ports 8X71

## Configuration

| Propriété                             | Signification                                                   |
|---------------------------------------|-----------------------------------------------------------------|
| monitor.scan-interval                 | Durée entre 2 scan de process                                   |
| monitor.processes[X].name             | Nom d'affichage du process X                                    |
| monitor.processes[X].command-pattern  | Commande du process X pour le répérer parmi les process système |