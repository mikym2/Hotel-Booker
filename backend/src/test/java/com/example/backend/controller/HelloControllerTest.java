package com.example.backend.controller;


import com.example.backend.JPA.entities.ContactMessage;
import com.example.backend.service.ContactMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HelloController.class)
@Import(HelloController.ApiExceptionHandler.class)
public class HelloControllerTest {

    //MockMvc: A Spring class that allows you to simulate HTTP requests and inspect HTTP responses
    //without starting a real HTTP server.
    @Autowired
    private MockMvc mockMvc;

    //@MockitoBean creates a Mockito mock and registers it in the Spring application context,
    // replacing an existing bean of the matching type.
    @MockitoBean
    private ContactMessageService service;

    @Test
    void getHelloReturnsGreeting() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Hello from Spring Boot!"));
    }

    @Test
    void submitContactReturnsSuccessMessage() throws Exception {
        ContactMessage saved =
                new ContactMessage("Alice", "Hello!");

        when(service.create("Alice", "Hello!"))
                .thenReturn(saved);

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Alice",
                                    "message": "Hello!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Message received successfully Alice!"));

        verify(service).create("Alice", "Hello!");
    }

    @Test
    void getContactsReturnsListOfMessages() throws Exception {
        when(service.findAll()).thenReturn(List.of(
                new ContactMessage("Alice", "Hello!"),
                new ContactMessage("Bob", "Hi!")
        ));

        mockMvc.perform(get("/api/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[0].message").value("Hello!"))
                .andExpect(jsonPath("$[1].name").value("Bob"));

        verify(service).findAll();
    }

    //we do not have a when clause here because it'll fail when it does the @valid aka even before it enters the logic
    @Test
    void serviceContactWithBlankMessage()
            throws Exception {

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Alice",
                                    "message": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Message is required."));

        verifyNoInteractions(service);
    }

    @Test
    void serviceContactWithBlankName() throws Exception{
        when(service.create("", "This is Alice"))
                .thenThrow(new IllegalArgumentException("Name is required."));

        mockMvc.perform(post("/api/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "",
                            "message": "This is Alice"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Name is required."));

        verifyNoInteractions(service);

    }

    @Test
    void serviceContactWithLongName() throws Exception{
        String longName = "A".repeat(101);

        mockMvc.perform(post("/api/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name":"%s",
                            "message": "This is a long name"
                        }
                        """.formatted(longName)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Name cannot exceed 100 characters."));


        verifyNoInteractions(service);
    }
}
