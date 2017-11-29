package org.esco.notification.event.service;

import org.esco.notification.data.Event;
import org.esco.notification.data.UserEvent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FowardingRoutingService implements RoutingService {
    @Override
    public List<String> getMatchingUsers(Event event) {
        List<String> userUuids = new ArrayList<>();

        if (event.getHeader().getUserUuids() != null) {
            userUuids.addAll(event.getHeader().getUserUuids());
        }

        return userUuids;
    }

    @Override
    public List<String> getMatchingMedias(UserEvent userEvent) {
        List<String> medias = new ArrayList<>();

        if (userEvent.getHeader().getEvent().getMedias() != null) {
            medias.addAll(userEvent.getHeader().getEvent().getMedias());
        }

        return medias;
    }
}
