package com.example.api.responses;

import lombok.Builder;

public class AuthUserResponse extends UserResponse {

    public String token;

    @Builder(builderMethodName = "authUserResponseBuilder")
    public AuthUserResponse(String id, String name, com.example.api.objects.Address address, String phoneNumber,
            String email, java.time.LocalDateTime createdTimestamp, java.time.LocalDateTime updatedTimestamp,
            String token) {
        super(id, name, address, phoneNumber, email, createdTimestamp, updatedTimestamp);
        this.token = token;
    }

}
