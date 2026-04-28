

# Kafka

Docker qui permet de lancer un cluster kafka et de créer les topics et les utilisateurs :
- Lancement de 3 brokers et de leurs controlleurs associées. Ils sont accessibles sur aux adresses `localhost:29092`, `localhost:39092` et `localhost:49092`
  - La communication interborker se fait en `PLAIN`
  - Les clients doivent s'authentifier par login/mot de passe en `PLAIN`
- Création des topics (dans le script `start-kafka.sh`)
- Le docker lance également une UI permettant de visualiser l'état du kafka (provectuslabs/kafka-ui) accessible à l'adresse http://localhost:9091