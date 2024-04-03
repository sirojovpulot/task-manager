package uz.task.manager.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import uz.task.manager.entity.User;
import uz.task.manager.payload.ApiResponse;
import uz.task.manager.payload.SignInRequest;
import uz.task.manager.payload.SignUpRequest;
import uz.task.manager.repository.UserRepository;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    public void testSignIn_Success() {
        SignInRequest signInRequest = new SignInRequest("username", "password");
        User user = User.builder()
                .username("username")
                .password("encodedPassword")
                .fullName("FullName").build();

        when(userRepository.findByUsernameIgnoreCase("username")).thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(jwtService.generateToken(user)).thenReturn("access_token");

        ApiResponse response = authService.signIn(signInRequest);

        assertTrue(response.getSuccess());
        assertNotNull(response.getData());
        assertTrue(((Map<String, Object>) response.getData()).containsKey("access_token"));
    }

    @Test
    public void testSignIn_UserNotFound() {
        SignInRequest signInRequest = new SignInRequest("username", "password");

        when(userRepository.findByUsernameIgnoreCase("username")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authService.signIn(signInRequest));

        assertEquals("User not found", exception.getReason());
    }

    @Test
    public void testSignUp_Success() {
        SignUpRequest signUpRequest = new SignUpRequest("username", "password", "FullName");

        when(userRepository.existsByUsernameIgnoreCase("username")).thenReturn(false);

        ApiResponse response = authService.signUp(signUpRequest);

        assertEquals("User registered successfully", response.getMessage());
    }

    @Test
    public void testSignUp_UserAlreadyExists() {
        SignUpRequest signUpRequest = new SignUpRequest("username", "password", "FullName");

        when(userRepository.existsByUsernameIgnoreCase("username")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authService.signUp(signUpRequest));

        assertEquals("User is already exist", exception.getReason());
    }
}