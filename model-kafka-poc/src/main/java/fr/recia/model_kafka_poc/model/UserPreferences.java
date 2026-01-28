package fr.recia.model_kafka_poc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPreferences {
    private String userId;
    private ChannelPreferences global;
    private Map<String, ServicePreferences> services;
}