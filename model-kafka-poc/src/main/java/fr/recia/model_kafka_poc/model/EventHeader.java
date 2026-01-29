package fr.recia.model_kafka_poc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventHeader {
    private String eventId;
    private Priority priority;
    private String service;
    private List<Channel> channels;
    private String createdAt;
}
