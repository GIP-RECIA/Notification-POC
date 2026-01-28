package fr.recia.model_kafka_poc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServicePreferences {
    private boolean enabled;
    private boolean override;
    private Map<Priority, ChannelPreferences> priorities;
}
