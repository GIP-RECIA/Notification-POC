package fr.recia.notifications.monitor.metrics;

import fr.recia.notifications.monitor.config.MonitorProperties;
import fr.recia.notifications.monitor.config.ProcessDefinition;
import fr.recia.notifications.monitor.model.MonitoredProcess;
import fr.recia.notifications.monitor.service.ProcessMonitorService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ProcessMetrics {

    public ProcessMetrics(MeterRegistry registry, ProcessMonitorService monitor, MonitorProperties properties) {
        Gauge.builder("processes_up", monitor, ProcessMonitorService::countUp)
                .description("Number of configured processes currently running")
                .register(registry);

        Gauge.builder("processes_down", monitor, ProcessMonitorService::countDown)
                .description("Number of configured processes currently down")
                .register(registry);

        for (ProcessDefinition definition : properties.getProcesses()) {
            registerProcessGauges(registry, monitor, definition.getName());
        }
    }

    private void registerProcessGauges(MeterRegistry registry, ProcessMonitorService monitor, String serviceName) {
        Gauge.builder("process_up", monitor, m -> m.get(serviceName) != null && m.get(serviceName).isUp() ? 1.0 : 0.0)
                .tag("service", serviceName)
                .description("Whether the configured process is running")
                .register(registry);

        Gauge.builder("process_cpu_percent", monitor, m -> value(m.get(serviceName), MonitoredProcess::getCpuPercent))
                .tag("service", serviceName)
                .description("Process CPU usage")
                .register(registry);

        Gauge.builder("process_memory_bytes", monitor,
                        m -> value(m.get(serviceName), p -> (double) p.getMemoryBytes()))
                .tag("service", serviceName)
                .description("Process memory usage")
                .register(registry);
    }

    private double value(MonitoredProcess process, java.util.function.ToDoubleFunction<MonitoredProcess> function) {
        return process == null ? 0.0 : function.applyAsDouble(process);
    }
}
