package com.example.backend.JPA.entities;

import com.example.backend.JPA.repo.ContactMessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration test with postgress test db
 * */

// @DataJpaTest: Loads only the JPA-related components needed to test repositories and database persistence.
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE):Prevents Spring Boot from replacing
// your configured PostgreSQL database with an embedded test database.
@DataJpaTest
//tells spring to load properties file with test in the name
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
replace = AutoConfigureTestDatabase.Replace.NONE
)
class ContactMessageRespositoryIntegrationTest {

    @Autowired
    private ContactMessageRepository repository;

    @Test
    void saveAndRetrieveContactMessage(){

        //arrange
        ContactMessage contact =
                new ContactMessage("Alice", "Hello!");

        //act
        ContactMessage saved = repository.save(contact);
        repository.flush();

        ContactMessage retrieved =
                repository.findById(saved.getId()).orElseThrow();

        //assert
        assertThat(saved.getId()).isNotNull();
        assertThat(retrieved.getName()).isEqualTo("Alice");
        assertThat(retrieved.getMessage()).isEqualTo("Hello!");
    }

    @Test
    void findAllReturnsSavedMessages(){
        //repository.deleteAll();
        System.out.println("Messages before saving: " + repository.findAll().size());
        repository.save(new ContactMessage("Alice","Hello!"));
        repository.save(new ContactMessage("Bob","Hi!"));
        repository.flush();

        List<ContactMessage> result = repository.findAll();
        //should 2 here but it's 3 because of the previous test
        assertEquals(2,result.size());
    }
}

