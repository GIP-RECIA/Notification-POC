package org.esco.notification.emission.component;

import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * Get user UUID from OAuth 2 Authentication object.
 */
public interface UserDetailsAccessor {
    String getUserUuid(OAuth2Authentication authentication);
}
