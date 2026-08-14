package fr.recia.notifications.monitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "monitor")
public class MonitorProperties {
    private Duration scanInterval;
    private List<ProcessDefinition> processes;
}