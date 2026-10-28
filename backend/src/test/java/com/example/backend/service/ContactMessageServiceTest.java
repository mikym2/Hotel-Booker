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
    void createValidMessageSavesIt() {
        //return the first argument passed to the mocked method
        when(repository.save(any(ContactMessage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ContactMessage result =
                service.create("Alice", "Hello! ");

        assertEquals("Alice", result.getName());
        assertEquals("Hello!", result.getMessage());

        //verifies that your service called repository.save() with a ContactMessage whose name is "Alice"
        // and whose message is "Hello!".
        //argThat() lets you define a condition that an argument must satisfy
        verify(repository).save(argThat(contact ->
                contact.getName().equals("Alice")
                        && contact.getMessage().equals("Hello!")
        ));
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
    void createNullNameThrowsException(){
        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(null,"Hello!")
        );

        verifyNoInteractions(repository);
    }

    @Test
    void createBlankMessageThrowsException(){
        assertThrows(IllegalArgumentException.class,
                () -> service.create("Alice", "")
        );

        verifyNoInteractions(repository);
    }

    @Test
    void createNullMessageThrowsException(){
        assertThrows(IllegalArgumentException.class,
                () -> service.create("Alice", null)
        );
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
