package org.esco.notification.auth.repository;

import org.esco.notification.auth.domain.Role;
import org.esco.notification.auth.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByKey(String key);
}