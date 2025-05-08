package com.c1se22.publiclaundsmartsystem.controller;

import com.c1se22.publiclaundsmartsystem.payload.request.UserDeviceRegisterDto;
import com.c1se22.publiclaundsmartsystem.payload.response.UserDeviceResponseDto;
import com.c1se22.publiclaundsmartsystem.service.UserDeviceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-devices")
@AllArgsConstructor
public class UserDeviceController {
    UserDeviceService userDeviceService;
    @GetMapping("/{userId}")
    public ResponseEntity<List<UserDeviceResponseDto>> getDevicesByUserId(@PathVariable Integer userId){
        return ResponseEntity.ok(userDeviceService.getDeviceByUserId(userId));
    }

    @PostMapping("/register")
    public ResponseEntity<Boolean> registerDevice(@RequestBody @Valid UserDeviceRegisterDto userDevice){
        userDeviceService.registerDevice(userDevice);
        return ResponseEntity.ok(true);
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<Boolean> deactivateDevice(@RequestParam String fcmToken){
        return ResponseEntity.ok(userDeviceService.deactivateDevice(fcmToken));
    }
}
