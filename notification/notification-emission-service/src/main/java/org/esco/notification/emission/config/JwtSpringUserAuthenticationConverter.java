package org.esco.notification.emission.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

import java.util.Map;

public class JwtSpringUserAuthenticationConverter extends DefaultUserAuthenticationConverter {
    public Authentication extractAuthentication(Map<String, ?> map) {
        Authentication authentication = super.extractAuthentication(map);
        if (authentication instanceof AbstractAuthenticationToken) {
            ((AbstractAuthenticationToken) authentication).setDetails(map);
        }
        return authentication;
    }
}
