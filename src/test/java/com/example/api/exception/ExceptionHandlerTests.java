package com.example.api.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import com.example.api.handlers.GlobalExceptionHandler;

class ExceptionHandlerTests {
    
    private GlobalExceptionHandler exceptionHandler;
    
    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }
    
    @Test
    void handleValidationExceptions_ShouldReturnBadRequestWithErrors() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        
        FieldError fieldError = new FieldError("objectName", "field1", "error message");
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));
        
        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationExceptions(ex);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
        assertEquals("The request didn't supply all the necessary data", body.get("error"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) body.get("details");
        assertNotNull(details);
        assertEquals("error message", details.get("field1"));
    }
    
    @Test
    void handleInvalidOperationExceptions_ShouldReturnUnprocessableEntity() {
        // Arrange
        String errorMessage = "Invalid operation";
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, errorMessage);
        
        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleInvalidOperationExceptions(ex);
        
        // Assert
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), body.get("status"));
        assertEquals(errorMessage, body.get("error"));
    }
}
