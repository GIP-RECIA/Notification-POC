package org.esco.notification.auth.startup;

import org.esco.notification.auth.domain.Group;
import org.esco.notification.auth.domain.Role;
import org.esco.notification.auth.domain.User;
import org.esco.notification.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class Startup
        implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    AuthService authService;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        Role adminUsersRole = authService.findRoleByKey("admin.users");
        if (adminUsersRole == null) {
            adminUsersRole = new Role();
            adminUsersRole.setKey("admin.users");
            adminUsersRole.setName("Administrate users");
            adminUsersRole = authService.saveRole(adminUsersRole);
        }

        Group adminGroup = authService.findGroupByName("Admins");
        if (adminGroup == null) {
            adminGroup = new Group();
            adminGroup.setUuid("738bc118-7c54-410d-8176-902488904d78");
            adminGroup.setName("Admins");
            adminGroup.setRoles(Stream.of(adminUsersRole).collect(Collectors.toSet()));
            adminGroup = authService.saveGroup(adminGroup);
        }

        User adminUser = authService.findUserByUsername("admin");
        if (adminUser == null) {
            adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword("admin");
            adminUser.setUuid("2119970e-b733-455b-be22-1ab094ca5a38");
            adminUser.setGroups(Stream.of(adminGroup).collect(Collectors.toSet()));
            adminUser = authService.saveUser(adminUser);
        }

        User simpleUser = authService.findUserByUsername("user");
        if (simpleUser == null) {
            simpleUser = new User();
            simpleUser.setUsername("user");
            simpleUser.setPassword("user");
            simpleUser.setUuid("658ae620-7847-47f7-9f66-0eae73e1ade1");
            simpleUser = authService.saveUser(simpleUser);
        }
    }

}
