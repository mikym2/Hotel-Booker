package com.example.backend.service;

import com.example.backend.JPA.Entities.ContactMessage;
import com.example.backend.JPA.Entities.ContactMessageRepository;
import org.springframework.boot.data.metrics.DefaultRepositoryTagsProvider;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * create() validates the input and saves the message.
 * findAll() retrieves the saved messages.
 * @Service tells Spring to register this class as a bean that can be injected into other classes.
 * */
@Service
public class ContactMessageService {

    private final ContactMessageRepository repository;

    public ContactMessageService(ContactMessageRepository repository){
        this.repository = repository;
    }

    public ContactMessage create(String name, String message){
        if(name == null ||  name.isBlank()){
            throw  new IllegalArgumentException("Name is required.");
        }

        if(message == null || message.isBlank()){
            throw new IllegalArgumentException("Message is required.");
        }

        ContactMessage contactMessage = new ContactMessage(name.trim(),message.trim());

        return repository.save(contactMessage);
    }

    public List<ContactMessage> findAll(){
        return repository.findAll();
    }
}
