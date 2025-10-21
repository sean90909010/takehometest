package com.example.api.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.example.api.Main;

class HelloTests {

    Main main = new Main();
    
	@DisplayName("hello - success case")
	@Order(1)
    @Test
	void hello_endpoint() {
		ResponseEntity<String> resp = main.hello();
		assertNotNull(resp);
		assertEquals(200, resp.getStatusCode().value());
		assertNotNull(resp.getBody());
		String body = resp.getBody();
        assertNotNull(body);
        assertEquals("Hello, World!", body);
	}

}
