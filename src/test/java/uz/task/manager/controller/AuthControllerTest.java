package uz.task.manager.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uz.task.manager.payload.ApiResponse;
import uz.task.manager.payload.SignInRequest;
import uz.task.manager.payload.SignUpRequest;
import uz.task.manager.service.AuthService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void signIn_WithValidRequest_ShouldReturnOkResponse() {
        SignInRequest signInRequest = new SignInRequest("username", "password");

        when(authService.signIn(signInRequest))
                .thenReturn(ApiResponse.builder()
                        .data(Map.of("access_token", "token"))
                        .success(true)
                        .build());

        ResponseEntity<ApiResponse> response = authController.signIn(signInRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getSuccess());
        assertTrue(((Map<String, Object>) response.getBody().getData()).containsKey("access_token"));
        assertEquals("token", ((Map<String, Object>) response.getBody().getData()).get("access_token"));
    }

    @Test
    void signUp_WithValidRequest_ShouldReturnOkResponse() {
        SignUpRequest signUpRequest = new SignUpRequest("username", "password", "FullName");

        when(authService.signUp(signUpRequest))
                .thenReturn(ApiResponse.builder()
                        .message("User registered successfully")
                        .build());

        ResponseEntity<ApiResponse> response = authController.signUp(signUpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully", response.getBody().getMessage());
    }
}