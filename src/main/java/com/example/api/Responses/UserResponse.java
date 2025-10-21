package com.example.api.responses;

import java.time.LocalDateTime;

import com.example.api.objects.Address;

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
