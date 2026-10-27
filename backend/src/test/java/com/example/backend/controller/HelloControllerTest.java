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
                        .value("Message saved successfully! ID: null"));

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

    @Test
    void submitContactReturnsBadRequestWhenServiceRejectsInput()
            throws Exception {

        when(service.create("Alice", ""))
                .thenThrow(new IllegalArgumentException(
                        "Message is required."));

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
    }

}
