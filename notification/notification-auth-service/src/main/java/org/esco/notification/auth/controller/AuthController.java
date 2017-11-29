package org.esco.notification.auth.controller;

import org.esco.notification.auth.domain.User;
import org.esco.notification.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class AuthController {
    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('admin.users')")
    public List<User> getUsers() {
        return authService.findAllUsers();
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('admin.users')")
    public User createUser(User user) {
        User createdUser = authService.saveUser(user);
        return createdUser;
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasAuthority('admin.users')")
    public User updateUser(@PathVariable("id") long id, User user) {
        if (user.getId() != id) {
            throw new IllegalArgumentException("id parameter should match body content.");
        }
        User updatedUser = authService.saveUser(user);
        return updatedUser;
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority('admin.users')")
    public User deleteUser(@PathVariable("id") long id) {
        User deletedUser = authService.findUserById(id);
        authService.deleteUserById(id);
        return deletedUser;
    }
}
