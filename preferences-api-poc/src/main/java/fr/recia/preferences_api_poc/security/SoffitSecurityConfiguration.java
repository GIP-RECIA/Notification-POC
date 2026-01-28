package fr.recia.preferences_api_poc.security;

import fr.recia.soffit_java_client.SoffitJwtAuthenticationFilter;
import fr.recia.soffit_java_client.SoffitJwtValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SoffitSecurityConfiguration {

    private final SoffitJwtProperties jwtProperties;

    public SoffitSecurityConfiguration(SoffitJwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Bean
    SoffitJwtValidator soffitJwtValidator() {
        return new SoffitJwtValidator(jwtProperties.getSignatureKey());
    }

    @Bean
    SoffitJwtAuthenticationFilter soffitJwtAuthenticationFilter(SoffitJwtValidator validator) {
        return new SoffitJwtAuthenticationFilter(validator);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, SoffitJwtAuthenticationFilter filter) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health-check/**", "/ui/prefs/**", "/js/*", "/css/*").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

