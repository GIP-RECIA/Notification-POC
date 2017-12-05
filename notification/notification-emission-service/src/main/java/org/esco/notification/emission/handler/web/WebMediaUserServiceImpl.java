package org.esco.notification.emission.handler.web;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebMediaUserServiceImpl implements WebMediaUserService {
    private Map<String, OAuth2Authentication> usersByUuid = new HashMap<>();
    private Map<String, OAuth2Authentication> usersByName = new HashMap<>();


    private String getUserUuid(OAuth2Authentication authentication) {
        AbstractAuthenticationToken userAuthentication = (AbstractAuthenticationToken) authentication.getUserAuthentication();
        Map<String, ?> details = (Map<String, ?>) userAuthentication.getDetails();
        String userUuid = (String) details.get("user_uuid");
        return userUuid;
    }

    @Override
    public void registerUser(OAuth2Authentication authentication) {
        String userUuid = getUserUuid(authentication);
        usersByUuid.put(userUuid, authentication);
        usersByName.put(authentication.getName(), authentication);
    }

    @Override
    public void unregisterUser(OAuth2Authentication authentication) {
        String userUuid = getUserUuid(authentication);
        usersByUuid.remove(userUuid);
        usersByName.remove(authentication.getName());
    }

    @Override
    public OAuth2Authentication getUserByUuid(String userUuid) {
        return usersByUuid.get(userUuid);
    }

    @Override
    public OAuth2Authentication getUserByName(String name) {
        return usersByName.get(name);
    }
}
