package com.alan.crud_backend.service;

import com.alan.crud_backend.entity.User;
import com.alan.crud_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    @Test
    void shouldReturnAllUsers() {

        User user1 = new User(1L, "Alan", "alan@email.com");
        User user2 = new User(2L, "John", "john@email.com");

        when(repository.findAll())
                .thenReturn(List.of(user1, user2));

        List<User> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("Alan", result.get(0).getName());

        verify(repository).findAll();
    }

    @Test
    void shouldReturnUserById() {

        User user = new User(
                1L,
                "Alan",
                "alan@email.com"
        );

        when(repository.findById(1L))
                .thenReturn(Optional.of(user));

        User result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Alan", result.getName());

        verify(repository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.findById(99L)
        );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(repository).findById(99L);
    }

    @Test
    void shouldCreateUser() {

        User user = new User(
                null,
                "Alan",
                "alan@email.com"
        );

        User savedUser = new User(
                1L,
                "Alan",
                "alan@email.com"
        );

        when(repository.save(user))
                .thenReturn(savedUser);

        User result = service.create(user);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(repository).save(user);
    }

    @Test
    void shouldUpdateUser() {

        User existing = new User(
                1L,
                "Alan",
                "alan@email.com"
        );

        User updatedData = new User(
                null,
                "Alan Updated",
                "alan.updated@email.com"
        );

        User savedUser = new User(
                1L,
                "Alan Updated",
                "alan.updated@email.com"
        );

        when(repository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(repository.save(any(User.class)))
                .thenReturn(savedUser);

        User result = service.update(
                1L,
                updatedData
        );

        assertEquals(
                "Alan Updated",
                result.getName()
        );

        assertEquals(
                "alan.updated@email.com",
                result.getEmail()
        );

        verify(repository).findById(1L);
        verify(repository).save(existing);
    }

    @Test
    void shouldDeleteUser() {

        doNothing()
                .when(repository)
                .deleteById(1L);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }
}