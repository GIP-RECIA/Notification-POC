package org.esco.notification.emission.handler.web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebMediaController {
    @MessageMapping("/notifications")
    public String notifications() {
        return "notifications: " + "test";
    }
}
