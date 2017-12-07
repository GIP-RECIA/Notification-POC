package org.esco.notification.emission.component;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserDetailsAccessor {
    public String getUserUuid(OAuth2Authentication authentication) {
        AbstractAuthenticationToken userAuthentication = (AbstractAuthenticationToken) authentication.getUserAuthentication();
        Map<String, ?> details = (Map<String, ?>) userAuthentication.getDetails();
        String userUuid = (String) details.get("user_uuid");
        return userUuid;
    }
}
