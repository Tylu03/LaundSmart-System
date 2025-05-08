package com.c1se22.publiclaundsmartsystem.payload.request;

import com.c1se22.publiclaundsmartsystem.annotation.Email;
import com.c1se22.publiclaundsmartsystem.annotation.Password;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterDto {
    @NotNull(message="Username is required")
    @Size(min=5, max=20, message="Username must be between 5 and 20 characters")
    @Pattern(regexp="^[a-zA-Z0-9]*$", message="Only alphanumeric characters are allowed")
    private String username;
    @NotNull(message="Password is required")
    @Size(min=8, max=20, message="Password must be between 8 and 20 characters")
    @Password
    private String password;
    @NotNull(message="Retype password is required")
    @Size(min=8, max=20, message="Confirm password must be between 8 and 20 characters")
    private String confirmPassword;
    @NotNull(message="Email is required")
    @NotEmpty(message="Email is required")
    @Size(min=5, max=50, message="Email must be between 5 and 50 characters")
    @Email
    private String email;
    @NotNull(message="Fullname is required")
    @NotEmpty(message="Fullname is required")
    @Size(min=5, max=50, message="Fullname must be between 5 and 50 characters")
    @Pattern(regexp="^[a-zA-Z ]*$", message="Only alphabetic characters are allowed")
    private String fullname;
    @NotNull(message="Phone is required")
    @NotEmpty(message="Phone is required")
    @Size(min=10, max=15, message="Phone must be between 10 and 15 characters")
    @Pattern(regexp="^[0-9]*$", message="Only numeric characters are allowed")
    private String phone;
}
