package org.esco.notification.performance.component;

import org.esco.notification.performance.PerformanceTestApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

@Component
public class PerformanceStompSessionHandler extends StompSessionHandlerAdapter {
    private Logger log = LoggerFactory.getLogger(PerformanceTestApplication.class);

    @Autowired
    private PerformanceStompFrameHandler frameHandler;

    /**
     * This implementation is empty.
     */
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        log.info("afterConnected");
        session.subscribe("/notifications", frameHandler);
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        log.info("handleFrame");
    }
}
