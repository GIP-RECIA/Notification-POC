package fr.recia.notifications.delayer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class DelayerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DelayerApplication.class, args);
    }

}