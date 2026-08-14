package fr.recia.notifications.monitor.service;

import fr.recia.notifications.monitor.model.MonitoredProcess;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class ProcessMonitorService {

    private final OshiProcessScanner scanner;
    private final AtomicReference<Map<String, MonitoredProcess>> snapshot = new AtomicReference<>(Map.of());

    public ProcessMonitorService(OshiProcessScanner scanner) {
        this.scanner = scanner;
        refresh();
    }

    @Scheduled(fixedDelayString = "${monitor.scan-interval:2s}")
    public void refresh() {
        List<MonitoredProcess> processes = scanner.scan();
        Map<String, MonitoredProcess> byName = processes.stream().collect(Collectors.toUnmodifiableMap(MonitoredProcess::getServiceName, p -> p));
        snapshot.set(byName);
    }

    public List<MonitoredProcess> all() {
        return snapshot.get().values().stream().toList();
    }

    public MonitoredProcess get(String serviceName) {
        return snapshot.get().get(serviceName);
    }

    public boolean allUp() {
        return snapshot.get().values().stream().allMatch(MonitoredProcess::isUp);
    }

    public long countUp() {
        return snapshot.get().values().stream().filter(MonitoredProcess::isUp).count();
    }

    public long countDown() {
        return snapshot.get().values().stream().filter(p -> !p.isUp()).count();
    }
}
