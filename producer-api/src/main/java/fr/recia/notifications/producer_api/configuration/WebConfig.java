package fr.recia.notifications.producer_api.configuration;

import fr.recia.notifications.producer_api.security.ApiKeyFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<ApiKeyFilter> apiKeyFilterFilterRegistrationBean(ApiKeyFilter filter) {
        FilterRegistrationBean<ApiKeyFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(filter);
        bean.addUrlPatterns("/event/*");
        bean.setOrder(1);
        return bean;
    }
}
