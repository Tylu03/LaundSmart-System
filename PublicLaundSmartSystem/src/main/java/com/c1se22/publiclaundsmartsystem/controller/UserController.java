package com.c1se22.publiclaundsmartsystem.controller;

import com.c1se22.publiclaundsmartsystem.payload.response.UserDto;
import com.c1se22.publiclaundsmartsystem.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public  ResponseEntity<List<UserDto>> getUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public  ResponseEntity<UserDto> getUserById(@PathVariable Integer id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

//    @PostMapping
//    public ResponseEntity <UserDto> addUser(@RequestBody UserDto userDto){
//        return ResponseEntity.ok(userService.addUser(userDto));
//    }

    @PutMapping("/{id}")
    public ResponseEntity <UserDto> updateUser(@PathVariable Integer id, @RequestBody UserDto userDto){
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @DeleteMapping ("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity <String> deleteUser(@PathVariable Integer id){
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
