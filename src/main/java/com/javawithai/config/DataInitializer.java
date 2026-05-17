package com.javawithai.config;

import com.javawithai.model.Event;
import com.javawithai.repository.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EventRepository repository;

    public DataInitializer(EventRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            repository.save(new Event(1L, "Demo Event", LocalDateTime.of(2026, 5, 16, 10, 0)));
            repository.save(new Event(2L, "Second Event", LocalDateTime.of(2026, 5, 17, 12, 30)));
        }
    }
}
