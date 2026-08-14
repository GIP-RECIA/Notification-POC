package fr.recia.notifications.monitor.service;

import fr.recia.notifications.monitor.config.MonitorProperties;
import fr.recia.notifications.monitor.config.ProcessDefinition;
import fr.recia.notifications.monitor.model.MonitoredProcess;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OshiProcessScanner {

    private final MonitorProperties properties;
    private final OperatingSystem operatingSystem;
    private final Map<Integer, OSProcess> previousProcesses = new HashMap<>();

    public OshiProcessScanner(MonitorProperties properties) {
        this.properties = properties;
        this.operatingSystem = new SystemInfo().getOperatingSystem();
    }

    public synchronized List<MonitoredProcess> scan() {
        List<OSProcess> processes = operatingSystem.getProcesses(null, OperatingSystem.ProcessSorting.RSS_DESC, 0);
        Map<Integer, OSProcess> currentProcesses = new HashMap<>();
        for (OSProcess process : processes) {
            currentProcesses.put(process.getProcessID(), process);
        }
        List<MonitoredProcess> result = properties.getProcesses().stream()
                .map(definition -> find(definition, processes))
                .toList();
        previousProcesses.clear();
        previousProcesses.putAll(currentProcesses);
        return result;
    }

    private MonitoredProcess find(ProcessDefinition definition, List<OSProcess> processes) {

        return processes.stream()
                .filter(process -> matches(process, definition.getCommandPattern()))
                .findFirst()
                .map(process -> toMonitoredProcess(definition, process))
                .orElseGet(() -> new MonitoredProcess(definition.getName(), false, null, 0, 0, 0, null));
    }

    private MonitoredProcess toMonitoredProcess(ProcessDefinition definition, OSProcess process) {
        OSProcess previousProcess = previousProcesses.get(process.getProcessID());
        double cpuPercent = 0.0;
        if (previousProcess != null) {
            cpuPercent = process.getProcessCpuLoadBetweenTicks(previousProcess) * 100.0;
        }
        return new MonitoredProcess(definition.getName(), true, (long) process.getProcessID(), cpuPercent, process.getResidentSetSize(), process.getThreadCount(), process.getCommandLine());
    }

    private boolean matches(OSProcess process, String pattern) {
        return contains(process.getCommandLine(), pattern) || contains(process.getName(), pattern) || contains(process.getPath(), pattern);
    }

    private boolean contains(String value, String pattern) {
        return value != null && value.contains(pattern);
    }
}
