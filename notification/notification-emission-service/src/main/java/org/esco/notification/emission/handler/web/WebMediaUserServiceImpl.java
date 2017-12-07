package org.esco.notification.emission.handler.web;

import org.esco.notification.emission.component.UserDetailsAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebMediaUserServiceImpl implements WebMediaUserService {
    @Autowired
    private UserDetailsAccessor userDetailsAccessor;

    private Map<String, OAuth2Authentication> usersByUuid = new HashMap<>();
    private Map<String, OAuth2Authentication> usersByName = new HashMap<>();


    @Override
    public void registerUser(OAuth2Authentication authentication) {
        String userUuid = userDetailsAccessor.getUserUuid(authentication);
        usersByUuid.put(userUuid, authentication);
        usersByName.put(authentication.getName(), authentication);
    }

    @Override
    public void unregisterUser(OAuth2Authentication authentication) {
        String userUuid = userDetailsAccessor.getUserUuid(authentication);
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
