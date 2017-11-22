package org.esco.notification.auth.repository;

import org.esco.notification.auth.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUuid(String uuid);

    User findByUsername(String username);
}