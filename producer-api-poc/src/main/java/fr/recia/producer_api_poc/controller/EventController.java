package fr.recia.producer_api_poc.controller;

import fr.recia.model_kafka_poc.model.ServiceEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
@Slf4j
public class EventController {

    private final KafkaTemplate<String, ServiceEvent> kafkaTemplate;

    public EventController(KafkaTemplate<String, ServiceEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/emit")
    public ResponseEntity<Void> emit(@RequestBody ServiceEvent event) {
        try {
            kafkaTemplate.send("events.requested", event.getHeader().getEventId(), event);
            log.trace("Produced ServiceEvent {} in kafka", event);
        } catch (Exception e) {
            log.error("Unable to produce ServiceEvent {} to kafka", event, e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        return ResponseEntity.accepted().build();
    }

}
