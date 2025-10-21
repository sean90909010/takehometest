package com.example.api.endpoint;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import com.example.api.handlers.UserAPI;
import com.example.api.objects.Address;
import com.example.api.objects.User;
import com.example.api.requests.CreateUserRequest;
import com.example.api.requests.UpdateUserRequest;
import com.example.api.responses.UserResponse;
import com.example.api.security.JwtUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.web.server.ResponseStatusException;

@TestMethodOrder(OrderAnnotation.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@SuppressWarnings("null")
class UserAPITests {

	private JwtUtil jwtUtil;
	private UserAPI userAPI;

	@BeforeEach
	void setUp() {
		jwtUtil = Mockito.mock(JwtUtil.class);
		when(jwtUtil.generateToken(anyString())).thenReturn("test-token");
		userAPI = new UserAPI(jwtUtil);
		User.users.clear();
	}

	@AfterEach
	void tearDown() {
		User.users.clear();
	}

	static Stream<Arguments> validCreateRequests() {
		Address addr = Address.builder()
			.line1("1 Test St")
			.town("Testville")
			.county("Testshire")
			.postcode("TST1 1ST")
			.build();

		CreateUserRequest r1 = CreateUserRequest.builder()
			.name("Alice")
			.address(addr)
			.phoneNumber("+441234567890")
			.email("alice@example.com")
			.build();

		CreateUserRequest r2 = CreateUserRequest.builder()
			.name("Bob")
			.address(addr)
			.phoneNumber("+441234567891")
			.email("bob@example.com")
			.build();

		return Stream.of(Arguments.of(r1), Arguments.of(r2));
	}

	@ParameterizedTest
	@MethodSource("validCreateRequests")
	@DisplayName("createUser - success cases (parameterized)")
	@Order(1)
	void createUser_success(CreateUserRequest request) {
		ResponseEntity<UserResponse> resp = userAPI.createUser(request);
		assertNotNull(resp);
		assertEquals(201, resp.getStatusCode().value());
		assertNotNull(resp.getBody());
		UserResponse body = resp.getBody();
        assertNotNull(body);
		assertNotNull(body.getId());
		assertEquals(request.getName(), body.getName());
		// user stored in static map
		assertTrue(User.users.containsKey(body.getId()));
	}

	static Stream<Arguments> missingUserIds() {
		return Stream.of(Arguments.of("usr-xxxxxx"), Arguments.of("usr-unknown"));
	}

	@ParameterizedTest
	@MethodSource("missingUserIds")
	@DisplayName("getUser - not found cases (parameterized)")
	@Order(2)
	void getUser_notFound(String userId) {
		ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userAPI.getUser(userId));
		assertEquals(404, ex.getStatusCode().value());
	}

	static Stream<Arguments> updateAndDeleteScenarios() {
		return Stream.of(Arguments.of("Alice", "alice@example.com", "new-name", "new@example.com"));
	}

	@ParameterizedTest
	@MethodSource("updateAndDeleteScenarios")
	@DisplayName("updateUser and deleteUser lifecycle (parameterized)")
	@Order(3)
	void updateAndDelete_lifecycle(String name, String email, String newName, String newEmail) {
		// create user
		Address addr = Address.builder()
			.line1("1 Test St")
			.town("Testville")
			.county("Testshire")
			.postcode("TST1 1ST")
			.build();

		CreateUserRequest createReq = CreateUserRequest.builder()
			.name(name)
			.address(addr)
			.phoneNumber("+441234567899")
			.email(email)
			.build();

		ResponseEntity<UserResponse> created = userAPI.createUser(createReq);
		UserResponse createdBody = created.getBody();
		assertNotNull(createdBody);

		String id = createdBody.getId();

		// update user
		UpdateUserRequest upd = UpdateUserRequest.builder().name(newName).email(newEmail).build();
		ResponseEntity<UserResponse> updated = userAPI.updateUser(id, upd);
		assertEquals(200, updated.getStatusCode().value());
        assertNotNull(updated.getBody());
		assertEquals(newName, updated.getBody().getName());
		assertEquals(newEmail, updated.getBody().getEmail());

		// delete user
		ResponseEntity<String> deleted = userAPI.deleteUser(id);
		assertEquals(200, deleted.getStatusCode().value());
		assertFalse(User.users.containsKey(id));
	}

}
