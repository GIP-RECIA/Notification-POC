package org.esco.notification.auth.service;

import org.esco.notification.auth.domain.Group;
import org.esco.notification.auth.domain.Role;
import org.esco.notification.auth.domain.User;

import java.util.List;

public interface AuthService {
    User findUserByUsername(String username);

    User findUserById(Long userId);

    List<User> findAllUsers();

    User saveUser(User user);

    void deleteUserById(Long userId);

    Role findRoleByKey(String roleKey);

    Role saveRole(Role role);

    Group findGroupByName(String name);

    Group saveGroup(Group group);
}
