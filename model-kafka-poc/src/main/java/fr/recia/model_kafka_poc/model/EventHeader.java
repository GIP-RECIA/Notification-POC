package fr.recia.model_kafka_poc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventHeader {
    private String eventId;
    private Priority priority;
    private String service;
    private String createdAt;
}
