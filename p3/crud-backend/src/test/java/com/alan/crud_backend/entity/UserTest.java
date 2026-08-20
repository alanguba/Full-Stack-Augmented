package com.alan.crud_backend.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserUsingDefaultConstructor() {

        User user = new User();

        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
    }

    @Test
    void shouldCreateUserUsingAllArgsConstructor() {

        User user = new User(
                1L,
                "Alan",
                "alan@email.com"
        );

        assertEquals(1L, user.getId());
        assertEquals("Alan", user.getName());
        assertEquals("alan@email.com", user.getEmail());
    }

    @Test
    void shouldSetAndGetId() {

        User user = new User();

        user.setId(1L);

        assertEquals(1L, user.getId());
    }

    @Test
    void shouldSetAndGetName() {

        User user = new User();

        user.setName("Alan");

        assertEquals("Alan", user.getName());
    }

    @Test
    void shouldSetAndGetEmail() {

        User user = new User();

        user.setEmail("alan@email.com");

        assertEquals("alan@email.com", user.getEmail());
    }

    @Test
    void shouldSetAndGetAllFields() {

        User user = new User();

        user.setId(1L);
        user.setName("Alan");
        user.setEmail("alan@email.com");

        assertAll(
                () -> assertEquals(1L, user.getId()),
                () -> assertEquals("Alan", user.getName()),
                () -> assertEquals("alan@email.com", user.getEmail())
        );
    }
}