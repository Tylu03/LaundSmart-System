package com.c1se22.publiclaundsmartsystem.controller;

import com.c1se22.publiclaundsmartsystem.payload.request.LoginDto;
import com.c1se22.publiclaundsmartsystem.payload.request.RegisterDto;
import com.c1se22.publiclaundsmartsystem.payload.response.JwtResponse;
import com.c1se22.publiclaundsmartsystem.payload.response.LoginResponse;
import com.c1se22.publiclaundsmartsystem.payload.response.UserDto;
import com.c1se22.publiclaundsmartsystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginDto loginDto){
        LoginResponse loginResponse = authService.login(loginDto);
        return ResponseEntity.ok(JwtResponse.builder()
                .accessToken(loginResponse.getAccessToken())
                .userId(loginResponse.getUserId())
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<Boolean> register(@RequestBody @Valid RegisterDto registerDto){
        return ResponseEntity.ok(authService.register(registerDto));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(authService.me(userDetails.getUsername()));
    }
}
