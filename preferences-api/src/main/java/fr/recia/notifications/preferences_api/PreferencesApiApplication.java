package fr.recia.notifications.preferences_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "fr.recia")
public class PreferencesApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(PreferencesApiApplication.class, args);
	}
}
