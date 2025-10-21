package com.example.api.Requests;

import com.example.api.Objects.Address;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateUserRequest {
        
    public String name;
    public Address address;
    public String phoneNumber;
    public String email;
    
}
