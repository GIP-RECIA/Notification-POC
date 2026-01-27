package org.esco.notification.performance.component;

import org.esco.notification.data.Notification;
import org.esco.notification.performance.PerformanceTestApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class PerformanceStompFrameHandler implements StompFrameHandler {
    private Logger log = LoggerFactory.getLogger(PerformanceTestApplication.class);

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Notification.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        log.info(String.valueOf(payload));
    }
}
