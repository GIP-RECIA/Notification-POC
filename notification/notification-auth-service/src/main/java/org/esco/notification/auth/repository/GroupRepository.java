package org.esco.notification.auth.repository;

import org.esco.notification.auth.domain.Group;
import org.esco.notification.auth.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface GroupRepository extends CrudRepository<Group, Long> {
    Group findByUuid(String uuid);

    Group findByName(String name);
}
