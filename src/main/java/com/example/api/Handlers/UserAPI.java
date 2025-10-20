package com.example.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAPI {
    
    @PostMapping("/v1/users")
    public User createUser() {

}
