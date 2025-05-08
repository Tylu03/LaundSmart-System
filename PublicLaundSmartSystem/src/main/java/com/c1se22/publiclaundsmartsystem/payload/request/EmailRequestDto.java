package com.c1se22.publiclaundsmartsystem.payload.request;

import com.c1se22.publiclaundsmartsystem.annotation.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
public class EmailRequestDto {
    @NotNull(message = "Email cannot be null")
    @Email
    private String email;
}
