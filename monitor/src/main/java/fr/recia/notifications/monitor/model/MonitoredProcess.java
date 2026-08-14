package fr.recia.notifications.monitor.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonitoredProcess{
    private String serviceName;
    private boolean up;
    private Long pid;
    private double cpuPercent;
    private long memoryBytes;
    private long threads;
    private String commandLine;
}