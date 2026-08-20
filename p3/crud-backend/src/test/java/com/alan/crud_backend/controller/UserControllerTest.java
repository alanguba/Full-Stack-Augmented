package com.alan.crud_backend.controller;

import com.alan.crud_backend.entity.User;
import com.alan.crud_backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
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

        User user1 = new User(1L, "Alan", "alan@email.com");
        User user2 = new User(2L, "John", "john@email.com");

        Mockito.when(userService.findAll())
                .thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Alan"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("John"));
    }

    @Test
    void shouldReturnUserById() throws Exception {

        User user = new User(1L, "Alan", "alan@email.com");

        Mockito.when(userService.findById(1L))
                .thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alan"))
                .andExpect(jsonPath("$.email").value("alan@email.com"));
    }

    @Test
    void shouldCreateUser() throws Exception {

        User request = new User(null, "Alan", "alan@email.com");
        User response = new User(1L, "Alan", "alan@email.com");

        Mockito.when(userService.create(any(User.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alan"))
                .andExpect(jsonPath("$.email").value("alan@email.com"));

        verify(userService).create(any(User.class));
    }

    @Test
    void shouldUpdateUser() throws Exception {

        User request = new User(null, "Alan Updated", "alan@email.com");
        User response = new User(1L, "Alan Updated", "alan@email.com");

        Mockito.when(userService.update(eq(1L), any(User.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alan Updated"));

        verify(userService).update(eq(1L), any(User.class));
    }

    @Test
    void shouldDeleteUser() throws Exception {

        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk());

        verify(userService).delete(1L);
    }
}