package org.esco.notification.emission.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore;

import java.lang.reflect.Field;

/**
 * CAS Resource Server configuration
 */
@Configuration
@EnableResourceServer
@Profile("jwk-cas")
public class JwkCasResourceServerConfig implements ResourceServerConfigurer {
    @Autowired
    private ResourceServerProperties resourceServiceProperties;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("jwt");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/ping").permitAll()
                .antMatchers("/stomp").permitAll()
                .anyRequest().authenticated();
    }

    public UserAuthenticationConverter userAuthenticationConverter() {
        return new JwkCasUserAuthenticationConverter();
    }

    public AccessTokenConverter accessTokenConverter() {
        DefaultAccessTokenConverter defaultAccessTokenConverter = new DefaultAccessTokenConverter();
        defaultAccessTokenConverter.setUserTokenConverter(userAuthenticationConverter());
        return defaultAccessTokenConverter;
    }

    @Bean
    public TokenStore tokenStore() throws IllegalAccessException, NoSuchFieldException {
        JwkTokenStore tokenStore = new JwkTokenStore(resourceServiceProperties.getJwk().getKeySetUri());

        // Access internal field
        // See https://github.com/spring-projects/spring-security-oauth/issues/1015
        // This workaround should be removed with Spring Boot>=1.5.10
        Field f = tokenStore.getClass().getDeclaredField("delegate"); //NoSuchFieldException
        f.setAccessible(true);
        JwtTokenStore delegate = (JwtTokenStore) f.get(tokenStore); //IllegalAccessException

        f = delegate.getClass().getDeclaredField("jwtTokenEnhancer");
        f.setAccessible(true);

        JwtAccessTokenConverter delegatingAccessTokenConverter = (JwtAccessTokenConverter) f.get(delegate);
        delegatingAccessTokenConverter.setAccessTokenConverter(accessTokenConverter());

        return tokenStore;
    }
}
