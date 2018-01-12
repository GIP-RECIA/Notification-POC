package org.esco.notification.emission.service;

import org.esco.notification.data.Notification;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

/**
 * Search for existing {@link Notification} in ElasticSearch data store.
 */
public interface ElasticService {
    List<Notification> findNotifications(Principal principal, String... medias) throws IOException;
}
