package fr.recia.notifications.preferences_api.configuration;

import fr.recia.notifications.model_kafka.model.ChannelPreferences;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "prefs")
public class PreferencesProperties {
    private List<String> notificationServices;
    private List<String> systemServices = new ArrayList<>();
    private ChannelPreferences defaultChannels;
}

