package com.example.backend.service;


import com.example.backend.JPA.entities.ContactMessage;
import com.example.backend.JPA.repo.ContactMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContactMessageServiceTest {

    @Mock
    private ContactMessageRepository repository;

    private ContactMessageService service;

    //can also use @InjectMocks to create the service and inject the mock repository into it
    // @InjectMocks
    // private ContactMessageService service;
    @BeforeEach
    void setUp(){
        service = new ContactMessageService(repository);
    }

    @Test
    void createValidMessageSavesIt(){
        ContactMessage contact =
                new ContactMessage("Alice","Hello!");

        when(repository.save(any(ContactMessage.class))).thenReturn(contact);

        ContactMessage result =
                service.create("Alice", "Hello! ");

        assertEquals("Alice", result.getName());
        assertEquals("Hello!", result.getMessage());
    }

    @Test
    void createBlankNameThrowsException(){
        assertThrows(
                IllegalArgumentException.class,
                () -> service.create("   ", "Hello!")
        );
        verifyNoInteractions(repository);
    }

    @Test
    void findAllReturnsRepositoryResults(){
        List<ContactMessage> contacts = List.of(
                new ContactMessage("Alice","Hello!"),
                new ContactMessage("Bob","Hi!")
        );

        when(repository.findAll()).thenReturn(contacts);

        List<ContactMessage> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getName());
        assertEquals("Bob", result.get(1).getName());

        verify(repository).findAll();
    }


}
