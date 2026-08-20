package com.alan.crud_backend.controller;


import com.alan.crud_backend.entity.User;
import com.alan.crud_backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public User findById(
            @PathVariable Long id) {

        return service.findById(id);
    }

    @PostMapping
    public User create(
            @RequestBody User user) {

        return service.create(user);
    }

    @PutMapping("/{id}")
    public User update(
            @PathVariable Long id,
            @RequestBody User user) {

        return service.update(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id) {

        service.delete(id);
    }
}