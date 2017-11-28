package org.esco.notification.emission.service;

import org.esco.notification.data.Emission;
import org.esco.notification.data.Notification;
import org.esco.notification.emission.exception.NotificationPerformException;
import org.esco.notification.emission.handler.MediaHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NotificationPerformerServiceImpl implements NotificationPerformerService {
    @Autowired
    private List<MediaHandler> handlersList;

    @Autowired
    private EmissionEmitterService emissionEmitterService;

    @Autowired
    private NotificationObjectConverter notificationObjectConverter;

    private Map<String, MediaHandler> handlers = new HashMap<>();

    @PostConstruct
    private void initHandlers() {
        this.handlers = handlersList.stream().collect(Collectors.toMap(MediaHandler::getMediaKey, Function.identity()));
    }

    @Override
    public void performNotification(Notification notification) throws NotificationPerformException {
        String media = notification.getHeader().getMedia();
        if (!handlers.containsKey(media)) {
            throw new NotificationPerformException("Unsupported handler: " + media);
        }

        MediaHandler mediaHandler = handlers.get(media);
        mediaHandler.performNotification(notification);

        Emission emission = notificationObjectConverter.toEmission(notification, new Date());
        emissionEmitterService.emit(emission);
    }
}
