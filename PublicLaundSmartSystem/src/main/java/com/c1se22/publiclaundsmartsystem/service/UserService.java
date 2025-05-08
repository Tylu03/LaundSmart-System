package com.c1se22.publiclaundsmartsystem.service;

import com.c1se22.publiclaundsmartsystem.payload.response.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserById(Integer id);
//    UserDto addUser(UserDto userDto);
    UserDto updateUser(Integer id, UserDto userDto);
    void deleteUser(Integer id);

}
