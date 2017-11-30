package org.esco.notification.emission.handler.web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebMediaController {
    @MessageMapping("/notifications")
    public String notifications(Principal principal) {
        return "notifications: " + "test";
    }
}
