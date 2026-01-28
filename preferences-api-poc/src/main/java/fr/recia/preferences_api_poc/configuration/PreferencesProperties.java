package fr.recia.preferences_api_poc.configuration;

import fr.recia.model_kafka_poc.model.ChannelPreferences;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "prefs")
public class PreferencesProperties {
    private List<String> notificationServices;
    private ChannelPreferences defaultChannels;
}

