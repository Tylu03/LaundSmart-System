package com.c1se22.publiclaundsmartsystem.service;

import com.c1se22.publiclaundsmartsystem.payload.request.LoginDto;
import com.c1se22.publiclaundsmartsystem.payload.response.LoginResponse;
import com.c1se22.publiclaundsmartsystem.payload.request.RegisterDto;
import com.c1se22.publiclaundsmartsystem.payload.response.UserDto;

public interface AuthService {
    LoginResponse login(LoginDto loginDto);
    boolean register(RegisterDto registerDto);
    UserDto me(String username);
//    boolean forgotPassword(String email);
//    boolean changePassword(String username, String password);
}
