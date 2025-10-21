package com.example.api.Responses;

import java.time.LocalDateTime;

import com.example.api.Objects.Address;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserResponse {

    public String id;
    public String name;
    public Address address;
    public String phoneNumber;
    public String email;
    public LocalDateTime createdTimestamp;
    public LocalDateTime updatedTimestamp;

}
