package com.example.api.Objects;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class User {

    public static final Map<String, User> users = new HashMap<>();

    @Pattern(regexp = "^usr-[A-Za-z0-9]+$", message = "ID must start with 'usr-' followed by alphanumeric characters")
    private String id;

    @NotBlank
    private String name;
    @NonNull
    private Address address;
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Email
    @NotBlank
    private String email;

    @Builder.Default
    private LocalDateTime createdTimestamp = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedTimestamp = LocalDateTime.now();

    private HashMap<String, Account> accounts;


}
