package com.example.backend.JPA.Entities;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * By extending JpaRepository, Spring Data JPA provides methods such as:
 * - save(message) — insert or update a message.
 * - findAll() — retrieve all messages.
 * - findById(id) — retrieve a message by ID.
 * - deleteById(id) — delete a message by ID.
 * Spring generates the repository implementation at runtime.
 *
 * */
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
}
