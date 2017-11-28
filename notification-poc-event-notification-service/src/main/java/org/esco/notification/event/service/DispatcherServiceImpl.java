package org.esco.notification.event.service;

import org.esco.notification.data.Event;
import org.esco.notification.data.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DispatcherServiceImpl implements DispatcherService {
    @Autowired
    private EventObjectConverter converter;

    @Autowired
    private RoutingService routingService;

    @Override
    public List<Notification> dispatchEvent(Event event) {
        return routingService.getMatchingUsers(event).stream()
                .map(uuid -> converter.toUserEvent(event, uuid))
                .flatMap(userEvent -> routingService.getMatchingMedias(userEvent)
                        .stream()
                        .map(media -> converter.toNotification(userEvent, media)
                        ))
                .collect(Collectors.toList());
    }
}
