package org.esco.notification.emission.component;

import org.springframework.security.oauth2.provider.OAuth2Authentication;

public interface UserDetailsAccessor {
    String getUserUuid(OAuth2Authentication authentication);
}
