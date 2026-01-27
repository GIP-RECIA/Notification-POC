package org.esco.notification.auth.config;

import org.esco.notification.auth.service.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

import java.util.Map;

/**
 * Authentication converter to use for Spring Authorization Server.
 */
public class CustomUserAuthenticationConverter extends DefaultUserAuthenticationConverter {
    /**
     * Extract information about the user to be used in an access token (i.e. for resource servers).
     *
     * @param userAuthentication an authentication representing a user
     * @return a map of key values representing the unique information about the user
     */
    public Map<String, ?> convertUserAuthentication(Authentication userAuthentication) {
        Map<String, Object> data = (Map<String, Object>) super.convertUserAuthentication(userAuthentication);

        CustomUserDetails userDetails = (CustomUserDetails) userAuthentication.getPrincipal();
        if (userDetails != null) {
            if (userDetails.getGroupUuids() != null && userDetails.getGroupUuids().size() > 0) {
                data.put("user_group_uuids", userDetails.getGroupUuids());
            }
            if (userDetails.getUserUuid() != null) {
                data.put("user_uuid", userDetails.getUserUuid());
            }
            if (userDetails.getUserFirstName() != null) {
                data.put("user_first_name", userDetails.getUserFirstName());
            }
            if (userDetails.getUserLastName() != null) {
                data.put("user_last_name", userDetails.getUserLastName());
            }
        }

        return data;
    }
}
