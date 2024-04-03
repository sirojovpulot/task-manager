package uz.task.manager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import uz.task.manager.entity.User;
import uz.task.manager.payload.ApiResponse;
import uz.task.manager.payload.SignInRequest;
import uz.task.manager.payload.SignUpRequest;
import uz.task.manager.repository.UserRepository;
import uz.task.manager.service.AuthService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse signIn(SignInRequest signInRequest) {
        User user = userRepository.findByUsernameIgnoreCase(signInRequest.getUsername().trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User not found"));
        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword()))
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Password is incorrect");
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername().trim().toLowerCase(), signInRequest.getPassword().trim()));
        if (authentication.isAuthenticated()) {
            String access_token = jwtService.generateToken(user);
            Map<String, Object> map = new HashMap<>();
            map.put("access_token", access_token);
            return ApiResponse.builder()
                    .success(true)
                    .data(map)
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid user request!");
        }
    }

    @Override
    @Transactional
    public ApiResponse signUp(SignUpRequest signUpRequest) {
        Boolean isUserExisted = userRepository.existsByUsernameIgnoreCase(signUpRequest.getUsername().trim().toLowerCase());
        if (isUserExisted) throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already exist");
        User user = User.builder()
                .username(signUpRequest.getUsername().trim().toLowerCase())
                .password(passwordEncoder.encode(signUpRequest.getPassword().trim()))
                .fullName(signUpRequest.getFullName().trim())
                .build();
        userRepository.save(user);
        return ApiResponse.builder()
                .message("User registered successfully").build();
    }
}
