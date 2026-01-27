package org.esco.notification.performance;

import org.esco.notification.performance.component.PerformanceStompSessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@SpringBootApplication
@EnableAutoConfiguration
public class PerformanceTestApplication implements CommandLineRunner {
    private Logger log = LoggerFactory.getLogger(PerformanceTestApplication.class);

    @Autowired
    private WebSocketStompClient stompClient;

    @Autowired
    private PerformanceStompSessionHandler sessionHandler;

    @Value("${performance.connections}")
    private int connections;

    @Value("${performance.url}")
    private String url;

    public static void main(String[] args) {
        SpringApplication.run(PerformanceTestApplication.class, args);
    }

    @Override
    public void run(String... args) {
        for (int i = 0; i < connections; i++) {
            stompClient.connect(url, sessionHandler);
        }

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
        }
    }
}