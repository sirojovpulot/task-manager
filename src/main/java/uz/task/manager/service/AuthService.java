package uz.task.manager.service;

import uz.task.manager.payload.ApiResponse;
import uz.task.manager.payload.SignInRequest;
import uz.task.manager.payload.SignUpRequest;

public interface AuthService {
    ApiResponse signIn(SignInRequest signInRequest);

    ApiResponse signUp(SignUpRequest signUpRequest);
}
