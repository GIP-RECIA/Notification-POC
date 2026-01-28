package fr.recia.preferences_api_poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "fr.recia")
public class PreferencesApiPocApplication {
	public static void main(String[] args) {
		SpringApplication.run(PreferencesApiPocApplication.class, args);
	}
}
