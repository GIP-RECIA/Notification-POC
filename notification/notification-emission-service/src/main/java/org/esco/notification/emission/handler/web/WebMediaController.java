package org.esco.notification.emission.handler.web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebMediaController {
    @MessageMapping("/user/queue/notification")
    public void userNotification(Principal principal) {
        // May be implemented to return notification history
    }
}
