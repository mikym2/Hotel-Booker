package com.example.backend.JPA.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

/**
 * @Entity tells JPA that this class should be stored in a database.
 * @Table specifies the table name.
 * @Id marks the primary key.
 * @GeneratedValue lets PostgreSQL generate the ID.
 * @Column(nullable = false) prevents these fields from being null in the database.
 * */

//A database entity
@Entity
@Table(name="contact_messages")
public class ContactMessage {

    @Id
    //The database generates the ID as part of inserting the row, typically using an identity column.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false,length = 2000)
    private String message;

    public ContactMessage(){

    }

    public ContactMessage(String name, String message){
        this.name = name;
        this.message = message;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }
}

