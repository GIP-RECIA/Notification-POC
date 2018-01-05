package org.esco.notification.auth.service;

import org.esco.notification.auth.domain.Role;
import org.esco.notification.auth.domain.User;
import org.esco.notification.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Load principal details (authorities, user object ...) from it's username.
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("The username %s doesn't exist", username));
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        Set<Role> roles = user.getGroups().stream().flatMap((group -> group.getRoles().stream())).distinct().collect(Collectors.toSet());

        roles.forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getKey()));
        });

        UserDetails userDetails = new CustomUserDetails(user, authorities);

        return userDetails;
    }
}
