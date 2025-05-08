package com.c1se22.publiclaundsmartsystem.service.impl;

import com.c1se22.publiclaundsmartsystem.entity.Machine;
import com.c1se22.publiclaundsmartsystem.entity.User;
import com.c1se22.publiclaundsmartsystem.enums.ErrorCode;
import com.c1se22.publiclaundsmartsystem.exception.APIException;
import com.c1se22.publiclaundsmartsystem.exception.ResourceNotFoundException;
import com.c1se22.publiclaundsmartsystem.payload.response.UserDto;
import com.c1se22.publiclaundsmartsystem.repository.MachineRepository;
import com.c1se22.publiclaundsmartsystem.repository.UserRepository;
import com.c1se22.publiclaundsmartsystem.service.MachineService;
import com.c1se22.publiclaundsmartsystem.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    MachineRepository machineRepository;
    MachineService machineService;

    @Override
    public List<UserDto> getAllUsers(){
        return userRepository.findAll().stream().map(this::mapToUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Integer id){
        User user = userRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("User", "id", id.toString()));
        return mapToUserDto(user);
    }

//    @Override
//    public UserDto addUser(UserDto userDto) {
//        User user = new User();
//        user.setUsername(userDto.getUsername());
//        user.setFullname(userDto.getFullname());
//        user.setEmail(userDto.getEmail());
//        user.setPhone(userDto.getPhone());
//        user.setBalance(userDto.getBalance());
//        user.setCreatedAt(userDto.getCreatedAt());
//        user.setLastLoginAt(userDto.getLastLoginAt());
//        return mapToUserDto(userRepository.save(user));
//    }

    @Override
    public UserDto updateUser(Integer id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("User", "id", id.toString())
        );
        user.setUsername(userDto.getUsername());
        user.setFullname(userDto.getFullname());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setBalance(userDto.getBalance());
        user.setCreatedAt(userDto.getCreatedAt());
        user.setLastLoginAt(userDto.getLastLoginAt());
        return mapToUserDto(userRepository.save(user));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("User", "id", id.toString()));
        user.setIsActive(false);
        if (user.getRoles().stream().noneMatch(role -> role.getName().equals("ROLE_OWNER"))){
            userRepository.save(user);
            return;
        }
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))){
            throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.CAN_NOT_DELETE, "user with role ADMIN");
        }
        List<Machine> machines = machineRepository.findMachinesByOwnerId(id);
        try {
            for (Machine machine : machines){
                machineService.deleteMachine(machine.getId());
            }
        } catch (Exception e){
            throw new APIException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.CAN_NOT_DELETE, "machine still in use");
        }
        user.getRoles().removeIf(role -> role.getName().equals("ROLE_OWNER"));
        userRepository.save(user);
    }

    private UserDto mapToUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .balance(user.getBalance())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .isActive(user.getIsActive())
                .build();
    }
}
