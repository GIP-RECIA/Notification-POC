package fr.recia.notifications.consumer_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, DataMongoAutoConfiguration.class})
public class ConsumerWebKafkaApplication {
	public static void main(String[] args) {
		SpringApplication.run(ConsumerWebKafkaApplication.class, args);
	}

}
