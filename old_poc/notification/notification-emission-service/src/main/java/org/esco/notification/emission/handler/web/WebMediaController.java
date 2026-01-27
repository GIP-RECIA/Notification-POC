package org.esco.notification.emission.handler.web;

import org.esco.notification.data.EventContent;
import org.esco.notification.data.Notification;
import org.esco.notification.emission.service.ElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/web")
public class WebMediaController {
    @Autowired
    private ElasticService elasticService;

    @RequestMapping(value = "/notifications", method = RequestMethod.GET)
    public List<EventContent> getNotifications(Principal principal) throws IOException {
        List<Notification> results = elasticService.findNotifications(principal, "web");
        return results.stream().map(Notification::getContent).collect(Collectors.toList());
    }
}
