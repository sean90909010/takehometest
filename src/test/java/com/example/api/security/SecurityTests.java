package com.example.api.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.api.objects.Address;
import com.example.api.objects.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

class SecurityTests {
    
    private JwtUtil jwtUtil;
    private JwtAuthFilter jwtAuthFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        // Create fresh instances for each test
        jwtUtil = new JwtUtil();
        jwtAuthFilter = new JwtAuthFilter(jwtUtil);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);

        // Clear security context and user map before each test
        SecurityContextHolder.clearContext();
        User.users.clear();
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Arrange
        String userId = "usr-123456";

        // Act
        String token = jwtUtil.generateToken(userId);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Verify token can be validated
        String extractedUserId = jwtUtil.validateAndExtractUserId(token);
        assertEquals(userId, extractedUserId);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnNull() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        String result = jwtUtil.validateAndExtractUserId(invalidToken);

        // Assert
        assertNull(result);
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldAuthenticate() throws IOException, ServletException {
        // Arrange
        String userId = "usr-123456";
        User user = User.builder()
            .id(userId)
            .name("Test User")
            .address(Address.builder()
                .line1("123 Test St")
                .town("Testville")
                .county("Testshire")
                .postcode("TE1 1ST")
                .build())
            .phoneNumber("+441234567890")
            .email("test@example.com")
            .build();
        User.users.put(userId, user);

        String token = jwtUtil.generateToken(userId);
        request.addHeader("Authorization", "Bearer " + token);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(user, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldNotAuthenticate() throws IOException, ServletException {
        // Arrange
        request.addHeader("Authorization", "Bearer invalid.token.here");

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithNoToken_ShouldNotAuthenticate() throws IOException, ServletException {
        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithNonBearerToken_ShouldNotAuthenticate() throws IOException, ServletException {
        // Arrange
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}