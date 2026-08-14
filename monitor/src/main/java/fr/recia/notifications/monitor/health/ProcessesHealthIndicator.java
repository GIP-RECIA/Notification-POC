package fr.recia.notifications.monitor.health;

import fr.recia.notifications.monitor.service.ProcessMonitorService;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.boot.health.contributor.Health;
import org.springframework.stereotype.Component;

@Component
public class ProcessesHealthIndicator implements HealthIndicator {

    private final ProcessMonitorService monitor;

    public ProcessesHealthIndicator(ProcessMonitorService monitor) {
        this.monitor = monitor;
    }

    @Override
    public Health health() {
        Health.Builder health = monitor.allUp() ? Health.up() : Health.down();
        return health.build();
    }
}