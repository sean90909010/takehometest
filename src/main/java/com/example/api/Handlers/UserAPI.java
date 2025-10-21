package com.example.api.handlers;

import java.security.SecureRandom;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.api.objects.User;
import com.example.api.requests.CreateUserRequest;
import com.example.api.requests.UpdateUserRequest;
import com.example.api.responses.AuthUserResponse;
import com.example.api.responses.UserResponse;
import com.example.api.security.JwtUtil;

import jakarta.validation.Valid;

@RestController
public class UserAPI {

    private static final String PREFIX = "usr-";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ID_LENGTH = 6; // change as desired
    private static final SecureRandom RANDOM = new SecureRandom();

    private JwtUtil jwtUtil;

    public UserAPI(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public static String generateUserId() {
        StringBuilder sb = new StringBuilder(PREFIX);
        for (int i = 0; i < ID_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
    
    @PostMapping(value = "/v1/users", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {

        User newUser = User.builder()
        .id(generateUserId())
        .name(request.name)
        .address(request.address)
        .phoneNumber(request.phoneNumber)
        .email(request.email)
        .build();

        User.users.put(newUser.getId(), newUser);
                // 2) Generate a JWT with subject = userId
        String token = jwtUtil.generateToken(newUser.getId());

        // 3) Mark the request as authenticated right now
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(newUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        return ResponseEntity.created(java.net.URI.create("/v1/users/" + newUser.getId()))
                     .body(userToAuthUserResponse(newUser, token));
        
    }

    @PreAuthorize("#userId == principal.id")
    @GetMapping(value = "/v1/users/{userId}", produces = "application/json")
    public ResponseEntity<UserResponse> getUser(@PathVariable("userId") String userId) {
        System.out.println("Fetching user with ID: " + userId);
        System.out.println("Current users in store: " + User.users.keySet());
        User user = User.users.get(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return ResponseEntity.ok().body(userToUserResponse(user));
    }

    @PatchMapping(value = "/v1/users/{userId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("userId") String userId, @RequestBody UpdateUserRequest request) {
        User existingUser = User.users.get(userId);

        if (existingUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (request.name != null) {
            existingUser.setName(request.name);
        }
        if (request.address != null) {
            existingUser.setAddress(request.address);
        }
        if (request.phoneNumber != null) {
            existingUser.setPhoneNumber(request.phoneNumber);
        }
        if (request.email != null) {
            existingUser.setEmail(request.email);
        }

        User.users.put(userId, existingUser);

        return ResponseEntity.ok().body(userToUserResponse(existingUser));
    }

    @DeleteMapping("/v1/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") String userId) {
        if(!User.users.containsKey(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        User.users.remove(userId);
        return ResponseEntity.ok().body("Deleted user {userId} successfully".replace("{userId}", userId));

    }

    private UserResponse userToUserResponse(User user) {
        return UserResponse.builder()
        .id(user.getId())
        .name(user.getName())
        .address(user.getAddress())
        .phoneNumber(user.getPhoneNumber())
        .email(user.getEmail())
        .createdTimestamp(user.getCreatedTimestamp())
        .updatedTimestamp(user.getUpdatedTimestamp())
        .build();
    }   
    
    private UserResponse userToAuthUserResponse(User user, String token) {
        return AuthUserResponse.authUserResponseBuilder()
        .id(user.getId())
        .name(user.getName())
        .address(user.getAddress())
        .phoneNumber(user.getPhoneNumber())
        .email(user.getEmail())
        .createdTimestamp(user.getCreatedTimestamp())
        .updatedTimestamp(user.getUpdatedTimestamp())
        .token(token)
        .build();
    }

}
