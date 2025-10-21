package com.example.api.requests;

import com.example.api.objects.Address;

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
