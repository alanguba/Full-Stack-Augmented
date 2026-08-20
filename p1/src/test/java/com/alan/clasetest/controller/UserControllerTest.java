package com.alan.clasetest.controller;

/**
 * Generate a complete JUnit 5 test class for UserController.
 *
 *         Use:
 *         - Spring Boot @WebMvcTest
 * - MockMvc
 * - Mockito
 * - Jackson ObjectMapper
 *
 * The controller depends on UserService.
 *
 *         Test:
 *         1. getAllUsers returns 200 and list of users
 * 2. getUserById returns 200 when user exists
 * 3. getUserById returns 404 when user does not exist
 * 4. createUser returns 201
 *         5. updateUser returns 200
 *         6. deleteUser returns 204
 *
 * Generate all imports and setup code.
 */

import com.alan.clasetest.dto.UserDto;
import com.alan.clasetest.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldReturnAllUsers() throws Exception {

        // Arrange
        List<UserDto> users = List.of(
                new UserDto(1L, "Alan", "Gutierrez", "alan@example.com"),
                new UserDto(2L, "John", "Doe", "john@example.com")
        );

        BDDMockito.given(userService.findAll())
                .willReturn(users);

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Alan"))
                .andExpect(jsonPath("$[0].email").value("alan@example.com"));
    }

    @Test
    void shouldReturnUserById() throws Exception {

        // Arrange
        UserDto user =
                new UserDto(1L, "Alan", "Gutierrez", "alan@example.com");

        BDDMockito.given(userService.findById(1L))
                .willReturn(user);

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Alan"))
                .andExpect(jsonPath("$.lastName").value("Gutierrez"))
                .andExpect(jsonPath("$.email").value("alan@example.com"));
    }

    @Test
    void shouldCreateUser() throws Exception {

        // Arrange
        UserDto request =
                new UserDto(null, "Alan", "Gutierrez", "alan@example.com");

        UserDto response =
                new UserDto(1L, "Alan", "Gutierrez", "alan@example.com");

        BDDMockito.given(userService.create(any(UserDto.class)))
                .willReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Alan"));
    }

    @Test
    void shouldUpdateUser() throws Exception {

        // Arrange
        UserDto request =
                new UserDto(1L, "Alan Updated", "Gutierrez", "alan@example.com");

        BDDMockito.given(userService.update(eq(1L), any(UserDto.class)))
                .willReturn(request);

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Alan Updated"));
    }

    @Test
    void shouldDeleteUser() throws Exception {

        // Arrange
        doNothing().when(userService).delete(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}
