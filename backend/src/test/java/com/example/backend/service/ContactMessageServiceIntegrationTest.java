package com.example.backend.service;

import com.example.backend.JPA.entities.ContactMessage;
import com.example.backend.JPA.repo.ContactMessageRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @SpringBootTest starts the Spring application context and injects the real service and repository.
 * @ActiveProfiles("test") tells Spring to load your test profile.
 * @Transactional rolls back each test's database transaction afterward, preventing test records from remaining in the database.
 * */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ContactMessageServiceIntegrationTest {

    @Autowired
    private ContactMessageService service;

    @Autowired
    private ContactMessageRepository repository;

    @Test
    void createValidMessageAndPersist(){
        ContactMessage contactMessage =
                service.create("Alice","Integration test!");

        assertNotNull(contactMessage.getId());

        ContactMessage saved = repository.findById(contactMessage.getId())
                .orElseThrow();

        assertEquals("Alice", saved.getName());
        assertEquals("Integration test!",saved.getMessage());
    }

    @Test
    void findAllSavedMessages(){
        service.create("Alice","First");
        service.create("Bob","Second");

        var messages = service.findAll();

        assertEquals(2,messages.size());
        assertTrue(messages.stream()
                .anyMatch(m -> m.getName().equals("Alice")));
        assertTrue(messages.stream()
                .anyMatch(m -> m.getName().equals("Bob")));
    }

    @Test
    void createBlankNameWithNoSave(){
        assertThrows(IllegalArgumentException.class,
                () ->service.create(" ", "Hello!"));

        assertEquals(0, repository.count());
    }
}
