package org.esco.notification.emission.service;

import org.esco.notification.data.Notification;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface ElasticService {
List<Notification> findNotifications(Principal principal, String ... medias) throws IOException;
}
