package com.example.api.requests;

import com.example.api.objects.Address;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateUserRequest {
    
    @NotNull(message = "Name cannot be null")
    @Valid
    public String name;

    @NotNull(message = "Address cannot be null")
    @Valid
    public Address address;

    @NotNull(message = "Phone number cannot be null")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid phone number format: Numbers must be in E.164 format, starting with '+' followed by country code and up to 14 digits")
    @Valid
    public String phoneNumber;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email format is invalid")
    @Valid
    public String email;
    
}
