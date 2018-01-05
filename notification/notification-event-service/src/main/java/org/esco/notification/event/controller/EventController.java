package org.esco.notification.event.controller;

import org.esco.notification.data.Event;
import org.esco.notification.event.exception.EventEmitException;
import org.esco.notification.event.exception.EventValidateException;
import org.esco.notification.event.service.EventEmitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API to emit events
 */
@RestController
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventEmitterService eventEmitterService;

    @RequestMapping(path = "/emit", method = RequestMethod.POST)
    public void emit(@RequestBody Event event) throws EventValidateException, EventEmitException {
        eventEmitterService.validate(event);
        eventEmitterService.emit(event);
    }
}
