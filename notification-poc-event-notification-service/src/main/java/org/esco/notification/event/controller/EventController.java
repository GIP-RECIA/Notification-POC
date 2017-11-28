package org.esco.notification.event.controller;

import org.esco.notification.data.Event;
import org.esco.notification.event.exception.EventEmissionException;
import org.esco.notification.event.exception.EventValidationException;
import org.esco.notification.event.service.EventEmitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventEmitterService eventEmitterService;

    @RequestMapping(path = "/emit", method = RequestMethod.POST)
    public void emit(@RequestBody Event event) throws EventValidationException, EventEmissionException {
        eventEmitterService.validate(event);
        eventEmitterService.emit(event);
    }
}
