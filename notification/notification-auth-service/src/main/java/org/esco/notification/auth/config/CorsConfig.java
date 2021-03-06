package org.esco.notification.auth.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS Configuration to allow browser access for another host.
 */
@Configuration
public class CorsConfig {
    /**
     * Allowing all origins, headers and methods here is only intended to keep this example simple.
     * This is not a default recommended configuration.
     *
     * Make adjustments as necessary to your use case.
     */
    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        CorsFilter corsFilter = new CorsFilter(source);

        // Should be called before @EnableAuthorizationServer configuration
        FilterRegistrationBean bean = new FilterRegistrationBean(corsFilter);
        bean.setOrder(-100);
        return bean;
    }
}
