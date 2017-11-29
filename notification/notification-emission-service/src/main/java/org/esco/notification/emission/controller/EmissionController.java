package org.esco.notification.emission.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/")
public class EmissionController {
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String ping() {
        return "ping";
    }

    @RequestMapping(value = "/pong", method = RequestMethod.GET)
    public String pong(Principal principal) {
        return "pong";
    }
}
