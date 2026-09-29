package fr.recia.notifications.producer_api.controller;

import fr.recia.notifications.model_kafka.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class TestDeMonteEnCharge {

    @Autowired
    private KafkaTemplate<String, ServiceEvent> kafkaTemplate;

    @Test
    public void testMilliers() throws InterruptedException {
        int totalMessages = 10000;
        int concurrency = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(concurrency);
        System.out.println("Début du test. Lancement de :" + totalMessages + " messages");;

        for (int i=0; i<totalMessages; i++) {
            executorService.submit(() -> {
                String id = UUID.randomUUID().toString();
                ServiceEvent event = new ServiceEvent(
                        new EventHeader(id, Priority.NORMAL, "ok", List.of(Channel.WEB), "2026-05-18"),
                        new Content("Notif de stress", "Contenu", "Lien"),
                        new Target(TargetType.UID, List.of("f1700ivg"))
                );
                kafkaTemplate.send("notifications.events.requested", id, event);
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(5,TimeUnit.MINUTES);
        kafkaTemplate.flush();

        System.out.println(">>> TIR REUSSI : Tous les messages sont dans Kafka. En attente du traitement des consumers...");
        Thread.sleep(30000);
    }
}
