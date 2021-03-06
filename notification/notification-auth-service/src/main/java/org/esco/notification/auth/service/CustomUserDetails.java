package org.esco.notification.auth.service;

import org.esco.notification.auth.domain.Group;
import org.esco.notification.auth.domain.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private final Set<String> groupUuids;
    private final String userUuid;
    private final String userFirstName;
    private final String userLastName;

    public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUsername(), user.getEncodedPassword(), authorities);
        this.groupUuids = user.getGroups().stream().map(Group::getUuid).distinct().collect(Collectors.toSet());
        this.userUuid = user.getUuid();
        this.userFirstName = user.getFirstName();
        this.userLastName = user.getLastName();
    }

    public CustomUserDetails(User user, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUsername(), user.getEncodedPassword(), enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.groupUuids = user.getGroups().stream().map(Group::getUuid).distinct().collect(Collectors.toSet());
        this.userUuid = user.getUuid();
        this.userFirstName = user.getFirstName();
        this.userLastName = user.getLastName();
    }

    public Set<String> getGroupUuids() {
        return groupUuids;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }
}
