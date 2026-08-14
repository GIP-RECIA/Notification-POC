package fr.recia.notifications.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProcessMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProcessMonitorApplication.class, args);
    }
}
