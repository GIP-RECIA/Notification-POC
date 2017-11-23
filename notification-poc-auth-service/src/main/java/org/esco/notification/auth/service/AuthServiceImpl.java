package org.esco.notification.auth.service;

import org.esco.notification.auth.domain.Group;
import org.esco.notification.auth.domain.Role;
import org.esco.notification.auth.domain.User;
import org.esco.notification.auth.repository.GroupRepository;
import org.esco.notification.auth.repository.RoleRepository;
import org.esco.notification.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public User findUserById(Long userId) {
        return userRepository.findOne(userId);
    }

    @Override
    public User saveUser(User user) {
        if (user.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setEncodedPassword(encodedPassword);
            user.setPassword(null);
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.delete(userId);
    }

    @Override
    public Role findRoleByKey(String roleKey) {
        return roleRepository.findByKey(roleKey);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Group findGroupByName(String name) {
        return groupRepository.findByName(name);
    }

    @Override
    public Group saveGroup(Group group) {
        return groupRepository.save(group);
    }
}
