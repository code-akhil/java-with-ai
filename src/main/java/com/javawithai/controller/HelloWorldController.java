package com.javawithai.controller;

import com.javawithai.model.Event;
import com.javawithai.repository.EventRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    private final EventRepository eventRepository;

    public HelloWorldController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping("/hello_world")
    public String helloWorld() {
        return "Hello, World!";
    }

    @GetMapping("/hello_world/{eventId}")
    public ResponseEntity<Event> getEventById(@PathVariable("eventId") Long eventId) {
        return eventRepository.findById(eventId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
