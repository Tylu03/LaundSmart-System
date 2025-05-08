package com.c1se22.publiclaundsmartsystem.payload.response;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Integer id;
    private String username;
    private String fullname;
    private String email;
    private String phone;
    private BigDecimal balance = BigDecimal.ZERO;
    private Boolean isActive;
    private LocalDate createdAt;
    private LocalDateTime lastLoginAt;
    private String roleName;
    private Integer roleId;
}
