package com.alan.clasetest.service;


import com.alan.clasetest.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> findAll();

    UserDto findById(Long id);

    UserDto create(UserDto userDto);

    UserDto update(Long id, UserDto userDto);

    void delete(Long id);
}