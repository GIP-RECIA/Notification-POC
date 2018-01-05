package org.esco.notification.emission.handler.web;

import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * Allow registration/unregistration of user informations,
 * and get authentication objects from user UUID or name.
 */
public interface WebMediaUserService {
    void registerUser(OAuth2Authentication authentication);

    void unregisterUser(OAuth2Authentication authentication);

    OAuth2Authentication getUserByUuid(String userUuid);

    OAuth2Authentication getUserByName(String name);
}
